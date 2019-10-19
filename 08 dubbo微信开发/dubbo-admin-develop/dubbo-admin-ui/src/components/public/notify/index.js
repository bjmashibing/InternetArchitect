/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
Usage:
  main.js
    import Notify from './components/public/notify'
    Vue.use(Notify)

  Some.vue:
    this.$notify(message, color)
    this.$notify.error(message)
    this.$notify.success(message)
    this.$notify.info(message)
 */
import Snackbar from './Snackbar.vue'

const Notify = {}

Notify.install = function (Vue) {
  const SnackbarConstructor = Vue.extend(Snackbar)
  const instance = new SnackbarConstructor()
  let vm = instance.$mount()
  document.querySelector('body').appendChild(vm.$el)

  Vue.prototype.$notify = (text, color) => {
    instance.text = text
    instance.color = color
    instance.show = true
  }
  Vue.prototype.$notify.error = text => {
    instance.text = text
    instance.color = 'error'
    instance.show = true
  }
  Vue.prototype.$notify.success = text => {
    instance.text = text
    instance.color = 'success'
    instance.show = true
  }
  Vue.prototype.$notify.info = text => {
    instance.text = text
    instance.color = 'info'
    instance.show = true
  }
}

export default Notify
