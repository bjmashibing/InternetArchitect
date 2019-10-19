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

package org.apache.dubbo.admin.registry.config.impl;

import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
import org.apache.dubbo.admin.registry.config.GovernanceConfiguration;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.SPI;
import org.springframework.beans.factory.annotation.Value;

@SPI("apollo")
public class ApolloConfiguration implements GovernanceConfiguration {

    @Value("${admin.apollo.token}")
    private String token;

    @Value("${admin.apollo.cluster}")
    private String cluster;

    @Value("${admin.apollo.namespace}")
    private String namespace;

    @Value("${admin.apollo.env}")
    private String env;

    @Value("${admin.apollo.appId}")
    private String appId;

    private URL url;
    private ApolloOpenApiClient client;


    @Override
    public void setUrl(URL url) {
       this.url = url;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public void init() {
        client = ApolloOpenApiClient.newBuilder().withPortalUrl(url.getAddress()).withToken(token).build();
    }

    @Override
    public String setConfig(String key, String value) {
        return setConfig(null, key, value);
    }

    @Override
    public String getConfig(String key) {
        return getConfig(null, key);
    }

    @Override
    public boolean deleteConfig(String key) {
        return deleteConfig(null, key);
    }

    @Override
    public String setConfig(String group, String key, String value) {
        if (group == null) {
            group = namespace;
        }
        OpenItemDTO openItemDTO = new OpenItemDTO();
        openItemDTO.setKey(key);
        openItemDTO.setValue(value);
        client.createItem(appId, env, cluster, group, openItemDTO);
        return value;
    }

    @Override
    public String getConfig(String group, String key) {
        if (group == null) {
            group = namespace;
        }
        OpenItemDTO openItemDTO =  client.getItem(appId, env, cluster, group, key);
        if (openItemDTO != null) {
            return openItemDTO.getValue();
        }
        return null;
    }

    @Override
    public boolean deleteConfig(String group, String key) {
        if (group == null) {
            group = namespace;
        }
        //TODO user login user name as the operator
        client.removeItem(appId, env, cluster, group, key, "admin");
        return true;
    }

    @Override
    public String getPath(String key) {
        return null;
    }

    @Override
    public String getPath(String group, String key) {
        return null;
    }
}
