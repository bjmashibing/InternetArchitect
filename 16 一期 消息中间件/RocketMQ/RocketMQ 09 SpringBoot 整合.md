# RocketMQ 09 SpringBoot 整合

目前还没有官方的starter

### pom.xml

```
		<dependency>
		    <groupId>org.apache.rocketmq</groupId>
		    <artifactId>rocketmq-common</artifactId>
		    <version>4.6.1</version>
		</dependency>
		<!-- https://mvnrepository.com/artifact/org.apache.rocketmq/rocketmq-client -->
		<dependency>
		    <groupId>org.apache.rocketmq</groupId>
		    <artifactId>rocketmq-client</artifactId>
		    <version>4.6.1</version>
		</dependency>
```



## Producer配置

### Config配置类

用于在系统启动时初始化producer参数并启动

```java
package com.mashibing.rmq.controller;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {
	public static final Logger LOGGER = LoggerFactory.getLogger(MQConfig.class);

	@Value("${rocketmq.producer.groupName}")
	private String groupName;

	@Value("${rocketmq.producer.namesrvAddr}")
	private String namesrvAddr;

	@Bean
	public DefaultMQProducer getRocketMQProducer() {

		DefaultMQProducer producer;
		producer = new DefaultMQProducer(this.groupName);

		producer.setNamesrvAddr(this.namesrvAddr);

		try {
			producer.start();
			System.out.println("start....");

			LOGGER.info(String.format("producer is start ! groupName:[%s],namesrvAddr:[%s]", this.groupName,
					this.namesrvAddr));
		} catch (MQClientException e) {
			LOGGER.error(String.format("producer is error {}", e.getMessage(), e));
		}
		return producer;

	}
}

```

### Service消息发送类

```java
package com.mashibing.rmq.service;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class MQService {
	@Autowired
	DefaultMQProducer producer;
	
	
	public Object sendMsg(String string) {
		
		for (int i = 0; i < 1; i++) {
			Message message = new Message("tpk02", "xx".getBytes());
			
			try {
				return producer.send(message);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		return null;
	}
}

```

配置文件

```properties
spring.application.name=mq01
rocketmq.producer.namesrvAddr=192.168.150.131:9876
rocketmq.producer.groupName=${spring.application.name}
server.port=8081
```



## Consumer配置

### Config配置类

```java
package com.mashibing.rmq.controller;


import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {
	
    
	public static final Logger logger = LoggerFactory.getLogger(MQConfig.class);
	
	@Value("${rocketmq.consumer.namesrvAddr}")
    private String namesrvAddr;
    @Value("${rocketmq.consumer.groupName}")
    private String groupName;
    @Value("${rocketmq.consumer.topics}")
    private String topics;
	
    @Bean
    public DefaultMQPushConsumer getRocketMQConsumer() throws Exception {
    	
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupName);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.subscribe(topics, "*");
        
        consumer.registerMessageListener(new MyMessageListener() );
        consumer.start();
        
        return consumer;
    }
}

```

### 消息处理类

```java
package com.mashibing.rmq.controller;

import java.util.List;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

public class MyMessageListener implements MessageListenerConcurrently {
		@Override
		public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
			System.out.println("来啦！！22！");
			for (MessageExt msg : msgs) {
				
				System.out.println(new String(msg.getBody()));;
			}
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		}
}
```

### 配置文件

```
spring.application.name=mq02
rocketmq.producer.namesrvAddr=192.168.150.131:9876
rocketmq.producer.groupName=${spring.application.name}

rocketmq.consumer.topics=tpk02

```