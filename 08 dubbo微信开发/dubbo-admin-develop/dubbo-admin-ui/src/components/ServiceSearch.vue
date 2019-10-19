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
  <v-container id="search" grid-list-xl fluid >
    <v-layout row wrap>
      <v-flex lg12>
        <v-card flat color="transparent">
          <v-card-text>
            <v-form>
              <v-layout row wrap>
                <v-combobox
                  id="serviceSearch"
                  :loading="searchLoading"
                  :items="typeAhead"
                  :search-input.sync="input"
                  v-model="filter"
                  flat
                  append-icon=""
                  hide-no-data
                  :suffix="queryBy"
                  :hint="hint"
                  :label="$t('searchDubboService')"
                  @keyup.enter="submit"
                ></v-combobox>
                <v-menu class="hidden-xs-only">
                  <v-btn slot="activator" large icon>
                    <v-icon>unfold_more</v-icon>
                  </v-btn>

                  <v-list>
                    <v-list-tile
                      v-for="(item, i) in items"
                      :key="i"
                      @click="selected = i">
                      <v-list-tile-title >{{ $t(item.title) }}</v-list-tile-title>
                    </v-list-tile>
                  </v-list>
                </v-menu>
                <v-btn @click="submit" color="primary" large>{{ $t('search') }}</v-btn>
              </v-layout>
            </v-form>
          </v-card-text>
        </v-card>
      </v-flex>
    </v-layout>

    <v-flex lg12>
      <v-card>
        <v-toolbar flat color="transparent" class="elevation-0">
          <v-toolbar-title><span class="headline">{{$t('searchResult')}}</span></v-toolbar-title>
          <v-spacer></v-spacer>
        </v-toolbar>

        <v-card-text class="pa-0">
          <template>
            <v-data-table
              class="elevation-0 table-striped"
              :pagination.sync="pagination"
              :total-items="totalItems"
              :headers="headers"
              :items="services"
              :loading="loadingServices"
            >
              <template slot="items" slot-scope="props">
                <td >{{props.item.service}}</td>
                <td>{{props.item.group}}</td>
                <td>{{props.item.version}}</td>
                <td>{{props.item.appName}}</td>
                <td class="text-xs-center px-0" nowrap>
                  <v-btn
                    class="tiny"
                    color='success'
                    :href='getHref(props.item.service, props.item.appName, props.item.group, props.item.version)'
                  >
                   {{ $t('detail') }}
                  </v-btn>
                  <v-btn
                    small
                    class="tiny"
                    outline
                    :href='toTestService(props.item)'
                  >
                    {{$t('test')}}
                  </v-btn>
                  <v-menu
                  >
                    <v-btn
                      slot="activator"
                      class="tiny"
                      outline
                      small
                    >
                      {{ $t('more') }}
                      <v-icon class="tiny-icon" >arrow_drop_down</v-icon>
                    </v-btn>
                    <v-list>
                      <v-list-tile
                        v-for="(item, i) in options"
                        :key="i"
                        :href='governanceHref(item.value, props.item.service, props.item.appName, props.item.group, props.item.version)'
                      >
                        <v-list-tile-title class="small-list">{{ $t(item.title) }}</v-list-tile-title>
                      </v-list-tile>
                    </v-list>
                  </v-menu>
                </td>
              </template>
            </v-data-table>
          </template>
          <v-divider></v-divider>
        </v-card-text>
      </v-card>
    </v-flex>
  </v-container>
