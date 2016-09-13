#include <ros/ros.h>
#include <hector_path_follower/hector_path_follower.h>
#include <hector_nav_msgs/GetRobotTrajectory.h>
#include <geometry_msgs/PoseStamped.h>
#include <nav_msgs/Path.h>
#include <move_base_msgs/MoveBaseAction.h>
#include <actionlib/client/simple_action_client.h>
#include <actionlib_msgs/GoalStatus.h>
#include <actionlib/client/terminal_state.h>
#include <math.h>

typedef actionlib::SimpleActionClient<move_base_msgs::MoveBaseAction> MoveBaseClient;
bool goalExpired = true;
bool gotBasePose = false;
//bool firstGoalSet = false;
geometry_msgs::PoseStamped current_goal;
geometry_msgs::PoseStamped pose;
geometry_msgs::PoseStamped basePose;

void poseCallback(const geometry_msgs::PoseStamped msg){
    pose = msg;
    if(!gotBasePose){
        basePose = msg;
    }
}

int main(int argc, char **argv) {
    ros::init(argc, argv, ROS_PACKAGE_NAME);
    ros::NodeHandle nh;
    ros::Rate loop_rate(1);
    ros::Subscriber pose_sub = nh.subscribe("slam_out_pose", 1, poseCallback);
    ros::ServiceClient exploration_plan_service_client_;
    exploration_plan_service_client_ = nh.serviceClient<hector_nav_msgs::GetRobotTrajectory>("get_exploration_path");

    MoveBaseClient ac("move_base", true);
    while(!ac.waitForServer(ros::Duration(5.0))){
        ROS_INFO("waiting for ac client");
    }
    ros::Duration(10.0);
    while(ros::ok()){
        if(goalExpired){
            hector_nav_msgs::GetRobotTrajectory srv_exploration_plan;
            if(exploration_plan_service_client_.call(srv_exploration_plan)){
                current_goal = srv_exploration_plan.response.trajectory.poses.back();
                goalExpired = false;
            }
            else{
                ROS_INFO("[exploration_node] failed to find new goal heading home!");
            }
        }
        else{
           move_base_msgs::MoveBaseGoal goal;
           goal.target_pose = current_goal;
           ac.sendGoal(goal);
           ROS_INFO("[exploration_node] HI Google Science Fair! goal sent");
           if(ac.waitForResult(ros::Duration(2.0))){
               goalExpired = true;
               ROS_INFO("[exploration_node] reached goal finding new goal");
           }
           else{
               if(ac.getState().ACTIVE){
                //checks distance to goal
                float targetDist = fabs(sqrt(pow(current_goal.pose.position.x - pose.pose.position.x, 2) + pow(current_goal.pose.position.y - pose.pose.position.y, 2)));
                if(targetDist < 1.5){
                    ROS_INFO("[exploration_node] Quadcopter near goal (%f) finding new goal... ", targetDist);
                    goalExpired = true;
                }
                else{
                    ROS_INFO("[exploration_node] goal not reached waiting");
                }
               }
               else{
                ROS_INFO("[exploration_node]something went wrong finding new goal");
                goalExpired = true;
               }

           }
        }
        ros::spinOnce();
        loop_rate.sleep();
    }
}

