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

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import geometry_msgs.Pose;

/**
 * @author tom.borzone@google.com (Tommaso Borzone)
 */
public class MainActivity extends RosActivity {

  /**
   * This EXTRA_INPUT is generated as an example of a custom string to pass to the child activity as an intent,
   * and then to the view as an attribute
   */
  public static final String EXTRA_INPUT = "org.ros.android.android_ros_app_component.extra.INPUT";
  /**
   * This EXTRA_NUMBER_OF_POINTS is an other EXTRA which collects the points generated in the ChildActivity
   */
  public static final String EXTRA_NUMBER_OF_POINTS = "org.ros.android.android_ros_app_component.extra.NUMBER_OF_POINTS";
  public static final int ACTIVITY_REQUEST = 1;

  private Client client;
  private Talker talker;
  private EditText mNumOfPoints;
  private int number_of_points;
  private int windowsSize;
  private ArrayList<geometry_msgs.Pose> poses = new ArrayList<>();
  private ArrayList<Point> Points;
  private AlertDialog.Builder mADBuilder;
  private Resources res;

  /**
   * The new On Activity Result Callback implement the callback function used by the
   * RosActivity.onActivityResult methods, If you add Intent or interaction you should rather
   * modify this callback rather then override onActivityResult
   */
  private OnActivityResultCallback myActivityResultCallback = new OnActivityResultCallback() {
    @Override
    public void execute(int requestCode, int resultCode, Intent data) {
      if (requestCode == ACTIVITY_REQUEST) {
        if (resultCode == RESULT_OK) {
          Bundle extras = data.getExtras();
          Points = extras.getParcelableArrayList(ChildActivity.EXTRA_POINTS_REPLY);
          windowsSize = extras.getInt(ChildActivity.EXTRA_WINDOW_SIZE_REPLY);
          try {
            new AsyncTask<Void, Void, Void>() {
              @Override
              protected Void doInBackground(Void... params) {
                MainActivity.this.init(MainActivity.this.nodeMainExecutorService);
                return null;
              }
            }.execute();
          } catch (Exception e) {
            AlertDialog ad = mADBuilder.create();
            ad.setTitle("Something got wrong.");
            ad.setMessage(e.getMessage());
            ad.show();
            return;
          }
        }
      }
      if (requestCode == MASTER_CHOOSER_REQUEST_CODE) {
        if (resultCode == RESULT_OK) {
          String host;
          String networkInterfaceName = data.getStringExtra("ROS_MASTER_NETWORK_INTERFACE");
          // Handles the default selection and prevents possible errors
          if (networkInterfaceName == null || networkInterfaceName.equals("")) {
            host = InetAddressFactory.newNonLoopback().getHostAddress();
          } else {
            try {
              NetworkInterface networkInterface = NetworkInterface.getByName(networkInterfaceName);
              host = InetAddressFactory.newNonLoopbackForNetworkInterface(networkInterface).getHostAddress();
            } catch (SocketException e) {
              throw new RosRuntimeException(e);
            }
          }
          MainActivity.this.nodeMainExecutorService.setRosHostname(host);
          if (data.getBooleanExtra("ROS_MASTER_CREATE_NEW", false)) {
            MainActivity.this.nodeMainExecutorService.startMaster(data.getBooleanExtra("ROS_MASTER_PRIVATE", true));
          } else {
            URI uri;
            try {
              uri = new URI(data.getStringExtra("ROS_MASTER_URI"));
            } catch (URISyntaxException e) {
              throw new RosRuntimeException(e);
            }
            MainActivity.this.nodeMainExecutorService.setMasterUri(uri);
          }
        } else {
          // Without a master URI configured, we are in an unusable state.
          //6MainActivity.this.nodeMainExecutorService.forceShutdown();
        }
      }
    }
  };

  public MainActivity() {
    // The RosActivity constructor configures the notification title and ticker
    // messages.
    super("FFLOR Control Formation", "FFLOR Control Formation");
    MainActivity.this.setOnActivityResultCallback(myActivityResultCallback);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity_layout);
    res = getResources();

    mNumOfPoints = findViewById(R.id.edit_num_of_points);
    mADBuilder = new AlertDialog.Builder(this);
  }

  public void launchChildActivity(View view) {
    Intent intent = new Intent(this, ChildActivity.class);
    String input = "MyCustomInputStringIfNeeded";
    number_of_points = getNumberOfPoints(mNumOfPoints.getText().toString(), mADBuilder);
    if (number_of_points == -1) {return;}
    intent.putExtra(EXTRA_INPUT, input);
    intent.putExtra(EXTRA_NUMBER_OF_POINTS, number_of_points);
    startActivityForResult(intent, ACTIVITY_REQUEST);
  }

  private int getNumberOfPoints(String numOfPoints, AlertDialog.Builder adb)
  {
    int number_of_points;
    try {
      number_of_points = Integer.parseInt(numOfPoints);
    }
    catch (NumberFormatException e) {
      AlertDialog ad = adb.create();
      ad.setMessage("The entered number of robots has incorrect format.");
      ad.show();
      return -1;
    }
    if (number_of_points < 3) {
      AlertDialog ad = adb.create();
      ad.setMessage("The entered number of robots must be greater then 2.");
      ad.show();
      return -1;
    }
    return number_of_points;
  }

  /**
   * In this example Init, which is executed once the child activity return OK, we implement a
   * simple example exercise where we populate a Pose array with Quaternions and Point where Pose,
   * Quaternions and Pint are ROS message objects. Then we execute two nodes, a simple service client
   * and a data publisher
   */
  @Override
  protected void init(NodeMainExecutor nodeMainExecutor) {
	NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(getRosHostname());

	String mapSize = res.getString(R.string.meters);
    double scaleFactor;
    try {
      scaleFactor = (Double.parseDouble(mapSize))/windowsSize;
    }
    catch (NumberFormatException e) {
      AlertDialog ad = mADBuilder.create();
      ad.setMessage("The real size of the map format is not correct.");
      ad.show();
      return;
    }
    int counter = 0;

	for (Point point : Points) {
      geometry_msgs.Pose pose = nodeConfiguration.getTopicMessageFactory().newFromType(Pose._TYPE);
      geometry_msgs.Point ros_point = nodeConfiguration.getTopicMessageFactory().newFromType(geometry_msgs.Point._TYPE);
      geometry_msgs.Quaternion quat = nodeConfiguration.getTopicMessageFactory().newFromType(geometry_msgs.Quaternion._TYPE);
      quat.setW(1.0);
      quat.setX(0.0);
      quat.setY(0.0);
      quat.setZ(0.0);
      ros_point.setX(point.x * scaleFactor);
      ros_point.setY(point.y * scaleFactor);
      ros_point.setZ(0.0);
      pose.setPosition(ros_point);
      pose.setOrientation(quat);
      poses.add(pose);
      counter ++;
    }

	talker = new Talker(res.getString(R.string.publish_topic_id), poses, res.getString(R.string.frame_id));

	nodeConfiguration.setMasterUri(getMasterUri());
	nodeMainExecutor.execute(talker, nodeConfiguration);

    try {
      client = new Client(res.getString(R.string.service_client_id));
        nodeMainExecutor.execute(client, nodeConfiguration);
    } catch (RosRuntimeException e) {
        AlertDialog ad = mADBuilder.create();
        ad.setMessage("You are trying to start the client node but the service is down.");
        ad.show();
        return;
    }
  }
}
