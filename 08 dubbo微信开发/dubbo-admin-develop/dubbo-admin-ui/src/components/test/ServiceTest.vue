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
        <breadcrumb title="serviceTest" :items="breads"></breadcrumb>
      </v-flex>
      <v-flex lg12>
          <v-card flat color="transparent">
            <v-card-text>
              <v-form>
                <v-layout row wrap>
                  <v-combobox
                    id="serviceTestSearch"
                    :loading="searchLoading"
                    :items="typeAhead"
                    :search-input.sync="input"
                    v-model="filter"
                    flat
                    append-icon=""
                    hide-no-data
                    :hint="$t('testModule.searchServiceHint')"
                    :label="$t('placeholders.searchService')"
                    @keyup.enter="submit"
                  ></v-combobox>
                  <v-btn @click="submit" color="primary" large>{{ $t('search') }}</v-btn>
                </v-layout>
              </v-form>
            </v-card-text>
          </v-card>
        </v-flex>
      <v-flex xs12>
        <v-card>
          <v-toolbar flat color="transparent" class="elevation-0">
            <v-toolbar-title><span class="headline">{{$t('methods')}}</span></v-toolbar-title>
            <v-spacer></v-spacer>
          </v-toolbar>
          <v-card-text class="pa-0">
            <v-data-table :headers="headers" :items="methods" hide-actions class="elevation-1">
              <template slot="items" slot-scope="props">
                <td>{{ props.item.name }}</td>
                <td>
                  <v-chip xs v-for="(type, index) in props.item.parameterTypes" :key="index" label>{{ type }}</v-chip>
                </td>
                <td>
                  <v-chip label>{{ props.item.returnType }}</v-chip>
                </td>
                <td class="text-xs-right">
                  <v-tooltip bottom>
                    <v-btn
                      fab dark small color="blue" slot="activator"
                      :href="getHref(props.item.application, props.item.service, props.item.signature)"
                    >
                      <v-icon>edit</v-icon>
                    </v-btn>
                    <span>{{$t('test')}}</span>
                  </v-tooltip>
                </td>
              </template>
            </v-data-table>
          </v-card-text>
        </v-card>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
  import JsonEditor from '@/components/public/JsonEditor'
  import Search from '@/components/public/Search'
  import Breadcrumb from '@/components/public/Breadcrumb'

  export default {
    name: 'ServiceTest',
    components: {
      JsonEditor,
      Search,
      Breadcrumb
    },
    data () {
      return {
        typeAhead: [],
        input: null,
        searchLoading: false,
        timerID: null,
        filter: '',
        breads: [
          {
            text: 'serviceSearch',
            href: '/test'
          }
        ],
        headers: [
        ],
        service: null,
        methods: [],
        services: [],
        loading: false
      }
    },
    methods: {
      querySelections (v) {
        if (this.timerID) {
          clearTimeout(this.timerID)
        }
        // Simulated ajax query
        this.timerID = setTimeout(() => {
          if (v && v.length >= 4) {
            this.searchLoading = true
            this.typeAhead = this.$store.getters.getServiceItems(v)
            this.searchLoading = false
            this.timerID = null
          } else {
            this.typeAhead = []
          }
        }, 500)
      },
      submit () {
        this.filter = document.querySelector('#serviceTestSearch').value.trim()
        if (this.filter) {
          let filter = this.filter.replace('/', '*')
          this.search(filter)
        } else {
          this.$notify.error('service is needed')
          return false
        }
      },

      setHeaders: function () {
        this.headers = [
          {
            text: this.$t('methodName'),
            value: 'method',
            sortable: false
          },
          {
            text: this.$t('parameterList'),
            value: 'parameter',
            sortable: false
          },
          {
            text: this.$t('returnType'),
            value: 'returnType',
            sortable: false
          },
          {
            text: '',
            value: 'operation',
            sortable: false
          }
        ]
      },
      search (filter) {
        if (!filter) {
          return
        }
        this.$axios.get('/service/' + filter).then(response => {
          this.service = response.data
          this.methods = []
          if (this.service.metadata) {
            let methods = this.service.metadata.methods
            for (let i = 0; i < methods.length; i++) {
              let method = {}
              let sig = methods[i].name + '~'
              let parameters = methods[i].parameterTypes
              let length = parameters.length
              for (let j = 0; j < length; j++) {
                sig = sig + parameters[j]
                if (j !== length - 1) {
                  sig = sig + ';'
                }
              }
              method.signature = sig
              method.name = methods[i].name
              method.parameterTypes = methods[i].parameterTypes
              method.returnType = methods[i].returnType
              method.service = response.data.service
              method.application = response.data.application
              this.methods.push(method)
            }
          }
        }).catch(error => {
          this.showSnackbar('error', error.response.data.message)
        })
      },
      searchServices () {
        let filter = this.filter || ''
        if (!filter.startsWith('*')) {
          filter = '*' + filter
        }
        if (!filter.endsWith('*')) {
          filter += '*'
        }
        const pattern = 'service'
        this.loading = true
        this.$axios.get('/service', {
          params: {
            pattern, filter
          }
        }).then(response => {
          this.services = response.data
        }).finally(() => {
          this.loading = false
        })
      },
      getHref (application, service, method) {
        return `/#/testMethod?application=${application}&service=${service}&method=${method}`
      }
    },
    computed: {
      area () {
        return this.$i18n.locale
      }
    },
    watch: {
      input (val) {
        this.querySelections(val)
      },
      area () {
        this.setHeaders()
      }
    },
    mounted () {
      this.$store.dispatch('loadServiceItems')
      let query = this.$route.query
      this.filter = query['service'] || ''
      if ('group' in query) {
        this.filter = query['group'] + '/' + this.filter
      }
      if ('version' in query) {
        this.filter = this.filter + ':' + query['version']
      }
      if (this.filter) {
        this.search(this.filter.replace('/', '*'))
      }
      this.setHeaders()
    }
  }
</script>
<style>
  .v-breadcrumbs {
    padding-left: 0;
  }
</style>
