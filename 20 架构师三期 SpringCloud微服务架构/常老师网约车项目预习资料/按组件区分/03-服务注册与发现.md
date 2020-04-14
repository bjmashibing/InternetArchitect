# 11 服务注册与发现

## 11.1 Eureka 单节点搭建

1. pom.xml

   ```sh
   <dependency>
   	<groupId>org.springframework.cloud</groupId>
   	<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
   </dependency>
   
   有的教程中还引入spring-boot-starter-web，其实不用。因为上面的依赖已经包含了它。在pom中点此依赖进去，一共点4次spring-cloud-netflix-eureka-server，发现web的依赖。
   
   ```

2. application.yml

   ```sh
   eureka: 
     client:
       #是否将自己注册到Eureka Server,默认为true，由于当前就是server，故而设置成false，表明该服务不会向eureka注册自己的信息
       register-with-eureka: false
       #是否从eureka server获取注册信息，由于单节点，不需要同步其他节点数据，用false
       fetch-registry: false
       #设置服务注册中心的URL，用于client和server端交流
       service-url:                      
         defaultZone: http://root:root@eureka-7901:7901/eureka/
   ```

3. 代码

   ```sh
   启动类上添加此注解标识该服务为配置中心
   @EnableEurekaServer
   ```

4. PS：Eureka会暴露一些端点。端点用于Eureka Client注册自身，获取注册表，发送心跳。

5. 简单看一下eureka server控制台，实例信息区，运行环境信息区，Eureka Server自身信息区。

## 11.2 整体介绍

1. 背景：在传统应用中，组件之间的调用，通过有规范的约束的接口来实现，从而实现不同模块间良好的协作。但是被拆分成微服务后，每个微服务实例的网络地址都可能动态变化，数量也会变化，使得原来硬编码的地址失去了作用。需要一个中心化的组件来进行服务的登记和管理。
2. 概念：实现服务治理，即管理所有的服务信息和状态。

```sh
注册中心相当于买票乘车，只看有没有票（有没有服务），有就去买票（获取注册列表），然后乘车（调用）。不必关心有多少火车在运行。
```

3. 注册中心好处：不用关心有多少提供方。

4. 注册中心有哪些:Eureka，Nacos，Consul，Zookeeper等。

5. 服务注册与发现包括两部分，一个是服务器端，另一个是客户端。

   Server是一个公共服务，为Client提供服务注册和发现的功能，维护注册到自身的Client的相关信息，同时提供接口给Client获取注册表中其他服务的信息，使得动态变化的Client能够进行服务间的相互调用。

   Client将自己的服务信息通过一定的方式登记到Server上，并在正常范围内维护自己信息一致性，方便其他服务发现自己，同时可以通过Server获取到自己依赖的其他服务信息，完成服务调用，还内置了负载均衡器，用来进行基本的负载均衡。

6. 我们课程的Spring Cloud是用Eureka作为服务注册中心。

7. Eureka：是一个RESTful风格的服务，是一个用于服务发现和注册的基础组件，是搭建Spring Cloud微服务的前提之一，它屏蔽了Server和client的交互细节，使得开发者将精力放到业务上。

8. serverA从serverB同步信息，则serverB是serverA的peer。

9. 上面例子中如果service-url为空，且register-with-eureka，fetch-registry为true，则会报错，Cannot execute request on any known server，因为server同时也是一个client，他会尝试注册自己，所以要有一个注册中心url去注册。

10. Netflix开源的组件。包括server和client两部分。

    ```sh
    https://github.com/Netflix/Eureka
    ```

## 11.3 注册中心和微服务间的关系

> 《服务注册与发现关系图》

### 11.3.1 client功能

1. 注册：每个微服务启动时，将自己的网络地址等信息注册到注册中心，注册中心会存储（内存中）这些信息。
2. 获取服务注册表：服务消费者从注册中心，查询服务提供者的网络地址，并使用该地址调用服务提供者，为了避免每次都查注册表信息，所以client会定时去server拉取注册表信息到缓存到client本地。
3. 心跳：各个微服务与注册中心通过某种机制（心跳）通信，若注册中心长时间和服务间没有通信，就会注销该实例。
4. 调用：实际的服务调用，通过注册表，解析服务名和具体地址的对应关系，找到具体服务的地址，进行实际调用。

### 11.3.2 server注册中心功能

1. 服务注册表：记录各个微服务信息，例如服务名称，ip，端口等。

   注册表提供 查询API（查询可用的微服务实例）和管理API（用于服务的注册和注销）。

2. 服务注册与发现：注册：将微服务信息注册到注册中心。发现：查询可用微服务列表及其网络地址。

3. 服务检查：定时检测已注册的服务，如发现某实例长时间无法访问，就从注册表中移除。

组件：Eureka , Consul , ZooKeeper，nacos等。

## 11.4 服务注册

例子：api-listen-order

1. pom.xml

```sh
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>
```

2. application.yml

```sh
#注册中心
eureka: 
  client:
    #设置服务注册中心的URL
    service-url:                      
      defaultZone: http://root:root@localhost:7900/eureka/
```

ps:不想注册，设置成false即可，实例演示结果：注册中心没有实例信息。找控制台204信息也没有找到。

```sh
spring: 
  cloud:
    service-registry:
      auto-registration:
        enabled: false
```

注册成功：

```sh
DiscoveryClient_API-LISTEN-ORDER/api-listen-order:30.136.133.9:port - registration status: 204
```

后面源码讲手动注册。

PS:

Eureka Server与Eureka Client之间的联系主要通过心跳的方式实现。心跳(Heartbeat)即Eureka Client定时向Eureka Server汇报本服务实例当前的状态，维护本服务实例在注册表中租约的有效性。

Eureka Client将定时从Eureka Server中拉取注册表中的信息，并将这些信息缓存到本地，用于服务发现。



## 11.5 Eureka高可用

高可用：可以通过运行多个Eureka server实例并相互注册的方式实现。Server节点之间会彼此增量地同步信息，从而确保节点中数据一致。

1. 注册中心改造

application.yml

参考：#高可用2个节点的yml

```sh
#高可用2个节点
#应用名称及验证账号
spring: 
  application: 
    name: eureka
    
  security: 
    user: 
      name: root
      password: root

logging:
  level:
    root: debug
    
---
spring:
  profiles: 7901
server: 
  port: 7901
eureka:
  instance:
    hostname: eureka-7901  
  client:
    #设置服务注册中心的URL
    service-url:                      
      defaultZone: http://root:root@eureka-7902:7902/eureka/
---    
spring:
  profiles: 7902
server: 
  port: 7902
eureka:
  instance:
    hostname: eureka-7902  
  client:
       #设置服务注册中心的URL
    service-url:                      
      defaultZone: http://root:root@eureka-7901:7901/eureka/


```

---将配置文件分成2段，每段指定spring.profiles。第一段没有指定，所以共用。



2. 服务注册改造

   api-listen-order

```sh
eureka: 
  client:
    #设置服务注册中心的URL
    service-url:                      
      defaultZone: http://root:root@eureka-7901:7901/eureka/,http://root:root@eureka-7902:7902/eureka/
  
```

写一个地址也行（但是server得互相注册），EurekaServer会自动同步，但为了避免极端情况，还是写多个。

集群PS:

集群中各个server会从其他server同步注册表信息。

## 11.6 Eureka 端点

```sh
看官网：
https://github.com/Netflix/eureka/wiki/Eureka-REST-operations

body:
<instance>
<instanceId>instanceid-2019</instanceId>
<hostName>eureka-7900</hostName>
<app>EUREKA-custom</app>
<ipAddr>127.0.0.1</ipAddr>
<status>UP</status>
<overriddenstatus>UNKNOWN</overriddenstatus>
<port enabled="true">1900</port>
<securePort enabled="false">443</securePort>
<countryId>1</countryId>
<dataCenterInfo class="com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo">
<name>MyOwn</name>
</dataCenterInfo>

</instance>

地址：localhost:7900/eureka/apps/{applicaitonName}

yapi：http://yapi.demo.qunar.com/
```

例子：手写注册服务

步骤：

1. 启动单节点eureka，7900。
2. 打开yapi。yapi->Spring Cloud学习->测试集合->Eureka->向EurekaServer注册服务。
3. 执行访问，查看eureka server控制台。

ps：一些语言也有Eureka Client的开源实现，比如node.JS



------

2020-01-12 第一节课完。

课下问题：

1. eureka无用。其实只是2.0不更新了。1.0还在更新，也在大量的用。

   ```sh
   https://github.com/Netflix/eureka/wiki
   ```

   即使它以后都不用了 eureka 的思路也是值得学习的。毕竟服务注册中心，就这些东西。

2. lombok使用

   ```sh
   <!-- lombok -->
   <dependency>
   	<groupId>org.projectlombok</groupId>
   	<artifactId>lombok</artifactId>
   	<version>1.18.8</version>
   </dependency>
   
   ide安装插件
   
   getter/setter
   ```

3. 域名问题

   ```sh
   域名在 物理机的host文件配置，只是为了好区分，不是必须的。只要能访问到机器就行，用localhost，ip均可。
   ```

4. 多节点注意事项

   ```sh
   问题：eureka server间 设置peer。A->B,B->C,C->A，结果注册信息并不同步。
   看例子:
   依次启动7901,7902,7903。
   启动成功，注册api-driver ->7901
   发现只有7901和7902有 api-driver而 7903没有。
   
   简单说：api-driver向 7901注册，7902将api-driver同步到7902，但是不会同步到7903。后面源码会讲到。
   多节点建议：设置A->B,A->C其他类似。尽量不要跨 eureka节点。一对多，面面对到。
   
   讲解下图。前置概念peer。清除流程。
   
   功能点：
   peer启动：
   1、拉取它的peer的注册表。
   2、把自己注册到peer上。
   3、完成2之后，2中的peer会把它同步到，2中peer的peer。
   
   ```

   > 《eureka集群复制流程图》，为什么有时候3个实例，后来都变成2个实例了。

