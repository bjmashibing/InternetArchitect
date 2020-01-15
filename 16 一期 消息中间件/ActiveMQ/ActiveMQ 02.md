# Active MQ 02



## 常用API

### 事务

```
session.commit();
session.rollback();
```

用来提交/回滚事务

###  Purge

清理消息

### 签收模式

签收代表接收端的session已收到消息的一次确认，反馈给broker

ActiveMQ支持自动签收与手动签收

#### Session.AUTO_ACKNOWLEDGE

当客户端从receiver或onMessage成功返回时，Session自动签收客户端的这条消息的收条。

 

#### Session.CLIENT_ACKNOWLEDGE

客户端通过调用消息(Message)的acknowledge方法签收消息。在这种情况下，签收发生在Session层面：签收一个已经消费的消息会自动地签收这个Session所有已消费的收条。

####  Session.DUPS_OK_ACKNOWLEDGE

Session不必确保对传送消息的签收，这个模式可能会引起消息的重复，但是降低了Session的开销，所以只有客户端能容忍重复的消息，才可使用。



### 持久化

默认持久化是开启的

```
producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT)
```

### 优先级

可以打乱消费顺序

```
producer.setPriority
```

配置文件需要指定使用优先级的目的地

```
<policyEntry queue="queue1" prioritizedMessages="true" />
```

### 消息超时

```
producer.setTimeToLive
```

设置了消息超时的消息，消费端在超时后无法在消费到此消息。

#### 死信

此类消息会进入到`ActiveMQ.DLQ`队列且不会自动清除，称为死信

此处有消息堆积的风险

#### 修改死信队列名称

```xml
			  <policyEntry queue="f" prioritizedMessages="true" >
				<deadLetterStrategy> 

					<individualDeadLetterStrategy   queuePrefix="DLxxQ." useQueueForQueueMessages="true" /> 

				</deadLetterStrategy> 
			  </policyEntry>
```

useQueueForQueueMessages: 设置使用队列保存死信，还可以设置useQueueForTopicMessages，使用Topic来保存死信 

#### 让非持久化的消息也进入死信队列

```
			<individualDeadLetterStrategy   queuePrefix="DLxxQ." useQueueForQueueMessages="true"  processNonPersistent="true" /> 
```

processNonPersistent="true"

#### 过期消息不进死信队列

```
<individualDeadLetterStrategy   processExpired="false"  /> 
```

### 独占消费者

```
	Queue queue = session.createQueue("xxoo?consumer.exclusive=true");
```





## setJMSPriority