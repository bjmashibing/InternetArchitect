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
import org.apache.dubbo.admin.model.dto.WeightDTO;
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
@RequestMapping("/api/{env}/rules/weight")
public class WeightController {

    private final OverrideService overrideService;
    private final ProviderService providerService;

    @Autowired
    public WeightController(OverrideService overrideService, ProviderService providerService) {
        this.overrideService = overrideService;
        this.providerService = providerService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public boolean createWeight(@RequestBody WeightDTO weightDTO, @PathVariable String env) {
        if (StringUtils.isBlank(weightDTO.getService()) && StringUtils.isBlank(weightDTO.getApplication())) {
            throw new ParamValidationException("Either Service or application is required.");
        }
        String application = weightDTO.getApplication();
        if (StringUtils.isNotEmpty(application) && this.providerService.findVersionInApplication(application).equals("2.6")) {
            throw new VersionValidationException("dubbo 2.6 does not support application scope blackwhite list config");
        }
        overrideService.saveWeight(weightDTO);
        return true;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public boolean updateWeight(@PathVariable String id, @RequestBody WeightDTO weightDTO, @PathVariable String env) {
        if (id == null) {
            throw new ParamValidationException("Unknown ID!");
        }
        id = id.replace(Constants.ANY_VALUE, Constants.PATH_SEPARATOR);
        WeightDTO weight = overrideService.findWeight(id);
        if (weight == null) {
            throw new ResourceNotFoundException("Unknown ID!");
        }
        overrideService.updateWeight(weightDTO);
        return true;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<WeightDTO> searchWeight(@RequestParam(required = false) String service,
                                        @RequestParam(required = false) String application,
                                        @PathVariable String env) {
        if (StringUtils.isBlank(service) && StringUtils.isBlank(application)) {
            throw new ParamValidationException("Either service or application is required");
        }
        WeightDTO weightDTO;
        if (StringUtils.isNotBlank(application)) {
            weightDTO = overrideService.findWeight(application);
        } else {
            weightDTO = overrideService.findWeight(service);
        }
        List<WeightDTO> weightDTOS = new ArrayList<>();
        if (weightDTO != null) {
            weightDTOS.add(weightDTO);
        }

        return weightDTOS;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public WeightDTO detailWeight(@PathVariable String id, @PathVariable String env) {
        id = id.replace(Constants.ANY_VALUE, Constants.PATH_SEPARATOR);
        WeightDTO weightDTO = overrideService.findWeight(id);
        if (weightDTO == null) {
            throw new ResourceNotFoundException("Unknown ID!");
        }
        return weightDTO;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public boolean deleteWeight(@PathVariable String id, @PathVariable String env) {
        id = id.replace(Constants.ANY_VALUE, Constants.PATH_SEPARATOR);
        overrideService.deleteWeight(id);
        return true;
    }
}
