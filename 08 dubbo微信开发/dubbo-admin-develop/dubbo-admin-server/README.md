## Dubbo Admin后端配置
当前版本的的Dubbo Admin支持Dubbo2.7的新特性(元数据展示，应用级别的配置)，同时也兼容Dubbo2.6的用法，两个版本的配置不同，具体介绍如下：  
* application.properties配置
  * dubbo.config.center: 2.7的配置，推荐用法，填写配置中心的地址，并且在配置中心的相应目录下配置注册中心和元数据中心的地址，Dubbo Admin会根据相应的协议，初始化对应的客户端，实现对配置中心，元数据中心的访问
  * dubbo.registry.address: 2.6的配置，只配置注册中心的地址。采用此配置，Dubbo Admin会把配置，元数据相关的内容也写入注册中心的不同目录下
  * 两种配置，都可以兼容Dubbo2.6和2.7的版本，即使配置了dubbo.config.center，对于2.6版本的客户端，也会收到对应版本的路由，动态配置规则，并且按照规则内容生效