5. yml配置文件分段。

6. 可以独立使用，利用它的各种端点做开发，甚至可以自己做个服务注册中心。

------



## 11.7 Eureka 原理



1. 本质：存储了每个客户端的注册信息。EurekaClient从EurekaServer同步获取服务注册列表。通过一定的规则选择一个服务进行调用。

2. Eureka架构图

   > 《 Eureka架构图》

3. 详解

- 服务提供者：是一个eureka client，向Eureka Server注册和更新自己的信息，同时能从Eureka Server注册表中获取到其他服务的信息。
- 服务注册中心：提供服务注册和发现的功能。每个Eureka Cient向Eureka Server注册自己的信息，也可以通过Eureka Server获取到其他服务的信息达到发现和调用其他服务的目的。
- 服务消费者：是一个eureka client，通过Eureka Server获取注册到其上其他服务的信息，从而根据信息找到所需的服务发起远程调用。
- 同步复制：Eureka Server之间注册表信息的同步复制，使Eureka Server集群中不同注册表中服务实例信息保持一致。
- 远程调用：服务客户端之间的远程调用。
- 注册：Client端向Server端注册自身的元数据以供服务发现。
- 续约：通过发送心跳到Server以维持和更新注册表中服务实例元数据的有效性。当在一定时长内，Server没有收到Client的心跳信息，将默认服务下线，会把服务实例的信息从注册表中删除。
- 下线：Client在关闭时主动向Server注销服务实例元数据，这时Client的服务实例数据将从Server的注册表中删除。
- 获取注册表：Client向Server请求注册表信息，用于服务发现，从而发起服务间远程调用。

```sh
让我们自己做：如何做。？

客户端：
拉取注册表
从注册表选一个
调用

服务端：
写个web server。
功能：
1、定义注册表：
Map<name,Map<id,InstanceInfo>>。
2、别人可以向我注册自己的信息。
3、别人可以从我这里拉取他人的注册信息。
4、我和我的同类可以共享注册表。

eureka是用：jersey实现，也是个mvc框架。
我们可以自己写个spring boot web实现。
```



## 11.7 Eureka Client源码

1. 功能复习

   ```sh
   https://github.com/Netflix/eureka/wiki/Eureka-REST-operations
   注意地址中的v2 是没有的。
   
   查询所有实例信息：http://localhost:7900/eureka/apps
   
   注册服务：http://localhost:7900/eureka/apps/{applicationName}
   ```

   > 《Eureka Client工作流程图》

