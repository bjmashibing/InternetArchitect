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
package org.apache.dubbo.admin.common.util;

import org.apache.dubbo.admin.model.dto.BalancingDTO;
import org.apache.dubbo.admin.model.dto.DynamicConfigDTO;
import org.apache.dubbo.admin.model.dto.WeightDTO;
import org.apache.dubbo.admin.model.store.OverrideConfig;
import org.apache.dubbo.admin.model.store.OverrideDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OverrideUtils.java
 *
 */
public class OverrideUtils {
    public static OverrideConfig weightDTOtoConfig(WeightDTO weightDTO) {
        OverrideConfig overrideConfig = new OverrideConfig();
        overrideConfig.setType(Constants.WEIGHT);
        overrideConfig.setEnabled(true);
        overrideConfig.setSide(Constants.PROVIDER_SIDE);
        overrideConfig.setAddresses(weightDTO.getAddresses());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(Constants.WEIGHT, weightDTO.getWeight());
        overrideConfig.setParameters(parameters);
        return overrideConfig;
    }

    public static OverrideConfig balancingDTOtoConfig(BalancingDTO balancingDTO) {
        OverrideConfig overrideConfig = new OverrideConfig();
        overrideConfig.setType(Constants.BALANCING);
        overrideConfig.setEnabled(true);
        overrideConfig.setSide(Constants.CONSUMER_SIDE);
        Map<String, Object> parameters = new HashMap<>();
        if (balancingDTO.getMethodName().equals("*")) {
            parameters.put("loadbalance", balancingDTO.getStrategy());
        } else {
            parameters.put(balancingDTO.getMethodName() + ".loadbalance", balancingDTO.getStrategy());
        }
        overrideConfig.setParameters(parameters);
        return overrideConfig;
    }

    public static DynamicConfigDTO createFromOverride(OverrideDTO overrideDTO) {
        DynamicConfigDTO dynamicConfigDTO = new DynamicConfigDTO();
        dynamicConfigDTO.setConfigVersion(overrideDTO.getConfigVersion());
        List<OverrideConfig> configs = new ArrayList<>();
        for (OverrideConfig overrideConfig : overrideDTO.getConfigs()) {
            if (overrideConfig.getType() == null) {
                configs.add(overrideConfig);
            }
        }
        if (configs.size() == 0) {
            return null;
        }
        dynamicConfigDTO.setConfigs(configs);
        if (overrideDTO.getScope().equals(Constants.APPLICATION)) {
            dynamicConfigDTO.setApplication(overrideDTO.getKey());
        } else {
            dynamicConfigDTO.setService(overrideDTO.getKey());
        }
        dynamicConfigDTO.setEnabled(overrideDTO.isEnabled());
        return dynamicConfigDTO;
    }

    public static WeightDTO configtoWeightDTO(OverrideConfig config, String scope, String key) {
        WeightDTO weightDTO = new WeightDTO();
        if (scope.equals(Constants.APPLICATION)) {
            weightDTO.setApplication(key);
        } else {
            weightDTO.setService(key);
        }
        weightDTO.setWeight((int)config.getParameters().get(Constants.WEIGHT));
        weightDTO.setAddresses(config.getAddresses());
        return weightDTO;
    }

    public static BalancingDTO configtoBalancingDTO(OverrideConfig config, String scope, String key) {
        BalancingDTO balancingDTO = new BalancingDTO();
        if (scope.equals(Constants.APPLICATION)) {
            balancingDTO.setApplication(key);
        } else {
            balancingDTO.setService(key);
        }
        for (Map.Entry<String, Object> entry : config.getParameters().entrySet()) {
            String k = entry.getKey();
            String method;
            if (k.contains(".")) {
                method = k.split("\\.")[0];
            } else {
                method = "*";
            }
            balancingDTO.setMethodName(method);
            balancingDTO.setStrategy((String)entry.getValue());
        }
        return balancingDTO;
    }
}
