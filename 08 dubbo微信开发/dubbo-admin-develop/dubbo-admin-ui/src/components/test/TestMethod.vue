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
        <breadcrumb title="testMethod" :items="breads"></breadcrumb>
      </v-flex>
    <v-flex class="test-form" lg12 xl6>
        <v-card>
          <v-card-title class="headline">{{$t('testMethod') + ': ' + method.signature}}</v-card-title>
          <v-card-text>
            <json-editor id="test" v-model="method.json"/>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn id="execute" mt-0 color="primary" @click="executeMethod()">{{$t('execute')}}</v-btn>
          </v-card-actions>
        </v-card>
      </v-flex>
      <v-flex class="test-result" lg12 xl6>
        <v-card>
          <v-card-title class="headline">{{$t('result') }}
            <span class="green--text" v-if="success===true">{{ $t('success')}}</span>
            <span class="red--text" v-if="success===false">{{ $t('fail')}}</span>
          </v-card-title>
          <v-card-text>
            <json-editor v-model="result" name="Result" readonly></json-editor>
          </v-card-text>
        </v-card>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
  import JsonEditor from '@/components/public/JsonEditor'
  import Breadcrumb from '@/components/public/Breadcrumb'
  import axios from 'axios'
  import set from 'lodash/set'
  import util from '@/util'

  export default {
    name: 'TestMethod',
    components: {
      JsonEditor,
      Breadcrumb,
      axios
    },
    data () {
      return {
        baseURL: '/api/dev',
        success: null,
        breads: [
          {
            text: 'serviceSearch',
            href: 'test'
          },
          {
            text: 'serviceTest',
            href: '',
            strong: this.$route.query['service']
          }
        ],
        service: this.$route.query['service'],
        application: this.$route.query['application'],
        method: {
          name: null,
          signature: this.$route.query['method'],
          parameterTypes: [],
          json: [],
          jsonTypes: []
        },
        result: null
      }
    },
    methods: {
      executeMethod () {
        this.convertType(this.method.json, this.method.jsonTypes)
        let serviceTestDTO = {
          service: this.service,
          method: this.method.name,
          parameterTypes: this.method.parameterTypes,
          params: this.method.json
        }
        axios.post(this.baseURL + '/test', serviceTestDTO)
          .then(response => {
            if (response && response.status === 200) {
              this.success = true
              this.result = response.data
            }
          })
          .catch(error => {
            this.success = false
            this.result = error.response.data
          })
      },

      convertType (params, types) {
        const p = util.flattenObject(params)
        const t = util.flattenObject(types)
        Object.keys(p).forEach(key => {
          if (typeof t[key] === 'string' && typeof p[key] !== 'string') {
            set(params, key, String(p[key]))
          }
        })
      }
    },
    mounted () {
      const query = this.$route.query
      const method = query['method']

      if (method) {
        const [methodName, parametersTypes] = method.split('~')
        this.method.name = methodName
        if (parametersTypes) {
          this.method.parameterTypes = parametersTypes.split(';')
        } else { // if parametersTypes === "",  "".split(";") will produce [""], which is wrong
          this.method.parameterTypes = []
        }
      }

      let url = '/test/method?' + 'application=' + this.application +
                '&service=' + this.service + '&method=' + method
      this.$axios.get(encodeURI(url))
        .then(response => {
          this.method.json = response.data.parameterTypes
          this.method.jsonTypes = response.data.parameterTypes
        })
    }
  }
</script>

<style scoped>
</style>