2. 源码解读

   下面的讲解按照顺序进行。

   - spring boot项目引入eureka-client依赖，并注入spring 容器。

     在spring-boot项目中pom文件里面添加的依赖中的bean。是如何注册到spring-boot项目的spring容器中的呢？spring.factories文件是帮助spring-boot项目包以外的bean（即在pom文件中添加依赖中的bean）注册到spring-boot项目的spring容器的。

     由于@ComponentScan注解只能扫描spring-boot项目包内的bean并注册到spring容器中，因此需要@EnableAutoConfiguration（在SpringBootApplication下），注解来注册项目包外的bean。而spring.factories文件，则是用来记录项目包外需要注册的bean类名。

     

     点进去@SpringBootApplication注解，发现@EnableAutoConfiguration。点@EnableAutoConfiguration进去。

     ```sh
     @Import(AutoConfigurationImportSelector.class)
     public @interface EnableAutoConfiguration {
     ```

     点AutoConfigurationImportSelector进去

     ```sh
     发现下面代码
     	@Override
     	public String[] selectImports(AnnotationMetadata annotationMetadata) {
     		if (!isEnabled(annotationMetadata)) {
     			return NO_IMPORTS;
     		}
     		AutoConfigurationMetadata autoConfigurationMetadata = AutoConfigurationMetadataLoader
     				.loadMetadata(this.beanClassLoader);
     		AutoConfigurationEntry autoConfigurationEntry = getAutoConfigurationEntry(autoConfigurationMetadata,
     				annotationMetadata);
     		return StringUtils.toStringArray(autoConfigurationEntry.getConfigurations());
     	}
     ```

     此方法时，向spring ioc容器注入bean。selectImports，返回bean全名。import将bean全名注入。而注入的bean都是些什么呢？

     点：getAutoConfigurationEntry进去，有一句

     ```sh
     List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
     ```

     点getCandidateConfigurations进去：

     ```sh
     List<String> configurations = SpringFactoriesLoader.loadFactoryNames(getSpringFactoriesLoaderFactoryClass(),
     				getBeanClassLoader());
     			
     ```

     点SpringFactoriesLoader进去：

     ```sh
     public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";	
     ```

     

   - 找eureka client 配置相关类

     ```sh
     在api-listen-order(其他eureka client项目均可)项目中，找到
     spring-cloud-netflix-eureka-client-2.1.2.RELEASE下META-INF下spring.factories。此文件中，有如下配置信息：
     
     
     EurekaClientAutoConfiguration（Eureka client自动配置类，负责Eureka client中关键beans的配置和初始化），
     RibbonEurekaAutoConfiguration（Ribbon负载均衡相关配置）	
     EurekaDiscoveryClientConfiguration（配置自动注册和应用的健康检查器）。
     ```

   - EurekaDiscoveryClientConfiguration介绍

     ```sh
     找到此类：org.springframework.cloud.netflix.eureka.EurekaDiscoveryClientConfiguration中的注解@ConditionalOnClass(EurekaClientConfig.class)，
     ```

   - EurekaClientConfig介绍

     ```sh
     点击进去查看EurekaClientConfig是个接口，查看其实现类EurekaClientConfigBean。此类里封装了Eureka Client和Eureka Server交互所需要的配置信息。看此类代码：
     
       public static final String PREFIX = "eureka.client";
          表示在配置文件中用eureka.client.属性名配置。
     ```

   - Eureka 实例相关配置

     ```sh
     从org.springframework.cloud.client.discovery.DiscoveryClient顶级接口入手，前面介绍过spring common。看其在Eureka中的实现类org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient。有一个属性：
      private final EurekaClient eurekaClient，查看其实现类：com.netflix.discovery.DiscoveryClient。
     有一个属性：
      private final ApplicationInfoManager applicationInfoManager（应用信息管理器，点进去此类，发现此类总有两个属性:
      private InstanceInfo instanceInfo;
     private EurekaInstanceConfig config;
      服务实例的信息类InstanceInfo和服务实例配置信息类EurekaInstanceConfig）。
     ```

   - InstanceInfo介绍

     ```sh
     打开InstanceInfo里面有instanceId等服务实例信息。
      InstanceInfo封装了将被发送到Eureka Server进行注册的服务实例元数据。它在Eureka Server列表中代表一个服务实例，其他服务可以通过instanceInfo了解到该服务的实例相关信息，包括地址等，从而发起请求。
     ```

   - EurekaInstanceConfig介绍

     ```sh
     EurekaInstanceConfig是个接口，找到它的实现类org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean。
      此类封装了EurekaClient自身服务实例的配置信息，主要用于构建InstanceInfo。看到此类有一段代码：@ConfigurationProperties("eureka.instance")，
      在配置文件中用eureka.instance.属性配置。EurekaInstanceConfigBean提供了默认值。
     ```

   - 通过EurekaInstanceConfig构建instanceInfo

     ```
     在ApplicationInfoManager中有一个方法
     public void initComponent(EurekaInstanceConfig config)中有一句：
     this.instanceInfo = new EurekaConfigBasedInstanceInfoProvider(config).get();
     通过EurekaInstanceConfig构造instanceInfo。
     ```

   - 顶级接口DiscoveryClient介绍

     ```
     介绍一下spring-cloud-commons-2.2.1.realease包下，org.springframework.cloud.client.discovery.DiscoveryClient接口。定义用来服务发现的客户端接口，是客户端进行服务发现的核心接口，是spring cloud用来进行服务发现的顶级接口，在common中可以看到其地位。在Netflix Eureka和Consul中都有具体的实现类。
     org.springframework.cloud.client.discovery.DiscoveryClient的类注释:
     	Represents read operations commonly available to discovery services such as Netflix Eureka or consul.io。
     	代表通用于服务发现的读操作，例如在 eureka或consul中。
     	有
     	String description();//获取实现类的描述。
     	List<String> getServices();//获取所有服务实例id。
     	List<ServiceInstance> getInstances(String serviceId);//通过服务id查询服务实例信息列表。
     ```

   - Eureka 的实现

     ```
     接下来我们找Eureka的实现类。org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient。
     查看方法。
     public List<ServiceInstance> getInstances(String serviceId)，
     组合了com.netflix.discovery.EurekaClient来实现。
     ```

   - EurekaClient的实现

     ```
     EurekaClient有一个注解@ImplementedBy(DiscoveryClient.class)，此类的默认实现类：com.netflix.discovery.DiscoveryClient。提供了:
     服务注册到server方法register().
     续约boolean renew().
     下线public synchronized void shutdown().
     查询服务列表 功能。
     想想前面的图中client的功能。提供了于Eureka Server交互的关键逻辑。
     ```

   - com.netflix.discovery.DiscoveryClient

     ```
     com.netflix.discovery.DiscoveryClient实现了EurekaClient（继承了LookupService）
     ```

   - com.netflix.discovery.shared.LookupService

     ```
     LookupService作用：发现活跃的服务实例。
     根据服务实例注册的appName来获取封装有相同appName的服务实例信息容器： 
     Application getApplication(String appName)。
     获取所有的服务实例信息：
     Applications getApplications();
     根据实例id，获取服务实例信息：
     List<InstanceInfo> getInstancesById(String id);
     
     上面提到一个Application，它持有服务实例信息列表。它是同一个服务的集群信息。比如api-passenger的所有服务信息，这些服务都在api-passenger服务名下面。
     
     而instanceInfo代表一个服务实例的信息。为了保证原子性，比如对某个instanceInfo的操作，使用了大量同步的代码。比如下面代码：
     public void addInstance(InstanceInfo i) {
     	instancesMap.put(i.getId(), i);
     	synchronized (instances) {
     		instances.remove(i);
     		instances.add(i);
     		isDirty = true;
     	}
     }
     
     Applications是注册表中，所有服务实例信息的集合。
     ```

   - 健康检测器和事件监听器

     ```
     EurekaClient在LookupService上做了扩充。提供了更丰富的获取服务实例的方法。按住不表。我们看一下另外两个方法：
     
     public void registerHealthCheck(HealthCheckHandler healthCheckHandler),向client注册 健康检查处理器，client存在一个定时任务通过HealthCheckHandler检查当前client状态，当client状态发生变化时，将会触发新的注册事件，去更新eureka server的注册表中的服务实例信息。
     通过HealthCheckHandler 实现应用状态检测。HealthCheckHandler的实现类org.springframework.cloud.netflix.eureka.EurekaHealthCheckHandler，看其构造函数：
     public EurekaHealthCheckHandler(HealthAggregator healthAggregator) {
     		Assert.notNull(healthAggregator, "HealthAggregator must not be null");
     		this.healthIndicator = new CompositeHealthIndicator(healthAggregator);
     }
     private final CompositeHealthIndicator healthIndicator;此类事属于org.springframework.boot.actuate.health包下，可以得出，是通过actuator来实现对应用的检测的。
     
     public void registerEventListener(EurekaEventListener eventListener)注册事件监听器，当实例信息有变时，触发对应的处理事件。
     ```

     

   - 找到com.netflix.discovery.DiscoveryClient

     ```
     在api-listen-order项目中，找到spring-cloud-netflix-eureka-client-2.1.2.RELEASE下META-INF下spring.factories。此文件中org.springframework.cloud.bootstrap.BootstrapConfiguration=\
     org.springframework.cloud.netflix.eureka.config.EurekaDiscoveryClientConfigServiceBootstrapConfiguration，此类有个注解：
     @Import({ EurekaDiscoveryClientConfiguration.class, // this emulates
     		// @EnableDiscoveryClient, the import
     		// selector doesn't run before the
     		// bootstrap phase
     		EurekaClientAutoConfiguration.class })
     注解中有个类：	EurekaClientAutoConfiguration，此类中有如下代码：
     CloudEurekaClient cloudEurekaClient = new CloudEurekaClient(appManager,
     					config, this.optionalArgs, this.context);
     （debug可以调试到）
     通过CloudEurekaClient找到：public class CloudEurekaClient extends DiscoveryClient。
     
     ```

   - com.netflix.discovery.DiscoveryClient构造函数-不注册不拉取

     ```
     DiscoveryClient的构造函数：
     DiscoveryClient(ApplicationInfoManager applicationInfoManager, EurekaClientConfig config, AbstractDiscoveryClientOptionalArgs args,Provider<BackupRegistry> backupRegistryProvider, EndpointRandomizer endpointRandomizer) 
     此方法中依次执行了 从eureka server中拉取注册表，服务注册，初始化发送心跳，缓存刷新（定时拉取注册表信息），按需注册定时任务等，贯穿了Eureka Client启动阶段的各项工作。
     
     构造函数353行：
     if (config.shouldFetchRegistry()) {
       	this.registryStalenessMonitor = new ThresholdLevelsMetric(this, METRIC_REGISTRY_PREFIX + "lastUpdateSec_", new long[]{15L, 30L, 60L, 120L, 240L, 480L});
     } else {
       	this.registryStalenessMonitor = ThresholdLevelsMetric.NO_OP_METRIC;
     }
       shouldFetchRegistry，点其实现类EurekaClientConfigBean，找到它其实对应于：eureka.client.fetch-register，true：表示client从server拉取注册表信息。
     
     下面：
     if (config.shouldRegisterWithEureka()) {
       	this.heartbeatStalenessMonitor = new ThresholdLevelsMetric(this, METRIC_REGISTRATION_PREFIX + "lastHeartbeatSec_", new long[]{15L, 30L, 60L, 120L, 240L, 480L});
     } else {
       	this.heartbeatStalenessMonitor = ThresholdLevelsMetric.NO_OP_METRIC;
     }
       shouldRegisterWithEureka，点其实现类EurekaClientConfigBean，找到它其实对应于：
       eureka.client.register-with-eureka：true：表示client将注册到server。
     
       if (!config.shouldRegisterWithEureka() && !config.shouldFetchRegistry()) {
       如果以上两个都为false，则直接返回，构造方法执行结束，既不服务注册，也不服务发现。
     
     ```

   - com.netflix.discovery.DiscoveryClient构造函数-两个定时任务

     ```
     顺着上面代码往下看：
       scheduler = Executors.newScheduledThreadPool(2,
                           new ThreadFactoryBuilder()
                                   .setNameFormat("DiscoveryClient-%d")
                                   .setDaemon(true)
                                   .build());
       定义了一个基于线程池的定时器线程池，大小为2。
       往下：
       heartbeatExecutor：用于发送心跳，
       cacheRefreshExecutor：用于刷新缓存。
     ```

   - com.netflix.discovery.DiscoveryClient构造函数-client和server交互的Jersey客户端

     ```
     接着构建eurekaTransport = new EurekaTransport();它是eureka Client和eureka server进行http交互jersey客户端。点开EurekaTransport，看到许多httpclient相关的属性。
     
     ```

   - com.netflix.discovery.DiscoveryClient构造函数-拉取注册信息

     ```
       if (clientConfig.shouldFetchRegistry() && !fetchRegistry(false)) {
                   fetchRegistryFromBackup();
       }
       如果判断的前部分为true，执行后半部分fetchRegistry。此时会从eureka server拉取注册表中的信息，将注册表缓存到本地，可以就近获取其他服务信息，减少于server的交互。
     ```

   - com.netflix.discovery.DiscoveryClient构造函数-服务注册

     ```
      if (clientConfig.shouldRegisterWithEureka() && clientConfig.shouldEnforceRegistrationAtInit()) {
                   try {
                       if (!register() ) {
                           throw new IllegalStateException("Registration error at startup. Invalid server response.");
                       }
                   } catch (Throwable th) {
                       logger.error("Registration error at startup: {}", th.getMessage());
                       throw new IllegalStateException(th);
                   }
               }注册失败抛异常。
     ```

   - com.netflix.discovery.DiscoveryClient构造函数-启动定时任务

     ```
     在构造方法的最后initScheduledTasks();此方法中，启动3个定时任务。方法内有statusChangeListener，按需注册是一个事件StatusChangeEvent，状态改变，则向server注册。
     ```

   - com.netflix.discovery.DiscoveryClient构造函数-总结

     ```
     总结DiscoveryClient构造关键过程：
       初始化一堆信息。
       从拉取注册表信息。
       向server注册自己。
       初始化3个任务。
       详细后面继续讲。源码就是这样，得层层拨开。
     ```

   - 拉取注册表信息详解

     ```
     上面的fetchRegistry(false)，点进去，看注释：
       // If the delta is disabled or if it is the first time, get all  applications。
       如果增量式拉取被禁止或第一次拉取注册表，则进行全量拉取：getAndStoreFullRegistry()。
       否则进行增量拉取注册表信息getAndUpdateDelta(applications)。
       一般情况，在Eureka client第一次启动，会进行全量拉取。之后的拉取都尽量尝试只进行增量拉取。
     
       拉取服务注册表：
       全量拉取：getAndStoreFullRegistry();
       增量拉取：getAndUpdateDelta(applications);
     ```

   - 全量拉取

     ```
     进入getAndStoreFullRegistry() 方法，有一方法：eurekaTransport.queryClient.getApplications。
       通过debug发现 实现类是AbstractJerseyEurekaHttpClient，点开，debug出 
       webResource地址为：http://root:root@eureka-7900:7900/eureka/apps/，此端点用于获取server中所有的注册表信息。
       getAndStoreFullRegistry()可能被多个线程同时调用，导致新拉取的注册表被旧的覆盖(如果新拉取的动作设置apps阻塞的情况下）。
       此时用了AutomicLong来进行版本管理，如果更新时版本不一致，不保存apps。
       通过这个判断fetchRegistryGeneration.compareAndSet(currentUpdateGeneration, currentUpdateGeneration + 1)，如果版本一致，并设置新版本（+1），
       接着执行localRegionApps.set(this.filterAndShuffle(apps));过滤并洗牌apps。点开this.filterAndShuffle(apps)实现，继续点apps.shuffleAndIndexInstances，继续点shuffleInstances，继续点application.shuffleAndStoreInstances，继续点_shuffleAndStoreInstances，发现if (filterUpInstances && InstanceStatus.UP != instanceInfo.getStatus())。只保留状态为UP的服务。
     ```

   - 增量拉取

     ```
     回到刚才的fetchRegistry方法中，getAndUpdateDelta，增量拉取。通过getDelta方法，看到实际拉取的地址是：apps/delta，如果获取到的delta为空，则全量拉取。
       通常来讲是3分钟之内注册表的信息变化（在server端判断），获取到delta后，会更新本地注册表。
       增量式拉取是为了维护client和server端 注册表的一致性，防止本地数据过久，而失效，采用增量式拉取的方式，减少了client和server的通信量。
       client有一个注册表缓存刷新定时器，专门负责维护两者之间的信息同步，但是当增量出现意外时，定时器将执行，全量拉取以更新本地缓存信息。更新本地注册表方法updateDelta，有一个细节。
       if (ActionType.ADDED.equals(instance.getActionType())) ，public enum ActionType {
               ADDED, // Added in the discovery server
               MODIFIED, // Changed in the discovery server
               DELETED
               // Deleted from the discovery server
           }，
       在InstanceInfo instance中有一个instance.getActionType()，ADDED和MODIFIED状态的将更新本地注册表applications.addApplication，DELETED将从本地剔除掉existingApp.removeInstance(instance)。
     ```

   - 服务注册

     ```
     好了拉取完eureka server中的注册表了，接着进行服务注册。回到DiscoveryClient构造函数。
       拉取fetchRegistry完后进行register注册。由于构造函数开始时已经将服务实例元数据封装好了instanceInfo，所以此处之间向server发送instanceInfo，
       通过方法httpResponse = eurekaTransport.registrationClient.register(instanceInfo);看到String urlPath = "apps/" + info.getAppName();又是一个server端点，退上去f7，httpResponse.getStatusCode() == Status.NO_CONTENT.getStatusCode();204状态码，则注册成功。
     ```

   - 初始化3个定时任务

     ```
     接着
       // finally, init the schedule tasks (e.g. cluster resolvers, heartbeat, instanceInfo replicator, fetch
       initScheduledTasks();看注释初始化3个定时任务。
       题外话：
       client会定时向server发送心跳，维持自己服务租约的有效性，用心跳定时任务实现;
       而server中会有不同的服务实例注册进来，一进一出，就需要数据的同步。所以client需要定时从server拉取注册表信息，用缓存定时任务实现;
       client如果有变化，也会及时更新server中自己的信息，用按需注册定时任务实现。
     
       就是这三个定时任务。	
     
     进 initScheduledTasks()方法中，clientConfig.shouldFetchRegistry()，
     从server拉取注册表信息。
     int registryFetchIntervalSeconds = clientConfig.getRegistryFetchIntervalSeconds()拉取的时间间隔，eureka.client.registry-fetch-interval-seconds进行设置。
     
     int renewalIntervalInSecs = nstanceInfo.getLeaseInfo().getRenewalIntervalInSecs();心跳定时器，默认30秒。
     
     心跳定时任务和缓存刷新定时任务是有scheduler 的 schedule提交的，鼠标放到scheduler上，看到一句话 A scheduler to be used for the following 3 tasks:- updating service urls- scheduling a TimedSuperVisorTask。
     知道循环逻辑是由TimedSuperVisorTask实现的。
       new TimedSupervisorTask(
                                   "heartbeat",
                                   scheduler,
                                   heartbeatExecutor,
                                   renewalIntervalInSecs,
                                   TimeUnit.SECONDS,
                                   expBackOffBound,
                                   new HeartbeatThread()看到HeartbeatThread线程。
     点进去public void run() {
                   if (renew()) {
                       lastSuccessfulHeartbeatTimestamp = System.currentTimeMillis();
                   }
               }
       里面是renew（）方法。
       
       scheduler.schedule(
                           new TimedSupervisorTask(
                                   "cacheRefresh",
                                   scheduler,
                                   cacheRefreshExecutor,
                                   registryFetchIntervalSeconds,
                                   TimeUnit.SECONDS,
                                   expBackOffBound,
                                   new CacheRefreshThread()
                           ),
       看到CacheRefreshThread，进去，发现 class CacheRefreshThread implements Runnable {
               public void run() {
                   refreshRegistry();
               }
           }是用的refreshRegistry，进去发现fetchRegistry。回到原来讲过的地方。
           
       boolean renew() {
               EurekaHttpResponse<InstanceInfo> httpResponse;
               try {
                   httpResponse = eurekaTransport.registrationClient.sendHeartBeat(instanceInfo.getAppName(), instanceInfo.getId(), instanceInfo, null);
                   logger.debug(PREFIX + "{} - Heartbeat status: {}", appPathIdentifier, httpResponse.getStatusCode());
                   if (httpResponse.getStatusCode() == Status.NOT_FOUND.getStatusCode()) {
                       REREGISTER_COUNTER.increment();
                       logger.info(PREFIX + "{} - Re-registering apps/{}", appPathIdentifier, instanceInfo.getAppName());
                       long timestamp = instanceInfo.setIsDirtyWithTime();
                       boolean success = register();
                       if (success) {
                           instanceInfo.unsetIsDirty(timestamp);
                       }
                       return success;
                   }
                   return httpResponse.getStatusCode() == Status.OK.getStatusCode();
               } catch (Throwable e) {
                   logger.error(PREFIX + "{} - was unable to send heartbeat!", appPathIdentifier, e);
                   return false;
               }
           }看到如果遇到404，server没有此实例，则重新发起注册。如果续约成功返回 200.
           点sendHeartBeat进去String urlPath = "apps/" + appName + '/' + id;
           
     还有一个定时任务，按需注册。当instanceinfo和status发生变化时，需要向server同步，去更新自己在server中的实例信息。保证server注册表中服务实例信息的有效和可用。
       // InstanceInfo replicator
               instanceInfoReplicator = new InstanceInfoReplicator(
                       this,
                       instanceInfo,
                       clientConfig.getInstanceInfoReplicationIntervalSeconds(),
                       2); // burstSize
       
               statusChangeListener = new ApplicationInfoManager.StatusChangeListener() {
                   @Override
                   public String getId() {
                       return "statusChangeListener";
                   }
       
                @Override
                   public void notify(StatusChangeEvent statusChangeEvent) {
                       if (InstanceStatus.DOWN == statusChangeEvent.getStatus() ||
                               InstanceStatus.DOWN == statusChangeEvent.getPreviousStatus()) {
                           // log at warn level if DOWN was involved
                           logger.warn("Saw local status change event {}", statusChangeEvent);
                       } else {
                           logger.info("Saw local status change event {}", statusChangeEvent);
                       }
                       instanceInfoReplicator.onDemandUpdate();
                   }
               };
               if (clientConfig.shouldOnDemandUpdateStatusChange()) {
                   applicationInfoManager.registerStatusChangeListener(statusChangeListener);
               }
           instanceInfoReplicator.start(clientConfig.getInitialInstanceInfoReplicationIntervalSeconds());    
           
     此定时任务有2个部分，
       1：定时刷新服务实例信息和检查应用状态的变化，在服务实例信息发生改变的情况下向server重新发起注册。InstanceInfoReplicator点进去。看到一个方法    
       public void run() {
               try {
                   discoveryClient.refreshInstanceInfo();//刷新instanceinfo。
       			//如果实例信息有变，返回数据更新时间。
                   Long dirtyTimestamp = instanceInfo.isDirtyWithTime();
                   if (dirtyTimestamp != null) {
                       discoveryClient.register();//注册服务实例。
                       instanceInfo.unsetIsDirty(dirtyTimestamp);
                   }
               } catch (Throwable t) {
                   logger.warn("There was a problem with the instance info replicator", t);
               } finally {
               //延时执行下一个检查任务。用于再次调用run方法，继续检查服务实例信息和状态的变化。
                   Future next = scheduler.schedule(this, replicationIntervalSeconds, TimeUnit.SECONDS);
                   scheduledPeriodicRef.set(next);
               }
           }      
     
     refreshInstanceInfo点进去，看方法注释：如果有变化，在下次心跳时，同步向server。
     
     2.注册状态改变监听器，在应用状态发生变化时，刷新服务实例信息，在服务实例信息发生改变时向server注册。  看这段            
        statusChangeListener = new ApplicationInfoManager.StatusChangeListener() {
                       @Override
                       public String getId() {
                           return "statusChangeListener";
                       }
      @Override
                   public void notify(StatusChangeEvent statusChangeEvent) {
                       if (InstanceStatus.DOWN == statusChangeEvent.getStatus() ||
                               InstanceStatus.DOWN == statusChangeEvent.getPreviousStatus()) {
                           // log at warn level if DOWN was involved
                           logger.warn("Saw local status change event {}", statusChangeEvent);
                       } else {
                           logger.info("Saw local status change event {}", statusChangeEvent);
                       }
                       instanceInfoReplicator.onDemandUpdate();
                   }
               };如果状态发生改变，调用onDemandUpdate（），点onDemandUpdate进去，看到InstanceInfoReplicator.this.run();     
               
     总结：两部分，一部分自己去检查，一部分等待状态监听事件。
     
     初始化定时任务完成，最后一步启动步骤完成。接下来就是正常服务于业务。然后消亡。          
     ```

