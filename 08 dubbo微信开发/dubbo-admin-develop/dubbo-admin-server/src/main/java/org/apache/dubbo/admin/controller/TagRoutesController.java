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
import org.apache.dubbo.admin.model.dto.TagRouteDTO;
import org.apache.dubbo.admin.service.ProviderService;
import org.apache.dubbo.admin.service.RouteService;
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
@RequestMapping("/api/{env}/rules/route/tag")
public class TagRoutesController {


    private final RouteService routeService;
    private final ProviderService providerService;

    @Autowired
    public TagRoutesController(RouteService routeService, ProviderService providerService) {
        this.routeService = routeService;
        this.providerService = providerService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public boolean createRule(@RequestBody TagRouteDTO routeDTO, @PathVariable String env) {
        String app = routeDTO.getApplication();
        if (StringUtils.isEmpty(app)) {
            throw new ParamValidationException("app is Empty!");
        }
        if (providerService.findVersionInApplication(app).equals("2.6")) {
            throw new VersionValidationException("dubbo 2.6 does not support tag route");
        }
        routeService.createTagRoute(routeDTO);
        return true;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public boolean updateRule(@PathVariable String id, @RequestBody TagRouteDTO routeDTO, @PathVariable String env) {

        id = id.replace(Constants.ANY_VALUE, Constants.PATH_SEPARATOR);
        String app = routeDTO.getApplication();
        if (providerService.findVersionInApplication(app).equals("2.6")) {
            throw new VersionValidationException("dubbo 2.6 does not support tag route");
        }
        if (routeService.findTagRoute(id) == null) {
            throw new ResourceNotFoundException("can not find tag route, Id: " + id);
        }
        routeService.updateTagRoute(routeDTO);
        return true;

    }

    @RequestMapping(method = RequestMethod.GET)
    public List<TagRouteDTO> searchRoutes(@RequestParam String application, @PathVariable String env) {
        if (StringUtils.isBlank(application)) {
            throw new ParamValidationException("application is required.");
        }
        List<TagRouteDTO> result = new ArrayList<>();
        String version = "2.6";
        try {
            version = providerService.findVersionInApplication(application);
        } catch (ParamValidationException e) {
            //ignore
        }
        if (version.equals("2.6")) {
            return result;
        }

        TagRouteDTO tagRoute = routeService.findTagRoute(application);
        if (tagRoute != null) {
            result.add(tagRoute);
        }
        return result;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public TagRouteDTO detailRoute(@PathVariable String id, @PathVariable String env) {
        id = id.replace(Constants.ANY_VALUE, Constants.PATH_SEPARATOR);
        TagRouteDTO tagRoute = routeService.findTagRoute(id);
        if (tagRoute == null) {
            throw new ResourceNotFoundException("Unknown ID!");
        }
        return tagRoute;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public boolean deleteRoute(@PathVariable String id, @PathVariable String env) {
        id = id.replace(Constants.ANY_VALUE, Constants.PATH_SEPARATOR);
        routeService.deleteTagRoute(id);
        return true;
    }

    @RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
    public boolean enableRoute(@PathVariable String id, @PathVariable String env) {
        id = id.replace(Constants.ANY_VALUE, Constants.PATH_SEPARATOR);
        routeService.enableTagRoute(id);
        return true;
    }

    @RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
    public boolean disableRoute(@PathVariable String id, @PathVariable String env) {
        id = id.replace(Constants.ANY_VALUE, Constants.PATH_SEPARATOR);
        routeService.disableTagRoute(id);
        return true;
    }
}

