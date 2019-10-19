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

import org.apache.dubbo.admin.model.dto.BalancingDTO;
import org.apache.dubbo.admin.model.dto.DynamicConfigDTO;
import org.apache.dubbo.admin.model.dto.WeightDTO;

public interface OverrideService {

    void saveOverride(DynamicConfigDTO override);

    void updateOverride(DynamicConfigDTO override);

    void deleteOverride(String id);

    void enableOverride(String id);

    void disableOverride(String id);

    DynamicConfigDTO findOverride(String id);

    void saveWeight(WeightDTO weightDTO);

    void updateWeight(WeightDTO weightDTO);

    void deleteWeight(String id);

    WeightDTO findWeight(String id);

    void saveBalance(BalancingDTO balancingDTO);

    void updateBalance(BalancingDTO balancingDTO);

    void deleteBalance(String id);

    BalancingDTO findBalance(String id);

}
