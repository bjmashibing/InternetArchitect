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

package org.apache.dubbo.admin.service.impl;

import org.apache.dubbo.admin.common.util.Constants;
import org.apache.dubbo.admin.model.dto.ConfigDTO;
import org.apache.dubbo.admin.service.ManagementService;
import org.springframework.stereotype.Component;

import static org.apache.dubbo.admin.common.util.Constants.GLOBAL_CONFIG_PATH;

@Component
public class ManagementServiceImpl extends AbstractService implements ManagementService {
    @Override
    public void setConfig(ConfigDTO config) {
        if (Constants.GLOBAL_CONFIG.equals(config.getKey())) {
            dynamicConfiguration.setConfig(GLOBAL_CONFIG_PATH, config.getConfig());
        } else {
            dynamicConfiguration.setConfig(getPath(config.getKey()), config.getConfig());
        }
    }

    @Override
    public String getConfig(String key) {
        if (Constants.GLOBAL_CONFIG.equals(key)) {
            return dynamicConfiguration.getConfig(GLOBAL_CONFIG_PATH);
        }
        return dynamicConfiguration.getConfig(getPath(key));
    }

    @Override
    public String getConfigPath(String key) {
        if (Constants.GLOBAL_CONFIG.equals(key)) {
            return dynamicConfiguration.getPath(GLOBAL_CONFIG_PATH);
        }
        return dynamicConfiguration.getPath(getPath(key));
    }

    @Override
    public boolean updateConfig(ConfigDTO configDTO) {
        String key = configDTO.getKey();
        if (Constants.GLOBAL_CONFIG.equals(key)) {
            dynamicConfiguration.setConfig(GLOBAL_CONFIG_PATH, configDTO.getConfig());
        } else {
            dynamicConfiguration.setConfig(getPath(key), configDTO.getConfig());
        }
        return true;
    }

    @Override
    public boolean deleteConfig(String key) {
        if (Constants.GLOBAL_CONFIG.equals(key)) {
            dynamicConfiguration.deleteConfig(GLOBAL_CONFIG_PATH);
        } else {
            dynamicConfiguration.deleteConfig(getPath(key));
        }
        return true;
    }

    private String getPath(String key) {
        return Constants.CONFIG_KEY + Constants.PATH_SEPARATOR + key + Constants.PATH_SEPARATOR
                + Constants.DUBBO_PROPERTY;
    }

}
