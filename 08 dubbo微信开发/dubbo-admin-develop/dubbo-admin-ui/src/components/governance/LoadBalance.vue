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
  <v-container grid-list-xl fluid >
    <v-layout row wrap>
      <v-flex lg12>
        <breadcrumb title="loadBalance" :items="breads"></breadcrumb>
      </v-flex>
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
                  @keyup.enter="submit"
                  v-model="filter"
                  flat
                  append-icon=""
                  hide-no-data
                  :suffix="queryBy"
                  :label="$t('searchBalanceRule')"
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
                      <v-list-tile-title>{{ $t(item.title) }}</v-list-tile-title>
                    </v-list-tile>
                  </v-list>
                </v-menu>
                <v-btn @click="submit" color="primary" large>{{$t('search')}}</v-btn>

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
          <v-btn outline color="primary" @click.stop="openDialog" class="mb-2">{{$t('create')}}</v-btn>
        </v-toolbar>

        <v-card-text class="pa-0" v-show="selected == 0">
          <v-data-table
            :headers="serviceHeaders"
            :items="loadBalances"
            hide-actions
            class="elevation-0"
          >
            <template slot="items" slot-scope="props">
              <td class="text-xs-left">{{ props.item.service }}</td>
              <td class="text-xs-left">{{ props.item.methodName }}</td>
              <td class="text-xs-center px-0">
                <v-tooltip bottom v-for="op in operations" :key="op.id">
                  <v-icon small class="mr-2" slot="activator" @click="itemOperation(op.icon, props.item)">
                    {{op.icon}}
                  </v-icon>
                  <span>{{$t(op.tooltip)}}</span>
                </v-tooltip>
              </td>
            </template>
          </v-data-table>
        </v-card-text>

        <v-card-text class="pa-0" v-show="selected == 1">
          <v-data-table
            :headers="appHeaders"
            :items="loadBalances"
            hide-actions
            class="elevation-0"
          >
            <template slot="items" slot-scope="props">
              <td class="text-xs-left">{{ props.item.application }}</td>
              <td class="text-xs-left">{{ props.item.methodName }}</td>
              <td class="text-xs-center px-0">
                <v-tooltip bottom v-for="op in operations" :key="op.id">
                  <v-icon small class="mr-2" slot="activator" @click="itemOperation(op.icon, props.item)">
                    {{op.icon}}
                  </v-icon>
                  <span>{{$t(op.tooltip)}}</span>
                </v-tooltip>
              </td>
            </template>
          </v-data-table>
        </v-card-text>
      </v-card>
    </v-flex>

    <v-dialog   v-model="dialog" width="800px" persistent >
      <v-card>
        <v-card-title class="justify-center">
          <span class="headline">{{$t('createNewLoadBalanceRule')}}</span>
        </v-card-title>
        <v-card-text >
          <v-text-field
            label="Service Unique ID"
            :hint="$t('dataIdHint')"
            v-model="service"
            :readonly="readonly"
          ></v-text-field>
          <v-text-field
            :label="$t('appName')"
            :hint="$t('appNameHint')"
            v-model="application"
            :readonly="readonly"
          ></v-text-field>
          <v-layout row justify-space-between>
            <v-flex >
              <v-text-field
                :label="$t('method')"
                :hint="$t('methodHint')"
                v-model="rule.method"
                :readonly="readonly"
              ></v-text-field>
            </v-flex>
            <v-spacer></v-spacer>
            <v-flex>
              <v-select
                :items="rule.strategyKey"
                :label="$t('strategy')"
                v-model="rule.strategy"
                :readonly="readonly"
              ></v-select>
            </v-flex>
          </v-layout>

        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="darken-1" flat @click.native="closeDialog">{{$t('close')}}</v-btn>
          <v-btn color="primary darken-1" depressed @click.native="saveItem">{{$t('save')}}</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="warn" persistent max-width="500px">
      <v-card>
        <v-card-title class="headline">{{$t(this.warnTitle)}}</v-card-title>
        <v-card-text >{{this.warnText}}</v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="darken-1" flat @click.native="closeWarn">{{$t('cancel')}}</v-btn>
          <v-btn color="primary darken-1" depressed @click.native="deleteItem(warnStatus.id)">{{$t('confirm')}}</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

  </v-container>

