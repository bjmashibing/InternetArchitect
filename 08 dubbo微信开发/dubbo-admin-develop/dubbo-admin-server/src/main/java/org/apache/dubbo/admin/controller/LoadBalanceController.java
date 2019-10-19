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

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.admin.common.exception.ParamValidationException;
import org.apache.dubbo.admin.common.exception.ResourceNotFoundException;
import org.apache.dubbo.admin.common.exception.VersionValidationException;
import org.apache.dubbo.admin.common.util.Constants;
import org.apache.dubbo.admin.model.dto.BalancingDTO;
import org.apache.dubbo.admin.service.OverrideService;
import org.apache.dubbo.admin.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/{env}/rules/balancing")
public class LoadBalanceController {

    private final OverrideService overrideService;
    private final ProviderService providerService;

    @Autowired
    public LoadBalanceController(OverrideService overrideService, ProviderService providerService) {
        this.overrideService = overrideService;
        this.providerService = providerService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public boolean createLoadbalance(@RequestBody BalancingDTO balancingDTO, @PathVariable String env) throws ParamValidationException {
        if (StringUtils.isBlank(balancingDTO.getService()) && StringUtils.isBlank(balancingDTO.getApplication())) {
            throw new ParamValidationException("Either Service or application is required.");
        }
        String application = balancingDTO.getApplication();
        if (StringUtils.isNotEmpty(application) && this.providerService.findVersionInApplication(application).equals("2.6")) {
            throw new VersionValidationException("dubbo 2.6 does not support application scope load balancing config");
        }
        overrideService.saveBalance(balancingDTO);
        return true;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public boolean updateLoadbalance(@PathVariable String id, @RequestBody BalancingDTO balancingDTO, @PathVariable String env) throws ParamValidationException {
        if (id == null) {
            throw new ParamValidationException("Unknown ID!");
        }
        id = id.replace(Constants.ANY_VALUE, Constants.PATH_SEPARATOR);
        BalancingDTO balancing = overrideService.findBalance(id);
        if (balancing == null) {
            throw new ResourceNotFoundException("Unknown ID!");
        }

        overrideService.saveBalance(balancingDTO);
        return true;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<BalancingDTO> searchLoadbalances(@RequestParam(required = false) String service,
                                                 @RequestParam(required = false) String application,
                                                 @PathVariable String env) {

        if (StringUtils.isBlank(service) && StringUtils.isBlank(application)) {
            throw new ParamValidationException("Either service or application is required");
        }
        BalancingDTO balancingDTO;
        if (StringUtils.isNotBlank(application)) {
            balancingDTO = overrideService.findBalance(application);
        } else {
            balancingDTO = overrideService.findBalance(service);
        }
        List<BalancingDTO> balancingDTOS = new ArrayList<>();
        if (balancingDTO != null) {
            balancingDTOS.add(balancingDTO);
        }
        return balancingDTOS;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public BalancingDTO detailLoadBalance(@PathVariable String id, @PathVariable String env) throws ParamValidationException {
        id = id.replace(Constants.ANY_VALUE, Constants.PATH_SEPARATOR);
        BalancingDTO balancingDTO = overrideService.findBalance(id);
        if (balancingDTO == null) {
            throw new ResourceNotFoundException("Unknown ID!");
        }
        return balancingDTO;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public boolean deleteLoadBalance(@PathVariable String id, @PathVariable String env) {
        if (id == null) {
            throw new IllegalArgumentException("Argument of id is null!");
        }
        id = id.replace(Constants.ANY_VALUE, Constants.PATH_SEPARATOR);
        overrideService.deleteBalance(id);
        return true;
    }


}
