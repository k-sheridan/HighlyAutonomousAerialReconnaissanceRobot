#include <stdio.h>
#include <unistd.h>			//Used for UART
#include <fcntl.h>			//Used for UART
#include <termios.h>		//Used for UART
#include <iostream>
#include <sys/ioctl.h>
// ROS STUFF
#include <boost/lexical_cast.hpp>
#include <ros/ros.h>
#include <std_msgs/String.h>
#include <std_msgs/Float32MultiArray.h>
#include "std_msgs/ByteMultiArray.h"
#include <geometry_msgs/PoseStamped.h>
#include <geometry_msgs/Pose.h>
#include <geometry_msgs/Twist.h>
#include <sstream>
#include <string>
#include <string.h>
#include <math.h>
#include <tf/tf.h>

float char2Float(unsigned char data[], int offset);

//ROS variables
std::string serialInString;
std::string wifiInString = "-----";
std::string poseString = "false";
std::string cmdString = "false";

geometry_msgs::Pose last_pose;
ros::Time last_time;
//=-=-=-=-=-=-=
int uart0_filestream = -1;
bool bytesReceived = true;
int connectionCounter = 0;
bool connection = false;

bool poseRecieved = false;
bool cmdRecieved = false;

std::string stringifyFloat(float value){
    std::ostringstream oss;
    oss << value;
    return oss.str();
}
void cmdCallback(const geometry_msgs::Twist::ConstPtr &msg){
    //ROS_INFO("cmd recieved");
    cmdString.clear();
    std::ostringstream strTemp;

    strTemp << ",true," << msg->linear.x << "," << msg->linear.y << "," << msg->angular.z;

    cmdRecieved = true;
    cmdString = strTemp.str();
    //std::cout << cmdString << "\n";
}

void poseCallback(const geometry_msgs::PoseStamped::ConstPtr &msg){
    //ROS_INFO("pose recieved");
    //calculate rate algorithm
    double predx = msg->pose.position.x - last_pose.position.x;
    double predy = msg->pose.position.y - last_pose.position.y;


    double dist = sqrt(pow(predx, 2.0) + pow(predy, 2.0));

    double slope = std::atan2(predy, predx);

    if(slope < 0.0){
        slope += 6.283185307;
    }
    double quadZ;
    tf::Pose tf_pose;
    tf::poseMsgToTF(last_pose, tf_pose);
    quadZ = tf::getYaw(tf_pose.getRotation());
    if(quadZ < 0.0){
        quadZ += 6.283185307;
    }


    double relSlope = slope - quadZ;

    double dx1 = dist * std::cos(relSlope);
    double dy1 = dist * std::sin(relSlope);


    //float dx = dx1;
    //float dy = dy1;

    tf::poseMsgToTF(msg->pose, tf_pose);
    quadZ = tf::getYaw(tf_pose.getRotation());
    if(quadZ < 0.0){
        quadZ += 6.283185307;
    }
    //ROS_INFO("quad angle: %f", quadZ);

    relSlope = slope - quadZ;


    double dx2 = dist * std::cos(relSlope);
    double dy2 = dist * std::sin(relSlope);

    double dx = (dx1 + dx2) / 2.0;
    double dy = (dy1 + dy2) / 2.0;


    ros::Duration dt = ros::Time::now() - last_time;

    //***
    dx = dx / dt.toSec();
    dy = dy / dt.toSec();


    //ROS_INFO("%i %i ",(int)(dx * 100.0), (int)(dy * 100.0));
    //ROS_INFO("quad angle: &f slope angle: %f", quadZ, slope);

    //std::cout << "quadz:" << quadZ << "slope:" << slope << "dist:" << dist << "x:" << dx << "y:" << dy << "\n";

    last_pose = msg->pose;
    last_time = ros::Time::now();


    poseString.clear();
    std::ostringstream strTemp;

    strTemp << ",true," << dx << "," << dy << "," << msg->pose.orientation.z;

    poseRecieved = true;
    poseString = strTemp.str();
    //std::cout << poseString << "\n";
}

void wifiInCallback(const geometry_msgs::Twist msg){
    std::ostringstream strTemp;

    if(msg.angular.z == 1.00){
        strTemp << "true," << msg.linear.x;
    }
    else{
        strTemp << "false," << msg.linear.x;
    }

    wifiInString = strTemp.str();

    if(connectionCounter >= 2){
        bytesReceived = true;
    }
    connectionCounter = 0;
    connection = true;
}

void connectionCheck(){
  if(connectionCounter >= 2){
    connection = false;
  }
  connectionCounter++;
}

