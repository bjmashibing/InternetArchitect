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
        <breadcrumb title="tagRule" :items="breads"></breadcrumb>
      </v-flex>
      <v-flex xs12 >
        <search id="serviceSearch" v-model="filter" :submit="submit" :label="$t('searchTagRule')"></search>
      </v-flex>
    </v-layout>
    <v-flex lg12>
      <v-card>
        <v-toolbar flat color="transparent" class="elevation-0">
          <v-toolbar-title><span class="headline">{{$t('searchResult')}}</span></v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn outline color="primary" @click.stop="openDialog" class="mb-2">{{$t('create')}}</v-btn>
        </v-toolbar>

        <v-card-text class="pa-0" >
          <v-data-table
            :headers="headers"
            :items="tagRoutingRules"
            hide-actions
            class="elevation-0"
          >
            <template slot="items" slot-scope="props">
              <td class="text-xs-left">{{ props.item.application }}</td>
              <td class="text-xs-left">{{ props.item.enabled }}</td>
              <td class="text-xs-center px-0">
                <v-tooltip bottom v-for="op in operations" :key="op.id">
                  <v-icon small class="mr-2" slot="activator" @click="itemOperation(op.icon(props.item), props.item)">
                    {{op.icon(props.item)}}
                  </v-icon>
                  <span>{{$t(op.tooltip(props.item))}}</span>
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
          <span class="headline">{{$t('createNewTagRule')}}</span>
        </v-card-title>
        <v-card-text >
          <v-text-field
            :label="$t('appName')"
            :hint="$t('appNameHint')"
            v-model="application"
          ></v-text-field>

          <v-subheader class="pa-0 mt-3">{{$t('ruleContent')}}</v-subheader>
          <ace-editor v-model="ruleText" :readonly="readonly"></ace-editor>

        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn flat @click.native="closeDialog">{{$t('close')}}</v-btn>
          <v-btn depressed color="primary" @click.native="saveItem">{{$t('save')}}</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="warn.display" persistent max-width="500px">
      <v-card>
        <v-card-title class="headline">{{$t(this.warn.title)}}</v-card-title>
        <v-card-text >{{this.warn.text}}</v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn flat @click.native="closeWarn">CANCLE</v-btn>
          <v-btn depressed color="primary" @click.native="deleteItem(warn.status)">{{$t('confirm')}}</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

  </v-container>

