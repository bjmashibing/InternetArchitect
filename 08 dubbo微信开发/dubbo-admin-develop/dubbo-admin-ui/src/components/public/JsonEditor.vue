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
  <div class="jsoneditor-vue-container"></div>
</template>

<script>
  import JSONEditor from 'jsoneditor'
  import 'jsoneditor/dist/jsoneditor.css'

  export default {
    name: 'json-editor',
    props: {
      value: null,
      mode: {
        type: String,
        default: 'tree'
      },
      modes: {
        type: Array,
        default: () => ['tree', 'code']
      },
      templates: Array,
      name: {
        type: String,
        default: 'Parameters'
      },
      readonly: {
        type: Boolean,
        default: false
      }
    },
    data () {
      return {
        $jsoneditor: null
      }
    },
    watch: {
      value (newVal, oldVal) {
        if (newVal !== oldVal && this.$jsoneditor) {
          this.$jsoneditor.update(newVal || {})
        }
      }
    },
    mounted () {
      const options = {
        name: this.name,
        navigationBar: false,
        search: false,
        mode: this.mode,
        modes: this.modes,
        onEditable: (node) => !this.readonly,
        onChange: () => {
          if (this.$jsoneditor) {
            const json = this.$jsoneditor.get()
            this.$emit('input', json)
          }
        },
        templates: this.templates
      }
      this.$jsoneditor = new JSONEditor(this.$el, options)
      this.$jsoneditor.set(this.value || {})
      this.$jsoneditor.expandAll()
    },
    beforeDestroy () {
      if (this.$jsoneditor) {
        this.$jsoneditor.destroy()
        this.$jsoneditor = null
      }
    }
  }
</script>

<style scoped>
  .jsoneditor-vue-container {
    width: 100%;
    height: 100%;
  }
  >>> .ace_gutter {
    z-index: auto;
  }
</style>
