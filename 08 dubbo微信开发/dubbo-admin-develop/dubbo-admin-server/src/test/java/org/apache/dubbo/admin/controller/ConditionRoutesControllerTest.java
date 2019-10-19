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
import org.apache.dubbo.admin.common.util.YamlParser;
import org.apache.dubbo.admin.model.dto.ConditionRouteDTO;
import org.apache.dubbo.admin.model.store.RoutingRule;
import org.apache.dubbo.admin.service.ProviderService;
import org.junit.After;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ConditionRoutesControllerTest extends AbstractSpringIntegrationTest {
  private final String env = "whatever";

  @MockBean
  private ProviderService providerService;

  @After
  public void tearDown() throws Exception {
    if (zkClient.checkExists().forPath("/dubbo") != null) {
      zkClient.delete().deletingChildrenIfNeeded().forPath("/dubbo");
    }
  }

  @Test
  public void shouldThrowWhenParamInvalid() {
    String uuid = UUID.randomUUID().toString();

    ConditionRouteDTO dto = new ConditionRouteDTO();
    ResponseEntity<String> responseEntity = restTemplate.postForEntity(
        url("/api/{env}/rules/route/condition"), dto, String.class, env
    );
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(responseEntity.getBody(), containsString("serviceName and app is Empty!"));

    dto.setApplication("application" + uuid);
    when(providerService.findVersionInApplication(dto.getApplication())).thenReturn("2.6");
    responseEntity = restTemplate.postForEntity(
        url("/api/{env}/rules/route/condition"), dto, String.class, env
    );
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    assertThat(responseEntity.getBody(), containsString("dubbo 2.6 does not support application scope routing rule"));
  }

  @Test
  public void shouldCreateRule() {
    String uuid = UUID.randomUUID().toString();
    String application = "application" + uuid;
    String service = "service" + uuid;
    List<String> conditions = Collections.singletonList("=> host != 172.22.3.91");

    ConditionRouteDTO dto = new ConditionRouteDTO();
    dto.setService(service);
    dto.setConditions(conditions);

    ResponseEntity<String> responseEntity = restTemplate.postForEntity(
        url("/api/{env}/rules/route/condition"), dto, String.class, env
    );
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));

    dto.setApplication(application);
    when(providerService.findVersionInApplication(dto.getApplication())).thenReturn("2.7");

    responseEntity = restTemplate.postForEntity(
        url("/api/{env}/rules/route/condition"), dto, String.class, env
    );
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
  }

  @Test
  public void shouldUpdateRule() throws Exception {
    String service = "org.apache.dubbo.demo.DemoService";
    String content = "conditions:\n"
        + "- => host != 172.22.3.111\n"
        + "- => host != 172.22.3.112\n"
        + "enabled: true\n"
        + "force: true\n"
        + "key: " + service + "\n"
        + "priority: 0\n"
        + "runtime: false\n"
        + "scope: service";
    String path = "/dubbo/config/" + service + "/condition-router";
    zkClient.create().creatingParentContainersIfNeeded().forPath(path);
    zkClient.setData().forPath(path, content.getBytes());

    List<String> newConditions = Arrays.asList("=> host != 172.22.3.211", "=> host != 172.22.3.212");

    ConditionRouteDTO dto = new ConditionRouteDTO();
    dto.setConditions(newConditions);
    dto.setService(service);

    ResponseEntity<String> responseEntity = restTemplate.exchange(
        url("/api/{env}/rules/route/condition/{service}"), HttpMethod.PUT,
        new HttpEntity<>(dto, null), String.class, env, service
    );
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));

    byte[] bytes = zkClient.getData().forPath(path);
    String updatedConfig = new String(bytes);
    RoutingRule rule = YamlParser.loadObject(updatedConfig, RoutingRule.class);
    assertThat(rule.getConditions(), containsInAnyOrder(newConditions.toArray()));
  }

  @Test
  public void shouldGetServiceRule() throws Exception {
    String service = "org.apache.dubbo.demo.DemoService";
    String content = "conditions:\n"
        + "- => host != 172.22.3.111\n"
        + "- => host != 172.22.3.112\n"
        + "enabled: true\n"
        + "force: true\n"
        + "key: " + service + "\n"
        + "priority: 0\n"
        + "runtime: false\n"
        + "scope: service";
    String path = "/dubbo/config/" + service + "/condition-router";
    zkClient.create().creatingParentContainersIfNeeded().forPath(path);
    zkClient.setData().forPath(path, content.getBytes());

    ResponseEntity<List<ConditionRouteDTO>> responseEntity = restTemplate.exchange(
        url("/api/{env}/rules/route/condition/?service={service}"), HttpMethod.GET,
        null, new ParameterizedTypeReference<List<ConditionRouteDTO>>() {
        }, env, service
    );
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    assertThat(responseEntity.getBody(), hasSize(1));
    List<String> conditions = responseEntity.getBody()
        .stream()
        .flatMap(it -> it.getConditions().stream())
        .collect(Collectors.toList());
    assertThat(conditions, hasSize(2));
    assertThat(conditions, containsInAnyOrder("=> host != 172.22.3.111", "=> host != 172.22.3.112"));
  }

  @Test
  public void shouldDeleteRule() throws Exception {
    String service = "org.apache.dubbo.demo.DemoService";
    String content = "conditions:\n"
        + "- => host != 172.22.3.111\n"
        + "- => host != 172.22.3.112\n"
        + "enabled: true\n"
        + "force: true\n"
        + "key: " + service + "\n"
        + "priority: 0\n"
        + "runtime: false\n"
        + "scope: service";
    String path = "/dubbo/config/" + service + "/condition-router";
    zkClient.create().creatingParentContainersIfNeeded().forPath(path);
    zkClient.setData().forPath(path, content.getBytes());

    assertNotNull("zk path should not be null before deleting", zkClient.checkExists().forPath(path));

    ResponseEntity<String> responseEntity = restTemplate.exchange(
        url("/api/{env}/rules/route/condition/{service}"), HttpMethod.DELETE,
        null, String.class, env, service
    );
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));

    assertNull(zkClient.checkExists().forPath(path));
  }

  @Test
  public void shouldThrowWhenDetailRouteWithUnknownId() {
    ResponseEntity<String> responseEntity = restTemplate.getForEntity(
        url("/api/{env}/rules/route/condition/{id}"), String.class, env, "non-existed-service"
    );
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.NOT_FOUND));
  }

  @Test
  public void shouldGetRouteDetail() throws Exception {
    String service = "org.apache.dubbo.demo.DemoService";
    String content = "conditions:\n"
        + "- => host != 172.22.3.111\n"
        + "- => host != 172.22.3.112\n"
        + "enabled: true\n"
        + "force: true\n"
        + "key: " + service + "\n"
        + "priority: 0\n"
        + "runtime: false\n"
        + "scope: service";
    String path = "/dubbo/config/" + service + "/condition-router";
    zkClient.create().creatingParentContainersIfNeeded().forPath(path);
    zkClient.setData().forPath(path, content.getBytes());

    ResponseEntity<ConditionRouteDTO> responseEntity = restTemplate.getForEntity(
        url("/api/{env}/rules/route/condition/{id}"), ConditionRouteDTO.class, env, service
    );
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));

    ConditionRouteDTO conditionRouteDTO = responseEntity.getBody();
    assertNotNull(conditionRouteDTO);
    assertThat(conditionRouteDTO.getConditions(), hasSize(2));
    assertThat(conditionRouteDTO.getConditions(), containsInAnyOrder("=> host != 172.22.3.111", "=> host != 172.22.3.112"));
  }

  @Test
  public void shouldEnableRoute() throws Exception {
    String service = "org.apache.dubbo.demo.DemoService";
    String content = "conditions:\n"
        + "- => host != 172.22.3.111\n"
        + "- => host != 172.22.3.112\n"
        + "enabled: false\n"
        + "force: true\n"
        + "key: " + service + "\n"
        + "priority: 0\n"
        + "runtime: false\n"
        + "scope: service";
    String path = "/dubbo/config/" + service + "/condition-router";
    zkClient.create().creatingParentContainersIfNeeded().forPath(path);
    zkClient.setData().forPath(path, content.getBytes());

    byte[] bytes = zkClient.getData().forPath(path);
    String updatedConfig = new String(bytes);
    RoutingRule rule = YamlParser.loadObject(updatedConfig, RoutingRule.class);
    assertFalse(rule.isEnabled());

    restTemplate.put(url("/api/{env}/rules/route/condition/enable/{id}"), null, env, service);

    bytes = zkClient.getData().forPath(path);
    updatedConfig = new String(bytes);
    rule = YamlParser.loadObject(updatedConfig, RoutingRule.class);
    assertTrue(rule.isEnabled());
  }

  @Test
  public void shouldDisableRoute() throws Exception {
    String service = "org.apache.dubbo.demo.DemoService";
    String content = "conditions:\n"
        + "- => host != 172.22.3.111\n"
        + "- => host != 172.22.3.112\n"
        + "enabled: true\n"
        + "force: false\n"
        + "key: " + service + "\n"
        + "priority: 0\n"
        + "runtime: false\n"
        + "scope: service";
    String path = "/dubbo/config/" + service + "/condition-router";
    zkClient.create().creatingParentContainersIfNeeded().forPath(path);
    zkClient.setData().forPath(path, content.getBytes());

    byte[] bytes = zkClient.getData().forPath(path);
    String updatedConfig = new String(bytes);
    RoutingRule rule = YamlParser.loadObject(updatedConfig, RoutingRule.class);
    assertTrue(rule.isEnabled());

    restTemplate.put(url("/api/{env}/rules/route/condition/disable/{id}"), null, env, service);

    bytes = zkClient.getData().forPath(path);
    updatedConfig = new String(bytes);
    rule = YamlParser.loadObject(updatedConfig, RoutingRule.class);
    assertFalse(rule.isEnabled());
  }
}
