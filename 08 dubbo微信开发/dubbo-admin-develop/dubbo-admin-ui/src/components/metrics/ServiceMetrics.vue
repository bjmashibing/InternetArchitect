<!--
  - Licensed to the Apache Software Foundation (ASF) under one or more
  - contributor license agreements.  See the NOTICE file distributed with
  - this work for additional information regarding copyright ownership.
  - The ASF licenses this file to You under the Apache License, Version 2.0
  - (the "License"); you may not use this file except in compliance with
  - the License.  You may obtain a copy of the License at
  -
  -     http://www.apache.org/licenses/LICENSE-2.0
  -
  - Unless required by applicable law or agreed to in writing, software
  - distributed under the License is distributed on an "AS IS" BASIS,
  - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  - See the License for the specific language governing permissions and
  - limitations under the License.
  -->

<template>
  <v-container grid-list-xl fluid>
    <v-layout row wrap>
      <v-flex lg12>
        <breadcrumb title="metrics" :items="breads"></breadcrumb>
      </v-flex>
      <v-flex xs12 >
        <search id="serviceSearch" v-model="filter" :submit="submit" :label="$t('searchSingleMetrics')"></search>
      </v-flex>
      <v-flex lg4 sm6 xs12>
        <v-card>
          <v-card-text>
            <h4>Provider (Total)</h4>
            <hr style="height:3px;border:none;border-top:3px double #9D9D9D;" />
            <chart ref="chart1"  :options="provider.qps" :autoresize="true"/>
            <hr style="height:1px;border:none;border-top:1px solid #ADADAD;" />
            <chart ref="chart1" :options="provider.rt" :autoresize="true" />
            <hr style="height:1px;border:none;border-top:1px solid #ADADAD;" />
            <chart ref="chart1" :options="provider.success_rate" :autoresize="true" />
            <hr style="height:1px;border:none;border-top:1px solid #ADADAD;" />
          </v-card-text>
        </v-card>
      </v-flex>
      <v-flex lg4 sm6 xs12>
        <v-card>
          <v-card-text>
            <h4>Consumer (Total)</h4>
            <hr style="height:3px;border:none;border-top:3px double #9D9D9D;" />
            <chart ref="chart1" :options="consumer.qps" :autoresize="true" />
            <hr style="height:1px;border:none;border-top:1px solid #ADADAD;" />
            <chart ref="chart1" :options="consumer.rt" :autoresize="true" />
            <hr style="height:1px;border:none;border-top:1px solid #ADADAD;" />
            <chart ref="chart1" :options="consumer.success_rate" :autoresize="true" />
            <hr style="height:1px;border:none;border-top:1px solid #ADADAD;" />
          </v-card-text>
        </v-card>
      </v-flex>
      <v-flex lg4 sm6 xs12>
        <v-card>
          <v-card-text>
            <h4>Thread Pool</h4>
            <hr style="height:3px;border:none;border-top:3px double #9D9D9D;" />
              <div class="layout row ma-0 align-center justify-space-between">
                <div class="text-box">
                  <div class="subheading pb-2">active count</div>
                  <span class="grey--text">{{this.threadPoolData.active}} <v-icon small color="green">{{this.threadPoolData.active_trending}}</v-icon> </span>
                </div>
                <div class="chart">
                  <v-progress-circular
                    :size="60"
                    :width="5"
                    :rotate="360"
                    :value="this.threadPoolData.activert"
                    color="success"
                  >
                    {{this.threadPoolData.activert}}
                  </v-progress-circular>
                </div>
              </div>
            <hr style="height:1px;border:none;border-top:1px solid #ADADAD;" />
            <div class="layout row ma-0 align-center justify-space-between">
              <div class="subheading pb-2">core size</div>
              <span class="grey--text">{{this.threadPoolData.core}} </span>
              <div class="subheading pb-2">max size</div>
              <span class="grey--text">{{this.threadPoolData.max}} </span>
              <div class="subheading pb-2">current size</div>
              <span class="grey--text">{{this.threadPoolData.current}} </span>
              <div style="height:60px"></div>
              <v-icon small color="green">{{this.threadPoolData.current_trending}}</v-icon>
            </div>
            <hr style="height:1px;border:none;border-top:1px solid #ADADAD;" />
          </v-card-text>
        </v-card>
      </v-flex>
      <v-flex sm12>
        <h3>{{$t('methodMetrics')}}</h3>
      </v-flex>
      <v-flex lg12 >
        <v-tabs
          class="elevation-1">
          <v-tab>
            {{$t('providers')}}
          </v-tab>
          <v-tab>
            {{$t('consumers')}}
          </v-tab>
          <v-tab-item>
            <v-data-table
              id="providerList"
              class="elevation-1"
              :headers="headers"
              :items="providerDetails"
            >
              <template slot="items" slot-scope="props">
                <td>{{props.item.service}}</td>
                <td>{{props.item.method}}</td>
                <td>{{props.item.qps}}</td>
                <td>{{props.item.rt}}</td>
                <td>{{props.item.successRate}}</td>
              </template>
            </v-data-table>
          </v-tab-item>
          <v-tab-item >
            <v-data-table
              class="elevation-1"
              :headers="headers"
              :items="consumerDetails"
            >
              <template slot="items" slot-scope="props">
                <td>{{props.item.service}}</td>
                <td>{{props.item.method}}</td>
                <td>{{props.item.qps}}</td>
                <td>{{props.item.rt}}</td>
                <td>{{props.item.successRate}}</td>
              </template>
            </v-data-table>
          </v-tab-item>
        </v-tabs>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
  import Material from 'vuetify/es5/util/colors'
  import Breadcrumb from '@/components/public/Breadcrumb'
  import Search from '@/components/public/Search'

  export default {

    name: 'ServiceMetrics',
    components: {
      Breadcrumb,
      Search
    },
    data () {
      return {
        provider: {
          metrics: [
            'dubbo.provider.qps', 'dubbo.provider.rt', 'dubbo.provider.success_rate'
          ],
          qps: {
            title: {
              text: 'qps',
              padding: [50, 5, 5, 5],
              textStyle: {
                fontWeight: 200,
                fontSize: 12
              }
            },
            tooltip: {},
            xAxis: {
              show: false,
              type: 'category',
              data: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
            },
            yAxis: {
              show: false
            },
            series: [{
              type: 'line',
              data: []
            }]
          },
          rt: {
            title: {
              text: 'rt(ms)',
              padding: [50, 5, 5, 5],
              textStyle: {
                fontWeight: 200,
                fontSize: 12
              }
            },
            tooltip: {},
            xAxis: {
              show: false,
              type: 'category',
              data: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
            },
            yAxis: {
              show: false
            },
            series: [{
              type: 'line',
              data: []
            }]
          },
          success_rate: {
            title: {
              text: 'success rate',
              padding: [50, 5, 5, 5],
              textStyle: {
                fontWeight: 200,
                fontSize: 12
              }
            },
            tooltip: {},
            xAxis: {
              show: false,
              type: 'category',
              data: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
            },
            yAxis: {
              show: false
            },
            series: [{
              type: 'line',
              data: []
            }]
          }
        },
        consumer: {
          metrics: [
            'dubbo.consumer.qps', 'dubbo.consumer.rt', 'dubbo.consumer.success_rate'
          ],
          qps: {
            title: {
              text: 'qps',
              padding: [50, 5, 5, 5],
              textStyle: {
                fontWeight: 200,
                fontSize: 12
              }

            },
            tooltip: {},
            xAxis: {
              show: false,
              type: 'category',
              data: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
            },
            yAxis: {
              show: false
            },
            series: [{
              type: 'line',
              data: []
            }]
          },
          rt: {
            title: {
              text: 'rt(ms)',
              padding: [50, 5, 5, 5],
              textStyle: {
                fontWeight: 200,
                fontSize: 12
              }
            },
            tooltip: {},
            xAxis: {
              show: false,
              type: 'category',
              data: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
            },
            yAxis: {
              show: false
            },
            series: [{
              type: 'line',
              data: []
            }]
          },
          success_rate: {
            title: {
              text: 'success rate',
              padding: [50, 5, 5, 5],
              textStyle: {
                fontWeight: 200,
                fontSize: 12
              }
            },
            tooltip: {},
            xAxis: {
              show: false,
              type: 'category',
              data: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
            },
            yAxis: {
              show: false
            },
            series: [{
              type: 'line',
              data: []
            }]
          }
        },
        threadPoolData: {
          'core': 0,
          'max': 0,
          'current': 0,
          'current_trending': '',
          'active': 0,
          'active_trending': '',
          'activert': 0
        },
        selectedTab: 'tab-1',
        filter: '',
        headers: [],
        providerDetails: [],
        consumerDetails: [],
        option: null,
        color: Material,
        breads: [
          {
            text: 'metrics',
            href: ''
          }
        ]

      }
    },
    /*
    * */
    methods: {
      submit: function () {
        this.searchByIp(this.filter, true)
      },
      searchByIp: function (filter, rewrite) {
        if (this.filter === '') {
          return
        }

        if (rewrite) {
          this.$router.push({path: '/metrics', query: {ip: this.filter}})
        }
        let url = '/metrics/ipAddr/?ip=' + filter + '&group=dubbo'
        this.$axios.get(url)
          .then(response => {
            if (!response.data) {
              return
            }
            this.dealNormal(response.data)
            this.dealMajor(response.data)
            this.dealThreadPoolData(response.data)
          })
      },
      dealThreadPoolData: function (data) {
        for (let index in data) {
          let metricsDTO = data[index]
          if ((metricsDTO['metric']).indexOf('threadPool') >= -1) {
            let metric = metricsDTO['metric'].substring(metricsDTO['metric'].lastIndexOf('.') + 1)
            if (metric === 'active' || metric === 'current') {
              let trending = metric + '_trending'
              this.threadPoolData[trending] = this.dealTrending(this.threadPoolData[metric], metricsDTO['value'])
            }
            this.threadPoolData[metric] = metricsDTO['value']
          }
        }
        this.threadPoolData.activert = (100 * this.threadPoolData.active / this.threadPoolData.current).toFixed(2)
      },
      getKey: function (key) {
        return key.substring(key.lastIndexOf('.') + 1)
      },
      dealMajor: function (data) {
        for (let index in data) {
          let metricsDTO = data[index]
          if (this.provider.metrics.indexOf(metricsDTO['metric']) !== -1) {
            let key = this.getKey(metricsDTO['metric'])
            let data = this.provider[key].series[0].data
            if (data.length === 10) {
              data.push(metricsDTO['value'])
              data.shift()
            } else {
              data.push(metricsDTO['value'])
            }
            this.provider[key].series.data = data
          }
          if (this.consumer.metrics.indexOf(metricsDTO['metric']) !== -1) {
            let key = this.getKey(metricsDTO['metric'])
            let data = this.consumer[key].series[0].data
            if (data.length === 10) {
              data.push(metricsDTO['value'])
              data.shift()
            } else {
              data.push(metricsDTO['value'])
            }
            this.consumer[key].series.data = data
          }
        }
      },
      dealNormal: function (data) {
        let serviceMethodMap = {}
        for (let index in data) {
          let metricsDTO = data[index]
          if (metricsDTO['metricLevel'] === 'NORMAL') {
            let metric = metricsDTO['metric'] + ''
            let isProvider = metric.split('.')[1]
            metric = isProvider + '.' + metric.substring(metric.lastIndexOf('.') + 1)

            let methodMap = serviceMethodMap[metricsDTO.tags.service]
            if (!methodMap) {
              methodMap = {}
              serviceMethodMap[metricsDTO.tags.service] = methodMap
            }
            let metricMap = methodMap[metricsDTO.tags.method]

            if (!metricMap) {
              metricMap = {}
              serviceMethodMap[metricsDTO.tags.service][metricsDTO.tags.method] = metricMap
            }
            metricMap[metric] = metricsDTO['value']
          }
        }
        this.providerDetails = []
        this.consumerDetails = []
        for (let service in serviceMethodMap) {
          for (let method in serviceMethodMap[service]) {
            let metricsMap = serviceMethodMap[service][method]
            this.addDataToDetails(this.providerDetails, service, method, metricsMap, 'provider')
            this.addDataToDetails(this.consumerDetails, service, method, metricsMap, 'consumer')
          }
        }
      },
      addDataToDetails: function (sideDetails, service, method, metricsMap, side) {
        if (metricsMap[side + '.qps'] && metricsMap[side + '.success_rate'] && metricsMap[side + '.success_bucket_count']) {
          sideDetails.push({
            service: service,
            method: method,
            qps: metricsMap[side + '.qps'].toFixed(2),
            rt: metricsMap[side + '.rt'].toFixed(2),
            successRate: metricsMap[side + '.success_rate'],
            successCount: metricsMap[side + '.success_bucket_count']
          })
        }
      },
      dealTrending: function (oldValue, curValue) {
        if (curValue > oldValue) {
          return 'trending_up'
        }
        if (curValue < oldValue) {
          return 'trending_down'
        }
        return ''
      },
      setHeaders: function () {
        this.headers = [
          {
            text: this.$t('service'),
            value: 'service'
          },
          {
            text: this.$t('method'),
            value: 'method'
          },
          {
            text: this.$t('qps'),
            value: 'qps'
          },
          {
            text: this.$t('rt'),
            value: 'rt'
          },
          {
            text: this.$t('successRate'),
            value: 'successRate'
          }
        ]
      }
    },
    computed: {
      area () {
        return this.$i18n.locale
      }
    },
    watch: {
      area () {
        this.setHeaders()
      }
    },
    mounted: function () {
      this.setHeaders()
      let filter = null
      let query = this.$route.query
      Object.keys(query).forEach(function (key) {
        if (key === 'ip') {
          filter = query[key]
        }
      })
      if (filter !== null) {
        this.filter = filter
        this.searchByIp(this.filter, false)
      }
      setInterval(() => {
        this.searchByIp(this.filter, false)
      }, 5000)
    }
  }
</script>

<style scoped>
  .echarts {
    width: 105%;
    height: 68px;
  }


</style>
