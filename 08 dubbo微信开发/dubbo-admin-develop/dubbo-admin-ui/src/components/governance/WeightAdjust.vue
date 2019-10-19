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
        <breadcrumb title="weightAdjust" :items="breads"></breadcrumb>
      </v-flex>
      <v-flex lg12>
        <v-card flat color="transparent">
          <v-card-text>
            <v-form>
              <v-layout row wrap>
                <v-combobox
                  id="serviceSearch"
                  v-model="filter"
                  :items="typeAhead"
                  :search-input.sync="input"
                  :loading="searchLoading"
                  @keyup.enter="submit"
                  flat
                  append-icon=""
                  hide-no-data
                  :suffix="queryBy"
                  :label="$t('searchWeightRule')"
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
            :items="weights"
            hide-actions
            class="elevation-0"
          >
            <template slot="items" slot-scope="props">
              <td class="text-xs-left">{{ props.item.service }}</td>
              <td class="text-xs-left">{{ props.item.weight }}</td>
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
            :items="weights"
            hide-actions
            class="elevation-0"
          >
            <template slot="items" slot-scope="props">
              <td class="text-xs-left">{{ props.item.application }}</td>
              <td class="text-xs-left">{{ props.item.weight }}</td>
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
          <span class="headline">{{$t('createNewWeightRule')}}</span>
        </v-card-title>
        <v-card-text >
          <v-text-field
            label="Service Unique ID"
            :hint="$t('serviceIdHint')"
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
                :label="$t('weight')"
                :hint="$t('weightHint')"
                v-model="rule.weight"
                :readonly="readonly"
              ></v-text-field>
            </v-flex>
            <v-spacer></v-spacer>
            <v-flex>
              <v-text-field
                :label="$t('address')"
                :hint="$t('weightAddressHint')"
                v-model="rule.address"
                :readonly="readonly"
              ></v-text-field>
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
        <v-card-title class="headline">{{this.warnTitle}}</v-card-title>
        <v-card-text >{{this.warnText}}</v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="darken-1" flat @click.native="closeWarn">{{$t('cancel')}}</v-btn>
          <v-btn color="primary darken-1" depressed @click.native="deleteItem(warnStatus)">{{$t('confirm')}}</v-btn>
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
          text: 'weightAdjust',
          href: ''
        }
      ],
      selected: 0,
      dropdown_font: [ 'Service', 'App', 'IP' ],
      ruleKeys: ['weight', 'address'],
      pattern: 'Service',
      filter: '',
      dialog: false,
      warn: false,
      application: '',
      updateId: '',
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
      weights: [
      ],
      template:
        'weight: 100  # 100 for default\n' +
        'addresses:   # addresses\'s ip\n' +
        '  - 192.168.0.1\n' +
        '  - 192.168.0.2',
      ruleText: '',
      rule: {
        weight: 100,
        address: ''
      },
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
            text: this.$t('weight'),
            value: 'weight',
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
      setAppHeaders: function () {
        this.appHeaders = [
          {
            text: this.$t('appName'),
            value: 'application',
            align: 'left'
          },
          {
            text: this.$t('weight'),
            value: 'weight',
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
        let url = '/rules/weight/?' + type + '=' + this.filter
        this.$axios.get(url)
          .then(response => {
            this.weights = response.data
            if (rewrite) {
              if (this.selected === 0) {
                this.$router.push({path: 'weight', query: {service: this.filter}})
              } else if (this.selected === 1) {
                this.$router.push({path: 'weight', query: {application: this.filter}})
              }
            }
          })
      },
      closeDialog: function () {
        this.ruleText = this.template
        this.rule.address = ''
        this.rule.weight = 100
        this.service = ''
        this.dialog = false
        this.readonly = false
      },
      openDialog: function () {
        this.updateId = ''
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
        let weight = {}
        // let weight = yaml.safeLoad(this.ruleText)
        if (!this.service && !this.application) {
          this.$notify.error('Either service or application is needed')
          return
        }
        if (this.service && this.application) {
          this.$notify.error('You can not set both service ID and application name')
          return
        }
        weight.service = this.service
        weight.application = this.application
        weight.weight = this.rule.weight
        weight.addresses = this.rule.address.split(',')
        let vm = this
        if (this.updateId) {
          if (this.updateId === 'close') {
            this.closeDialog()
          } else {
            weight.id = this.updateId
            this.$axios.put('/rules/weight/' + weight.id, weight)
              .then(response => {
                if (response.status === 200) {
                  if (vm.service) {
                    vm.selected = 0
                    vm.search(vm.service, true)
                    vm.filter = vm.service
                  } else {
                    vm.selected = 1
                    vm.search(vm.application, true)
                    vm.filter = vm.application
                  }
                  this.closeDialog()
                  this.$notify.success('Update success')
                }
              })
          }
        } else {
          this.$axios.post('/rules/weight', weight)
            .then(response => {
              if (response.status === 201) {
                if (this.service) {
                  vm.selected = 0
                  vm.search(vm.service, true)
                  vm.filter = vm.service
                } else {
                  vm.selected = 1
                  vm.search(vm.application, true)
                  vm.filter = vm.application
                }
                vm.closeDialog()
                vm.$notify.success('Create success')
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
            this.$axios.get('/rules/weight/' + itemId)
                .then(response => {
                  let weight = response.data
                  this.handleWeight(weight, true)
                  this.updateId = 'close'
                })
            break
          case 'edit':
            this.$axios.get('/rules/weight/' + itemId)
                .then(response => {
                  let weight = response.data
                  this.handleWeight(weight, false)
                  this.updateId = itemId
                })
            break
          case 'delete':
            this.openWarn('warnDeleteWeightAdjust', 'service: ' + itemId)
            this.warnStatus.operation = 'delete'
            this.warnStatus.id = itemId
        }
      },
      handleWeight: function (weight, readonly) {
        this.service = weight.service
        this.application = weight.application
        // delete weight.service
        // delete weight.application
        // this.ruleText = yaml.safeDump(weight)
        this.rule.weight = weight.weight
        this.rule.address = weight.addresses.join(',')
        this.readonly = readonly
        this.dialog = true
      },
      setHeight: function () {
        this.height = window.innerHeight * 0.5
      },
      deleteItem: function (warnStatus) {
        this.$axios.delete('/rules/weight/' + warnStatus.id)
          .then(response => {
            if (response.status === 200) {
              this.warn = false
              this.search(this.filter, false)
              this.$notify.success('Delete success')
            }
          })
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
      area () {
        this.setAppHeaders()
        this.setServiceHeaders()
      },
      input (val) {
        this.querySelections(val)
      }
    },
    mounted: function () {
      this.setAppHeaders()
      this.setServiceHeaders()
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

<style scoped>

</style>
