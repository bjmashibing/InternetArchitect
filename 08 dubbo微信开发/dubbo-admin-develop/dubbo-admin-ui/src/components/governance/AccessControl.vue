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
        <breadcrumb title="accessControl" :items="breads"></breadcrumb>
      </v-flex>
      <v-flex lg12>
        <v-card flat color="transparent">
          <v-card-text>
            <v-form>
              <v-layout row wrap>
                <v-combobox
                  id="serviceSearch"
                  v-model="filter"
                  :loading="searchLoading"
                  :items="typeAhead"
                  :search-input.sync="input"
                  @keyup.enter="submit"
                  flat
                  append-icon=""
                  hide-no-data
                  :suffix="queryBy"
                  :label="$t('searchAccessRule')"
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
        <v-toolbar flat
                   color="transparent"
                   class="elevation-0">
          <v-toolbar-title>
            <span class="headline">{{$t('searchResult')}}</span>
          </v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn outline
                 color="primary"
                 @click.stop="toCreate"
                 class="mb-2">{{$t('create')}}</v-btn>
        </v-toolbar>

        <v-card-text class="pa-0" v-show="selected == 0">
          <v-data-table :headers="serviceHeaders"
                        :items="accesses"
                        :loading="loading"
                        hide-actions
                        class="elevation-0">
            <template slot="items"
                      slot-scope="props">
              <td class="text-xs-left">{{ props.item.service }}</td>
              <td class="text-xs-center px-0">
                <v-tooltip bottom>
                  <v-icon small
                          class="mr-2"
                          color="blue"
                          slot="activator"
                          @click="toEdit(props.item, true)">visibility</v-icon>
                  <span>{{$t('view')}}</span>
                </v-tooltip>
                <v-tooltip bottom>
                  <v-icon small
                          class="mr-2"
                          color="blue"
                          slot="activator"
                          @click="toEdit(props.item, false)">edit</v-icon>
                  <span>{{$t('edit')}}</span>
                </v-tooltip>
                <v-tooltip bottom>
                  <v-icon small
                          class="mr-2"
                          slot="activator"
                          color="red"
                          @click="toDelete(props.item)">delete</v-icon>
                  <span>{{$t('delete')}}</span>
                </v-tooltip>
              </td>
            </template>
          </v-data-table>
        </v-card-text>
        <v-card-text class="pa-0" v-show="selected == 1">
          <v-data-table :headers="appHeaders"
                        :items="accesses"
                        :loading="loading"
                        hide-actions
                        class="elevation-0">
            <template slot="items"
                      slot-scope="props">
              <td class="text-xs-left">{{ props.item.application }}</td>
              <td class="text-xs-center px-0">
                <v-tooltip bottom>
                  <v-icon small
                          class="mr-2"
                          color="blue"
                          slot="activator"
                          @click="toEdit(props.item, true)">visibility</v-icon>
                  <span>{{$t('view')}}</span>
                </v-tooltip>
                <v-tooltip bottom>
                  <v-icon small
                          class="mr-2"
                          color="blue"
                          slot="activator"
                          @click="toEdit(props.item, false)">edit</v-icon>
                  <span>Edit</span>
                </v-tooltip>
                <v-tooltip bottom>
                  <v-icon small
                          class="mr-2"
                          slot="activator"
                          color="red"
                          @click="toDelete(props.item)">delete</v-icon>
                  <span>Delete</span>
                </v-tooltip>
              </td>
            </template>
          </v-data-table>
        </v-card-text>
      </v-card>
    </v-flex>

    <v-dialog v-model="modal.enable"
              width="800px"
              persistent>
      <v-card>
        <v-card-title class="justify-center">
          <span class="headline">{{ modal.title }} Access Control</span>
        </v-card-title>
        <v-card-text>
          <v-form ref="modalForm">
            <v-text-field
              label="Service Unique ID"
              :hint="$t('dataIdHint')"
              :readonly="modal.readonly"
              v-model="modal.service"
            ></v-text-field>
            <v-text-field
              :label="$t('appName')"
              :hint="$t('appNameHint')"
              :readonly="modal.readonly"
              v-model="modal.application"
            ></v-text-field>
            <v-layout row justify-space-between>
              <v-flex >
                <v-text-field
                  :readonly="modal.readonly"
                  :label="$t('whiteList')"
                  v-model="modal.whiteList"
                  :hint="$t('whiteListHint')">
                </v-text-field>
              </v-flex>
              <v-spacer></v-spacer>
              <v-flex>
                <v-text-field
                  :label="$t('blackList')"
                  :hint="$t('blackListHint')"
                  v-model="modal.blackList"
                  :readonly="modal.id != null">
                </v-text-field>
              </v-flex>
            </v-layout>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="darken-1"
                 flat
                 @click="closeModal()">{{$t('close')}}</v-btn>
          <v-btn color="primary"
                 depressed
                 @click="modal.click">{{ modal.saveBtn }}</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="confirm.enable"
              persistent
              max-width="500px">
      <v-card>
        <v-card-title class="headline">{{this.confirm.title}}</v-card-title>
        <v-card-text>{{this.confirm.text}}</v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="darken-1"
                 flat
                 @click="confirm.enable = false">{{$t('disagree')}}</v-btn>
          <v-btn color="primary"
                 depressed
                 @click="deleteItem(confirm.id)">{{$t('agree')}}</v-btn>
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
  name: 'AccessControl',
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
        text: 'accessControl',
        href: ''
      }
    ],
    selected: 0,
    filter: null,
    loading: false,
    serviceHeaders: [],
    appHeaders: [],
    accesses: [],
    searchLoading: false,
    typeAhead: [],
    input: null,
    timerID: null,
    modal: {
      enable: false,
      readonly: false,
      title: 'Create New',
      saveBtn: 'Create',
      click: () => {},
      id: null,
      service: null,
      application: null,
      content: '',
      blackList: '',
      whiteList: '',
      template:
        'blacklist:\n' +
        '  - 1.1.1.1\n' +
        '  - 2.2.2.2\n' +
        'whitelist:\n' +
        '  - 3.3.3.3\n' +
        '  - 4.4.*\n'
    },
    services: [],
    confirm: {
      enable: false,
      title: '',
      text: '',
      id: null
    },
    snackbar: {
      enable: false,
      text: ''
    }
  }),
  methods: {
    setAppHeaders () {
      this.appHeaders = [
        {
          text: this.$t('appName'),
          value: 'application',
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
    setServiceHeaders () {
      this.serviceHeaders = [
        {
          text: this.$t('serviceName'),
          value: 'service',
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
    search (rewrite) {
      if (!this.filter) {
        this.$notify.error('Either service or application is needed')
        return
      }
      let type = this.items[this.selected].value
      this.loading = true
      if (rewrite) {
        if (this.selected === 0) {
          this.$router.push({
            path: 'access',
            query: {service: this.filter}
          })
        } else if (this.selected === 1) {
          this.$router.push({
            path: 'access',
            query: {application: this.filter}
          })
        }
      }
      let url = '/rules/access/?' + type + '=' + this.filter
      this.$axios.get(url)
        .then(response => {
          this.accesses = response.data
          this.loading = false
        }).catch(error => {
          this.showSnackbar('error', error.response.data.message)
          this.loading = false
        })
    },
    closeModal () {
      this.modal.enable = false
      this.modal.id = null
      this.$refs.modalForm.reset()
    },
    toCreate () {
      Object.assign(this.modal, {
        enable: true,
        title: 'Create New',
        saveBtn: 'Create',
        readonly: false,
        content: this.modal.template,
        click: this.createItem
      })
    },
    createItem () {
      // let doc = yaml.load(this.modal.content)
      this.filter = ''
      if (!this.modal.service && !this.modal.application) {
        this.$notify.error('Either service or application is needed')
        return
      }
      if (this.modal.service && this.modal.application) {
        this.$notify.error('You can not set both service ID and application name')
        return
      }
      let vm = this
      let blackList = []
      let whiteList = []
      if (this.modal.blackList) {
        blackList = this.modal.blackList.split(',')
      }
      if (this.modal.whiteList) {
        whiteList = this.modal.whiteList.split(',')
      }
      this.$axios.post('/rules/access', {
        service: this.modal.service,
        application: this.modal.application,
        whitelist: whiteList,
        blacklist: blackList
      }).then(response => {
        if (response.status === 201) {
          if (vm.modal.service) {
            vm.selected = 0
            vm.filter = vm.modal.service
          } else {
            vm.selected = 1
            vm.filter = vm.modal.application
          }
          this.search()
          this.closeModal()
        }
        this.showSnackbar('success', 'Create success')
      }).catch(error => this.showSnackbar('error', error.response.data.message))
    },
    toEdit (item, readonly) {
      let itemId = null
      if (this.selected === 0) {
        itemId = item.service
      } else {
        itemId = item.application
      }
      if (itemId.includes('/')) {
        itemId = itemId.replace('/', '*')
      }
      Object.assign(this.modal, {
        enable: true,
        readonly: readonly,
        title: 'Edit',
        saveBtn: 'Update',
        click: this.editItem,
        id: itemId,
        service: item.service,
        application: item.application,
        whiteList: item.whitelist,
        blackList: item.blacklist
        // content: yaml.safeDump({blacklist: item.blacklist, whitelist: item.whitelist})
      })
    },
    editItem () {
      // let doc = yaml.load(this.modal.content)
      let blackList = this.modal.blackList.split(',')
      let whiteList = this.modal.whiteList.split(',')
      let vm = this
      this.$axios.put('/rules/access/' + this.modal.id, {
        whitelist: whiteList,
        blacklist: blackList,
        application: this.modal.application,
        service: this.modal.service

      }).then(response => {
        if (response.status === 200) {
          if (vm.modal.service) {
            vm.selected = 0
            vm.filter = vm.modal.service
          } else {
            vm.selected = 1
            vm.filter = vm.modal.application
          }
          vm.closeModal()
          vm.search()
        }
        this.showSnackbar('success', 'Update success')
      }).catch(error => this.showSnackbar('error', error.response.data.message))
    },
    toDelete (item) {
      let itemId = null
      if (this.selected === 0) {
        itemId = item.service
      } else {
        itemId = item.application
      }
      if (itemId.includes('/')) {
        itemId = itemId.replace('/', '*')
      }
      Object.assign(this.confirm, {
        enable: true,
        title: 'warnDeleteAccessControl',
        text: `Id: ${itemId}`,
        id: itemId
      })
    },
    deleteItem (id) {
      this.$axios.delete('/rules/access/' + id)
      .then(response => {
        this.showSnackbar('success', 'Delete success')
        this.search(this.filter)
      }).catch(error => this.showSnackbar('error', error.response.data.message))
    },
    showSnackbar (color, message) {
      this.$notify(message, color)
      this.confirm.enable = false
    }
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
      this.setAppHeaders()
      this.setServiceHeaders()
    }
  },
  mounted () {
    this.setAppHeaders()
    this.setServiceHeaders()
    this.$store.dispatch('loadServiceItems')
    this.$store.dispatch('loadAppItems')
    let query = this.$route.query
    if ('service' in query) {
      this.filter = query['service']
      this.selected = 0
    }
    if ('application' in query) {
      this.filter = query['application']
      this.selected = 1
    }
    if (this.filter !== null) {
      this.search()
    }
  },
  components: {
    Breadcrumb,
    AceEditor,
    Search
  }
}
</script>
