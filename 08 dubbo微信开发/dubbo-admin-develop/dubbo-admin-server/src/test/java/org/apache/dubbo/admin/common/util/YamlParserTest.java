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

package org.apache.dubbo.admin.common.util;

import org.apache.dubbo.admin.model.dto.DynamicConfigDTO;
import org.apache.dubbo.admin.model.store.OverrideConfig;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class YamlParserTest {

    private String streamToString(InputStream stream) throws IOException {
        byte[] bytes = new byte[stream.available()];
        stream.read(bytes);
        return new String(bytes);
    }

    @Test
    public void parseLoadBalanceTest() throws IOException {
        try (InputStream yamlStream = this.getClass().getResourceAsStream("/LoadBalance.yml")) {
            DynamicConfigDTO overrideDTO = YamlParser.loadObject(streamToString(yamlStream), DynamicConfigDTO.class);
            assertEquals("v2.7", overrideDTO.getConfigVersion());
            assertEquals(false, overrideDTO.isEnabled());
            List<OverrideConfig> configs = overrideDTO.getConfigs();
            assertEquals(2, configs.size());

            OverrideConfig first = configs.get(0);
            assertEquals("0.0.0.0:20880", first.getAddresses().get(0));
            assertEquals("provider", first.getSide());
            Map<String, Object> parameters = first.getParameters();
            assertEquals(1, parameters.size());
            assertEquals(2000, parameters.get("timeout"));

            OverrideConfig second = configs.get(1);
            assertEquals("balancing", second.getType());
            assertEquals(true, second.isEnabled());
            parameters = second.getParameters();
            assertEquals(2, parameters.size());
            assertEquals("*", parameters.get("method"));
            assertEquals("random", parameters.get("strategy"));
        }
    }


}