- 服务下线

  ```
  服务下线：在应用关闭时，client会向server注销自己，在Discoveryclient销毁前，会执行下面清理方法。 
  @PreDestroy
  @Override
  public synchronized void shutdown() ，看此方法上有一个注解，表示：在销毁前执行此方法。unregisterStatusChangeListener注销监听器。cancelScheduledTasks取消定时任务。unregister服务下线。eurekaTransport.shutdown关闭jersy客户端 等。
  
  unregister点进去。cancel点进去。AbstractJerseyEurekaHttpClient。String urlPath = "apps/" + appName + '/' + id;看到url和http请求delete方法。      
  ```

  - client源码总结

    ```
    总结：源码其实两部分内容：
    1、client自身的操作。
    2、server的配合。（https://github.com/Netflix/eureka/wiki/Eureka-REST-operations）。
      一切尽在：《Eureka Client工作流程图》
    ```

  

## 11.8 Eureka Server源码

1. Eureka Server功能复习

   接受服务注册
   接受服务心跳
   服务剔除
   服务下线
   集群同步
   获取注册表中服务实例信息

   

   需要注意的是，Eureka Server同时也是一个Eureka Client，在不禁止Eureka Server的客户端行为时，它会向它配置文件中的其他Eureka Server进行拉取注册表、服务注册和发送心跳等操作。

