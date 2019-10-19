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
      <v-flex xs12 >
        <search id="serviceSearch" v-model="filter" :submit="submit" :label="$t('searchDubboConfig')" :hint="$t('configNameHint')"></search>
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
            :items="dubboConfig"
            hide-actions
            class="elevation-0"
          >
            <template slot="items" slot-scope="props">
              <td class="text-xs-left">
                <v-tooltip bottom >
                   <span slot="activator">
                     {{props.item.key}}
                   </span>
                  <span>{{props.item.path}}</span>
                </v-tooltip>
              </td>
              <td class="text-xs-left">
                <v-chip
                  :color="getColor(props.item.scope)"
                  text-color="white">
                  {{props.item.scope}}
                </v-chip>
              </td>
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
          <span class="headline">{{$t('createNewDubboConfig')}}</span>
        </v-card-title>
        <v-card-text >
          <v-text-field
            :label="$t('appName')"
            :hint="$t('configNameHint')"
            v-model="key"
          ></v-text-field>

          <v-subheader class="pa-0 mt-3">{{$t('configContent')}}</v-subheader>
          <ace-editor lang="properties" v-model="rule" :readonly="readonly"></ace-editor>

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
        <v-card-title class="headline">{{$t(this.warn.title) + this.warnStatus.id}}</v-card-title>
        <v-card-text >{{this.warn.text}}</v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn flat @click.native="closeWarn">{{$t('cancel')}}</v-btn>
          <v-btn depressed color="primary" @click.native="deleteItem(warnStatus)">{{$t('confirm')}}</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

  </v-container>
</template>

<script>
    import AceEditor from '@/components/public/AceEditor'
    import Search from '@/components/public/Search'
    export default {
      name: 'Management',
      components: {
        AceEditor,
        Search
      },
      data: () => ({
        configCenter: '',
        rule: '',
        updateId: '',
        key: '',
        filter: '',
        readonly: false,
        dialog: false,
        operations: [
          {id: 0, icon: 'visibility', tooltip: 'view'},
          {id: 1, icon: 'edit', tooltip: 'edit'},
          {id: 3, icon: 'delete', tooltip: 'delete'}
        ],
        warn: {
          display: false,
          title: '',
          text: '',
          status: {}
        },
        warnStatus: {},
        dubboConfig: [],
        headers: []
      }),
      methods: {
        setHeaders () {
          this.headers = [
            {
              text: this.$t('name'),
              value: 'name',
              align: 'left'
            },
            {
              text: this.$t('scope'),
              value: 'scope',
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
        itemOperation (icon, item) {
          switch (icon) {
            case 'visibility':
              this.dialog = true
              this.rule = item.config
              this.key = item.key
              this.readonly = true
              this.updateId = 'close'
              break
            case 'edit':
              this.dialog = true
              this.rule = item.config
              this.key = item.key
              this.updateId = item.key
              this.readonly = false
              break
            case 'delete':
              this.openWarn('warnDeleteConfig')
              this.warnStatus.id = item.key
          }
        },
        deleteItem: function (warnStatus) {
          this.$axios.delete('/manage/config/' + warnStatus.id)
            .then(response => {
              if (response.status === 200) {
                this.warn.display = false
                this.search(this.filter)
                this.$notify.success('Delete success')
              }
            })
        },
        closeDialog: function () {
          this.rule = ''
          this.key = ''
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
          let configDTO = {}
          if (!this.key) {
            this.$notify.error('Config key is needed')
            return
          }
          configDTO.key = this.key
          configDTO.config = this.rule
          let vm = this
          if (this.updateId) {
            if (this.updateId === 'close') {
              this.closeDialog()
            } else {
              this.$axios.put('/manage/config/' + this.updateId, configDTO)
                .then(response => {
                  if (response.status === 200) {
                    vm.search(vm.key)
                    vm.filter = vm.key
                    this.closeDialog()
                    this.$notify.success('Update success')
                  }
                })
            }
          } else {
            this.$axios.post('/manage/config/', configDTO)
              .then(response => {
                if (response.status === 201) {
                  vm.search(vm.key)
                  vm.filter = vm.key
                  vm.closeDialog()
                  vm.$notify.success('Create success')
                }
              })
          }
        },
        getColor (scope) {
          if (scope === 'global') {
            return 'red'
          }
          if (scope === 'application') {
            return 'green'
          }
          if (scope === 'service') {
            return 'blue'
          }
        },
        submit () {
          if (!this.filter) {
            this.$notify.error('application is needed')
            return
          }
          this.filter = this.filter.trim()
          this.search()
        },
        search () {
          this.$axios.get('/manage/config/' + this.filter)
            .then(response => {
              if (response.status === 200) {
                this.dubboConfig = response.data
                this.$router.push({path: 'management', query: {key: this.filter}})
              }
            })
        }
      },
      mounted () {
        this.setHeaders()
        let query = this.$route.query
        let filter = null
        Object.keys(query).forEach(function (key) {
          if (key === 'key') {
            filter = query[key]
          }
        })
        if (filter !== null) {
          this.filter = filter
        } else {
          this.filter = 'global'
        }
        this.search()
      }
    }
</script>

<style scoped>

</style>