</template>
<script>
  export default {
    data: () => ({
      items: [
        {id: 0, title: 'serviceName', value: 'service'},
        {id: 1, title: 'ip', value: 'ip'},
        {id: 2, title: 'app', value: 'application'}
      ],
      options: [
        { title: 'routingRule',
          value: 'routingRule'
        },
        { title: 'tagRule',
          value: 'tagRule'
        },
        { title: 'dynamicConfig',
          value: 'config'
        },
        { title: 'accessControl',
          value: 'access'
        },
        { title: 'weightAdjust',
          value: 'weight'
        },
        { title: 'loadBalance',
          value: 'loadbalance'
        }
      ],
      timerID: null,
      searchLoading: false,
      selected: 0,
      input: null,
      typeAhead: [],
      resultPage: {},
      filter: '',
      headers: [],
      pagination: {
        page: 1,
        rowsPerPage: 10 // -1 for All
      },
      totalItems: 0,
      loadingServices: false
    }),
    computed: {
      queryBy () {
        return this.$t('by') + this.$t(this.items[this.selected].title)
      },
      hint () {
        if (this.selected === 0) {
          return this.$t('serviceSearchHint')
        } else if (this.selected === 1) {
          return this.$t('ipSearchHint')
        } else if (this.selected === 2) {
          return this.$t('appSearchHint')
        }
      },
      area () {
        return this.$i18n.locale
      },
      services () {
        if (!this.resultPage || !this.resultPage.content) {
          return []
        }
        return this.resultPage.content
      }
    },
    watch: {
      input (val) {
        this.querySelections(val)
      },
      area () {
        this.setHeaders()
      },
      pagination: {
        handler (newVal, oldVal) {
          if (newVal.page === oldVal.page && newVal.rowsPerPage === oldVal.rowsPerPage) {
            return
          }
          const filter = this.$route.query.filter || '*'
          const pattern = this.$route.query.pattern || 'service'
          this.search(filter, pattern, false)
        },
        deep: true
      }
    },
    methods: {
      setHeaders: function () {
        this.headers = [
          {
            text: this.$t('serviceName'),
            value: 'service',
            align: 'left'
          },
          {
            text: this.$t('group'),
            value: 'group',
            align: 'left'
          },
          {
            text: this.$t('version'),
            value: 'version',
            align: 'left'
          },
          {
            text: this.$t('app'),
            value: 'application',
            align: 'left'
          },
          {
            text: this.$t('operation'),
            value: 'operation',
            sortable: false,
            width: '110px'
          }
        ]
      },
      querySelections (v) {
        if (this.timerID) {
          clearTimeout(this.timerID)
        }
        // Simulated ajax query
        this.timerID = setTimeout(() => {
          if (v && v.length >= 4) {
            this.searchLoading = true
            if (this.selected === 0) {
              this.typeAhead = this.$store.getters.getServiceItems(v)
            } else if (this.selected === 2) {
              this.typeAhead = this.$store.getters.getAppItems(v)
            }
            this.searchLoading = false
            this.timerID = null
          } else {
            this.typeAhead = []
          }
        }, 500)
      },
      getHref: function (service, app, group, version) {
        let query = 'service=' + service + '&app=' + app
        if (group !== null) {
          query = query + '&group=' + group
        }
        if (version != null) {
          query = query + '&version=' + version
        }
        return '#/serviceDetail?' + query
      },
      governanceHref: function (type, service, appName, group, version) {
        let base = '#/governance/' + type
        let query = service
        if (type === 'tagRule') {
          query = appName
        }
        if (group !== null) {
          query = group + '/' + query
        }
        if (version !== null) {
          query = query + ':' + version
        }
        if (type === 'tagRule') {
          return base + '?application=' + query
        }
        return base + '?service=' + query
      },
      submit () {
        this.filter = document.querySelector('#serviceSearch').value.trim()
        if (this.filter) {
          let pattern = this.items[this.selected].value
          this.search(this.filter, pattern, true)
        } else {
          return false
        }
      },
      search: function (filter, pattern, rewrite) {
        const page = this.pagination.page - 1
        const size = this.pagination.rowsPerPage === -1 ? this.totalItems : this.pagination.rowsPerPage
        this.loadingServices = true
        this.$axios.get('/service', {
          params: {
            pattern,
            filter,
            page,
            size
          }
        }).then(response => {
          this.resultPage = response.data
          this.totalItems = this.resultPage.totalElements
          if (rewrite) {
            this.$router.push({path: 'service', query: {filter: filter, pattern: pattern}})
          }
        }).finally(() => {
          this.loadingServices = false
        })
      },
      toTestService (item) {
        let base = '#/test'
        let query = '?service=' + item.service
        if (item.group) {
          query = query + '&group=' + item.group
        }
        if (item.version) {
          query = query + '&version=' + item.version
        }
        return base + query
      }
    },
    mounted: function () {
      this.setHeaders()
      this.$store.dispatch('loadServiceItems')
      this.$store.dispatch('loadAppItems')
      let query = this.$route.query
      let filter = null
      let pattern = null
      Object.keys(query).forEach(function (key) {
        if (key === 'filter') {
          filter = query[key]
        }
        if (key === 'pattern') {
          pattern = query[key]
        }
      })
      if (filter != null && pattern != null) {
        this.filter = filter
        if (pattern === 'service') {
          this.selected = 0
        } else if (pattern === 'application') {
          this.selected = 2
        } else if (pattern === 'ip') {
          this.selected = 1
        }
        this.search(filter, pattern, false)
      } else {
        // display all existing services by default
        this.filter = '*'
        this.selected = 0
        pattern = 'service'
        this.search(this.filter, pattern, true)
      }
    }

  }
</script>

<style scoped>
  table.v-table tbody td {
    word-break: break-all;
  }

  .tiny {
    min-width: 30px;
    height: 20px;
    font-size: 8px;
  }

  .tiny-icon {
    font-size: 18px;
  }

  .small-list {
    font-size: 10px;
  }

</style>

