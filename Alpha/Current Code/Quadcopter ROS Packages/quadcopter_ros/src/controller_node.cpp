#include <ros/ros.h>
#include <nav_msgs/Path.h>
#include <geometry_msgs/Twist.h>
#include <hector_nav_msgs/GetRobotTrajectory.h>
#include <hector_path_follower/hector_path_follower.h>

pose_follower::HectorPathFollower path_follower_;
bool pathSet = false;

void pathCallback(const nav_msgs::Path::ConstPtr &msg){
    ROS_INFO("message receieved: %i", (int)msg->poses.size());
    path_follower_.setPlan(msg->poses);
    pathSet = true;
}

int main(int argc, char** argv){
    ros::init(argc, argv, "controller_node");
    ros::NodeHandle n;
    ros::Rate loop_rate(5);

    tf::TransformListener tf1_;

    path_follower_.initialize(&tf1_);

    ros::Publisher cmd_pub = n.advertise<geometry_msgs::Twist>("cmd_vel", 10);
    ros::Subscriber path_sub = n.subscribe("trajMsg", 1000 , pathCallback);

    while(ros::ok()){
        if(pathSet == true){
            geometry_msgs::Twist twist;
            path_follower_.computeVelocityCommands(twist);
            cmd_pub.publish(twist);
        }
        else{
            //ROS_INFO("path is still not set");
        }
        ros::spinOnce();
        loop_rate.sleep();
    }
}
