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

import org.apache.dubbo.admin.common.exception.ResourceNotFoundException;
import org.apache.dubbo.admin.common.util.Constants;
import org.apache.dubbo.admin.common.util.ConvertUtil;
import org.apache.dubbo.admin.common.util.RouteUtils;
import org.apache.dubbo.admin.common.util.YamlParser;
import org.apache.dubbo.admin.model.domain.Route;
import org.apache.dubbo.admin.model.dto.AccessDTO;
import org.apache.dubbo.admin.model.dto.ConditionRouteDTO;
import org.apache.dubbo.admin.model.dto.TagRouteDTO;
import org.apache.dubbo.admin.model.store.RoutingRule;
import org.apache.dubbo.admin.model.store.TagRoute;
import org.apache.dubbo.admin.service.RouteService;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RouteServiceImpl extends AbstractService implements RouteService {

    private String prefix = Constants.CONFIG_KEY;

    @Override
    public void createConditionRoute(ConditionRouteDTO conditionRoute) {
        String id = ConvertUtil.getIdFromDTO(conditionRoute);
        String path = getPath(id, Constants.CONDITION_ROUTE);
        String existConfig = dynamicConfiguration.getConfig(path);
        RoutingRule existRule = null;
        if (existConfig != null) {
            existRule = YamlParser.loadObject(existConfig, RoutingRule.class);
        }
        existRule = RouteUtils.insertConditionRule(existRule, conditionRoute);
        //register2.7
        dynamicConfiguration.setConfig(path, YamlParser.dumpObject(existRule));

        //register2.6
        if (StringUtils.isNotEmpty(conditionRoute.getService())) {
            Route old = convertRouteToOldRoute(conditionRoute);
            registry.register(old.toUrl().addParameter(Constants.COMPATIBLE_CONFIG, true));
        }

    }

    @Override
    public void updateConditionRoute(ConditionRouteDTO newConditionRoute) {
        String id = ConvertUtil.getIdFromDTO(newConditionRoute);
        String path = getPath(id, Constants.CONDITION_ROUTE);
        String existConfig = dynamicConfiguration.getConfig(path);
        if (existConfig == null) {
            throw new ResourceNotFoundException("no existing condition route for path: " + path);
        }
        RoutingRule routingRule = YamlParser.loadObject(existConfig, RoutingRule.class);
        ConditionRouteDTO oldConditionRoute = RouteUtils.createConditionRouteFromRule(routingRule);
        routingRule = RouteUtils.insertConditionRule(routingRule, newConditionRoute);
        dynamicConfiguration.setConfig(path, YamlParser.dumpObject(routingRule));

        //for 2.6
        if (StringUtils.isNotEmpty(newConditionRoute.getService())) {
            Route old = convertRouteToOldRoute(oldConditionRoute);
            Route updated = convertRouteToOldRoute(newConditionRoute);
            registry.unregister(old.toUrl().addParameter(Constants.COMPATIBLE_CONFIG, true));
            registry.register(updated.toUrl().addParameter(Constants.COMPATIBLE_CONFIG, true));
        }
    }

    @Override
    public void deleteConditionRoute(String id) {
        if (StringUtils.isEmpty(id)) {
            // throw exception
        }
        String path = getPath(id, Constants.CONDITION_ROUTE);
        String config = dynamicConfiguration.getConfig(path);
        if (config == null) {
            //throw exception
        }
        RoutingRule route = YamlParser.loadObject(config, RoutingRule.class);
        List<String> blackWhiteList = RouteUtils.filterBlackWhiteListFromConditions(route.getConditions());
        if (blackWhiteList.size() != 0) {
           route.setConditions(blackWhiteList);
            dynamicConfiguration.setConfig(path, YamlParser.dumpObject(route));
        } else {
            dynamicConfiguration.deleteConfig(path);
        }

        //for 2.6
        if (route.getScope().equals(Constants.SERVICE)) {
            RoutingRule originRule = YamlParser.loadObject(config, RoutingRule.class);
            ConditionRouteDTO conditionRouteDTO = RouteUtils.createConditionRouteFromRule(originRule);
            Route old = convertRouteToOldRoute(conditionRouteDTO);
            registry.unregister(old.toUrl().addParameter(Constants.COMPATIBLE_CONFIG, true));
        }
    }

    @Override
    public void deleteAccess(String id) {
        String path = getPath(id, Constants.CONDITION_ROUTE);
        String config = dynamicConfiguration.getConfig(path);
        if (config != null) {
            RoutingRule ruleDTO = YamlParser.loadObject(config, RoutingRule.class);
            List<String> blackWhiteList = RouteUtils.filterBlackWhiteListFromConditions(ruleDTO.getConditions());
            List<String> conditions = RouteUtils.filterConditionRuleFromConditions(ruleDTO.getConditions());
            if (conditions.size() == 0) {
                dynamicConfiguration.deleteConfig(path);
            } else {
                ruleDTO.setConditions(conditions);
                dynamicConfiguration.setConfig(path, YamlParser.dumpObject(ruleDTO));
            }
            //2.6
            if (ruleDTO.getScope().equals(Constants.SERVICE) && blackWhiteList.size() > 0) {
                Route route = RouteUtils.convertBlackWhiteListtoRoute(blackWhiteList, Constants.SERVICE, id);
                registry.unregister(route.toUrl());
            }
        }
    }

    @Override
    public void createAccess(AccessDTO accessDTO) {
        String id = ConvertUtil.getIdFromDTO(accessDTO);
        String path = getPath(id, Constants.CONDITION_ROUTE);
        String config = dynamicConfiguration.getConfig(path);
        List<String> blackWhiteList = RouteUtils.convertToBlackWhiteList(accessDTO);
        RoutingRule ruleDTO;
        if (config == null) {
            ruleDTO = new RoutingRule();
            ruleDTO.setEnabled(true);
            if (StringUtils.isNoneEmpty(accessDTO.getApplication())) {
                ruleDTO.setScope(Constants.APPLICATION);
            } else {
                ruleDTO.setScope(Constants.SERVICE);
            }
            ruleDTO.setKey(id);
            ruleDTO.setConditions(blackWhiteList);
        } else {
            ruleDTO = YamlParser.loadObject(config, RoutingRule.class);
            if (ruleDTO.getConditions() == null) {
                ruleDTO.setConditions(blackWhiteList);
            } else {
                ruleDTO.getConditions().addAll(blackWhiteList);
            }
        }
        dynamicConfiguration.setConfig(path, YamlParser.dumpObject(ruleDTO));

        //for 2.6
        if (ruleDTO.getScope().equals("service")) {
            Route route = RouteUtils.convertAccessDTOtoRoute(accessDTO);
            registry.register(route.toUrl());
        }

    }

    @Override
    public AccessDTO findAccess(String id) {
        String path = getPath(id, Constants.CONDITION_ROUTE);
        String config = dynamicConfiguration.getConfig(path);
        if (config != null) {
            RoutingRule ruleDTO = YamlParser.loadObject(config, RoutingRule.class);
            List<String> blackWhiteList = RouteUtils.filterBlackWhiteListFromConditions(ruleDTO.getConditions());
            return RouteUtils.convertToAccessDTO(blackWhiteList, ruleDTO.getScope(), ruleDTO.getKey());
        }
        return null;
    }

    @Override
    public void updateAccess(AccessDTO accessDTO) {
        String key = ConvertUtil.getIdFromDTO(accessDTO);
        String path = getPath(key, Constants.CONDITION_ROUTE);
        List<String> blackWhiteList = RouteUtils.convertToBlackWhiteList(accessDTO);
        String config = dynamicConfiguration.getConfig(path);
        List<String> oldList = null;
        if (config != null) {
            RoutingRule ruleDTO = YamlParser.loadObject(config, RoutingRule.class);
            oldList = RouteUtils.filterBlackWhiteListFromConditions(ruleDTO.getConditions());
            List<String> conditions = RouteUtils.filterConditionRuleFromConditions(ruleDTO.getConditions());
            conditions.addAll(blackWhiteList);
            ruleDTO.setConditions(conditions);
            dynamicConfiguration.setConfig(path, YamlParser.dumpObject(ruleDTO));
        }

        //2.6
        if (StringUtils.isNotEmpty(accessDTO.getService())) {
            Route oldRoute = RouteUtils.convertBlackWhiteListtoRoute(oldList, Constants.SERVICE, key);
            Route newRoute = RouteUtils.convertAccessDTOtoRoute(accessDTO);
            registry.unregister(oldRoute.toUrl());
            registry.register(newRoute.toUrl());
        }
    }

    @Override
    public void enableConditionRoute(String id) {
        String path = getPath(id, Constants.CONDITION_ROUTE);
        String config = dynamicConfiguration.getConfig(path);
        if (config != null) {
            RoutingRule ruleDTO = YamlParser.loadObject(config, RoutingRule.class);

            if (ruleDTO.getScope().equals(Constants.SERVICE)) {
                //for2.6
                URL oldURL = convertRouteToOldRoute(RouteUtils.createConditionRouteFromRule(ruleDTO)).toUrl().addParameter(Constants.COMPATIBLE_CONFIG, true);
                registry.unregister(oldURL);
                oldURL = oldURL.addParameter("enabled", true);
                registry.register(oldURL);
            }

            //2.7
            ruleDTO.setEnabled(true);
            dynamicConfiguration.setConfig(path, YamlParser.dumpObject(ruleDTO));
        }

    }

    @Override
    public void disableConditionRoute(String serviceName) {
        String path = getPath(serviceName, Constants.CONDITION_ROUTE);
        String config = dynamicConfiguration.getConfig(path);
        if (config != null) {
            RoutingRule routeRule = YamlParser.loadObject(config, RoutingRule.class);

            if (routeRule.getScope().equals(Constants.SERVICE)) {
                //for 2.6
                URL oldURL = convertRouteToOldRoute(RouteUtils.createConditionRouteFromRule(routeRule)).toUrl().addParameter(Constants.COMPATIBLE_CONFIG,true);
                registry.unregister(oldURL);
                oldURL = oldURL.addParameter("enabled", false);
                registry.register(oldURL);
            }

            //2.7
            routeRule.setEnabled(false);
            dynamicConfiguration.setConfig(path, YamlParser.dumpObject(routeRule));
        }

    }

    @Override
    public ConditionRouteDTO findConditionRoute(String id) {
        String path = getPath(id, Constants.CONDITION_ROUTE);
        String config = dynamicConfiguration.getConfig(path);
        if (config != null) {
            RoutingRule routingRule = YamlParser.loadObject(config, RoutingRule.class);
            ConditionRouteDTO conditionRouteDTO = RouteUtils.createConditionRouteFromRule(routingRule);
            String service = conditionRouteDTO.getService();
            if (org.apache.commons.lang3.StringUtils.isNotBlank(service)) {
                conditionRouteDTO.setService(service.replace("*", "/"));
            }
            return conditionRouteDTO;
        }
        return null;
    }

    @Override
    public void createTagRoute(TagRouteDTO tagRoute) {
        String id = ConvertUtil.getIdFromDTO(tagRoute);
        String path = getPath(id,Constants.TAG_ROUTE);
        TagRoute store = RouteUtils.convertTagroutetoStore(tagRoute);
        dynamicConfiguration.setConfig(path, YamlParser.dumpObject(store));
    }

    @Override
    public void updateTagRoute(TagRouteDTO tagRoute) {
        String id = ConvertUtil.getIdFromDTO(tagRoute);
        String path = getPath(id, Constants.TAG_ROUTE);
        if (dynamicConfiguration.getConfig(path) == null) {
            throw new ResourceNotFoundException("can not find tagroute: " + id);
            //throw exception
        }
        TagRoute store = RouteUtils.convertTagroutetoStore(tagRoute);
        dynamicConfiguration.setConfig(path, YamlParser.dumpObject(store));

    }

    @Override
    public void deleteTagRoute(String id) {
        String path = getPath(id, Constants.TAG_ROUTE);
        dynamicConfiguration.deleteConfig(path);
    }

    @Override
    public void enableTagRoute(String id) {
        String path = getPath(id, Constants.TAG_ROUTE);
        String config = dynamicConfiguration.getConfig(path);
        if (config != null) {
            TagRoute tagRoute = YamlParser.loadObject(config, TagRoute.class);
            tagRoute.setEnabled(true);
            dynamicConfiguration.setConfig(path, YamlParser.dumpObject(tagRoute));
        }

    }

    @Override
    public void disableTagRoute(String id) {
        String path = getPath(id, Constants.TAG_ROUTE);
        String config = dynamicConfiguration.getConfig(path);
        if (config != null) {
            TagRoute tagRoute = YamlParser.loadObject(config, TagRoute.class);
            tagRoute.setEnabled(false);
            dynamicConfiguration.setConfig(path, YamlParser.dumpObject(tagRoute));
        }

    }

    @Override
    public TagRouteDTO findTagRoute(String id) {
        String path = getPath(id, Constants.TAG_ROUTE);
        String config = dynamicConfiguration.getConfig(path);
        if (config != null) {
            TagRoute tagRoute = YamlParser.loadObject(config, TagRoute.class);
            return RouteUtils.convertTagroutetoDisplay(tagRoute);
        }
        return null;
    }

    private String getPath(String key, String type) {
        key = key.replace("/", "*");
        if (type.equals(Constants.CONDITION_ROUTE)) {
            return prefix + Constants.PATH_SEPARATOR + key + Constants.PATH_SEPARATOR + "condition-router";
        } else {
            return prefix + Constants.PATH_SEPARATOR + key + Constants.PATH_SEPARATOR + "tag-router";
        }
    }

    private String parseCondition(List<String> conditions) {
        StringBuilder when = new StringBuilder();
        StringBuilder then = new StringBuilder();
        for (String condition : conditions) {
            condition = condition.trim();
            if (condition.contains("=>")) {
                String[] array = condition.split("=>", 2);
                String consumer = array[0].trim();
                String provider = array[1].trim();
                if (consumer.length() != 0) {
                    if (when.length() != 0) {
                        when.append(" & ").append(consumer);
                    } else {
                        when.append(consumer);
                    }
                }
                if (provider.length() != 0) {
                    if (then.length() != 0) {
                        then.append(" & ").append(provider);
                    } else {
                        then.append(provider);
                    }
                }
            }
        }
        return (when.append(" => ").append(then)).toString();
    }

    private Route convertRouteToOldRoute(ConditionRouteDTO route) {
        Route old = new Route();
        old.setService(route.getService());
        old.setEnabled(route.isEnabled());
        old.setForce(route.isForce());
        old.setRuntime(route.isRuntime());
        old.setPriority(route.getPriority());
        String rule = parseCondition(route.getConditions());
        old.setRule(rule);
        return old;
    }
}