</template>
<script>
  import yaml from 'js-yaml'
  import AceEditor from '@/components/public/AceEditor'
  import operations from '@/api/operation'
  import Search from '@/components/public/Search'
  import Breadcrumb from '@/components/public/Breadcrumb'

  export default {
    components: {
      AceEditor,
      Search,
      Breadcrumb
    },
    data: () => ({
      dropdown_font: [ 'Service', 'App', 'IP' ],
      ruleKeys: ['enabled', 'force', 'dynamic', 'runtime', 'group', 'version', 'rule'],
      pattern: 'Service',
      filter: '',
      dialog: false,
      updateId: '',
      application: '',
      warn: {
        display: false,
        title: '',
        text: '',
        status: {}
      },
      breads: [
        {
          text: 'serviceGovernance',
          href: ''
        },
        {
          text: 'tagRule',
          href: ''
        }
      ],
      height: 0,
      operations: operations,
      tagRoutingRules: [
      ],
      template:
        'force: false\n' +
        'enabled: true\n' +
        'runtime: false\n' +
        'tags:\n' +
        ' - name: tag1\n' +
        '   addresses: [192.168.0.1:20881]\n' +
        ' - name: tag2\n' +
        '   addresses: [192.168.0.2:20882]\n',
      ruleText: '',
      readonly: false,
      headers: []
    }),
    methods: {
      setHeaders: function () {
        this.headers = [
          {
            text: this.$t('appName'),
            value: 'application',
            align: 'left'
          },
          {
            text: this.$t('enabled'),
            value: 'enabled',
            sortable: false
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
        if (!this.filter) {
          this.$notify.error('application is needed')
          return
        }
        this.filter = this.filter.trim()
        this.search(true)
      },
      search: function (rewrite) {
        let url = '/rules/route/tag/?application' + '=' + this.filter
        this.$axios.get(url)
          .then(response => {
            this.tagRoutingRules = response.data
            if (rewrite) {
              this.$router.push({path: 'tagRule', query: {application: this.filter}})
            }
          })
      },
      closeDialog: function () {
        this.ruleText = this.template
        this.updateId = ''
        this.application = ''
        this.dialog = false
        this.readonly = false
      },
      openDialog: function () {
        this.dialog = true
      },
      openWarn: function (title, text) {
        this.warn.title = title
        this.warn.text = text
        this.warn.display = true
      },
      closeWarn: function () {
        this.warn.title = ''
        this.warn.text = ''
        this.warn.display = false
      },
      saveItem: function () {
        let rule = yaml.safeLoad(this.ruleText)
        if (!this.application) {
          this.$notify.error('application is required')
          return
        }
        rule.application = this.application
        let vm = this
        if (this.updateId) {
          if (this.updateId === 'close') {
            this.closeDialog()
          } else {
            rule.id = this.updateId
            this.$axios.put('/rules/route/tag/' + rule.id, rule)
              .then(response => {
                if (response.status === 200) {
                  vm.search(vm.application, true)
                  vm.closeDialog()
                  vm.$notify.success('Update success')
                }
              })
          }
        } else {
          this.$axios.post('/rules/route/tag/', rule)
            .then(response => {
              if (response.status === 201) {
                vm.search(vm.application, true)
                vm.filter = vm.application
                vm.closeDialog()
                vm.$notify.success('Create success')
              }
            })
            .catch(error => {
              console.log(error)
            })
        }
      },
      itemOperation: function (icon, item) {
        let itemId = item.application
        switch (icon) {
          case 'visibility':
            this.$axios.get('/rules/route/tag/' + itemId)
              .then(response => {
                let tagRoute = response.data
                this.handleBalance(tagRoute, true)
                this.updateId = 'close'
              })
            break
          case 'edit':
            let id = {}
            id.id = itemId
            this.$axios.get('/rules/route/tag/' + itemId)
              .then(response => {
                let conditionRoute = response.data
                this.handleBalance(conditionRoute, false)
                this.updateId = itemId
              })
            break
          case 'block':
            this.openWarn(' Are you sure to block Tag Rule', 'application: ' + item.application)
            this.warn.status.operation = 'disable'
            this.warn.status.id = itemId
            break
          case 'check_circle_outline':
            this.openWarn(' Are you sure to enable Tag Rule', 'application: ' + item.application)
            this.warn.status.operation = 'enable'
            this.warn.status.id = itemId
            break
          case 'delete':
            this.openWarn('warnDeleteTagRule', 'application: ' + item.application)
            this.warn.status.operation = 'delete'
            this.warn.status.id = itemId
        }
      },
      handleBalance: function (tagRoute, readonly) {
        this.application = tagRoute.application
        delete tagRoute.id
        delete tagRoute.app
        delete tagRoute.group
        delete tagRoute.application
        delete tagRoute.service
        delete tagRoute.priority
        this.ruleText = yaml.safeDump(tagRoute)
        this.readonly = readonly
        this.dialog = true
      },
      setHeight: function () {
        this.height = window.innerHeight * 0.5
      },
      deleteItem: function (warnStatus) {
        let id = warnStatus.id
        let operation = warnStatus.operation
        if (operation === 'delete') {
          this.$axios.delete('/rules/route/tag/' + id)
            .then(response => {
              if (response.status === 200) {
                this.warn.display = false
                this.search(this.filter, false)
                this.$notify.success('Delete success')
              }
            })
        } else if (operation === 'disable') {
          this.$axios.put('/rules/route/tag/disable/' + id)
            .then(response => {
              if (response.status === 200) {
                this.warn.display = false
                this.search(this.filter, false)
                this.$notify.success('Disable success')
              }
            })
        } else if (operation === 'enable') {
          this.$axios.put('/rules/route/tag/enable/' + id)
            .then(response => {
              if (response.status === 200) {
                this.warn.display = false
                this.search(this.filter, false)
                this.$notify.success('Enable success')
              }
            })
        }
      }
    },
    created () {
      this.setHeight()
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
      this.ruleText = this.template
      let query = this.$route.query
      let filter = null
      Object.keys(query).forEach(function (key) {
        if (key === 'application') {
          filter = query[key]
        }
      })
      if (filter !== null) {
        this.filter = filter
        this.search(false)
      }
    }

  }
</script>
