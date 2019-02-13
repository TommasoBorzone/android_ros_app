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

import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

import std_srvs.SetBoolRequest;
import std_srvs.SetBoolResponse;
import std_srvs.SetBool;


/**
 * @author tom.borzone@google.com (Tommaso Borzone)
 */

public class Client extends AbstractNodeMain {

  private String service_name;

  private java.util.List<multiagent_msgs.Displacement> form_positions;
  private int number_of_agents;

  public Client(String service)
  {
    service_name  = service;
  }
	
  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("android_ros_app/client");
  }

  @Override
  public void onStart(final ConnectedNode connectedNode) {
    ServiceClient<SetBoolRequest, SetBoolResponse> serviceClient;
    try {
      serviceClient = connectedNode.newServiceClient(service_name, SetBool._TYPE);
    } catch (ServiceNotFoundException e) {
      throw new RosRuntimeException(e);
    }
    final SetBoolRequest request = serviceClient.newMessage();
    //set the request/number_of_agents
	request.setData(true);
	  
    serviceClient.call(request, new ServiceResponseListener<SetBoolResponse>() {
      @Override
      public void onSuccess(SetBoolResponse response) {
        connectedNode.getLog().info(
                String.format("The response is : "));

      }

      @Override
      public void onFailure(RemoteException e) {
        throw new RosRuntimeException(e);
      }
    });
  }
}
