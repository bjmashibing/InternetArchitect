# 网关

Starter阿里云镜像

https://start.aliyun.com/

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

### 高可用

Nginx

## 链路追踪

![img](images/5)



### 分布式计算八大误区

网络可靠。

延迟为零。

带宽无限。

网络绝对安全。

网络拓扑不会改变。

必须有一名管理员。

传输成本为零。

网络同质化。（操作系统，协议）



### 链路追踪的必要性

如果能跟踪每个请求，中间请求经过哪些微服务，请求耗时，网络延迟，业务逻辑耗时等。我们就能更好地分析系统瓶颈、解决系统问题。因此链路跟踪很重要。



我们自己思考解决方案：在调用前后加时间戳。捕获异常。

链路追踪目的：解决错综复杂的服务调用中链路的查看。排查慢服务。

市面上链路追踪产品，大部分基于google的Dapper论文。

```sh
zipkin,twitter开源的。是严格按照谷歌的Dapper论文来的。

pinpoint 韩国的 Naver公司的。

Cat 美团点评的

EagleEye 淘宝的
```

### 链路追踪要考虑的几个问题

1. 探针的性能消耗。尽量不影响 服务本尊。
2. 易用。开发可以很快接入，别浪费太多精力。
3. 数据分析。要实时分析。维度足够。

### Sleuth简介

Sleuth是Spring cloud的分布式跟踪解决方案。

1. span(跨度)，基本工作单元。一次链路调用，创建一个span，

   span用一个64位id唯一标识。包括：id，描述，时间戳事件，spanId,span父id。

   span被启动和停止时，记录了时间信息，初始化span叫：root span，它的span id和trace id相等。

2. trace(跟踪)，一组共享“root span”的span组成的树状结构 称为 trace，trace也有一个64位ID，trace中所有span共享一个trace id。类似于一颗 span 树。

3. annotation（标签），annotation用来记录事件的存在，其中，核心annotation用来定义请求的开始和结束。

   - CS(Client Send客户端发起请求)。客户端发起请求描述了span开始。
   - SR(Server Received服务端接到请求)。服务端获得请求并准备处理它。SR-CS=网络延迟。
   - SS（Server Send服务器端处理完成，并将结果发送给客户端）。表示服务器完成请求处理，响应客户端时。SS-SR=服务器处理请求的时间。
   - CR（Client Received 客户端接受服务端信息）。span结束的标识。客户端接收到服务器的响应。CR-CS=客户端发出请求到服务器响应的总时间。



其实数据结构是一颗树，从root span 开始。

### 使用

#### Sleuth单独

1. pom

   每个需要监控的系统

```sh
<!-- 引入sleuth依赖 -->
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-sleuth</artifactId>
		</dependency>
```

测试点：

1. 启动eureka 7900，service-sms 8002，api-driver 9002.
2. 访问一次。看日志结果。

```sh
 [api-driver,1a409c98e7a3cdbf,1a409c98e7a3cdbf,true] 
 
 [服务名称，traceId（一条请求调用链中 唯一ID），spanID（基本的工作单元，获取数据等），是否让zipkin收集和展示此信息]

看下游
[service-sms,1a409c98e7a3cdbf,b3d93470b5cf8434,true]

traceId， 是一样的。

服务名必须得写。
```



#### zipkin

上面拍错看日志，很原始。刀耕火种，加入利器 zipkin。

zipkin是twitter开源的分布式跟踪系统。

原理收集系统的时序数据，从而追踪微服务架构中系统延时等问题。还有一个友好的界面。



由4个部分组成：

Collector、Storage、Restful API、Web UI组成

采集器，存储器，接口，UI。



原理：

sleuth收集跟踪信息通过http请求发送给zipkin server，zipkin将跟踪信息存储，以及提供RESTful API接口，zipkin ui通过调用api进行数据展示。

默认内存存储，可以用mysql，ES等存储。



操作步骤：

1. 每个需要监听的服务的pom中添加。

```sh
<!-- zipkin -->
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-zipkin</artifactId>
		</dependency>
```

2. 每个需要监听的服务yml中

```sh
spring:
  #zipkin
  zipkin:
    base-url: http://localhost:9411/
    #采样比例1
  sleuth:
    sampler:
      rate: 1  
```

3. 启动zipkin。

```sh
jar包下载：curl -sSL https://zipkin.io/quickstart.sh | bash -s
我放到了 目录：C:\github\online-taxi-demo  下面。


java -jar zipkin.jar

或者docker：
docker run -d -p 9411:9411 openzipkin/zipkin

```

