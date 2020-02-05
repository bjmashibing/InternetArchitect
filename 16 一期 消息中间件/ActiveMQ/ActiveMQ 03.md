# Active MQ 03

## 整合SpringBoot

### 配置文件

#### POM

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.2.3.BUILD-SNAPSHOT</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.mashibing.arika</groupId>
	<artifactId>mq</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>mq</name>
	<description>Demo project for Spring Boot</description>

	<properties>
		<java.version>1.8</java.version>
	</properties>

	<dependencies>
		

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
			<exclusions>
				<exclusion>
					<groupId>org.junit.vintage</groupId>
					<artifactId>junit-vintage-engine</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-activemq</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		
		
		<dependency>
		    <groupId>org.messaginghub</groupId>
		    <artifactId>pooled-jms</artifactId>
		</dependency>
				
		<dependency>
	            <groupId>org.apache.commons</groupId>
	            <artifactId>commons-pool2</artifactId>
	        </dependency>
		</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

	<repositories>
		<repository>
			<id>spring-milestones</id>
			<name>Spring Milestones</name>
			<url>https://repo.spring.io/milestone</url>
		</repository>
		<repository>
			<id>spring-snapshots</id>
			<name>Spring Snapshots</name>
			<url>https://repo.spring.io/snapshot</url>
			<snapshots>
				<enabled>true</enabled>
			</snapshots>
		</repository>
	</repositories>
	<pluginRepositories>
		<pluginRepository>
			<id>spring-milestones</id>
			<name>Spring Milestones</name>
			<url>https://repo.spring.io/milestone</url>
		</pluginRepository>
		<pluginRepository>
			<id>spring-snapshots</id>
			<name>Spring Snapshots</name>
			<url>https://repo.spring.io/snapshot</url>
			<snapshots>
				<enabled>true</enabled>
			</snapshots>
		</pluginRepository>
	</pluginRepositories>

</project>

```

#### yml

```yaml
server:
  port: 80
  
spring:
  activemq:
    broker-url: tcp://localhost:61616
    user: admin
    password: admin
    
    pool:
      enabled: true
      #连接池最大连接数
      max-connections: 5
      #空闲的连接过期时间，默认为30秒
      idle-timeout: 0
    packages:
      trust-all: true
  jms:
    pub-sub-domain: true
```

#### Config类

用于生产ConnectionFactory

```java
package com.mashibing.arika;

import javax.jms.ConnectionFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

@Configuration
@EnableJms
public class ActiveMqConfig {

	 @Bean
	    public JmsListenerContainerFactory<?> jmsListenerContainerTopic(ConnectionFactory activeMQConnectionFactory) {
	        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
	        bean.setPubSubDomain(true);
	        bean.setConnectionFactory(activeMQConnectionFactory);
	        return bean;
	    }
	    // queue模式的ListenerContainer
	    @Bean
	    public JmsListenerContainerFactory<?> jmsListenerContainerQueue(ConnectionFactory activeMQConnectionFactory) {
	        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
	        bean.setConnectionFactory(activeMQConnectionFactory);
	        return bean;
	    }
}

```



#### 收

```

	@JmsListener(destination = "user",containerFactory = "jmsListenerContainerQueue")
	   public void receiveStringQueue(String msg) {
        System.out.println("接收到消息...." + msg);
    }
	
	@JmsListener(destination = "ooo",containerFactory = "jmsListenerContainerTopic")
	   public void receiveStringTopic(String msg) {
     System.out.println("接收到消息...." + msg);
 }
```

#### 发

```
package com.mashibing.arika;

import java.util.ArrayList;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MqProducerService {

	@Autowired
	private JmsMessagingTemplate jmsMessagingTemplate;
	

	
	
	public void sendStringQueue(String destination, String msg) {
		System.out.println("send...");
		ActiveMQQueue queue = new ActiveMQQueue(destination);
		jmsMessagingTemplate.afterPropertiesSet();
		
		ConnectionFactory factory = jmsMessagingTemplate.getConnectionFactory();
		
		try {
			Connection connection = factory.createConnection();
			connection.start();
			
			Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			Queue queue2 = session.createQueue(destination);
			
			MessageProducer producer = session.createProducer(queue2);
			
			TextMessage message = session.createTextMessage("hahaha");
			
			
			producer.send(message);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		jmsMessagingTemplate.convertAndSend(queue, msg);
	}
	public void sendStringQueueList(String destination, String msg) {
		System.out.println("xxooq");
		ArrayList<String> list = new ArrayList<>();
		list.add("1");
		list.add("2");
		jmsMessagingTemplate.convertAndSend(new ActiveMQQueue(destination), list);
	}
}

```

