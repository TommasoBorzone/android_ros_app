/*
 * Copyright (C) 2019 Tommaso Borzone.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ros.android.android_ros_app_component;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import java.util.ArrayList;
import android_app_msgs.AndroidApp;

import geometry_msgs.Pose;

/**
 * A simple Publisher Node.
 * 
 * @author tom.borzone@google.com (Tommaso Borzone)
 */
public class Talker extends AbstractNodeMain {
  private String topic_name;

  private java.util.List<Pose> message_poses;
  private String message_frame_id;

  public Talker() {
    topic_name = "chatter";
  }

  public Talker(String topic, ArrayList<Pose> poses, String frame_id)
  {
    topic_name       = topic;
    message_poses    = poses;
    message_frame_id = frame_id;
  }

  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("android_ros_app/talker");
  }

  @Override
  public void onStart(final ConnectedNode connectedNode) {
    final Publisher<android_app_msgs.AndroidApp> publisher =
        connectedNode.newPublisher(topic_name, android_app_msgs.AndroidApp._TYPE);
    // This CancellableLoop will be canceled automatically when the node shuts
    // down.
    connectedNode.executeCancellableLoop(new CancellableLoop() {
      private int sequenceNumber;

      @Override
      protected void setup() {
        sequenceNumber = 0;
      }

      @Override
      protected void loop() throws InterruptedException {
        android_app_msgs.AndroidApp msg = publisher.newMessage();
        msg.setSomeFloat(0.0);
        msg.setSomePoses(message_poses);
        msg.setSomeString(message_frame_id);
        publisher.publish(msg);
        sequenceNumber++;
        Thread.sleep(1000);
      }
    });
  }
}
