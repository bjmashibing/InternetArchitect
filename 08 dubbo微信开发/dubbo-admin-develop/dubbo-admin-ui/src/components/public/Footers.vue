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
  <v-footer inset height="auto" class="pa-3 footer-border-top">
    <v-spacer></v-spacer>
    <span class="caption mr-1"><strong>Copyright</strong> &copy;2018-2019 <strong>The Apache Software Foundation.</strong></span>
  </v-footer>
</template>
<script>
  export default {
    name: 'footers'
  }
</script>

<style>
  .footer-border-top {
    border-top: solid 1px rgba(0,0,0,.12);
  }
</style>

