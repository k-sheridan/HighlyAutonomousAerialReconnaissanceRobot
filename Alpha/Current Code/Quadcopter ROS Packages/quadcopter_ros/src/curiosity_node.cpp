#include <ros/ros.h>
#include <image_transport/image_transport.h>
#include <opencv2/highgui/highgui.hpp>
#include <cv_bridge/cv_bridge.h>
#include <opencv2/objdetect/objdetect.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <stdio.h>
#include <iostream>
#include <geometry_msgs/PoseStamped.h>
#include <geometry_msgs/PoseArray.h>
#include <geometry_msgs/Pose.h>
#include <math.h>

using namespace cv;

cv::CascadeClassifier faceClassifier;
std::string faceCascadeName = "/home/ubuntu/catkin_ws/src/quadcopter_ros/classifiers/haarcascade_frontalface_alt.xml";

geometry_msgs::PoseStamped current_pose;
geometry_msgs::PoseArray facePoses;

double mapDouble(double x, double in_min, double in_max, double out_min, double out_max){
    return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
}

void poseCallback(const geometry_msgs::PoseStamped msg){
    current_pose = msg;
}

void detectFaces(Mat frame){
    std::vector<Rect> faces;
    faceClassifier.detectMultiScale(frame, faces, 1.1, 2, 0|CV_HAAR_SCALE_IMAGE, Size(15, 15));
    for(size_t i = 0; i < faces.size(); i++){
        ROS_INFO("face found size: %i, %i poseition: %i, %i", (int)faces[i].width, faces[i].height, faces[i].x, faces[i].y);
        Point center(faces[i].x + faces[i].width*0.5, faces[i].y + faces[i].height*0.5);
        ellipse(frame, center, Size(faces[i].width*0.5, faces[i].height*0.5), 0, 0, 360, Scalar(255), 4, 8, 0);

        //place face on map
        double dist = mapDouble((double)faces[i].width, 41.0, 76.0, 2.5, 1);
        double dTheta = mapDouble((double)faces[i].x, 0.0, 320.0, 0.52, -0.52);
        ROS_INFO("face relative to camera angle: %f dist: %f", dTheta, dist);
        double faceTheta = current_pose.pose.orientation.z + dTheta;
        double dX = dist * std::cos(faceTheta);
        double dY = dist * std::sin(faceTheta);

        geometry_msgs::Pose tempFacePose;
        tempFacePose.position.x = current_pose.pose.position.x + dX;
        tempFacePose.position.y = current_pose.pose.position.y + dY;
        facePoses.poses.push_back(tempFacePose);
        facePoses.header.frame_id = "map";
    }

}

int main(int argc, char** argv){
    ros::init(argc, argv, "curiosity_node");
    ros::NodeHandle n;
    ros::Subscriber pose_sub = n.subscribe("slam_out_pose", 1, poseCallback);
    ros::Publisher face_pub = n.advertise<geometry_msgs::PoseArray>("face_positions", 1);
    image_transport::ImageTransport it(n);
    image_transport::Publisher frame_pub;
    frame_pub = it.advertise("/curiosity/frame",1);
    ros::Rate loop_rate(0.5);

    if(!faceClassifier.load(faceCascadeName)){ // loads cascade into program
        ROS_ERROR("error loading cascade");
        return -1;
    }
    VideoCapture cap(0);
    cap.set(CV_CAP_PROP_FRAME_WIDTH, 320);
    cap.set(CV_CAP_PROP_FRAME_HEIGHT, 240);

    while(ros::ok()){
        Mat frame;
        ros::spinOnce();
        if(cap.isOpened()){
            if(cap.read(frame)){
                //ROS_INFO("%i, %i", frame.cols, frame.rows);
                cvtColor(frame, frame, CV_BGR2GRAY);
                detectFaces(frame);
            }
            else{
                ROS_INFO("frame read failed!");
            }
        }

        sensor_msgs::ImagePtr msg =  cv_bridge::CvImage(std_msgs::Header(), "mono8", frame).toImageMsg();
        frame_pub.publish(msg);
        face_pub.publish(facePoses);

        loop_rate.sleep();
    }
}
