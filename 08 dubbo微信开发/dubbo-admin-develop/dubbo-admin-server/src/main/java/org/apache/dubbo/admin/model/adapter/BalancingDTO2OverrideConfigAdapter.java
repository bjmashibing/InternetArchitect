/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.admin.model.adapter;

import org.apache.dubbo.admin.common.util.Constants;
import org.apache.dubbo.admin.model.dto.BalancingDTO;
import org.apache.dubbo.admin.model.store.OverrideConfig;

import java.util.HashMap;
import java.util.Map;

public class BalancingDTO2OverrideConfigAdapter extends OverrideConfig {
  public BalancingDTO2OverrideConfigAdapter(BalancingDTO balancingDTO) {
    setType(Constants.BALANCING);
    setEnabled(true);
    setSide(Constants.CONSUMER_SIDE);
    Map<String, Object> parameters = new HashMap<>();
    if (balancingDTO.getMethodName().equals("*")) {
      parameters.put("loadbalance", balancingDTO.getStrategy());
    } else {
      parameters.put(balancingDTO.getMethodName() + ".loadbalance", balancingDTO.getStrategy());
    }
    setParameters(parameters);
  }
}
