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

import org.apache.dubbo.admin.model.domain.Provider;
import org.apache.dubbo.admin.model.dto.ServiceDTO;
import org.apache.dubbo.metadata.identifier.MetadataIdentifier;

import java.util.List;
import java.util.Set;

/**
 * ProviderService
 *
 */
public interface ProviderService {

    void create(Provider provider);

//    void enableProvider(String id);

//    void disableProvider(String id);

//    void doublingProvider(String id);

//    void halvingProvider(String id);

    void deleteStaticProvider(String id);

    void updateProvider(Provider provider);

    Provider findProvider(String id);

    String getProviderMetaData(MetadataIdentifier providerIdentifier);

    /**
     * Get all provider's service name
     *
     * @return list of all provider's service name
     */
    Set<String> findServices();

    String findServiceVersion(String serviceName, String application);

    String findVersionInApplication(String application);

    List<String> findAddresses();

    List<String> findAddressesByApplication(String application);

    List<String> findAddressesByService(String serviceName);

    List<String> findApplicationsByServiceName(String serviceName);

    /**
     * Get provider list with specific service name.
     *
     * @param serviceName specific service name, cannot be fuzzy string
     * @return list of provider object
     */
    List<Provider> findByService(String serviceName);

    List<Provider> findByAppandService(String app, String serviceName);

    List<Provider> findAll();

    /**
     * Get provider list with specific ip address.
     *
     * @param providerAddress provider's ip address
     * @return list of provider object
     */
    List<Provider> findByAddress(String providerAddress);

    List<String> findServicesByAddress(String providerAddress);

    Set<String> findApplications();

    /**
     * Get provider list with specific application name.
     *
     * @param application specific application name
     * @return list of provider object
     */
    List<Provider> findByApplication(String application);

    List<String> findServicesByApplication(String application);

    List<String> findMethodsByService(String serviceName);

    Provider findByServiceAndAddress(String service, String address);

    /**
     * Get a set of service data object.
     *
     * ServiceDTO object contains base information include
     * service name , application, group and version.
     *
     * @param pattern {@code String} type of search
     * @param filter  {@code String} input filter string
     * @param env     {@code String}the environment of front end
     * @return a set of services for fore-end page
     */
    Set<ServiceDTO> getServiceDTOS(String pattern, String filter, String env);

}
