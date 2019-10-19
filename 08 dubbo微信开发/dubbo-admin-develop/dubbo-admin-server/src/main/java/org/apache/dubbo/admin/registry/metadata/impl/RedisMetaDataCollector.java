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

package org.apache.dubbo.admin.registry.metadata.impl;


import org.apache.dubbo.admin.registry.metadata.MetaDataCollector;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.metadata.identifier.MetadataIdentifier;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisMetaDataCollector implements MetaDataCollector {

    private  URL url;
    private JedisPool pool;
    private static final String META_DATA_SOTRE_TAG = ".metaData";
    @Override
    public void setUrl(URL url) {
        this.url = url;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public void init() {
        pool = new JedisPool(new JedisPoolConfig(), url.getHost(), url.getPort());
    }

    @Override
    public String getProviderMetaData(MetadataIdentifier key) {
        return doGetMetaData(key);
    }

    @Override
    public String getConsumerMetaData(MetadataIdentifier key) {
        return doGetMetaData(key);
    }

    private String doGetMetaData(MetadataIdentifier identifier) {
        //TODO error handing
        Jedis jedis = pool.getResource();
        String result = jedis.get(identifier.getUniqueKey(MetadataIdentifier.KeyTypeEnum.UNIQUE_KEY) + META_DATA_SOTRE_TAG);
        return result;
    }
}
