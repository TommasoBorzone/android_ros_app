##############################################################################
# CMake
##############################################################################

cmake_minimum_required(VERSION 2.8.3)
project(android_ros_app)

##############################################################################
# Catkin
##############################################################################

find_package(catkin REQUIRED 
	rosjava_build_tools
  	std_msgs
	std_srvs
	multiagent_msgs
  	android_app_msgs
)
# Set the gradle targets you want catkin's make to run by default
# e.g. usually catkin_android_setup(assembleRelease uploadArchives)
catkin_android_setup(assembleRelease uploadArchives)
catkin_package()


##############################################################################
# Installation
##############################################################################

install(DIRECTORY ${CATKIN_DEVEL_PREFIX}/${CATKIN_GLOBAL_MAVEN_DESTINATION}/org/ros/android/android_ros_app/ 
        DESTINATION ${CATKIN_GLOBAL_MAVEN_DESTINATION}/org/ros/android/android_ros_app/)
