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
package org.apache.dubbo.admin.common;

import java.util.HashMap;

/**
 * Common Response
 */
public class CommonResponse extends HashMap<String, Object> {

    private static final String MESSAGE = "message";

    private static final String SUCCESS = "success";

    private static final String DATA = "data";

    private static final String REDIRECT = "redirect";

    private static final String EMPTY = "";

    public boolean isSuccess() {
        return get(SUCCESS) != null && (Boolean) get(SUCCESS);
    }

    public String getMessage() {
        if (get(MESSAGE) != null) {
            return (String) get(MESSAGE);
        }
        return EMPTY;
    }

    private CommonResponse() {
        super();
        this.put(SUCCESS, false);
    }

    public CommonResponse success() {
        this.put(SUCCESS, true);
        return this;
    }

    public CommonResponse success(String message) {
        this.put(SUCCESS, true);
        this.put(MESSAGE, message);
        return this;
    }

    public CommonResponse fail(String message) {
        this.put(SUCCESS, false);
        this.put(MESSAGE, message);
        return this;
    }

    public CommonResponse redirect(String url) {
        this.put(REDIRECT, url);
        return this;
    }

    public CommonResponse setData(Object data) {
        return putData(DATA, data);
    }

    public CommonResponse putData(String key, Object data) {
        this.put(key, data);
        return this;
    }

    public static CommonResponse createCommonResponse() {
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.success();
        return commonResponse;
    }

    public static CommonResponse createCommonResponse(Object data) {
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.success();
        commonResponse.setData(data);
        return commonResponse;
    }
}