2. 源码解读

   - 启动server注册相关bean

     ```
     注册外部的配置类
     spring-cloud-netflix-eureka-server-2.1.2.REALEASE.jar
     中
     META-INF/spring.factories
     中
     org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
       org.springframework.cloud.netflix.eureka.server.EurekaServerAutoConfiguration
     启动时会自动加载：EurekaServerAutoConfiguration
     功能：向spring的bean工厂添加eureka-server相关功能的bean。
      
     但是EurekaServerAutoConfiguration的生效时有条件的。
      EurekaServerAutoConfiguration上有一个注解：@ConditionalOnBean(EurekaServerMarkerConfiguration.Marker.class)，意思是：只有在Spring容器里有Marker这个类的实例时，才会加载EurekaServerAutoConfiguration，这个就是控制是否开启Eureka Server的关键。
     ```

   - 开启eureka server

     ```
      开关：
     而在@EnableEurekaServer中，@Import(EurekaServerMarkerConfiguration.class)，意思是：动态注入此bean到spring 容器。引入了EurekaServerMarkerConfiguration.class。所以开启了Server服务。所以注册了前面说的：EurekaServerAutoConfiguration
     ```

   - 开启注册

     ```
     在EurekaServerMarkerConfiguration上有@Import(EurekaServerInitializerConfiguration.class)，导入了EurekaServerInitializerConfiguration，
     EurekaServerInitializerConfiguration
     		implements ServletContextAware, SmartLifecycle，SmartLifecycle的作用是：初始化完之后，
     执行public void start()方法。
     		
     ```

  在public void start()中，启动一个线程。看注释：log.info("Started Eureka Server");发布事件：publish(new EurekaRegistryAvailableEvent(getEurekaServerConfig()))，
     告诉client，可以来注册了。

```
 上面提到的 log.info("Started Eureka Server") 的上面一行。eurekaServerBootstrap.contextInitialized(EurekaServerInitializerConfiguration.this.servletContext);
 点contextInitialized进去，看到initEurekaServerContext，初始化eureka 上下文，点initEurekaServerContext进去，看到		
 		// Copy registry from neighboring eureka node
 		int registryCount = this.registry.syncUp();从相邻的eureka 节点复制注册表，
 下一行openForTraffic（主要是和client 交换信息，traffic），查看实现，PeerAwareInstanceRegistryImpl，开启任务postInit，进去之后发现剔除功能（剔除 没有续约的服务）。postInit，点进去，发现new EvictionTask()，点进去，看到run方法中，evict(compensationTimeMs)，点进去就到了，具体剔除逻辑，下面剔除的时候讲。
 ```
