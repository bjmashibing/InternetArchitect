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
import org.apache.dubbo.admin.model.dto.AccessDTO;
import org.apache.dubbo.admin.model.dto.ConditionRouteDTO;
import org.apache.dubbo.admin.service.ProviderService;
import org.apache.dubbo.admin.service.RouteService;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
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
import java.util.Objects;

@RestController
@RequestMapping("/api/{env}/rules/access")
public class AccessesController {
    private static final Logger logger = LoggerFactory.getLogger(AccessesController.class);

    private final RouteService routeService;
    private final ProviderService providerService;

    @Autowired
    public AccessesController(RouteService routeService, ProviderService providerService) {
        this.routeService = routeService;
        this.providerService = providerService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<AccessDTO> searchAccess(@RequestParam(required = false) String service,
                                        @RequestParam(required = false) String application,
                                        @PathVariable String env) {
        if (StringUtils.isBlank(service) && StringUtils.isBlank(application)) {
            throw new ParamValidationException("Either service or application is required");
        }
        List<AccessDTO> accessDTOS = new ArrayList<>();
        AccessDTO accessDTO;
        if (StringUtils.isNotBlank(application)) {
            accessDTO = routeService.findAccess(application);
        } else {
            accessDTO = routeService.findAccess(service);
        }
        if (accessDTO != null) {
            accessDTO.setEnabled(true);
            accessDTOS.add(accessDTO);
        }
        return accessDTOS;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public AccessDTO detailAccess(@PathVariable String id, @PathVariable String env) {
        id = id.replace(Constants.ANY_VALUE, Constants.PATH_SEPARATOR);
        AccessDTO accessDTO = routeService.findAccess(id);
        return accessDTO;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteAccess(@PathVariable String id, @PathVariable String env) {
        id = id.replace(Constants.ANY_VALUE, Constants.PATH_SEPARATOR);
        routeService.deleteAccess(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void createAccess(@RequestBody AccessDTO accessDTO, @PathVariable String env) {
        if (StringUtils.isBlank(accessDTO.getService()) && StringUtils.isBlank(accessDTO.getApplication())) {
            throw new ParamValidationException("Either Service or application is required.");
        }
        String application = accessDTO.getApplication();
        if (StringUtils.isNotEmpty(application) && "2.6".equals(providerService.findVersionInApplication(application))) {
            throw new VersionValidationException("dubbo 2.6 does not support application scope blackwhite list config");
        }
        if (accessDTO.getBlacklist() == null && accessDTO.getWhitelist() == null) {
            throw new ParamValidationException("One of Blacklist/Whitelist is required.");
        }
        routeService.createAccess(accessDTO);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public void updateAccess(@PathVariable String id, @RequestBody AccessDTO accessDTO, @PathVariable String env) {

        id = id.replace(Constants.ANY_VALUE, Constants.PATH_SEPARATOR);
        ConditionRouteDTO route = routeService.findConditionRoute(id);
        if (Objects.isNull(route)) {
            throw new ResourceNotFoundException("Unknown ID!");
        }
        routeService.updateAccess(accessDTO);
    }
}
