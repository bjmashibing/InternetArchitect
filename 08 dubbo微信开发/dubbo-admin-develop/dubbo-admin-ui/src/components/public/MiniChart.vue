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
      <div class="layout row ma-0 align-center justify-space-between">
        <div class="text-box">
          <div class="subheading pb-2">{{title}} </div>
          <span class="grey--text">{{subTitle}} <v-icon small :color="iconColor">{{icon}}</v-icon></span>
        </div>
        <div class="chart">
          <e-chart id="hello"
            :path-option="computeChartOption"
            height="68px"
            width="100%"
          >
          </e-chart>
        </div>
      </div>
</template>

<script>
  import EChart from '@/util/echart'
  export default {
    components: {
      EChart
    },
    props: {
      title: String,
      subTitle: String,
      icon: String,
      iconColor: {
        type: String,
        default: 'success'
      },
      type: String,
      chartColor: String,
      data: Array
    },
    data () {
      return {
        defaultOption: [
          ['dataset.source', this.data],
          ['xAxis.show', false],
          ['yAxis.show', false],
          ['color', [this.chartColor]]
        ]
      }
    },

    computed: {
      computeChartOption () {
        switch (this.type) {
          case 'bar':
            this.defaultOption.push(['series[0].type', 'bar'])
            break
          case 'area':
            this.defaultOption.push(['series[0].type', 'line'])
            this.defaultOption.push(['series[0].areaStyle', {}])
            break
          default:
            break
        }
        return this.defaultOption
      }
    },

    watch: {
      data (curVal, oldVal) {
        return this.defaultOption
      }
    }
  }
</script>

<style scoped>

</style>
