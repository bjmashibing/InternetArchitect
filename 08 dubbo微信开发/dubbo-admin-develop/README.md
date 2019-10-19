# Dubbo Admin

[![Build Status](https://travis-ci.org/apache/dubbo-admin.svg?branch=develop)](https://travis-ci.org/apache/dubbo-admin)
[![codecov](https://codecov.io/gh/apache/dubbo-admin/branch/develop/graph/badge.svg)](https://codecov.io/gh/apache/dubbo-admin)
![license](https://img.shields.io/github/license/apache/dubbo-admin.svg)

[中文说明](README_ZH.md)
### Demo Address
* http://47.91.207.147/#/service
* this demo is the latest version of `develop` branch, you can try it before building from source code
### Screenshot

![index](https://raw.githubusercontent.com/apache/dubbo-admin/develop/doc/images/index.png)

### Service Governance  
service governance follows the version of Dubbo 2.7, and compatible for Dubbo 2.6, please refer to [here](https://github.com/apache/dubbo-admin/wiki/The-compatibility-of-service-governance)
### admin UI

- [Vue.js](https://vuejs.org) and [Vuetify](https://vuetifyjs.com)
- [dubbo-admin-ui/README.md](dubbo-admin-ui/README.md) for more detail
- Set npm **proxy mirror**: you can set npm proxy mirror to speedup npm install: add `registry =https://registry.npm.taobao.org` to ~/.npmrc

### admin Server

* Standard spring boot project
* [configurations in application.properties](https://github.com/apache/dubbo-admin/wiki/Dubbo-Admin-configuration)


### Production Setup

1. Clone source code on develop branch `git clone https://github.com/apache/dubbo-admin.git`
2. Specify registry address in `dubbo-admin-server/src/main/resources/application.properties`
3. Build

    > - `mvn clean package`  
4. Start 
    * `mvn --projects dubbo-admin-server spring-boot:run`  
    OR
    * `cd dubbo-admin-distribution/target`;   `java -jar dubbo-admin-0.1.jar`
5. Visit `http://localhost:8080`
---

### Development Setup
* Run admin server project
   backend is a standard spring boot project, you can run it in any java IDE
* Run admin ui project
  run with `npm run dev`.
* visit web page
  visit `http://localhost:8081`, frontend supports hot reload.
 * CORS problem
    for the convenience of development, we deploy ui and server separately, so the frontend supports hot reload. In this mode, frontend will request `localhost:8080` to fetch data, this will cause a CORS problem, so we add a configuration in `dubbo-admin-ui/config/index.js` to support CORS. This config will be activated under `npm run dev` mode.

### Swagger support

Once deployed, you can check http://localhost:8080/swagger-ui.html to check all restful api and models


### License

Apache Dubbo admin is under the Apache 2.0 license, Version 2.0.
See [LICENSE](https://github.com/apache/dubbo-admin/blob/develop/LICENSE) for full license text.
