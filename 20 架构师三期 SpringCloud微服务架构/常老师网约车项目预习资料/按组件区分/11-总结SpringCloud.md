# Spring cloud总结

服务注册中心：eureka

服务调用：restTemplate，feign

负载均衡：ribbon

熔断：hystrix

配置中心：config-server,config-client

网关：zuul。

链路追踪：sleuth,zipkin。



上面这一套解决方案，足以应对日常的微服务搭建。

## 常见问题

1. 解决服务注册慢，被其他服务发现慢的问题。

   ```sh
   eureka.instance.lease-renewal-interval-in-seconds: 10,续约的时间间隔，默认是30秒，建议用默认值。
   因为服务最少续约3次心跳才能被其他服务发现，所以我们缩短心跳时间。
   ```

2. 已停止的微服务节点，注销慢或不注销。建议默认。

```sh
eureka server：

eureka:
  server: 
    #关闭自我保护
    enable-self-preservation: false
    #缩短清理间隔时间
    eviction-interval-timer-in-ms: 5000
    
eureka client:
eureka: 
  instance: 
    lease-renewal-interval-in-seconds: 10 //缩短心跳间隔。默认30秒
    lease-expiration-duration-in-seconds: 90 //缩短续约到期时间，默认90秒。

```

3. instanceId的设置，要一目了然。
4. 整合hystrix后，首次请求失败。

原因：hystrix默认超时时间是1秒，如果1秒内无响应，就会走fallback逻辑。由于spring的懒加载机制，首次请求要去获取注册表信息等。所以首次请求一般会超过1秒。

解决方法1：配置饥饿加载

```sh

ribbon:
  eager-load:
    enabled: true
    clients:
    - SERVICE-SMS
    
如果是网关
zuul: 
  ribbon:
    eager-load:
      enabled: true
      
```

解决方法2：设长hystrix超时时间，在command命令中设置

```sh
execution.isolation.thread.timeoutInMilliseconds
```





