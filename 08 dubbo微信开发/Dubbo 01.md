# Dubbo 01



## 架构模型



### 传统架构 All in One

测试麻烦，微小修改 全都得重新测

单体架构也称之为单体系统或者是单体应用。就是一种把系统中所有的功能、模块耦合在一个应用中的架构方式。其优点为：项目易于管理、部署简单。缺点：测试成本高、可伸缩性差、可靠性差、迭代困难、跨语言程度差、团队协作难

聚合项目划分

单项目容易 因为某个功能导致整体oom

拆分完 咋实现

###  SOA 架构: Service-Oriented Architecture

面向服务的架构（SOA）是一个组件模型，它将应用程序拆分成不同功能单元（称为服务）通过这些服务之间定义良好的接口和契约联系起来。接口是采用中立的方式进行定义的，它应该独立于实现服务的硬件平台、操作系统和编程语言。这使得构建在各种各样的系统中的服务可以以一种统一和通用的方式进行交互。

在没有实施SOA的年代，从我们研发的角度来看，只能在代码级别复用，即Ctrl +V。SOA出现，我们开始走向了模块、业务线的复用。

SOA年代的典型实现： SOAP协议，CXF框架，XML传输

xsd，数据校验

SOA架构伴随着软件研发行业20年的发展，在最初的时候，大型it公司内部系统规模越来越大，IT系统越来越复杂，All in One单体架构的思想导致公司内项目业务和数据相互隔离，形成了孤岛。

最初，我们使用数据库作为项目之间数据交互和中转的平台，现在我们有了消息中间件。

最初，我们使用XML完成系统之间解耦与相互关联，现在我们有了RPC，Restful

最初，我们使用业务维度划分整体项目结构，

最初，我们多项目节点维护一个共享数据中心，现在我们做冗余存储，闭环数据，保证高效运行及数据最终一致性

最初，SOA思想指导指导我们把所有的IT系统汇总成一个大的整体，按照业务维度划分服务，集中化管理 现在我们拆分抽象服务使其可以多系统复用相同的功能模块。



### 基于dubbo RPC的微服务式架构

RPC远程过程调用 : Remote Procedure Call Protocol

