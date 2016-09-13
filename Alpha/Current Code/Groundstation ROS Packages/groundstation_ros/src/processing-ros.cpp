#include<iostream>    //cout
#include<stdio.h> //printf
#include<string.h>    //strlen
#include<string>  //string
#include<sys/socket.h>    //socket
#include<arpa/inet.h> //inet_addr
#include<netdb.h> //hostent

#include <vector>
#include "ros/ros.h"
#include "geometry_msgs/PoseStamped.h"
#include "geometry_msgs/PoseWithCovariance.h"
#include "std_msgs/String.h"
#include "image_transport/image_transport.h"
#include <boost/algorithm/string.hpp>

#include <sstream>

using namespace std;
using namespace boost;

/**
    TCP Client class
*/
class tcp_client
{
private:
    int sock;
    std::string address;
    int port;
    struct sockaddr_in server;

public:
    tcp_client();
    bool conn(string, int);
    bool send_data(string data);
    string receive(int);
};

tcp_client::tcp_client()
{
    sock = -1;
    port = 0;
    address = "";
}

/**
    Connect to a host on a certain port number
*/
bool tcp_client::conn(string address , int port)
{
    //create socket if it is not already created
    if(sock == -1)
    {
        //Create socket
        sock = socket(AF_INET , SOCK_STREAM , 0);
        if (sock == -1)
        {
            perror("Could not create socket");
        }

        cout<<"Socket created\n";
    }
    else    {   /* OK , nothing */  }

    //setup address structure
    if(inet_addr(address.c_str()) == -1)
    {
        struct hostent *he;
        struct in_addr **addr_list;

        //resolve the hostname, its not an ip address
        if ( (he = gethostbyname( address.c_str() ) ) == NULL)
        {
            //gethostbyname failed
            herror("gethostbyname");
            cout<<"Failed to resolve hostname\n";

            return false;
        }

        //Cast the h_addr_list to in_addr , since h_addr_list also has the ip address in long format only
        addr_list = (struct in_addr **) he->h_addr_list;

        for(int i = 0; addr_list[i] != NULL; i++)
        {
            //strcpy(ip , inet_ntoa(*addr_list[i]) );
            server.sin_addr = *addr_list[i];

            cout<<address<<" resolved to "<<inet_ntoa(*addr_list[i])<<endl;

            break;
        }
    }

    //plain ip address
    else
    {
        server.sin_addr.s_addr = inet_addr( address.c_str() );
    }

    server.sin_family = AF_INET;
    server.sin_port = htons( port );

    //Connect to remote server
    if (connect(sock , (struct sockaddr *)&server , sizeof(server)) < 0)
    {
        perror("connect failed. Error");
        return 1;
    }

    cout<<"Connected\n";
    return true;
}

/**
    Send data to the connected host
*/
bool tcp_client::send_data(string data)
{
    //Send some data
    if( send(sock , data.c_str() , strlen( data.c_str() ) , 0) < 0)
    {
        perror("Send failed : ");
        return false;
    }

    //cout<<"Data send\n";

    return true;
}

/**
    Receive data from the connected host
*/
string tcp_client::receive(int size=512)
{
    char buffer[size];
    string reply;

    //Receive a reply from the server
    if( recv(sock , buffer , sizeof(buffer) , 0) < 0)
    {
        puts("recv failed");
    }

    reply = buffer;
    return reply;
}


//--------------initialize variables-------------------------
bool connectionVerified = false;
std::string serialInString = "-";
std::string poseInString = "-";
//-----------------------------------------------------------

std::string stringifyFloat(float value)
{
     std::ostringstream oss;
     oss << value;
     return oss.str();
}

void poseInCallBack(const geometry_msgs::PoseStamped& pose_msg){
    poseInString = "";
    poseInString += stringifyFloat(pose_msg.pose.position.x);
    poseInString += ",";
    poseInString += stringifyFloat(pose_msg.pose.position.y);
    poseInString += ",";
    poseInString += stringifyFloat(pose_msg.pose.orientation.w);
}

void serialInCallback(const std_msgs::String::ConstPtr& msg){
    serialInString = msg->data;
}

string sendPacket(char mode){
    //intitialize variables
    string dataString; // initialize the string
    // main logic
    if(mode == 'a'){ // base variable mode
        dataString += 'a';
        dataString += '\n';
        dataString += serialInString;
        dataString += ",";
        dataString += poseInString;
    }
    //-----------send out data

    return(dataString);
}


int main(int argc , char *argv[])
{
    tcp_client c;
    string host;

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    ros::init(argc, argv, "processing-ros_link"); // initializes ros

    ros::NodeHandle n;// neccesary for ros

    ros::Publisher wifi_pub = n.advertise<std_msgs::String>("wifi_cmd", 100); //creates a publisher for the recieved information (currently of type string)
    ros::Subscriber serial_sub = n.subscribe("serial_in", 100, serialInCallback);
    ros::Subscriber pose_sub = n.subscribe("slam_out_pose", 100, poseInCallBack);

    ros::Rate loop_rate(10); // used to keep polling at constant rate
    //-=-=-=-=-=-=-=-=-=-=-=-=-

    host = "127.0.0.1"; //local host

    //connect to host
    c.conn(host , 10002);

    //-=-=-=-=--=-=-=-=-=-=-=-=-

    while(ros::ok()){ //checks health of ros node
        //initialize variables

        string recvVal;

        //send some data
        c.send_data(sendPacket('a'));

        //received 200 bytes max
        recvVal = c.receive(200);
        std::cout << recvVal << "\n";
        //publish recvValProc;

        std_msgs::String wifiIn_msg;
        wifiIn_msg.data = recvVal;
        wifi_pub.publish(wifiIn_msg);

        ros::spinOnce();
        loop_rate.sleep(); // maintains constant loop speed while freeing up cpu time
    }

    //done
    return 0;
}