</template>
<script>
  import AceEditor from '@/components/public/AceEditor'
  import Search from '@/components/public/Search'
  import Breadcrumb from '@/components/public/Breadcrumb'
  export default {
    components: {
      AceEditor,
      Search,
      Breadcrumb
    },
    data: () => ({
      items: [
        {id: 0, title: 'serviceName', value: 'service'},
        {id: 1, title: 'app', value: 'application'}
      ],
      breads: [
        {
          text: 'serviceGovernance',
          href: ''
        },
        {
          text: 'loadBalance',
          href: ''
        }
      ],
      selected: 0,
      dropdown_font: [ 'Service', 'App', 'IP' ],
      ruleKeys: ['method', 'strategy'],
      pattern: 'Service',
      filter: '',
      updateId: '',
      dialog: false,
      warn: false,
      application: '',
      service: '',
      warnTitle: '',
      warnText: '',
      warnStatus: {},
      height: 0,
      searchLoading: false,
      typeAhead: [],
      input: null,
      timerID: null,
      operations: [
        {id: 0, icon: 'visibility', tooltip: 'view'},
        {id: 1, icon: 'edit', tooltip: 'edit'},
        {id: 3, icon: 'delete', tooltip: 'delete'}
      ],
      loadBalances: [],
      template:
        'methodName: *  # * for all methods\n' +
        'strategy:  # leastactive, random, roundrobin',
      rule: {
        method: '',
        strategy: '',
        strategyKey: [
          {
            text: 'Least Active',
            value: 'leastactive'
          },
          {
            text: 'Random',
            value: 'random'
          },
          {
            text: 'Round Robin',
            value: 'roundrobin'
          }

        ]
      },
      ruleText: '',
      readonly: false,
      serviceHeaders: [],
      appHeaders: []
    }),
    methods: {
      setServiceHeaders: function () {
        this.serviceHeaders = [
          {
            text: this.$t('serviceName'),
            value: 'service',
            align: 'left'
          },
          {
            text: this.$t('method'),
            value: 'method',
            align: 'left'

          },
          {
            text: this.$t('operation'),
            value: 'operation',
            sortable: false,
            width: '115px'
          }
        ]
      },
      setAppHeaders: function () {
        this.appHeaders = [
          {
            text: this.$t('appName'),
            value: 'application',
            align: 'left'
          },
          {
            text: this.$t('method'),
            value: 'method',
            align: 'left'

          },
          {
            text: this.$t('operation'),
            value: 'operation',
            sortable: false,
            width: '115px'
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
            } else if (this.selected === 1) {
              this.typeAhead = this.$store.getters.getAppItems(v)
            }
            this.searchLoading = false
            this.timerID = null
          } else {
            this.typeAhead = []
          }
        }, 500)
      },
      submit: function () {
        this.filter = document.querySelector('#serviceSearch').value.trim()
        this.search(true)
      },
      search: function (rewrite) {
        if (!this.filter) {
          this.$notify.error('Either service or application is needed')
          return
        }
        let type = this.items[this.selected].value
        let url = '/rules/balancing/?' + type + '=' + this.filter
        this.$axios.get(url)
          .then(response => {
            this.loadBalances = response.data
            if (rewrite) {
              if (this.selected === 0) {
                this.$router.push({path: 'loadbalance', query: {service: this.filter}})
              } else if (this.selected === 1) {
                this.$router.push({path: 'loadbalance', query: {application: this.filter}})
              }
            }
          })
      },
      closeDialog: function () {
        this.ruleText = this.template
        this.service = ''
        this.dialog = false
        this.updateId = ''
        this.readonly = false
      },
      openDialog: function () {
        this.dialog = true
      },
      openWarn: function (title, text) {
        this.warnTitle = title
        this.warnText = text
        this.warn = true
      },
      closeWarn: function () {
        this.warnTitle = ''
        this.warnText = ''
        this.warn = false
      },
      saveItem: function () {
        this.ruleText = this.verifyRuleText(this.ruleText)
        // let balancing = yaml.safeLoad(this.ruleText)
        let balancing = {}
        if (!this.service && !this.application) {
          this.$notify.error('Either service or application is needed')
          return
        }
        if (this.service && this.application) {
          this.$notify.error('You can not set both service ID and application name')
          return
        }
        let vm = this
        balancing.service = this.service
        balancing.application = this.application
        balancing.methodName = this.rule.method
        balancing.strategy = this.rule.strategy
        if (this.updateId) {
          if (this.updateId === 'close') {
            this.closeDialog()
          } else {
            balancing.id = this.updateId
            this.$axios.put('/rules/balancing/' + balancing.id, balancing)
              .then(response => {
                if (response.status === 200) {
                  if (vm.service) {
                    vm.selected = 0
                    vm.filter = vm.service
                    vm.search(true)
                  } else {
                    vm.selected = 1
                    vm.filter = vm.application
                    vm.search(true)
                  }
                  this.closeDialog()
                  this.$notify.success('Update success')
                }
              })
          }
        } else {
          this.$axios.post('/rules/balancing', balancing)
            .then(response => {
              if (response.status === 201) {
                if (vm.service) {
                  vm.selected = 0
                  vm.filter = vm.service
                  vm.search(true)
                } else {
                  vm.selected = 1
                  vm.filter = vm.application
                  vm.search(true)
                }
                this.closeDialog()
                this.$notify.success('Create success')
              }
            })
        }
      },
      itemOperation: function (icon, item) {
        let itemId = ''
        if (this.selected === 0) {
          itemId = item.service
        } else {
          itemId = item.application
        }
        if (itemId.includes('/')) {
          itemId = itemId.replace('/', '*')
        }
        switch (icon) {
          case 'visibility':
            this.$axios.get('/rules/balancing/' + itemId)
              .then(response => {
                let balancing = response.data
                this.handleBalance(balancing, true)
                this.updateId = 'close'
              })
            break
          case 'edit':
            this.$axios.get('/rules/balancing/' + itemId)
              .then(response => {
                let balancing = response.data
                this.handleBalance(balancing, false)
                this.updateId = itemId
              })
            break
          case 'delete':
            this.openWarn('warnDeleteBalancing', 'service: ' + itemId)
            this.warnStatus.operation = 'delete'
            this.warnStatus.id = itemId
        }
      },
      handleBalance: function (balancing, readonly) {
        this.service = balancing.service
        this.application = balancing.application
        // delete balancing.service
        // delete balancing.application
        // delete balancing.id
        // this.ruleText = yaml.safeDump(balancing)
        this.rule.method = balancing.methodName
        this.rule.strategy = balancing.strategy
        this.readonly = readonly
        this.dialog = true
      },
      setHeight: function () {
        this.height = window.innerHeight * 0.5
      },
      deleteItem: function (id) {
        this.$axios.delete('/rules/balancing/' + id)
          .then(response => {
            if (response.status === 200) {
              this.warn = false
              this.search(false)
              this.$notify.success('Delete success')
            }
          })
      },
      verifyRuleText: function (ruleText) {
        let lines = ruleText.split('\n')
        for (let i = 0; i < lines.length; i++) {
          if (lines[i].includes('methodName') && lines[i].includes('*')) {
            lines[i] = "methodName: '*'"
          }
        }
        let newText = lines.join('\n')
        return newText
      }
    },
    created () {
      this.setHeight()
    },
    computed: {
      queryBy () {
        return this.$t('by') + this.$t(this.items[this.selected].title)
      },
      area () {
        return this.$i18n.locale
      }
    },
    watch: {
      input (val) {
        this.querySelections(val)
      },
      area () {
        this.setServiceHeaders()
        this.setAppHeaders()
      }
    },
    mounted: function () {
      this.setServiceHeaders()
      this.setAppHeaders()
      this.$store.dispatch('loadServiceItems')
      this.$store.dispatch('loadAppItems')
      this.ruleText = this.template
      let query = this.$route.query
      let filter = null
      let vm = this
      Object.keys(query).forEach(function (key) {
        if (key === 'service') {
          filter = query[key]
          vm.selected = 0
        }
        if (key === 'application') {
          filter = query[key]
          vm.selected = 1
        }
      })
      if (filter !== null) {
        this.filter = filter
        this.search(false)
      }
    }

  }
</script>
