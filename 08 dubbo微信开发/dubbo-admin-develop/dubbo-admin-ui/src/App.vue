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
  <v-app :dark="dark">
    <drawer></drawer>
    <toolbar></toolbar>
    <v-content>
      <router-view/>
    </v-content>
    <footers></footers>
  </v-app>
</template>
<script>
  import Drawer from '@/components/public/Drawer'
  import Toolbar from '@/components/public/Toolbar'
  import Footers from '@/components/public/Footers'

  export default {
    components: {
      Drawer,
      Toolbar,
      Footers
    },
    data () {
      return {
        dark: false
      }
    },
    created () {
      window.getApp = this
    },
    name: 'App'
  }
</script>