远程过程调用协议，它是一种通过网络从远程计算机程序上请求服务，而不需要了解底层网络技术的协议。RPC协议假定某些传输协议的存在，如TCP或UDP，为通信程序之间携带信息数据。在[OSI](#_OSI网络通讯模型_:)网络通信模型中，RPC跨越了传输层和应用层。RPC使得开发包括网络分布式多程序在内的应用程序更加容易。

原来的RPC也有其他几种比如DCOM，CORBA，RMI（Java）等

- RMI——Remote Method Invoke：调用远程的方法。“方法”一般是附属于某个对象上的，所以通常RMI指对在远程的计算机上的某个对象，进行其方法函数的调用。
- RPC——Remote Procedure Call：远程过程调用。指的是对网络上另外一个计算机上的，某段特定的函数代码的调用。



![RMI 构建图 ](C:\Users\Administrator\Desktop\tmp\dubbo图\rpc-rmi_flow.png)







**传输协议**

- RPC，可以基于TCP协议，也可以基于HTTP协议
- HTTP，基于HTTP协议

**传输效率**

- RPC，使用自定义的TCP协议，可以让请求报文体积更小，或者使用HTTP2协议，也可以很好的减少报文的体积，提高传输效率
- HTTP，如果是基于HTTP1.1的协议，请求中会包含很多无用的内容，如果是基于HTTP2.0，那么简单的封装以下是可以作为一个RPC来使用的，这时标准RPC框架更多的是服务治理

**性能消耗**

- RPC，可以基于thrift实现高效的二进制传输
- HTTP，大部分是通过json来实现的，字节大小和序列化耗时都比thrift要更消耗性能

**负载均衡**

- RPC，基本都自带了负载均衡策略
- HTTP，需要配置Nginx，HAProxy来实现

**服务治理**

- RPC，能做到自动通知，不影响上游
- HTTP，需要事先通知，修改Nginx/HAProxy配置

RPC主要用于公司内部的服务调用，性能消耗低，传输效率高，服务治理方便。HTTP主要用于对外的异构环境，浏览器接口调用，APP接口调用，第三方接口调用等。

## 概念

###  Dubbo介绍

Apache Dubbo |ˈdʌbəʊ| 是一款高性能、轻量级的开源Java RPC框架，它提供了三大核心能力：面向接口的远程方法调用，智能容错和负载均衡，以及服务自动注册和发现。

Dubbo是阿里巴巴公司开源的一个高性能优秀的[服务框架](https://baike.baidu.com/item/服务框架)，使得应用可通过高性能的 RPC 实现服务的输出和输入功能，可以和[Spring](https://baike.baidu.com/item/Spring)框架无缝集成。Dubbo框架，是基于容器运行的.。容器是Spring。

官方网站 : http://dubbo.apache.org/

阿里巴巴已经将dubbo框架捐献给了Apache软件基金会



### Dubbo框架结构



![](C:\Users\Administrator\Desktop\tmp\dubbo\1.png)

### 角色

#### registry

注册中心. 是用于发布和订阅服务的一个平台.用于替代SOA结构体系框架中的ESB服务总线的。

##### 发布

开发服务端代码完毕后, 将服务信息发布出去. 实现一个服务的公开.

##### 订阅

客户端程序,从注册中心下载服务内容 这个过程是订阅.

订阅服务的时候, 会将发布的服务所有信息,一次性下载到客户端.

客户端也可以自定义, 修改部分服务配置信息. 如: 超时的时长, 调用的重试次数等.

#### consumer

服务的消费者, 就是服务的客户端.

消费者必须使用Dubbo技术开发部分代码. 基本上都是配置文件定义.

#### provider

服务的提供者, 就是服务端.

服务端必须使用Dubbo技术开发部分代码. 以配置文件为主.

#### container

容器. Dubbo技术的服务端(Provider), 在启动执行的时候, 必须依赖容器才能正常启动.

默认依赖的就是spring容器.  且Dubbo技术不能脱离spring框架.

在2.5.3版本的dubbo中, 默认依赖的是spring2.5版本技术. 可以选用spring4.5以下版本.

在2.5.7版本的dubbo中, 默认依赖的是spring4.3.10版本技术. 可以选择任意的spring版本.

#### monitor dubbo admin

监控中心. 是Dubbo提供的一个jar工程.

主要功能是监控服务端(Provider)和消费端(Consumer)的使用数据的. 如: 服务端是什么,有多少接口,多少方法, 调用次数, 压力信息等. 客户端有多少, 调用过哪些服务端, 调用了多少次等. 

### 执行流程

- start: 启动Spring容器时,自动启动Dubbo的Provider
- register: Dubbo的Provider在启动后自动会去注册中心注册内容.注册的内容包括:
  - Provider的 IP
  - Provider 的端口.
  - Provider 对外提供的接口列表.哪些方法.哪些接口类
  - Dubbo 的版本.
  - 访问Provider的协议.
- subscribe: 订阅.当Consumer启动时,自动去Registry获取到所已注册的服务的信息.
- notify: 通知.当Provider的信息发生变化时, 自动由Registry向Consumer推送通知.
- invoke: 调用. Consumer 调用Provider中方法
  - 同步请求.消耗一定性能.但是必须是同步请求,因为需要接收调用方法后的结果.

- count:次数. 每隔2分钟,provoider和consumer自动向Monitor发送访问次数.Monitor进行统计.

### 协议

#### Dubbo协议(官方推荐协议)

优点：

采用NIO复用单一长连接，并使用线程池并发处理请求，减少握手和加大并发效率，性能较好（推荐使用） 

缺点：

大文件上传时,可能出现问题(不使用Dubbo文件上传)

#### RMI(Remote Method Invocation)协议

优点:

JDK自带的能力。可与原生RMI互操作，基于TCP协议

缺点:

偶尔连接失败.

#### Hessian协议

优点:

可与原生Hessian互操作，基于HTTP协议

缺点:

需hessian.jar支持，http短连接的开销大

### 注册中心

####  Zookeeper(官方推荐)

优点:

支持分布式.很多周边产品.

缺点: 

受限于Zookeeper软件的稳定性.Zookeeper专门分布式辅助软件,稳定较优

### Multicast

优点:

去中心化,不需要单独安装软件.

 缺点:

2.2.1 Provider和Consumer和Registry不能跨机房(路由)

### Redis

优点:

支持集群,性能高

缺点:

要求服务器时间同步.否则可能出现集群失败问题.

### Simple

优点: 

标准RPC服务.没有兼容问题

缺点: 

不支持集群.



## 组件选型及成熟度

http://dubbo.apache.org/zh-cn/docs/user/maturity.html

### 功能成熟度

|            |          |                                                              |                                    |                |          |
| ---------- | -------- | ------------------------------------------------------------ | ---------------------------------- | -------------- | -------- |
| Feature    | Maturity | Strength                                                     | Problem                            | Advise         | User     |
| 并发控制   | Tested   | 并发控制                                                     |                                    | 试用           |          |
| 连接控制   | Tested   | 连接数控制                                                   |                                    | 试用           |          |
| 直连提供者 | Tested   | 点对点直连服务提供方，用于测试                               |                                    | 测试环境使用   | Alibaba  |
| 分组聚合   | Tested   | 分组聚合返回值，用于菜单聚合等服务                           | 特殊场景使用                       | 可用于生产环境 |          |
| 参数验证   | Tested   | 参数验证，JSR303验证框架集成                                 | 对性能有影响                       | 试用           | LaiWang  |
| 结果缓存   | Tested   | 结果缓存，用于加速请求                                       |                                    | 试用           |          |
| 泛化引用   | Stable   | 泛化调用，无需业务接口类进行远程调用，用于测试平台，开放网关桥接等 |                                    | 可用于生产环境 | Alibaba  |
| 泛化实现   | Stable   | 泛化实现，无需业务接口类实现任意接口，用于Mock平台           |                                    | 可用于生产环境 | Alibaba  |
| 回声测试   | Tested   | 回声测试                                                     |                                    | 试用           |          |
| 隐式传参   | Stable   | 附加参数                                                     |                                    | 可用于生产环境 |          |
| 异步调用   | Tested   | 不可靠异步调用                                               |                                    | 试用           |          |
| 本地调用   | Tested   | 本地调用                                                     |                                    | 试用           |          |
| 参数回调   | Tested   | 参数回调                                                     | 特殊场景使用                       | 试用           | Registry |
| 事件通知   | Tested   | 事件通知，在远程调用执行前后触发                             |                                    | 试用           |          |
| 本地存根   | Stable   | 在客户端执行部分逻辑                                         |                                    | 可用于生产环境 | Alibaba  |
| 本地伪装   | Stable   | 伪造返回结果，可在失败时执行，或直接执行，用于服务降级       | 需注册中心支持                     | 可用于生产环境 | Alibaba  |
| 延迟暴露   | Stable   | 延迟暴露服务，用于等待应用加载warmup数据，或等待spring加载完成 |                                    | 可用于生产环境 | Alibaba  |
| 延迟连接   | Tested   | 延迟建立连接，调用时建立                                     |                                    | 试用           | Registry |
| 粘滞连接   | Tested   | 粘滞连接，总是向同一个提供方发起请求，除非此提供方挂掉，再切换到另一台 |                                    | 试用           | Registry |
| 令牌验证   | Tested   | 令牌验证，用于服务授权                                       | 需注册中心支持                     | 试用           |          |
| 路由规则   | Tested   | 动态决定调用关系                                             | 需注册中心支持                     | 试用           |          |
| 配置规则   | Tested   | 动态下发配置，实现功能的开关                                 | 需注册中心支持                     | 试用           |          |
| 访问日志   | Tested   | 访问日志，用于记录调用信息                                   | 本地存储，影响性能，受磁盘大小限制 | 试用           |          |
| 分布式事务 | Research | JTA/XA三阶段提交事务                                         | 不稳定                             | 不可用         |          |



### 策略成熟度



| Feature           | Maturity | Strength                                                     | Problem                                    | Advise                   | User |
| ----------------- | -------- | ------------------------------------------------------------ | ------------------------------------------ | ------------------------ | ---- |
| Zookeeper注册中心 | Stable   | 支持基于网络的集群方式，有广泛周边开源产品，建议使用dubbo-2.3.3以上版本（推荐使用） | 依赖于Zookeeper的稳定性                    | 可用于生产环境           |      |
| Redis注册中心     | Stable   | 支持基于客户端双写的集群方式，性能高                         | 要求服务器时间同步，用于检查心跳过期脏数据 | 可用于生产环境           |      |
| Multicast注册中心 | Tested   | 去中心化，不需要安装注册中心                                 | 依赖于网络拓扑和路由，跨机房有风险         | 小规模应用或开发测试环境 |      |
| Simple注册中心    | Tested   | Dogfooding，注册中心本身也是一个标准的RPC服务                | 没有集群支持，可能单点故障                 | 试用                     |      |

| Feature        | Maturity | Strength               | Problem                                           | Advise         | User |
| -------------- | -------- | ---------------------- | ------------------------------------------------- | -------------- | ---- |
| Simple监控中心 | Stable   | 支持JFreeChart统计报表 | 没有集群支持，可能单点故障，但故障后不影响RPC运行 | 可用于生产环境 |      |

| Feature     | Maturity | Strength                                                     | Problem                               | Advise         | User    |
| ----------- | -------- | ------------------------------------------------------------ | ------------------------------------- | -------------- | ------- |
| Dubbo协议   | Stable   | 采用NIO复用单一长连接，并使用线程池并发处理请求，减少握手和加大并发效率，性能较好（推荐使用） | 在大文件传输时，单一连接会成为瓶颈    | 可用于生产环境 | Alibaba |
| Rmi协议     | Stable   | 可与原生RMI互操作，基于TCP协议                               | 偶尔会连接失败，需重建Stub            | 可用于生产环境 | Alibaba |
| Hessian协议 | Stable   | 可与原生Hessian互操作，基于HTTP协议                          | 需hessian.jar支持，http短连接的开销大 | 可用于生产环境 |         |

| Feature             | Maturity | Strength                              | Problem                                          | Advise         | User    |
| ------------------- | -------- | ------------------------------------- | ------------------------------------------------ | -------------- | ------- |
| Netty Transporter   | Stable   | JBoss的NIO框架，性能较好（推荐使用）  | 一次请求派发两种事件，需屏蔽无用事件             | 可用于生产环境 | Alibaba |
| Mina Transporter    | Stable   | 老牌NIO框架，稳定                     | 待发送消息队列派发不及时，大压力下，会出现FullGC | 可用于生产环境 | Alibaba |
| Grizzly Transporter | Tested   | Sun的NIO框架，应用于GlassFish服务器中 | 线程池不可扩展，Filter不能拦截下一Filter         | 试用           |         |

| Feature               | Maturity | Strength                                             | Problem                                                      | Advise         | User    |
| --------------------- | -------- | ---------------------------------------------------- | ------------------------------------------------------------ | -------------- | ------- |
| Hessian Serialization | Stable   | 性能较好，多语言支持（推荐使用）                     | Hessian的各版本兼容性不好，可能和应用使用的Hessian冲突，Dubbo内嵌了hessian3.2.1的源码 | 可用于生产环境 | Alibaba |
| Dubbo Serialization   | Tested   | 通过不传送POJO的类元信息，在大量POJO传输时，性能较好 | 当参数对象增加字段时，需外部文件声明                         | 试用           |         |
| Json Serialization    | Tested   | 纯文本，可跨语言解析，缺省采用FastJson解析           | 性能较差                                                     | 试用           |         |
| Java Serialization    | Stable   | Java原生支持                                         | 性能较差                                                     | 可用于生产环境 |         |

| Feature                | Maturity | Strength                                       | Problem                                                      | Advise         | User    |
| ---------------------- | -------- | ---------------------------------------------- | ------------------------------------------------------------ | -------------- | ------- |
| Javassist ProxyFactory | Stable   | 通过字节码生成代替反射，性能比较好（推荐使用） | 依赖于javassist.jar包，占用JVM的Perm内存，Perm可能要设大一些：java -XX:PermSize=128m | 可用于生产环境 | Alibaba |
| Jdk ProxyFactory       | Stable   | JDK原生支持                                    | 性能较差                                                     | 可用于生产环境 |         |

| Feature           | Maturity | Strength                                                     | Problem                                | Advise         | User     |
| ----------------- | -------- | ------------------------------------------------------------ | -------------------------------------- | -------------- | -------- |
| Failover Cluster  | Stable   | 失败自动切换，当出现失败，重试其它服务器，通常用于读操作（推荐使用） | 重试会带来更长延迟                     | 可用于生产环境 | Alibaba  |
| Failfast Cluster  | Stable   | 快速失败，只发起一次调用，失败立即报错,通常用于非幂等性的写操作 | 如果有机器正在重启，可能会出现调用失败 | 可用于生产环境 | Alibaba  |
| Failsafe Cluster  | Stable   | 失败安全，出现异常时，直接忽略，通常用于写入审计日志等操作   | 调用信息丢失                           | 可用于生产环境 | Monitor  |
| Failback Cluster  | Tested   | 失败自动恢复，后台记录失败请求，定时重发，通常用于消息通知操作 | 不可靠，重启丢失                       | 可用于生产环境 | Registry |
| Forking Cluster   | Tested   | 并行调用多个服务器，只要一个成功即返回，通常用于实时性要求较高的读操作 | 需要浪费更多服务资源                   | 可用于生产环境 |          |
| Broadcast Cluster | Tested   | 广播调用所有提供者，逐个调用，任意一台报错则报错，通常用于更新提供方本地状态 | 速度慢，任意一台报错则报错             | 可用于生产环境 |          |

| Feature                    | Maturity | Strength                                                     | Problem                                                      | Advise         | User    |
| -------------------------- | -------- | ------------------------------------------------------------ | ------------------------------------------------------------ | -------------- | ------- |
| Random LoadBalance         | Stable   | 随机，按权重设置随机概率（推荐使用）                         | 在一个截面上碰撞的概率高，重试时，可能出现瞬间压力不均       | 可用于生产环境 | Alibaba |
| RoundRobin LoadBalance     | Stable   | 轮询，按公约后的权重设置轮询比率                             | 存在慢的机器累积请求问题，极端情况可能产生雪崩               | 可用于生产环境 |         |
| LeastActive LoadBalance    | Stable   | 最少活跃调用数，相同活跃数的随机，活跃数指调用前后计数差，使慢的机器收到更少请求 | 不支持权重，在容量规划时，不能通过权重把压力导向一台机器压测容量 | 可用于生产环境 |         |
| ConsistentHash LoadBalance | Stable   | 一致性Hash，相同参数的请求总是发到同一提供者，当某一台提供者挂时，原本发往该提供者的请求，基于虚拟节点，平摊到其它提供者，不会引起剧烈变动 | 压力分摊不均                                                 | 可用于生产环境 |         |

| Feature      | Maturity | Strength                               | Problem                                      | Advise         | User    |
| ------------ | -------- | -------------------------------------- | -------------------------------------------- | -------------- | ------- |
| 条件路由规则 | Stable   | 基于条件表达式的路由规则，功能简单易用 | 有些复杂多分支条件情况，规则很难描述         | 可用于生产环境 | Alibaba |
| 脚本路由规则 | Tested   | 基于脚本引擎的路由规则，功能强大       | 没有运行沙箱，脚本能力过于强大，可能成为后门 | 试用           |         |

| Feature          | Maturity | Strength                                                     | Problem                                  | Advise         | User    |
| ---------------- | -------- | ------------------------------------------------------------ | ---------------------------------------- | -------------- | ------- |
| Spring Container | Stable   | 自动加载META-INF/spring目录下的所有Spring配置                |                                          | 可用于生产环境 | Alibaba |
| Jetty Container  | Stable   | 启动一个内嵌Jetty，用于汇报状态                              | 大量访问页面时，会影响服务器的线程和内存 | 可用于生产环境 | Alibaba |
| Log4j Container  | Stable   | 自动配置log4j的配置，在多进程启动时，自动给日志文件按进程分目录 | 用户不能控制log4j的配置，不灵活          | 可用于生产环境 | Alibaba |



## zookeeper安装

![1565092109186](C:\Users\Administrator\Desktop\tmp\dubbo图\1565092109186.png)

### 安装jdk

- rpm -ivh jdk-8u131-linux-x64.rpm
- java –version 检查是否成功

### 安装Zookeeper

下载 http://zookeeper.apache.org/ 并上传解压缩

#### 配置zookeeper环境变量

修改文件 vi /etc/profile 追加内容

在path后追加`/usr/local/zookeeper/bin`

**注意中间间隔是： 冒号**

```
export PATH=$PATH:/usr/local/hadoop/bin:/usr/local/hadoop/sbin:/usr/local/zo

okeeper/bin
```

source /etc/profile 重新加载配置

#### 修改zoo.cfg

重命名zoo_sample.cfg 为zoo.cfg

默认加载配置文件会找zoo.cfg这个文件

修改配置文件

```
vi /usr/local/zookeeper/conf/zoo.cfg
```

创建数据存放目录

Mkdir /data/zookeeper

创建Myid文件，并写入服务器编号

![1565092323114](C:\Users\Administrator\Desktop\tmp\dubbo图\1565092323114.png)

**注意**：这里写入myid文件的编号和接下来要配置的服务器列表编号一一对应，每台服务器配置的编号也应该不一样。

Myid文件里只有一个数字 1

创建好的这个目录用于存储zookeeper产生的数据

修改datadir为刚才我们创建的目录

dataDir=/data/zookeeper

![1565092366744](C:\Users\Administrator\Desktop\tmp\dubbo图\1565092366744.png)

在最后面加入集群服务器列表

```
server.1=192.168.2.51:2888:3888

server.2=cm02:2888:3888

server.3=cm03:2888:3888
```





配置的服务器集群个数建议是奇数的

半数以上节点存活，就可以对外提供服务

 

其中 server.x 这里的数字编号 就是我们的myid里写入的数字

Cm01是主机名或ip地址

接下来是对外通讯端口和内部选举端口

#### 启动

zkServer.sh start 命令启动一台zookeeper服务器

没报错的话 使用jps看一下进程

![1565092454496](C:\Users\Administrator\Desktop\tmp\dubbo图\1565092454496.png)

QuorumPeerMain是zookeeper的主进程

通过status命令可以查看服务器运行状态



![1565092410017](C:\Users\Administrator\Desktop\tmp\dubbo图\1565092410017.png)

**注意：当我们使用集群模式启动zookeeper的时候，由于我们只启动了一台服务器，集群总共3台，没有满足zookeeper半数以上节点运行原则，所以服务虽然起来了，但是没有办法对外提供服务。**

**这时我们需要启动第二台服务器**



## Dubbo Hello World



### 环境

SpringBoot + dubbo

### Pom.xml 依赖

```xml

		<!-- Aapche Dubbo -->


		<dependency>
			<groupId>org.apache.dubbo</groupId>
			<artifactId>dubbo-spring-boot-starter</artifactId>
			<version>${dubbo.version}</version>
		</dependency>

		<dependency>
			<groupId>org.apache.dubbo</groupId>
			<artifactId>dubbo</artifactId>
			<version>${dubbo.version}</version>
		</dependency>


		<!-- https://mvnrepository.com/artifact/org.apache.curator/curator-framework -->
		<dependency>
			<groupId>org.apache.curator</groupId>
			<artifactId>curator-framework</artifactId>
			<version>4.2.0</version>
		</dependency>

		<dependency>
			<groupId>org.apache.curator</groupId>
			<artifactId>curator-recipes</artifactId>
			<version>4.2.0</version>
			<exclusions>
				<exclusion>
					<groupId>org.apache.zookeeper</groupId>
					<artifactId>zookeeper</artifactId>
				</exclusion>
			</exclusions>
		</dependency>

		<dependency>
			<groupId>org.apache.zookeeper</groupId>
			<artifactId>zookeeper</artifactId>
			<version>3.4.14</version>
		</dependency>
	</dependencies>
```



### 服务提供方 provider

#### 配置文件

```pro
server.port=8081

spring.application.name=DemoProvider
dubbo.scan.base-packages=com.msb.db1.service

dubbo.protocol.name=dubbo
dubbo.protocol.port=666
dubbo.protocol.host=192.168.101.106

dubbo.registry.address=zookeeper://192.168.150.13:2181
```

#### 服务接口

```java
public interface DemoService {
    String sayHello(String name);
}

```

#### 接口实现

```java
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

@Service(version = "1.0.0" ,timeout = 10000, interfaceClass = DemoService.class)
@Component
public class DemoServiceImpl implements DemoService {

	@Override
	public String sayHello(String name) {
		// TODO Auto-generated method stub
		System.out.println("来啦~~~！");
		return "hello:" + name;
	}
}
```

### 服务消费方 customer

#### 配置

```
spring.application.name=DemoCustomer
dubbo.scan.base-packages=com.msb.db1.service

dubbo.registry.address=zookeeper://192.168.150.13:2181

```

#### 自动注入

```java
	@Reference(version = "1.0.0")
	DemoService serv;
	
```

### 接口

```java
public interface DemoService {

    String sayHello(String name);

}
```





本地存根（Stub）

服务端的骨架对象(Skeleton)