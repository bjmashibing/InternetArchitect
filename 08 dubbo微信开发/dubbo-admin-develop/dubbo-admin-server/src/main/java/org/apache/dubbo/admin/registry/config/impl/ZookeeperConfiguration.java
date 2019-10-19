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

package org.apache.dubbo.admin.registry.config.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.dubbo.admin.common.util.Constants;
import org.apache.dubbo.admin.registry.config.GovernanceConfiguration;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;

public class ZookeeperConfiguration implements GovernanceConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperConfiguration.class);
    private CuratorFramework zkClient;
    private URL url;
    private String root;

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
        if (url == null) {
            throw new IllegalStateException("server url is null, cannot init");
        }
        CuratorFrameworkFactory.Builder zkClientBuilder = CuratorFrameworkFactory.builder().
                connectString(url.getAddress()).
                retryPolicy(new ExponentialBackoffRetry(1000, 3));
        if (StringUtils.isNotEmpty(url.getUsername()) && StringUtils.isNotEmpty(url.getPassword())) {
            // add authorization
            String auth = url.getUsername() + ":" + url.getPassword();
            zkClientBuilder.authorization("digest", auth.getBytes());
        }
        zkClient = zkClientBuilder.build();
        String group = url.getParameter(Constants.GROUP_KEY, Constants.DEFAULT_ROOT);
        if (!group.startsWith(Constants.PATH_SEPARATOR)) {
            group = Constants.PATH_SEPARATOR + group;
        }
        root = group;
        zkClient.start();
    }

    @Override
    public String setConfig(String key, String value) {
        return setConfig(null, key, value);
    }

    @Override
    public String getConfig(String key) {
        return getConfig(null, key);
    }

    @Override
    public boolean deleteConfig(String key) {
        return deleteConfig(null, key);
    }

    @Override
    public String setConfig(String group, String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("key or value cannot be null");
        }
        String path = getNodePath(key, group);
        try {
            if (zkClient.checkExists().forPath(path) == null) {
                zkClient.create().creatingParentsIfNeeded().forPath(path);
            }
            zkClient.setData().forPath(path, value.getBytes());
            return value;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String getConfig(String group, String key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        String path = getNodePath(key, group);

        try {
            if (zkClient.checkExists().forPath(path) == null) {
                return null;
            }
            return new String(zkClient.getData().forPath(path));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean deleteConfig(String group, String key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        String path = getNodePath(key, group);
        try {
            zkClient.delete().forPath(path);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public String getPath(String key) {
        return getNodePath(key, null);
    }

    @Override
    public String getPath(String group, String key) {
        return getNodePath(key, group);
    }

    private String getNodePath(String path, String group) {
        if (path == null) {
            throw new IllegalArgumentException("path cannot be null");
        }
        return toRootDir(group) + path;
    }

    private String toRootDir(String group) {
        if (group != null) {
            if (!group.startsWith(Constants.PATH_SEPARATOR)) {
                root = Constants.PATH_SEPARATOR + group;
            } else {
                root = group;
            }
        }
        if (root.equals(Constants.PATH_SEPARATOR)) {
            return root;
        }
        return root + Constants.PATH_SEPARATOR;
    }
}
