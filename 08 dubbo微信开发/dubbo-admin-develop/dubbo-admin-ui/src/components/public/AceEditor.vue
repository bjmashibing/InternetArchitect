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

<!--
Usage:
  <template>
    <ace-editor v-model="content" width="100%" height="200px" lang="javascript" :theme="theme" />
  </template>
  <script>
  import AceEditor from '@/components/AceEditor'
  export default {
    components: {
      AceEditor
    },
    data: () => ({
      content: 'console.log("Hello Ace Editor!")',
      theme: 'dawn'
    })
  }
</script>
-->
<template>
  <div :style="{height: height, width: width}"></div>
</template>

<script>
import brace from 'brace'

export default {
  name: 'ace-editor',
  props: {
    value: String,
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '300px'
    },
    lang: {
      type: String,
      default: 'yaml'
    },
    theme: {
      type: String,
      default: 'monokai'
    },
    readonly: {
      type: Boolean,
      default: false
    },
    fontsize: {
      type: Number,
      default: 14
    },
    tabsize: {
      type: Number,
      default: 2
    },
    overrideValueHistory: {
      type: Boolean,
      default: true
    }
  },
  data () {
    return {
      $ace: null,
      _content: ''
    }
  },
  watch: {
    value (newVal, oldVal) {
      if (newVal !== oldVal) {
        if (this._content !== newVal) {
          this._content = newVal
          if (this.overrideValueHistory) {
            this.$ace.getSession().setValue(newVal)
          } else {
            this.$ace.setValue(newVal, 1)
          }
        }
      }
    },
    lang (newVal, oldVal) {
      if (newVal !== oldVal && newVal) {
        require(`brace/mode/${newVal}`)
        this.$ace.getSession().setMode(`ace/mode/${newVal}`)
      }
    },
    theme (newVal, oldVal) {
      if (newVal !== oldVal && newVal) {
        require(`brace/theme/${newVal}`)
        this.$ace.setTheme(`ace/theme/${newVal}`)
      }
    },
    readonly (newVal, oldVal) {
      if (newVal !== oldVal) {
        this.$ace.setReadOnly(newVal)
      }
    },
    fontsize (newVal, oldVal) {
      if (newVal !== oldVal) {
        this.$ace.setFontSize(newVal)
      }
    }
  },
  mounted () {
    this.$ace = brace.edit(this.$el)
    this.$ace.$blockScrolling = Infinity
    let {
        lang,
        theme,
        readonly,
        fontsize,
        tabsize,
        overrideValueHistory
      } = this

    this.$emit('init', this.$ace)

    let session = this.$ace.getSession()

    require(`brace/mode/${lang}`)
    session.setMode(`ace/mode/${lang}`)
    session.setTabSize(tabsize)
    session.setUseSoftTabs(true)
    session.setUseWrapMode(true)

    if (overrideValueHistory) {
      session.setValue(this.value)
    } else {
      this.$ace.setValue(this.value, 1)
    }

    require(`brace/theme/${theme}`)
    this.$ace.setTheme(`ace/theme/${theme}`)
    this.$ace.setReadOnly(readonly)
    this.$ace.setFontSize(fontsize)
    this.$ace.setShowPrintMargin(false)

    this.$ace.on('change', () => {
      var aceValue = this.$ace.getValue()
      this.$emit('input', aceValue)
      this._content = aceValue
    })
  }
}
</script>
