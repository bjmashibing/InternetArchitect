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

import Vue from 'vue'
import Router from 'vue-router'
import ServiceSearch from '@/components/ServiceSearch'
import ServiceDetail from '@/components/ServiceDetail'
import TestMethod from '@/components/test/TestMethod'
import RoutingRule from '@/components/governance/RoutingRule'
import TagRule from '@/components/governance/TagRule'
import AccessControl from '@/components/governance/AccessControl'
import LoadBalance from '@/components/governance/LoadBalance'
import WeightAdjust from '@/components/governance/WeightAdjust'
import Overrides from '@/components/governance/Overrides'
import ServiceTest from '@/components/test/ServiceTest'
import ServiceMock from '@/components/test/ServiceMock'
import ServiceMetrics from '@/components/metrics/ServiceMetrics'
import Management from '@/components/Management'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/service',
      name: 'ServiceSearch',
      component: ServiceSearch
    },
    {
      path: '/serviceDetail',
      name: 'ServiceDetail',
      component: ServiceDetail
    },
    {
      path: '/testMethod',
      name: 'TestMethod',
      component: TestMethod
    },
    {
      path: '/governance/routingRule',
      name: 'RoutingRule',
      component: RoutingRule
    },
    {
      path: '/governance/tagRule',
      name: 'TagRule',
      component: TagRule
    },
    {
      path: '/governance/access',
      name: 'AccessControl',
      component: AccessControl
    },
    {
      path: '/governance/loadbalance',
      name: 'LoadBalance',
      component: LoadBalance
    },
    { path: '/governance/weight',
      name: 'WeightAdjust',
      component: WeightAdjust
    },
    {
      path: '/governance/config',
      name: 'Overrides',
      component: Overrides
    },
    {
      path: '/test',
      name: 'ServiceTest',
      component: ServiceTest
    },
    {
      path: '/mock',
      name: 'ServiceMock',
      component: ServiceMock
    },
    {
      path: '/metrics',
      name: 'ServiceMetrics',
      component: ServiceMetrics
    },
    {
      path: '/management',
      name: 'Management',
      component: Management
    }

  ]
})
