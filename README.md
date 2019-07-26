# android_ros_pkg
Template package for an android activity whch use ros library.

# What is it?
It is a template catkin package to start your own android ros application. A custom ros message is resent to give an example of how to use it, together with a second activity which interact with the main one in the Ros App.

# How to use it
- It has been mostly done following the step at http://wiki.ros.org/android/Tutorials.
- Hence, before to clone the repo and compile (either with catkin or android studio), one need to be sure to follow the steps at http://wiki.ros.org/android/kinetic/Android%20Studio/Download to configure android studio and then the steps at http://wiki.ros.org/android/Tutorials/kinetic/Installation%20-%20ROS%20Development%20Environment to download and compile in the Home folder the android_core_components workspace.
- The build.gradle is configured such that the name of other subproject like android_ros_app_component should always start by "android_"
- Always check the build.gradle file to be sure that you implement all the package used in the app (e.g. ROS messages and services, libraries, other rosjava packages).

# Known Issue
The workspace has to be built several times because the genjava for the ros messages does not work the first time.

# Tip
Be sure that the following environment variable are updated in .bashrc file

    export PATH=${PATH}:/home/tommaso/Android/Sdk/tools:/home/tommaso/Android/Sdk/platform-tools:/home/tommaso/Documents/android-studio/bin:/home/tommaso/catkin_ws/devel/share/maven
    export ANDROID_HOME=/home/tommaso/Android/Sdk
    source ~/rosjava/devel/setup.bash
    export ROS_MAVEN_PATH=${ROS_MAVEN_PATH}:/home/tommaso/catkin_ws/devel/share/maven
    export CMAKE_PREFIX_PATH=/home/tommaso/rosjava/devel
    source ~/catkin_ws/devel/setup.bash