```

- PeerAwareInstanceRegistry接口

  ```
  在EurekaServerAutoConfiguration中 有 public EurekaServerContext eurekaServerContext，中有DefaultEurekaServerContext，点进去找到    
  	@PostConstruct
      @Override
      public void initialize() {
          logger.info("Initializing ...");
       peerEurekaNodes.start();
          try {
           registry.init(peerEurekaNodes);
          } catch (Exception e) {
              throw new RuntimeException(e);
          }
          logger.info("Initialized");
      }，其中peerEurekaNodes.start();启动一个只拥有一个线程的线程池，第一次进去会更新一下集群其他节点信息。registry.init(peerEurekaNodes);鼠标放在registry上，发现是PeerAwareInstanceRegistryImpl ， 的 注册信息管理类里面的init方法。PeerAwareInstanceRegistry是个接口，实现类是：PeerAwareInstanceRegistryImpl。PeerAwareInstanceRegistry接口，实现了com.netflix.eureka.registry.InstanceRegistry。	
  ```

- 服务实例注册表

  ```
  Server是围绕注册表管理的。有两个InstanceRegistry。
  com.netflix.eureka.registry.InstanceRegistry是euraka server中注册表管理的核心接口。职责是在内存中管理注册到Eureka Server中的服务实例信息。实现类有PeerAwareInstanceRegistryImpl。
     
  org.springframework.cloud.netflix.eureka.server.InstanceRegistry对PeerAwareInstanceRegistryImpl进行了继承和扩展，使其适配Spring cloud的使用环境，主要的实现由PeerAwareInstanceRegistryImpl提供。
     
  com.netflix.eureka.registry.InstanceRegistry extends LeaseManager<InstanceInfo>, LookupService<String> 。LeaseManager<InstanceInfo>是对注册到server中的服务实例租约进行管理。LookupService<String>是提供服务实例的检索查询功能。
     
  LeaseManager<InstanceInfo>接口的作用是对注册到Eureka Server中的服务实例租约进行管理，方法有：服务注册，下线，续约，剔除。此接口管理的类目前是InstanceInfo。InstanceInfo代表服务实例信息。
  
  PeerAwareInstanceRegistryImpl 增加了对peer节点的同步复制操作。使得eureka server集群中注册表信息保持一致。
  ```

- 接受服务注册

  > 《eureka服务端注册》

  ```
  我们学过Eureka Client在发起服务注册时会将自身的服务实例元数据封装在InstanceInfo中，然后将InstanceInfo发送到Eureka Server。Eureka Server在接收到Eureka Client发送的InstanceInfo后将会尝试将其放到本地注册表中以供其他Eureka Client进行服务发现。
  我们学过：通过 eureka/apps/{服务名}注册
  
  在EurekaServerAutoConfiguration中定义了 public FilterRegistrationBean jerseyFilterRegistration ，表名了 表明eureka-server使用了Jersey实现 对外的 restFull接口。注册一个 Jersey 的 filter ，配置好相应的Filter 和 url映射。
  
  -----------
  ```

```
public javax.ws.rs.core.Application jerseyApplication(方法：中。
 provider.addIncludeFilter(new AnnotationTypeFilter(Path.class));
		provider.addIncludeFilter(new AnnotationTypeFilter(Provider.class));
 添加一些过滤器，类似于过滤请求地址，Path类似于@RequestMapping，Provider类似于@Controller		
```

------

```
 在com.netflix.eureka.resources包下，是Eureka Server对于Eureka client的REST请求的定义。看ApplicationResource类（这是一类请求，应用类的请求），类似于应用@Controller注解：@Produces({"application/xml", "application/json"})，接受xml和json。见名识意 public Response addInstance。添加实例instanceinfo。 方法中，有一句：
 registry.register(info, "true".equals(isReplication));鼠标放在registry上PeerAwareInstanceRegistry接口，点击void register方法。发现 是PeerAwareInstanceRegistryImpl 的方法：public void register(final InstanceInfo info, final boolean isReplication) ，中有一句：super.register(info, leaseDuration, isReplication);
 进入下面正题：
 com.netflix.eureka.registry.AbstractInstanceRegistry
 register方法
 
 在register中，服务实例的InstanceInfo保存在Lease中，Lease在AbstractInstanceRegistry中统一通过ConcurrentHashMap保存在内存中。在服务注册过程中，会先获取一个读锁，防止其他线程对registry注册表进行数据操作，避免数据的不一致。然后从resgitry查询对应的InstanceInfo租约是否已经存在注册表中，根据appName划分服务集群，使用InstanceId唯一标记服务实例。如果租约存在，比较两个租约中的InstanceInfo的最后更新时间lastDirtyTimestamp，保留时间戳大的服务实例信息InstanceInfo。如果租约不存在，意味这是一次全新的服务注册，将会进行自我保护的统计，创建新的租约保存InstanceInfo。接着将租约放到resgitry注册表中。
 之后将进行一系列缓存操作并根据覆盖状态规则设置服务实例的状态，缓存操作包括将InstanceInfo加入用于统计Eureka Client增量式获取注册表信息的recentlyChangedQueue和失效responseCache中对应的缓存。最后设置服务实例租约的上线时间用于计算租约的有效时间，释放读锁并完成服务注册。
```

​     

- 接受心跳 续租，renew

  > 《Eureka服务端接收心跳》

  ```
  在Eureka Client完成服务注册之后，它需要定时向Eureka Server发送心跳请求(默认30秒一次)，维持自己在Eureka Server中租约的有效性。
  
  看另一类请求com.netflix.eureka.resources.InstanceResource。下public Response renewLease(方法。看到一行boolean isSuccess = registry.renew(app.getName(), id, isFromReplicaNode);
  点击renew的实现。
  进入下面正题：
  ```

```
Eureka Server处理心跳请求的核心逻辑位于AbstractInstanceRegistry#renew方法中。renew方法是对Eureka Client位于注册表中的租约的续租操作，不像register方法需要服务实例信息，仅根据服务实例的服务名和服务实例id即可更新对应租约的有效时间。
 com.netflix.eureka.registry.AbstractInstanceRegistry
renew
 //根据appName获取服务集群的租约集合
  Map<String, Lease<InstanceInfo>> gMap = registry.get(appName);
  //查看服务实例状态
   InstanceStatus overriddenInstanceStatus = this.getOverriddenInstanceStatus(
                         instanceInfo, leaseToRenew, isReplication);
                 if (overriddenInstanceStatus == InstanceStatus.UNKNOWN) {
 //统计每分钟续租次数
 renewsLastMin.increment();
 //更新租约
 leaseToRenew.renew();
 
 此方法中不关注InstanceInfo，仅关注于租约本身以及租约的服务实例状态。如果根据服务实例的appName和instanceInfoId查询出服务实例的租约，并且根据#getOverriddenInstanceStatus方法得到的instanceStatus不为InstanceStatus.UNKNOWN，那么更新租约中的有效时间，即更新租约Lease中的lastUpdateTimestamp，达到续约的目的；如果租约不存在，那么返回续租失败的结果。
```

- 服务剔除

  ```
  如果Eureka Client在注册后，既没有续约，也没有下线(服务崩溃或者网络异常等原因)，那么服务的状态就处于不可知的状态，不能保证能够从该服务实例中获取到回馈，所以需要服务剔除此方法定时清理这些不稳定的服务，该方法会批量将注册表中所有过期租约剔除。
  
  剔除是定时任务，默认60秒执行一次。延时60秒，间隔60秒
          evictionTimer.schedule(evictionTaskRef.get(),
                  serverConfig.getEvictionIntervalTimerInMs(),
                  serverConfig.getEvictionIntervalTimerInMs());
                  
  从上面eureka server启动来看，剔除的任务，是线程启动的，执行的是下面的方法。
  com.netflix.eureka.registry.AbstractInstanceRegistry
  evict
  
  判断是否开启自我保护
  if (!isLeaseExpirationEnabled()) {
  如果开启自我保护，不剔除。点进去isLeaseExpirationEnabled，查看实现类，有一个isSelfPreservationModeEnabled，点进去    @Override
      public boolean isSelfPreservationModeEnabled() {
          return serverConfig.shouldEnableSelfPreservation();
      }，发现EurekaServerConfig，的方法shouldEnableSelfPreservation，看其实现中有EurekaServerConfigBean，发现属性：enableSelfPreservation。
  
  
  紧接着一个大的for循环，便利注册表register，依次判断租约是否过期。一次性获取所有的过期租约。
  
  //获取注册表租约总数
  int registrySize = (int) getLocalRegistrySize();
  计算注册表租约的阈值 （总数乘以 续租百分比），得出要续租的数量
  int registrySizeThreshold = (int) (registrySize *    serverConfig.getRenewalPercentThreshold());
          
  总数减去要续租的数量，就是理论要剔除的数量        
  int evictionLimit = registrySize - registrySizeThreshold;
  
  //求 上面理论剔除数量，和过期租约总数的最小值。就是最终要提出的数量。
  int toEvict = Math.min(expiredLeases.size(), evictionLimit);
  
  然后剔除。用internalCancel(appName, id, false);执行 服务下线将服务从注册表清除掉。
  
  剔除的限制：
  1.自我保护期间不清除。
  2.分批次清除。
  ```

```
3.服务是逐个随机剔除，剔除均匀分布在所有应用中，防止在同一时间内同一服务集群中的服务全部过期被剔除，造成在大量剔除服务时，并在进行自我保护时，促使程序崩溃。
```

  EurekaServerInitializerConfiguration的 eurekaServerBootstrap.contextInitialized(方法，中initEurekaServerContext();点进去this.registry.openForTraffic(this.applicationInfoManager, registryCount);点进去，super.postInit();点进去evictionTaskRef.set(new EvictionTask());
             evictionTimer.schedule(evictionTaskRef.get(),
                  serverConfig.getEvictionIntervalTimerInMs(),
                     serverConfig.getEvictionIntervalTimerInMs());
     发现 定时任务。                
     

```
 剔除服务是个定时任务，用EvictionTask执行，默认60秒执行一次，延时60秒执行。定时剔除过期服务。
 
 服务剔除将会遍历registry注册表，找出其中所有的过期租约，然后根据配置文件中续租百分比阀值和当前注册表的租约总数量计算出最大允许的剔除租约的数量(当前注册表中租约总数量减去当前注册表租约阀值)，分批次剔除过期的服务实例租约。对过期的服务实例租约调用AbstractInstanceRegistry#internalCancel服务下线的方法将其从注册表中清除掉。
```

​     自我保护机制主要在Eureka Client和Eureka Server之间存在网络分区的情况下发挥保护作用，在服务器端和客户端都有对应实现。假设在某种特定的情况下(如网络故障)，Eureka Client和Eureka Server无法进行通信，此时Eureka Client无法向Eureka Server发起注册和续约请求，Eureka Server中就可能因注册表中的服务实例租约出现大量过期而面临被剔除的危险，然而此时的Eureka Client可能是处于健康状态的(可接受服务访问)，如果直接将注册表中大量过期的服务实例租约剔除显然是不合理的。
​     针对这种情况，Eureka设计了“自我保护机制”。在Eureka Server处，如果出现大量的服务实例过期被剔除的现象，那么该Server节点将进入自我保护模式，保护注册表中的信息不再被剔除，在通信稳定后再退出该模式；在Eureka Client处，如果向Eureka Server注册失败，将快速超时并尝试与其他的Eureka Server进行通信。“自我保护机制”的设计大大提高了Eureka的可用性。
​     

- 服务下线

  > 《Eureka服务下线》

  ```
  Eureka Client在应用销毁时，会向Eureka Server发送服务下线请求，清除注册表中关于本应用的租约，避免无效的服务调用。在服务剔除的过程中，也是通过服务下线的逻辑完成对单个服务实例过期租约的清除工作。
  
  在InstanceResource中，    public Response cancelLease(
              @HeaderParam(PeerEurekaNode.HEADER_REPLICATION) String isReplication)
  一行代码：boolean isSuccess = registry.cancel(app.getName(), id,
                  "true".equals(isReplication));点进去cancel，发现：internalCancel(appName, id, isReplication); 查看实现：        
  
  先获取读锁，防止被其他线程修改
  read.lock();
  根据appName获取服务实力集群。
  Map<String, Lease<InstanceInfo>> gMap = registry.get(appName);
  在内存中取消实例 id的服务
  if (gMap != null) {
                  leaseToCancel = gMap.remove(id);
              }
  ```

```
添加到最近下线服务的统计队列
 synchronized (recentCanceledQueue) {
              recentCanceledQueue.add(new Pair<Long, String>(System.currentTimeMillis(), appName + "(" + id + ")"));
             }
             
 往下判断leaseToCancel是否为空，租约不存在，返回false，
 如果存在，
 设置租约下线时间。 leaseToCancel.cancel();
 InstanceInfo instanceInfo = leaseToCancel.getHolder();
 获取持有租约的服务信息，标记服务实例为instanceInfo.setActionType(ActionType.DELETED);
 添加到租约变更记录队列
  recentlyChangedQueue.add(new RecentlyChangedItem(leaseToCancel));用于eureka client的增量拉取注册表信息。
 释放锁。
 
 首先通过registry根据服务名和服务实例id查询关于服务实例的租约Lease是否存在，统计最近请求下线的服务实例用于Eureka Server主页展示。如果租约不存在，返回下线失败；如果租约存在，从registry注册表中移除，设置租约的下线时间，同时在最近租约变更记录队列中添加新的下线记录，以用于Eureka Client的增量式获取注册表信息。
```

- 集群同步

  ```
  如果Eureka Server是通过集群的方式进行部署，那么为了维护整个集群中Eureka Server注册表数据的一致性，势必需要一个机制同步Eureka Server集群中的注册表数据。
  
  Eureka Server集群同步包含两个部分，
  一部分是Eureka Server在启动过程中从它的peer节点中拉取注册表信息，并将这些服务实例的信息注册到本地注册表中；
  另一部分是Eureka Server每次对本地注册表进行操作时，同时会将操作同步到它的peer节点中，达到集群注册表数据统一的目的。
  
  1.启动拉取别的peer
  在Eureka Server启动类中：EurekaServerInitializerConfiguration位于EurekaServerAutoConfiguration 的import注解中。一行：eurekaServerBootstrap.contextInitialized(
  进去：initEurekaServerContext();，点进去，一行：int registryCount = this.registry.syncUp();
  看注释：拉取注册表从邻近节点。点击syncUp()的实现方法进去：
  看循环：意思是，如果是i第一次进来，为0，不够等待的代码，直接执行下面的拉取服务实例。
  将自己作为一个eureka client，拉取注册表。并通过register(instance, instance.getLeaseInfo().getDurationInSecs(), true)注册到自身的注册表中。
  
  Eureka Server也是一个Eureka Client，在启动的时候也会进行DiscoveryClient的初始化，会从其对应的Eureka Server中拉取全量的注册表信息。在Eureka Server集群部署的情况下，Eureka Server从它的peer节点中拉取到注册表信息后，将遍历这个Applications，将所有的服务实例通过AbstractRegistry#register方法注册到自身注册表中。
  
  		int registryCount = this.registry.syncUp();
  		this.registry.openForTraffic(this.applicationInfoManager, registryCount);
  当执行完上面的syncUp逻辑后，在下面的openForTraffic，开启此server接受别的client注册，拉取注册表等操作。而在它首次拉取其他peer节点时，是不允许client的通信请求的。
  
  在openForTraffic中，初始化期望client发送过来的服务数量，即上面获取到的服务数量this.expectedNumberOfClientsSendingRenews = count;
  updateRenewsPerMinThreshold点进去，是计算自我保护的统计参数：
  this.numberOfRenewsPerMinThreshold = (int) (this.expectedNumberOfClientsSendingRenews
                  * (60.0 / serverConfig.getExpectedClientRenewalIntervalSeconds())
                  * serverConfig.getRenewalPercentThreshold());
  服务数*（每个服务每分钟续约次数）*阈值
  if (count > 0) {
              this.peerInstancesTransferEmptyOnStartup = false;
          }
  如果count=0，没有拉取到注册表信息，将此值设为true，表示其他peer来取空的实例信息，意味着，将不允许client从此server获取注册表信息。如果count>0，将此值设置为false，允许client来获取注册表。
  
  后面将服务置为上线，并开启剔除的定时任务。
  
  当Server的状态不为UP时，将拒绝所有的请求。在Client请求获取注册表信息时，Server会判断此时是否允许获取注册表中的信息。上述做法是为了避免Eureka Server在#syncUp方法中没有获取到任何服务实例信息时(Eureka Server集群部署的情况下)，Eureka Server注册表中的信息影响到Eureka Client缓存的注册表中的信息。因为是全量同步，如果server什么也没同步过来，会导致client清空注册表。导致服务调用出问题。
  
  
  2.Server之间注册表信息的同步复制
  为了保证Eureka Server集群运行时注册表信息的一致性，每个Eureka Server在对本地注册表进行管理操作时，会将相应的操作同步到所有peer节点中。
  
  在外部调用server的restful方法时，在com.netflix.eureka.resources包下的ApplicationResource资源中，查看每个服务的操作。比如服务注册public Response addInstance(，此方法中有
  registry.register(info, "true".equals(isReplication));点进去实现类：replicateToPeers(Action.Register, info.getAppName(), info.getId(), info, null, isReplication);这是一种情况。
  ```

```
在PeerAwareInstanceRegistryImpl类中，看其他操作，cancel，renew等中都有replicateToPeers，
 此方法中有个peerEurekaNodes，代表一个可同步数据的eureka Server的集合，如果注册表有变化，向此中的peer节点同步。

 replicateToPeers方法，它将遍历Eureka Server中peer节点，向每个peer节点发送同步请求。
             for (final PeerEurekaNode node : peerEurekaNodes.getPeerEurekaNodes()) {
                 // If the url represents this host, do not replicate to yourself.
                 if (peerEurekaNodes.isThisMyUrl(node.getServiceUrl())) {
                     continue;
                 }
                 replicateInstanceActionsToPeers(action, appName, id, info, newStatus, node);
             }
 此replicateInstanceActionsToPeers方法中，类PeerEurekaNode的实例node的各种方法，cancel，register，等，用了batchingDispatcher.process(，作用是将同一时间段内，相同服务实例的相同操作将使用相同的任务编号，在进行同步复制的时候，将根据任务编号合并操作，减少同步操作的数量和网络消耗，但是同时也造成了同步复制的延时性，不满足CAP中的C（强一致性）。
 所以Eureka，只满足AP。
 
 通过Eureka Server在启动过程中初始化本地注册表信息和Eureka Server集群间的同步复制操作，最终达到了集群中Eureka Server注册表信息一致的目的。
```

- 获取注册表中服务实例信息

  

```
Eureka Server中获取注册表的服务实例信息主要通过两个方法实现：AbstractInstanceRegistry#getApplicationsFromMultipleRegions从多地区获取全量注册表数据，AbstractInstanceRegistry#getApplicationDeltasFromMultipleRegions从多地区获取增量式注册表数据。
 
 1、全量：
 上面讲到从节点复制注册信息的时候，用方法public int syncUp() ，一行Applications apps = eurekaClient.getApplications();点进去实现类，有一行getApplicationsFromAllRemoteRegions(); 下面getApplicationsFromMultipleRegions，作用从多个地区中获取全量注册表信息，并封装成Applications返回，它首先会将本地注册表registry中的所有服务实例信息提取出来封装到Applications中，再根据是否需要拉取Region的注册信息，将远程拉取过来的Application放到上面的Applications中。最后得到一个全量的Applications。
 2、在前面提到接受服务注册，接受心跳等方法中，都有recentlyChangedQueue.add(new RecentlyChangedItem(lease));作用是将新变动的服务放到最近变化的服务实例信息队列中，用于记录增量是注册表信息。getApplicationDeltasFromMultipleRegions，实现了从远处eureka server中获取增量式注册表信息的能力。
 
 在EurekaServer对外restful中，在com.netflix.eureka.resources下，    
 @GET
     public Response getApplication(@PathParam("version") String version,
                                    @HeaderParam("Accept") final String acceptHeader,
                                    @HeaderParam(EurekaAccept.HTTP_X_EUREKA_ACCEPT) String eurekaAccept) {
                                    
 其中有一句：String payLoad = responseCache.get(cacheKey);在responseCache初始化的时候，它的构造方法ResponseCacheImpl(EurekaServerConfig serverConfig, ServerCodecs serverCodecs, AbstractInstanceRegistry registry) {中，Value value = generatePayload(key);点进去有一句：registry.getApplicationDeltasFromMultipleRegions(key.getRegions()));从远程获取delta增量注册信息。但是这个只是向client提供，不向server提供，因为server可以通过每次变更自动同步到peer。
 
 获取增量式注册表信息将会从recentlyChangedQueue中获取最近变化的服务实例信息。recentlyChangedQueue中统计了近3分钟内进行注册、修改和剔除的服务实例信息，在服务注册AbstractInstanceRegistry#registry、接受心跳请求AbstractInstanceRegistry#renew和服务下线AbstractInstanceRegistry#internalCancel等方法中均可见到recentlyChangedQueue对这些服务实例进行登记，用于记录增量式注册表信息。#getApplicationsFromMultipleRegions方法同样提供了从远程Region的Eureka Server获取增量式注册表信息的能力。
```

------

2020-01-19 第二节课完。

eureka源码总结：

1. client

- 拉取server注册表到本地。
- 注册服务。
- 初始化3个定时任务：心跳续约，定时拉取注册表，按需注册（InstanceInfoReplicator#run）。
- 发送下线。

2. server

- 接受服务注册
- 接受服务心跳
- 服务剔除
- 服务下线
- 集群同步（1.启动时从peer拉取信息，2.将注册到自己的服务同步到peer）



画了些图：

《Eureka服务端注册》
《Eureka服务端接收心跳》
《Eureka服务下线》



------



## 11.9 Eureka元数据

获取元数据信息

1. 从Eureka Server获取：

   启动eureka-7900。

   启动一个service-sms（8002）

   访问：http://localhost:7900/eureka/apps/service-sms
   看到如下结果：

   ```sh
   <application>
   <name>SERVICE-SMS</name>
   <instance>
   <instanceId>30.136.133.11:service-sms:8002</instanceId>
   <hostName>30.136.133.11</hostName>
   <app>SERVICE-SMS</app>
   <ipAddr>30.136.133.11</ipAddr>
   <status>UP</status>
   <overriddenstatus>UNKNOWN</overriddenstatus>
   <port enabled="true">8002</port>
   <securePort enabled="false">443</securePort>
   <countryId>1</countryId>
   <dataCenterInfo class="com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo">
   <name>MyOwn</name>
   </dataCenterInfo>
   <leaseInfo>
   <renewalIntervalInSecs>1</renewalIntervalInSecs>
   <durationInSecs>1</durationInSecs>
   <registrationTimestamp>1579673388471</registrationTimestamp>
   <lastRenewalTimestamp>1579673470319</lastRenewalTimestamp>
   <evictionTimestamp>0</evictionTimestamp>
   <serviceUpTimestamp>1579673387755</serviceUpTimestamp>
   </leaseInfo>
   <metadata>
   <user.password>root</user.password>
   <management.port>8002</management.port>
   <jmx.port>62449</jmx.port>
   <user.name>root</user.name>
   </metadata>
   <homePageUrl>http://30.136.133.11:8002/</homePageUrl>
   <statusPageUrl>http://30.136.133.11:8002/actuator/info</statusPageUrl>
   <healthCheckUrl>http://30.136.133.11:8002/actuator/health</healthCheckUrl>
   <vipAddress>service-sms</vipAddress>
   <secureVipAddress>service-sms</secureVipAddress>
   <isCoordinatingDiscoveryServer>false</isCoordinatingDiscoveryServer>
   <lastUpdatedTimestamp>1579673388471</lastUpdatedTimestamp>
   <lastDirtyTimestamp>1579673387301</lastDirtyTimestamp>
   <actionType>ADDED</actionType>
   </instance>
   </application>
   ```

2. 手写获取元数据。

   在一个eureka client（api-listen-order）中，写一个controller

```sh
import org.springframework.cloud.client.discovery.DiscoveryClient;

@RestController
@RequestMapping("/service-instance")
public class ServiceInstanceController {
	
	@Autowired
	private DiscoveryClient discoveryClient;
	
	@GetMapping("/query-by-application-name/{applicationName}")
	public List<ServiceInstance> getInstance(@PathVariable String applicationName){
		
		return discoveryClient.getInstances(applicationName);
		
	}

}
```

演示

启动eureka（7900）单节点

启动api-listen-order

```
实例演示：
获取服务元数据：

访问地址：
自己开发：
http://localhost:8084/service-instance/query-by-application-name/api-listen-order

http://localhost:8084/service-instance/query-by-application-name/service-sms

结果看到一堆json信息，

表示api-listen-order在eureka server中的实例信息。



```

1. 标准元数据和自定义元数据

2. 标准元数据：主机名，ip，端口，健康检查等信息。会被发布到注册表中，用于服务间调用。

3. 自定义元数据：

   ```sh
   eureka.instance.metadata-map：
     自定义key:自定义value
   ```

   远程客户端访问。自定义属性。

   api-listen-order中ServiceInstanceController，读取instance信息。

   ```sh
   启动eureka，api-listen-order,
   
   http://localhost:8084/service-instance/query-by-application-name/api-listen-order
   
   结果：
   {
   	"host": "127.0.0.1",
   	"port": 8084,
   	"metadata": {
   		"yueyi": "2019",
   		"user.password": "root",
   		"management.port": "8084",
   		"jmx.port": "61378",
   		"user.name": "root"
   	},
   	"secure": false,
   	"uri": "http://127.0.0.1:8084",
   	"instanceId": "api-listen-order:30.136.133.11:port",
   	"serviceId": "API-LISTEN-ORDER",
   	"instanceInfo": {
   		"instanceId": "api-listen-order:30.136.133.11:port",
   		"app": "API-LISTEN-ORDER",
   		"appGroupName": null,
   		"ipAddr": "127.0.0.1",
   		"sid": "na",
   		"homePageUrl": "http://127.0.0.1:8084/",
   		"statusPageUrl": "http://127.0.0.1:8084/actuator/info",
   		"healthCheckUrl": "http://127.0.0.1:8084/actuator/health",
   		"secureHealthCheckUrl": null,
   		"vipAddress": "api-listen-order",
   		"secureVipAddress": "api-listen-order",
   		"countryId": 1,
   		"dataCenterInfo": {
   			"@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
   			"name": "MyOwn"
   		},
   		"hostName": "127.0.0.1",
   		"status": "UP",
   		"overriddenStatus": "UNKNOWN",
   		"leaseInfo": {
   			"renewalIntervalInSecs": 1,
   			"durationInSecs": 1,
   			"registrationTimestamp": 1579489514655,
   			"lastRenewalTimestamp": 1579489524146,
   			"evictionTimestamp": 0,
   			"serviceUpTimestamp": 1579489514147
   		},
   		"isCoordinatingDiscoveryServer": false,
   		"metadata": {
   			"yueyi": "2019",
   			"user.password": "root",
   			"management.port": "8084",
   			"jmx.port": "61378",
   			"user.name": "root"
   		},
   		"lastUpdatedTimestamp": 1579489514655,
   		"lastDirtyTimestamp": 1579489514111,
   		"actionType": "ADDED",
   		"asgName": null
   	},
   	"scheme": null
   }
   ```

4. 自定义元数据，可以结合ribbon 分发规则使用。网关 做 分发规则 选择 服务时，可以作为依据。灰度发布。有作用。后面负载均衡有例子。此时只认识元数据。基于元数据做的例子，后面有。

   ```sh
   灰度发布例子：
   
   client端：
   eureka:
     instance:
       metadataMap:
         tag: pre-prd
   
   调用方：
   List<ServiceInstance> instances =  discoveryClient.getInstances(applicationName);
   		for (ServiceInstance serviceInstance : instances) {
   			Map<String, String> metadata = serviceInstance.getMetadata();
   			String metaValue = metadata.get("yueyi");
   			log.info("元数据："+metaValue);
   		}
   获取到元数据。
   
   分发的时候，根据app客户端传过来的参数：比如加tag=pre,只分发到pre-prd的eureka client。
   在新发布的app版本带tag参数。就可以实现灰度测试。
   ```



## 11.10 自我保护计算



1. 红色警告

   ```sh
      EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY'RE NOT. RENEWALS ARE LESSER THAN THRESHOLD AND HENCE THE INSTANCES ARE NOT BEING EXPIRED JUST TO BE SAFE.
   ```

   

2. 默认情况下，Eureka Server在一定时间内，没有接收到某个微服务心跳，会将某个微服务注销（90S）。但是当网络故障时，微服务与Server之间无法正常通信，上述行为就非常危险，因为微服务正常，不应该注销。

   Eureka Server通过自我保护模式来解决整个问题，当Server在短时间内丢失过多客户端时，那么Server会进入自我保护模式，会保护注册表中的微服务不被注销掉。当网络故障恢复后，退出自我保护模式。

3. 思想：宁可保留健康的和不健康的，也不盲目注销任何健康的服务。

4. 关闭自我保护。

   ```sh
   eureka:
     server: 
       enable-self-preservation: false
   ```

5. 自我保护触发

   自我保护机制的触发条件：
   （当每分钟心跳次数( renewsLastMin ) 小于 numberOfRenewsPerMinThreshold 时，并且开启自动保护模式开关( eureka.server.enable-self-preservation = true ) 时，触发自我保护机制，不再自动过期租约。）
   numberOfRenewsPerMinThreshold = expectedNumberOfRenewsPerMin * 续租百分比( eureka.server.renewalPercentThreshold, 默认0.85 )
   expectedNumberOfRenewsPerMin = 当前注册的应用实例数 x 2
   为什么乘以 2：
   默认情况下，注册的应用实例每半分钟续租一次，那么一分钟心跳两次，因此 x 2 。

   服务实例数：10个，期望每分钟续约数：10 * 2=20，期望阈值：20*0.85=17，自我保护少于17时 触发。

   剔除：

   ```sh
       AbstractInstanceRegistry
       
       public void evict(long additionalLeaseMs) {
           logger.debug("Running the evict task");
   
           if (!isLeaseExpirationEnabled()) {
               logger.debug("DS: lease expiration is currently disabled.");
               return;
       }
       此代码意思：if中判断为true，不走此逻辑，走下面的剔除。如果if为false。走此逻辑，不剔除。
   ```

   

   ```sh
   PeerAwareInstanceRegistryImpl
   
       @Override
       public boolean isLeaseExpirationEnabled() {
           if (!isSelfPreservationModeEnabled()) {
           //如果打开自我保护，不进入此逻辑。
               // The self preservation mode is disabled, hence allowing the instances to expire.
               return true;
           }
           return numberOfRenewsPerMinThreshold > 0 && getNumOfRenewsInLastMin() > numberOfRenewsPerMinThreshold;
       }
   ```

   > 《自我保护流程图》

   

## 11.11 多网卡选择

1. ip注册

```sh
eureka:
  instance:
    prefer-ip-address: true
表示将自己的ip注册到EurekaServer上。不配置或false，表示将操作系统的hostname注册到server
```

2. 服务器有多个网卡，eh0，eh1，eh2，只有eh0可以让外部其他服务访问进来，而Eureka client将eh1和eh2注册到Eureka server上，这样其他服务就无法访问该微服务了。

3. 指定Ip

   ```sh
   eureka:
     instance:
       prefer-ip-address: true
       ip-address: 实际能访问到的Ip
   ```

   如果设置了此时的ip-address，在元数据查看到就是此ip，其他服务也通过此ip来调用。

   ```sh
   {
   	"host": "127.0.0.1",
   	"port": 8084,
   	"metadata": {
   		"yueyi": "2019",
   		"user.password": "root",
   		"management.port": "8084",
   		"jmx.port": "61378",
   		"user.name": "root"
   	},
   	"secure": false,
   	"uri": "http://127.0.0.1:8084",
   	"instanceId": "api-listen-order:30.136.133.11:port",
   	"serviceId": "API-LISTEN-ORDER",
   	"instanceInfo": {
   		"instanceId": "api-listen-order:30.136.133.11:port",
   		"app": "API-LISTEN-ORDER",
   		"appGroupName": null,
   		"ipAddr": "127.0.0.1",
   		"sid": "na",
   		"homePageUrl": "http://127.0.0.1:8084/",
   		"statusPageUrl": "http://127.0.0.1:8084/actuator/info",
   		"healthCheckUrl": "http://127.0.0.1:8084/actuator/health",
   		"secureHealthCheckUrl": null,
   		"vipAddress": "api-listen-order",
   		"secureVipAddress": "api-listen-order",
   		"countryId": 1,
   		"dataCenterInfo": {
   			"@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
   			"name": "MyOwn"
   		},
   		"hostName": "127.0.0.1",
   		"status": "UP",
   		"overriddenStatus": "UNKNOWN",
   		"leaseInfo": {
   			"renewalIntervalInSecs": 1,
   			"durationInSecs": 1,
   			"registrationTimestamp": 1579489514655,
   			"lastRenewalTimestamp": 1579489524146,
   			"evictionTimestamp": 0,
   			"serviceUpTimestamp": 1579489514147
   		},
   		"isCoordinatingDiscoveryServer": false,
   		"metadata": {
   			"yueyi": "2019",
   			"user.password": "root",
   			"management.port": "8084",
   			"jmx.port": "61378",
   			"user.name": "root"
   		},
   		"lastUpdatedTimestamp": 1579489514655,
   		"lastDirtyTimestamp": 1579489514111,
   		"actionType": "ADDED",
   		"asgName": null
   	},
   	"scheme": null
   }
   ```

   

## 11.12 Eureka 健康检查

> 《健康检查效果》

由于server和client通过心跳保持 服务状态，而只有状态为UP的服务才能被访问。看eureka界面中的status。

比如心跳一直正常，服务一直UP，但是此服务DB连不上了，无法正常提供服务。

此时，我们需要将 微服务的健康状态也同步到server。只需要启动eureka的健康检查就行。这样微服务就会将自己的健康状态同步到eureka。配置如下即可。

在client端配置：将自己的健康状态传播到server。

```sh
eureka:
  client:
    healthcheck:
      enabled: true
```

健康检查组件

## 11.13 Eureka 配置

​    EurekaServerConfigBean：Eureka Server配置。

​	EurekaInstanceConfigBean：Eureka Client实例配置。

​	EurekaClientConfigBean：Eureka Client 客户端和服务端交互配置。

1. instanceId。
2. 通过ip注册。
3. 配置服务快速下线。

```sh
server:
eureka:
  server: 
  	#关闭自我保护
    enable-self-preservation: false
    #清理服务间隔时间，毫秒
    eviction-interval-timer-in-ms: 5000
    
client：
eureka: 
  client:
    healthcheck:
      #开启健康检查，需要引入actuator
      enabled: true
  instance: 
    #发送心跳给server的频率，每隔这个时间会主动心跳一次
    lease-renewal-interval-in-seconds: 1      
    #Server从收到client后，下一次收到心跳的间隔时间。超过这个时间没有接收到心跳EurekaServer就会将这个实例剔除
    lease-expiration-duration-in-seconds: 1   
```



其他配置：

```sh
"homePageUrl": "http://127.0.0.1:8084/",
"statusPageUrl": "http://127.0.0.1:8084/actuator/info",
"healthCheckUrl": "http://127.0.0.1:8084/actuator/health",

如果设置了

server:
    servlet:
        path: /path

需要：

eureka:
    instance:
        statusPageUrlPath: ${server.servlet.path}/actuator/info
        healthCheckUrlPath: ${server.servlet.path}/actuator/health
```



## 11.14 Eureka监听事件

EurekaInstanceCanceledEvent 服务下线事件

EurekaInstanceRegisteredEvent 服务注册事件

EurekaInstanceRenewedEvent 服务续约事件

EurekaRegistryAvailableEvent 注册中心可用事件

EurekaServerStartedEvent  注册中心启动

```sh
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CustomEvent {
	
	@EventListener
	public void listen(EurekaInstanceCanceledEvent e) {
		System.out.println(e.getServerId()+"下线事件");
	}
}

将Eureka Client停止后打印：
api-listen-order:30.136.133.9:port下线事件
```



## 11.15 Eureka缺陷

由于集群间的同步复制是通过HTTP的方式进行，基于网络的不可靠性，集群中的Eureka Server间的注册表信息难免存在不同步的时间节点，不满足CAP中的C(数据一致性)。



## 11.16 总结

eureka总结：

1. 节点搭建（单节点，多节点）
2. client和server功能
3. 端点
4. 原理
5. 源码
6. 元数据
7. 自我保护
8. 多网卡选择
9. 健康检查
10. 缺陷
11. 监听事件

------



后面例子：

后面有服务调用，便于理解例子

有几张图：《网约车整体架构图》，《乘客端整体设计》，《项目小知识》