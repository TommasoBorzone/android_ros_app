# android_ros_pkg
Template package for an android activity whch use ros library.

# What is it?
It is a template catkin package to start your own android ros application. A custom ros message is resent to give an example of how to use it, together with a second activity which interact with the main one in the Ros App.

# How to use it
- It has been mostly done following the step at http://wiki.ros.org/android/Tutorials.
- Hence, before to clone the repo and compile (either with catkin or android studio), one need to be sure to follow the steps at http://wiki.ros.org/android/kinetic/Android%20Studio/Download to configure android studio and then the steps at http://wiki.ros.org/android/Tutorials/kinetic/Installation%20-%20ROS%20Development%20Environment to download and compile in the Home folder the android_core_components workspace.
- The build.gradle is configure such that the name of other subproject like android_ros_app_component should always start by "android_"
