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

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class UrlUtilsTest {

    @Test
    public void testParamsMapToString() {
        Map<String, String[]> params = new HashMap<>();
        params.put("a", new String[]{"1", "2", "3"});
        params.put("b", new String[]{"8", "7", "6"});
        String result = UrlUtils.paramsMapToString(params);
        Assert.assertEquals(result, "&a=1,2,3&b=8,7,6");
    }

    @Test
    public void testArrayToString() {
        String[] strArr = {"1", "2", "3"};
        String result = UrlUtils.arrayToString(strArr);
        Assert.assertEquals(result, "1,2,3");
    }
}
