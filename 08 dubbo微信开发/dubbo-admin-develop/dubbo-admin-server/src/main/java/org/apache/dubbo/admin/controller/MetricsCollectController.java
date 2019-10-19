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

package org.apache.dubbo.admin.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.dubbo.admin.common.util.Constants;
import org.apache.dubbo.admin.common.util.Tool;
import org.apache.dubbo.admin.model.domain.Consumer;
import org.apache.dubbo.admin.model.domain.Provider;
import org.apache.dubbo.admin.model.dto.MetricDTO;
import org.apache.dubbo.admin.service.ConsumerService;
import org.apache.dubbo.admin.service.ProviderService;
import org.apache.dubbo.admin.service.impl.MetrcisCollectServiceImpl;
import org.apache.dubbo.metadata.definition.model.FullServiceDefinition;
import org.apache.dubbo.metadata.identifier.MetadataIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/api/{env}/metrics")
public class MetricsCollectController {

    @Autowired
    private ProviderService providerService;

    @Autowired
    private ConsumerService consumerService;

    @RequestMapping(method = RequestMethod.POST)
    public String metricsCollect(@RequestParam String group, @PathVariable String env) {
        MetrcisCollectServiceImpl service = new MetrcisCollectServiceImpl();
        service.setUrl("dubbo://127.0.0.1:20880?scope=remote&cache=true");

        return service.invoke(group).toString();
    }

    private String getOnePortMessage(String group, String ip, String port, String protocol) {
        MetrcisCollectServiceImpl metrcisCollectService = new MetrcisCollectServiceImpl();
        metrcisCollectService.setUrl(protocol + "://" + ip + ":" + port +"?scope=remote&cache=true");
        String res = metrcisCollectService.invoke(group).toString();
        return res;
    }

    @RequestMapping( value = "/ipAddr", method = RequestMethod.GET)
    public List<MetricDTO> searchService(@RequestParam String ip, @RequestParam String group, @PathVariable String env) {

        Map<String, String> configMap = new HashMap<>();
        addMetricsConfigToMap(configMap, ip);

//         default value
        if (configMap.size() <= 0) {
            configMap.put("20880", "dubbo");
        }
        List<MetricDTO> metricDTOS = new ArrayList<>();
        for (String port : configMap.keySet()) {
            String protocol = configMap.get(port);
            String res = getOnePortMessage(group, ip, port, protocol);
            metricDTOS.addAll(new Gson().fromJson(res, new TypeToken<List<MetricDTO>>(){}.getType()));
        }

        return metricDTOS;
    }

    protected void addMetricsConfigToMap(Map<String, String> configMap, String ip) {
        List<Provider> providers = providerService.findByAddress(ip);
        if (providers.size() > 0) {
            Provider provider = providers.get(0);
            String service = provider.getService();
            MetadataIdentifier providerIdentifier = new MetadataIdentifier(Tool.getInterface(service), Tool.getVersion(service), Tool.getGroup(service),
                    Constants.PROVIDER_SIDE, provider.getApplication());
            String metaData = providerService.getProviderMetaData(providerIdentifier);
            FullServiceDefinition providerServiceDefinition = new Gson().fromJson(metaData, FullServiceDefinition.class);
            Map<String, String> parameters = providerServiceDefinition.getParameters();
            configMap.put(parameters.get(Constants.METRICS_PORT), parameters.get(Constants.METRICS_PROTOCOL));
        } else {
            List<Consumer> consumers = consumerService.findByAddress(ip);
            if (consumers.size() > 0) {
                Consumer consumer = consumers.get(0);
                String service = consumer.getService();
                MetadataIdentifier consumerIdentifier = new MetadataIdentifier(Tool.getInterface(service), Tool.getVersion(service), Tool.getGroup(service),
                        Constants.CONSUMER_SIDE, consumer.getApplication());
                String metaData = consumerService.getConsumerMetadata(consumerIdentifier);
                Map<String, String> consumerParameters = new Gson().fromJson(metaData, Map.class);
                configMap.put(consumerParameters.get(Constants.METRICS_PORT), consumerParameters.get(Constants.METRICS_PROTOCOL));
            }
        }
    }
}
