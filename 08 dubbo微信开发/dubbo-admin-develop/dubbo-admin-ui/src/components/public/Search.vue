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
  <v-card flat color="transparent">
    <v-card-text>
      <v-layout row wrap >
        <v-text-field
          :label="label" clearable
          :hint="hint"
          v-bind:value="value"
          v-on:input="$emit('input', $event)"
          @keydown.enter="submit"
        ></v-text-field>
        <v-btn @click="submit" color="primary" large>{{$t('search')}}</v-btn>
      </v-layout>
    </v-card-text>
  </v-card>
</template>

<script>
  export default {
    name: 'search',
    props: {
      value: String,
      submit: {
        type: Function,
        default: null
      },
      label: {
        type: String,
        default: ''
      },
      hint: {
        type: String,
        default: ''
      }
    },
    data: () => ({
    }),
    methods: {
    }

  }
</script>

