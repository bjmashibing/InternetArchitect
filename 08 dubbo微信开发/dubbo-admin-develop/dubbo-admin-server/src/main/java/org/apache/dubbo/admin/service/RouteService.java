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
package org.apache.dubbo.admin.service;

import org.apache.dubbo.admin.model.dto.AccessDTO;
import org.apache.dubbo.admin.model.dto.ConditionRouteDTO;
import org.apache.dubbo.admin.model.dto.TagRouteDTO;

/**
 * RouteService
 *
 */
public interface RouteService {

    void createConditionRoute(ConditionRouteDTO conditionRoute);

    void updateConditionRoute(ConditionRouteDTO newConditionRoute);

    void deleteConditionRoute(String id);

    void deleteAccess(String id);

    void createAccess(AccessDTO accessDTO);

    AccessDTO findAccess(String id);

    void updateAccess(AccessDTO accessDTO);

    void enableConditionRoute(String id);


    void disableConditionRoute(String id);


    ConditionRouteDTO findConditionRoute(String id);

    void createTagRoute(TagRouteDTO tagRoute);

    void updateTagRoute(TagRouteDTO tagRoute);

    void deleteTagRoute(String id);


    void enableTagRoute(String id);


    void disableTagRoute(String id);


    TagRouteDTO findTagRoute(String id);


}
