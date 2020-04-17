# 网关

## 概念

服务治理，服务注册发现，服务调用，熔断。已经学完。

微服务基本模块已经有了，也可以做微服务了。但完成一个复杂的业务，可能需要多个微服务合作来完成，比如下单，需要用户服务，支付服务，地图服务，订单服务。一般是我们对外服务的窗口，进行服务内外隔离。一般微服务都在内网，不做安全验证，

就好像：很多明星，可以独立开演唱会（独立提供服务）。也可以去春晚（微服务群提供服务）。但一台春晚就不能让 观众一个一个调用了。观众要调用，需要检票啥的，检票就类似于网关，进来之后，界面随便看，不会说你 看个小品，还需要再检票。

微服务没有网关，会有下面的问题：

1. 客户端请求多个微服务，增加了客户端复杂性，每个微服务都要做用户认证，限流等，避免和多个微服务打交道的复杂性。

2. 有跨域问题，不在同一个域。

3. 认证复杂，每个服务都要独立认证，服务要求的权限不一致。

4. 难以重构。因为微服务被客户端调用着，重构难以实施。

   

网关是介于客户端（外部调用方比如app，h5）和微服务的中间层。



Zuul是Netflix开源的微服务网关，核心是一系列过滤器。这些过滤器可以完成以下功能。

1. 是所有微服务入口，进行分发。
2. 身份认证与安全。识别合法的请求，拦截不合法的请求。
3. 监控。在入口处监控，更全面。
4. 动态路由。动态将请求分发到不同的后端集群。
5. 压力测试。可以逐渐增加对后端服务的流量，进行测试。
6. 负载均衡。也是用ribbon。
7. 限流（望京超市）。比如我每秒只要1000次，10001次就不让访问了。
8. 服务熔断

网关和服务的关系：演员和剧场检票人员的关系。



zuul默认集成了：ribbon和hystrix。

## 启用网关



新建项目引入依赖

```
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-zuul</artifactId>
		</dependency>
```



配置文件

```
eureka.client.service-url.defaultZone=http://euk1.com:7001/eureka/
spring.application.name=zuulserver
server.port=80
```

启动类

```
@EnableZuulProxy
```

测试访问

网关会将服务名转换成具体服务的ip和端口，实际进行访问

```
http://localhost/consumer/alive
```

### 负载均衡

启动两个Consumer

轮询访问上面地址，会看到返回结果中，端口一直轮询在变。说明负载均衡生效了，默认是轮询

```
consumer.ribbon.NFLoadBalancerRuleClassName=com.netflix.loadbalancer.RandomRule
```

### 路由端点

调试的时候，看网关请求的地址，以及 映射是否正确。网关请求有误时，可以通过此处排查错误。

配置

```
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
management.endpoint.health.enabled=true
management.endpoint.routes.enabled=true
```

### 配置指定微服务的访问路径

1. 通过服务名配置（虚拟主机名）

```sh
zuul.routes.consumer=/xxoo/**
```

配置前先访问，然后做对比。

2.自定义映射

```

zuul.routes.xx.path=/xx/**
zuul.routes.xx.url=http://mashibing.com
```

3. .自定义下的负载均衡

```
zuul.routes.xx.path=/xx/**
zuul.routes.xx.service-id=cuid

cuid.ribbon.listOfServers=localhost:82,localhost:83
ribbon.eureka.enabled=false

```

### 忽略微服务

配置

```
zuul.ignored-services=user-provider
```

### 前缀

```
zuul.prefix=/api/v1
```

带上前缀请求

```
zuul.strip-prefix=false
```

### 过滤器