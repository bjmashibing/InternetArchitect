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

package org.apache.dubbo.admin.model.dto;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class ServiceDTO implements Comparable<ServiceDTO>{
    private String service;
    private String appName;
    private String group;
    private String version;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public int compareTo(ServiceDTO o) {
        int result = StringUtils.trimToEmpty(appName).compareTo(StringUtils.trimToEmpty(o.getAppName()));
        if (result == 0) {
            result = StringUtils.trimToEmpty(service).compareTo(StringUtils.trimToEmpty(o.getService()));
            if (result == 0) {
                result = StringUtils.trimToEmpty(group).compareTo(StringUtils.trimToEmpty(o.getGroup()));
            }
            if (result == 0) {
                result = StringUtils.trimToEmpty(version).compareTo(StringUtils.trimToEmpty(o.getVersion()));
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServiceDTO that = (ServiceDTO) o;
        return Objects.equals(service, that.service) && Objects.equals(appName, that.appName) && Objects
            .equals(group, that.group) && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(service, appName, group, version);
    }
}