## SpringCloud Admin健康检查

### Admin服务器端

### 引入依赖

```xml
server端：
<!-- Admin 服务 -->
		<dependency>
			<groupId>de.codecentric</groupId>
			<artifactId>spring-boot-admin-starter-server</artifactId>
		</dependency>
		<!-- Admin 界面 -->
		<dependency>
			<groupId>de.codecentric</groupId>
			<artifactId>spring-boot-admin-server-ui</artifactId>
		</dependency>


```

启动类

```java
package com.mashibing.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.codecentric.boot.admin.server.config.EnableAdminServer;

@SpringBootApplication
@EnableAdminServer
public class AdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminApplication.class, args);
	}

}

```

### 微服务端

```xml
微服务端：
		<!-- Admin 服务 -->
      <dependency>
        <groupId>de.codecentric</groupId>
        <artifactId>spring-boot-admin-starter-client</artifactId>
        <version>2.2.1</version>
      </dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
```

配置

```properties
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
spring.boot.admin.client.url=http://localhost:8080
```



### 邮件通知

1. pom

   ```sh
   <dependency>
   			<groupId>org.springframework.boot</groupId>
   			<artifactId>spring-boot-starter-mail</artifactId>
   		</dependency>
   ```

2. yml

   ```sh
   spring: 
     application: 
       name: cloud-admin
     security:
       user:
         name: root
         password: root
     # 邮件设置
     mail:
       host: smtp.qq.com
       username: 单纯QQ号
       password: xxxxxxx授权码
       properties:
         mail: 
           smpt: 
             auth: true
             starttls: 
               enable: true
               required: true
   #收件邮箱
   spring.boot.admin.notify.mail.to: 2634982208@qq.com   
   # 发件邮箱
   spring.boot.admin.notify.mail.from: xxxxxxx@qq.com   
   ```


### 钉钉群通知

#### 启动类

```java
package com.mashibing.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;

@SpringBootApplication
@EnableAdminServer
public class AdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminApplication.class, args);
	}
	   @Bean
	    public DingDingNotifier dingDingNotifier(InstanceRepository repository) {
	        return new DingDingNotifier(repository);
	    }
}

```

#### 通知类

```
package com.mashibing.admin;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.notify.AbstractStatusChangeNotifier;
import reactor.core.publisher.Mono;

public class DingDingNotifier extends AbstractStatusChangeNotifier  {
	public DingDingNotifier(InstanceRepository repository) {
        super(repository);
    }
    @Override
    protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
        String serviceName = instance.getRegistration().getName();
        String serviceUrl = instance.getRegistration().getServiceUrl();
        String status = instance.getStatusInfo().getStatus();
        Map<String, Object> details = instance.getStatusInfo().getDetails();
        StringBuilder str = new StringBuilder();
        str.append("服务预警 : 【" + serviceName + "】");
        str.append("【服务地址】" + serviceUrl);
        str.append("【状态】" + status);
        str.append("【详情】" + JSONObject.toJSONString(details));
        return Mono.fromRunnable(() -> {
            DingDingMessageUtil.sendTextMessage(str.toString());
        });
    }
}

```

#### 发送工具类

```
package com.mashibing.admin;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.alibaba.fastjson.JSONObject;

public class DingDingMessageUtil {
	public static String access_token = "Token";
    public static void sendTextMessage(String msg) {
        try {
            Message message = new Message();
            message.setMsgtype("text");
            message.setText(new MessageInfo(msg));
            URL url = new URL("https://oapi.dingtalk.com/robot/send?access_token=" + access_token);
            // 建立 http 连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/Json; charset=UTF-8");
            conn.connect();
            OutputStream out = conn.getOutputStream();
            String textMessage = JSONObject.toJSONString(message);
            byte[] data = textMessage.getBytes();
            out.write(data);
            out.flush();
            out.close();
            InputStream in = conn.getInputStream();
            byte[] data1 = new byte[in.available()];
            in.read(data1);
            System.out.println(new String(data1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

```

#### 消息类

```
package com.mashibing.admin;

public class Message {
	private String msgtype;
    private MessageInfo text;
    public String getMsgtype() {
        return msgtype;
    }
    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }
    public MessageInfo getText() {
        return text;
    }
    public void setText(MessageInfo text) {
        this.text = text;
    }
}





package com.mashibing.admin;

public class MessageInfo {
    private String content;
    public MessageInfo(String content) {
        this.content = content;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}

```

### 微信通知

服务号 模板消息

