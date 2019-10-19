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

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CoderUtilTest {

    @Test
    public void MD5_16bit() {
        assertNull(CoderUtil.MD5_16bit(null));

        String input = "dubbo";
        String output = "2CC9DEED96FE012E";
        assertEquals(output, CoderUtil.MD5_16bit(input));
    }

    @Test
    public void MD5_32bit() {
        String input = null;
        assertNull(CoderUtil.MD5_32bit(input));

        input = "dubbo";
        String output = "AA4E1B8C2CC9DEED96FE012EF2E0752A";
        assertEquals(output, CoderUtil.MD5_32bit(input));
    }

    @Test
    public void MD5_32bit1() {
        byte[] input = null;
        assertNull(CoderUtil.MD5_32bit(input));

        input = "dubbo".getBytes();
        String output = "AA4E1B8C2CC9DEED96FE012EF2E0752A";
        assertEquals(output, CoderUtil.MD5_32bit(input));
    }

    @Test
    public void decodeBase64() {
        try {
            CoderUtil.decodeBase64(null);
            fail("when param is null, this should throw exception");
        } catch (Exception e) {
        }

        String input = "ZHViYm8=";
        String output = "dubbo";
        assertEquals(output, CoderUtil.decodeBase64(input));
    }
}