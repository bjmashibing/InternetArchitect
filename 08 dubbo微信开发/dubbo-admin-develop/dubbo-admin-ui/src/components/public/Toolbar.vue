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
  <v-toolbar
    color="primary"
    fixed
    dark
    app
  >
    <v-toolbar-side-icon @click.stop="handleDrawerToggle"></v-toolbar-side-icon>
    <v-text-field
      flat
      hide-details
      solo-inverted
      prepend-inner-icon="search"
      :label="$t('serviceSearch')"
      class="hidden-sm-and-down"
      v-model="global"
      @keyup.enter="submit"
    >
    </v-text-field>

    <v-spacer></v-spacer>

    <!--settings button-->
    <v-btn icon v-if="false">
      <v-icon>settings</v-icon>
    </v-btn>

    <!--full screen button-->
    <v-btn icon @click="handleFullScreen()">
      <v-icon>fullscreen</v-icon>
    </v-btn>

    <!--language select button-->
    <v-menu  attach bottom left offset-y max-height="500">
      <v-btn flat slot="activator" style="mini-width: 48px">
        {{selectedLang}}
      </v-btn>
      <v-list class="pa-0">
        <v-list-tile v-for="(item, index) in lang" @click="change(index)" :key="index">
          <v-list-tile-content>
            <v-list-tile-title>{{item}}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
      </v-list>
    </v-menu>

    <!--notifictation button-->
    <v-menu offset-y origin="center center" class="elelvation-1" :nudge-bottom="14" transition="scale-transition" v-if="false">
      <v-btn icon flat slot="activator">
        <v-badge color="red" overlap>
          <span slot="badge">3</span>
          <v-icon medium>notifications</v-icon>
        </v-badge>
      </v-btn>
      <!--<notification-list></notification-list>-->
    </v-menu>

    <!--login user info-->
    <v-menu offset-y origin="center center" :nudge-bottom="10" transition="scale-transition" v-if="false">
      <v-btn icon large flat slot="activator">
        <v-avatar size="30px">
          <img src="@/assets/avatar.png" alt="Logined User" />
        </v-avatar>
      </v-btn>
      <v-list class="pa-0">
        <v-list-tile v-for="(item,index) in items" :to="!item.href ? { name: item.name } : null" :href="item.href" @click="item.click" ripple="ripple" :disabled="item.disabled" :target="item.target" rel="noopener" :key="index">
          <v-list-tile-action v-if="item.icon">
            <v-icon>{{ item.icon }}</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>{{ item.title }}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
      </v-list>
    </v-menu>
  </v-toolbar>
</template>
<script>
  import Util from '@/util'
  export default {
    name: 'toolbar',
    data: () => ({
      selectedLang: '',
      global: '',
      lang: [
        '简体中文',
        'English'
      ],
      items: [
        {
          icon: 'account_circle',
          href: '#',
          title: 'Profile',
          click: (e) => {
            console.log(e)
          }
        },
        {
          icon: 'settings',
          href: '#',
          title: 'Settings',
          click: (e) => {
            console.log(e)
          }
        },
        {
          icon: 'fullscreen_exit',
          href: '#',
          title: 'Logout',
          click: (e) => {
            window.getApp.$emit('APP_LOGOUT')
          }
        }
      ]
    }),
    methods: {
      submit () {
        if (window.location.href.includes('#/service')) {
          window.location.href = '#/service?filter=' + this.global + '&pattern=service'
          window.location.reload()
        } else {
          window.location.href = '#/service?filter=' + this.global + '&pattern=service'
        }
        this.global = ''
      },
      handleDrawerToggle () {
        window.getApp.$emit('DRAWER_TOGGLED')
      },
      change (index) {
        this.selectedLang = this.lang[index]
        if (index === 0) {
          this.$i18n.locale = 'zh'
        } else {
          this.$i18n.locale = 'en'
        }
        this.$store.dispatch('changeArea', {area: this.$i18n.locale})
        window.localStorage.setItem('locale', this.$i18n.locale)
        window.localStorage.setItem('selectedLang', this.selectedLang)
      },
      handleTheme () {
        window.getApp.$emit('CHANGE_THEME')
      },
      handleFullScreen () {
        Util.toggleFullScreen()
      }
    },
    mounted: function () {
      if (this.$i18n.locale === 'zh') {
        this.selectedLang = '简体中文'
      } else {
        this.selectedLang = 'English'
      }
    }
  }
</script>
