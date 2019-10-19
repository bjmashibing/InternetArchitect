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

import org.apache.dubbo.admin.model.domain.Consumer;
import org.apache.dubbo.admin.model.domain.Provider;
import org.apache.dubbo.common.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncUtils {

    public static final String SERVICE_FILTER_KEY = ".service";

    public static final String ADDRESS_FILTER_KEY = ".address";

    public static final String ID_FILTER_KEY = ".id";

    public static Provider url2Provider(Pair<String, URL> pair) {
        if (pair == null) {
            return null;
        }

        String id = pair.getKey();
        URL url = pair.getValue();

        if (url == null)
            return null;

        Provider p = new Provider();
        p.setHash(id);
        p.setService(url.getServiceKey());
        p.setAddress(url.getAddress());
        p.setApplication(url.getParameter(Constants.APPLICATION_KEY));
        p.setUrl(url.toIdentityString());
        p.setParameters(url.toParameterString());

        p.setDynamic(url.getParameter("dynamic", true));
        p.setEnabled(url.getParameter(Constants.ENABLED_KEY, true));
        p.setWeight(url.getParameter(Constants.WEIGHT_KEY, Constants.DEFAULT_WEIGHT));
        p.setUsername(url.getParameter("owner"));

        return p;
    }

    public static List<Provider> url2ProviderList(Map<String, URL> ps) {
        List<Provider> ret = new ArrayList<>();
        for (Map.Entry<String, URL> entry : ps.entrySet()) {
            ret.add(url2Provider(new Pair<>(entry.getKey(), entry.getValue())));
        }
        return ret;
    }

    public static Consumer url2Consumer(Pair<String, URL> pair) {
        if (pair == null) {
            return null;
        }

        String id = pair.getKey();
        URL url = pair.getValue();

        if (null == url)
            return null;

        Consumer c = new Consumer();
        c.setHash(id);
        c.setService(url.getServiceKey());
        c.setAddress(url.getHost());
        c.setApplication(url.getParameter(Constants.APPLICATION_KEY));
        c.setParameters(url.toParameterString());

        return c;
    }

    public static List<Consumer> url2ConsumerList(Map<String, URL> cs) {
        List<Consumer> list = new ArrayList<Consumer>();
        if (cs == null) return list;
        for (Map.Entry<String, URL> entry : cs.entrySet()) {
            list.add(url2Consumer(new Pair<>(entry.getKey(), entry.getValue())));
        }
        return list;
    }


    // Map<category, Map<servicename, Map<Long, URL>>>
    public static <SM extends Map<String, Map<String, URL>>> Map<String, URL> filterFromCategory(Map<String, SM> urls, Map<String, String> filter) {
        String c = (String) filter.get(Constants.CATEGORY_KEY);
        if (c == null) throw new IllegalArgumentException("no category");

        filter.remove(Constants.CATEGORY_KEY);
        return filterFromService(urls.get(c), filter);
    }


    // Map<servicename, Map<Long, URL>>
    public static Map<String, URL> filterFromService(Map<String, Map<String, URL>> urls, Map<String, String> filter) {
        Map<String, URL> ret = new HashMap<>();
        if (urls == null) return ret;

        String s = (String) filter.remove(SERVICE_FILTER_KEY);
        if (s == null) {
            for (Map.Entry<String, Map<String, URL>> entry : urls.entrySet()) {
                filterFromUrls(entry.getValue(), ret, filter);
            }
        } else {
            Map<String, URL> map = urls.get(s);
            filterFromUrls(map, ret, filter);
        }

        return ret;
    }

    // Map<Long, URL>
    static void filterFromUrls(Map<String, URL> from, Map<String, URL> to, Map<String, String> filter) {
        if (from == null || from.isEmpty()) return;

        for (Map.Entry<String, URL> entry : from.entrySet()) {
            URL url = entry.getValue();

            boolean match = true;
            for (Map.Entry<String, String> e : filter.entrySet()) {
                String key = e.getKey();
                String value = e.getValue();

                if (ADDRESS_FILTER_KEY.equals(key)) {
                    if (!value.equals(url.getIp())) {
                        match = false;
                        break;
                    }
                } else {
                    if (!value.equals(url.getParameter(key))) {
                        match = false;
                        break;
                    }
                }
            }

            if (match) {
                to.put(entry.getKey(), url);
            }
        }
    }

    public static <SM extends Map<String, Map<String, URL>>> Pair<String, URL> filterFromCategory(Map<String, SM> urls, String category, String id) {
        SM services = urls.get(category);
        if (services == null) return null;

        for (Map.Entry<String, Map<String, URL>> e1 : services.entrySet()) {
            Map<String, URL> u = e1.getValue();
            if (u.containsKey(id)) return new Pair<>(id, u.get(id));
        }
        return null;
    }
}
