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
import org.apache.dubbo.admin.model.dto.DynamicConfigDTO;
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
@RequestMapping("/api/{env}/rules/override")
public class OverridesController {

    private final OverrideService overrideService;
    private final ProviderService providerService;

    @Autowired
    public OverridesController(OverrideService overrideService, ProviderService providerService) {
        this.overrideService = overrideService;
        this.providerService = providerService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public boolean createOverride(@RequestBody DynamicConfigDTO overrideDTO, @PathVariable String env) {
        String serviceName = overrideDTO.getService();
        String application = overrideDTO.getApplication();
        if (StringUtils.isEmpty(serviceName) && StringUtils.isEmpty(application)) {
            throw new ParamValidationException("serviceName and application are Empty!");
        }
        if (StringUtils.isNotEmpty(application) && providerService.findVersionInApplication(application).equals("2.6")) {
            throw new VersionValidationException("dubbo 2.6 does not support application scope dynamic config");
        }
        overrideService.saveOverride(overrideDTO);
        return true;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public boolean updateOverride(@PathVariable String id, @RequestBody DynamicConfigDTO overrideDTO, @PathVariable String env) {
        id = id.replace(Constants.ANY_VALUE, Constants.PATH_SEPARATOR);
        DynamicConfigDTO old = overrideService.findOverride(id);
        if (old == null) {
            throw new ResourceNotFoundException("Unknown ID!");
        }
        overrideService.updateOverride(overrideDTO);
        return true;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<DynamicConfigDTO> searchOverride(@RequestParam(required = false) String service,
                                                 @RequestParam(required = false) String application,
                                                 @PathVariable String env) {
        DynamicConfigDTO override = null;
        List<DynamicConfigDTO> result = new ArrayList<>();
        if (StringUtils.isNotBlank(service)) {
            override = overrideService.findOverride(service);
        } else if(StringUtils.isNotBlank(application)){
            override = overrideService.findOverride(application);
        } else {
            throw new ParamValidationException("Either Service or application is required.");
        }
        if (override != null) {
            result.add(override);
        }
        return result;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public DynamicConfigDTO detailOverride(@PathVariable String id, @PathVariable String env) {
        id = id.replace(Constants.ANY_VALUE, Constants.PATH_SEPARATOR);
        DynamicConfigDTO override = overrideService.findOverride(id);
        if (override == null) {
            throw new ResourceNotFoundException("Unknown ID!");
        }

        return override;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public boolean deleteOverride(@PathVariable String id, @PathVariable String env) {
        id = id.replace(Constants.ANY_VALUE, Constants.PATH_SEPARATOR);
        overrideService.deleteOverride(id);
        return true;
    }

    @RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
    public boolean enableRoute(@PathVariable String id, @PathVariable String env) {

        id = id.replace(Constants.ANY_VALUE, Constants.PATH_SEPARATOR);
        overrideService.enableOverride(id);
        return true;
    }

    @RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
    public boolean disableRoute(@PathVariable String id, @PathVariable String env) {
        id = id.replace(Constants.ANY_VALUE, Constants.PATH_SEPARATOR);

        overrideService.disableOverride(id);
        return true;
    }
}