void transferSerialData(){
	//----- CHECK FOR ANY RX BYTES -----
	if (uart0_filestream != -1)
	{
		// Read up to 255 characters from the port if they are there
		unsigned char rx_buffer[256];
		int rx_length = read(uart0_filestream, (void*)rx_buffer, 255);		//Filestream, buffer to store in, number of bytes to read (max)
		if (rx_length < 0)
		{
			//An error occured (will occur if there are no bytes)
		}
		else if (rx_length == 0)
		{
			//No data waiting
		}
		else //process data
		{
			//Bytes received
			rx_buffer[rx_length] = '\0';
			std::string temp;
			for(int i = 0; i < rx_length; i++){
				temp += rx_buffer[i];
			}
			
			serialInString = temp;
			
			if(rx_length >= 4){ // check to see if the data is there
				bytesReceived = true;
			}
		}
	}
    //----- TX BYTES -----
    if(bytesReceived == true){
        unsigned char tx_buffer[25];
        //unsigned char tx2_buffer[25]; // the bytes to send to the teensy
		unsigned char *p_tx_buffer;
        //unsigned char *p2_tx_buffer;
	
		p_tx_buffer = &tx_buffer[0];
        //p2_tx_buffer = &tx2_buffer[0];

        //load data into the buffer for transmission
		*p_tx_buffer++ = 'a';
		*p_tx_buffer++ = '\n';
        if(connection){
            *p_tx_buffer++ = 't';
            *p_tx_buffer++ = 'r';
            *p_tx_buffer++ = 'u';
            *p_tx_buffer++ = 'e';
            *p_tx_buffer++ = ',';
        }
        else{
            wifiInString = "-----";
            *p_tx_buffer++ = 'f';
            *p_tx_buffer++ = 'a';
            *p_tx_buffer++ = 'l';
            *p_tx_buffer++ = 's';
            *p_tx_buffer++ = 'e';
            *p_tx_buffer++ = ',';
        }
		for(int i = 0; i < wifiInString.length(); i++){
			if(wifiInString[i] != NULL){
				*p_tx_buffer++ = wifiInString[i];
			}
			else{
				*p_tx_buffer++ = 'x';
			}
		}
        if(poseRecieved){
            for(int i = 0; i < poseString.length(); i++){
                *p_tx_buffer++ = poseString[i];
            }
            poseRecieved = false;
        }
        else{
            poseString = ",false";
            for(int i = 0; i < poseString.length(); i++){
                *p_tx_buffer++ = poseString[i];
            }
        }
        if(cmdRecieved){
            for(int i = 0; i < cmdString.length(); i++){
                *p_tx_buffer++ = cmdString[i];
            }
            cmdRecieved = false;
        }
        else{
            cmdString = ",false";
            for(int i = 0; i < cmdString.length(); i++){
                *p_tx_buffer++ = cmdString[i];
            }
        }
        *p_tx_buffer++ = ','; //final comma for last value

        //*p_tx_buffer++ = 'z';
        //ROS_INFO("%s%s%s", wifiInString.c_str(), poseString.c_str(), cmdString.c_str());
		//transmit
		if (uart0_filestream != -1)
		{
			int count = write(uart0_filestream, &tx_buffer[0], (p_tx_buffer - &tx_buffer[0]));		//Filestream, bytes to write, number of bytes to write
			if (count < 0)
			{
				ROS_INFO("UART TX error\n");
			}

			
			bytesReceived = false;
		}
		}
	//-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
}


int main(int argc, char **argv){
	// INITIALIZE ROS
	ros::init(argc, argv, "serial_node");
	ros::NodeHandle n;
	ros::Rate loop_rate(10);
    last_time = ros::Time::now();
	
	ros::Publisher serial_pub = n.advertise<std_msgs::String>("serial_in", 1000);
	ros::Subscriber wifi_sub = n.subscribe("wifi_cmd", 1000, wifiInCallback);
    ros::Subscriber pose_sub = n.subscribe("slam_out_pose", 1, poseCallback);
    ros::Subscriber cmd_sub = n.subscribe("cmd_vel", 1, cmdCallback);
	
	// INITIALIZE THE UART SERIAL PORT
	uart0_filestream = open("/dev/ttyAMA0", O_RDWR | O_NOCTTY | O_NDELAY);		
	
	if (uart0_filestream == -1)
	{
		//ERROR - CAN'T OPEN SERIAL PORT
		ROS_INFO("Error - Unable to open UART.  Ensure it is not in use by another application\n");
	}
	
	struct termios options;
	tcgetattr(uart0_filestream, &options);
	options.c_cflag = B115200 | CS8 | CLOCAL | CREAD;		//<Set baud rate
	options.c_iflag = IGNPAR;
	options.c_oflag = 0;
	options.c_lflag = 0;
	tcflush(uart0_filestream, TCIFLUSH);
	tcsetattr(uart0_filestream, TCSANOW, &options);
	
	// main loop
	
	while(ros::ok()){

        transferSerialData();

		//Publishes data to the topic
		std_msgs::String serialIn_msg;
		serialIn_msg.data = serialInString;
		serial_pub.publish(serialIn_msg);
		
		ros::spinOnce(); // process callbacks
		
        connectionCheck();

		loop_rate.sleep();	// keep 10 hertz loop rate
					
		//close(uart0_filestream);		
	}
}
