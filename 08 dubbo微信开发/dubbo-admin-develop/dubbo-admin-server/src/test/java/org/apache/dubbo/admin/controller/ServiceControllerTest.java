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

import org.apache.dubbo.admin.AbstractSpringIntegrationTest;
import org.apache.dubbo.admin.common.util.Constants;
import org.apache.dubbo.admin.model.dto.ServiceDTO;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.registry.Registry;
import org.apache.dubbo.registry.support.AbstractRegistry;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@Ignore
public class ServiceControllerTest extends AbstractSpringIntegrationTest {
  @Autowired
  private Registry registry;

  @After
  public void tearDown() throws Exception {
    final Set<URL> registered = ((AbstractRegistry) registry).getRegistered();
    for (final URL url : registered) {
      try {
        registry.unregister(url);
      } catch (Exception ignored) {
      }
    }
    TimeUnit.SECONDS.sleep(1);
  }

  @Test
  public void shouldGetAllServices() throws Exception {
    final int num = 10;
    for (int i = 0; i < num; i++) {
      final String service = "org.apache.dubbo.admin.test.service" + i;
      final URL url = i % 2 == 0
          ? generateProviderServiceUrl("dubbo-admin", service)
          : generateConsumerServiceUrl("dubbo-admin", service);
      registry.register(url);
    }
    TimeUnit.SECONDS.sleep(1);

    final ResponseEntity<Set<String>> response = restTemplate.exchange(
        url("/api/{env}/services"), HttpMethod.GET, null,
        new ParameterizedTypeReference<Set<String>>() {
        }, "whatever"
    );
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getBody(), hasSize(num));
  }

  @Test
  public void shouldGetAllApplications() throws Exception {
    final int num = 10;
    for (int i = 0; i < num; i++) {
      final String service = "org.apache.dubbo.admin.test.service";
      final URL url = generateProviderServiceUrl("dubbo-admin-" + i, service);
      registry.register(url);
    }
    TimeUnit.SECONDS.sleep(1);

    final ResponseEntity<Set<String>> responseEntity = restTemplate.exchange(
        url("/api/{env}/applications"), HttpMethod.GET, null,
        new ParameterizedTypeReference<Set<String>>() {
        }, "whatever"
    );
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    assertThat(responseEntity.getBody(), hasSize(num));
  }

  @Test
  public void shouldFilterUsingPattern() throws InterruptedException {
    final int num = 10;
    final String application = "dubbo-admin";
    for (int i = 0; i < num; i++) {
      final String service = "org.apache.dubbo.admin.test.service" + i + ".pattern" + (i % 2);
      registry.register(generateProviderServiceUrl(application, service));
    }
    TimeUnit.SECONDS.sleep(1);

    ResponseEntity<Set<ServiceDTO>> responseEntity = restTemplate.exchange(
        url("/api/{env}/service?pattern={pattern}&filter={filter}"),
        HttpMethod.GET, null, new ParameterizedTypeReference<Set<ServiceDTO>>() {
        }, "whatever", Constants.SERVICE, "*"
    );
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    assertThat(responseEntity.getBody(), hasSize(num));

    responseEntity = restTemplate.exchange(
        url("/api/{env}/service?pattern={pattern}&filter={filter}"),
        HttpMethod.GET, null, new ParameterizedTypeReference<Set<ServiceDTO>>() {
        }, "whatever", Constants.SERVICE, "*pattern0"
    );
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    assertThat(responseEntity.getBody(), hasSize(num / 2));

    responseEntity = restTemplate.exchange(
        url("/api/{env}/service?pattern={pattern}&filter={filter}"),
        HttpMethod.GET, null, new ParameterizedTypeReference<Set<ServiceDTO>>() {
        }, "whatever", Constants.SERVICE, "*pattern1"
    );
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    assertThat(responseEntity.getBody(), hasSize(num / 2));
  }

  private URL generateProviderServiceUrl(final String application, final String serviceName) {
    return URL.valueOf("dubbo://127.0.0.1:20881/" + serviceName
        + "?application=" + application
        + "&interface=" + serviceName
        + "&side=provider");
  }

  private URL generateConsumerServiceUrl(final String application, final String serviceName) {
    return URL.valueOf("dubbo://127.0.0.1:20881/" + serviceName
        + "?application=" + application
        + "&interface=" + serviceName
        + "&side=consumer");
  }
}
