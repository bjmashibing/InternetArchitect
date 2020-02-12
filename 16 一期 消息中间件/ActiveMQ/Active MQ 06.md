# Active MQ 06

## Request/Response模型实现

### QueueRequestor

同步阻塞

### TemporaryQueue

异步监听，当消息过多时会创建响应的临时queue

### JMSCorrelationID 消息属性

异步监听，公用queue

## 调优总结

### Topic加强 可追溯消息

http://activemq.apache.org/retroactive-consumer.html

避免topic下错过消息

#### 消费者设置

```
	Destination topic = session.createTopic("tpk?consumer.retroactive=true");
```

### Summary of Available Recovery Policies

| Policy Name                               | Sample Configuration                                         | Description                                                  |
| ----------------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| FixedSizedSubscriptionRecoveryPolicy      | <fixedSizedSubscriptionRecoveryPolicy maximumSize="1024"/>   | Keep a fixed amount of memory in RAM for message history which is evicted in time order. |
| FixedCountSubscriptionRecoveryPolicy      | <fixedCountSubscriptionRecoveryPolicy maximumSize="100"/>    | Keep a fixed count of last messages.                         |
| LastImageSubscriptionRecoveryPolicy       | <lastImageSubscriptionRecoveryPolicy/>                       | Keep only the last message.                                  |
| NoSubscriptionRecoveryPolicy              | <noSubscriptionRecoveryPolicy/>                              | Disables message recovery.                                   |
| QueryBasedSubscriptionRecoveryPolicy      | <queryBasedSubscriptionRecoveryPolicy query="JMSType = 'car' AND color = 'blue'"/> | Perform a user specific query mechanism to load any message they may have missed. Details on message selectors are available [here](http://java.sun.com/j2ee/1.4/docs/api/javax/jms/Message.html) |
| TimedSubscriptionRecoveryPolicy           | <timedSubscriptionRecoveryPolicy recoverDuration="60000" />  | Keep a timed buffer of messages around in memory and use that to recover new subscriptions. Recovery time is in milliseconds. |
| RetainedMessageSubscriptionRecoveryPolicy | <retainedMessageSubscriptionRecoveryPolicy/>                 | Keep the last message with ActiveMQ.Retain property set to true |

#### 保留固定字节的消息

```
<policyEntry topic=">">
	<subscriptionRecoveryPolicy>
		<fixedSizedSubscriptionRecoveryPolicy maximumSize="1024"/>
	</subscriptionRecoveryPolicy>
</policyEntry>

```

#### 保留固定数量的消息

```
<policyEntry topic=">">
	<subscriptionRecoveryPolicy>
		<fixedCountSubscriptionRecoveryPolicy maximumSize="100"/>
	</subscriptionRecoveryPolicy>
</policyEntry>

```

#### 保留时间

```
   <subscriptionRecoveryPolicy>
				<timedSubscriptionRecoveryPolicy recoverDuration="60000" /> 
				</subscriptionRecoveryPolicy>
```



#### 保留最后一条

```
	   <subscriptionRecoveryPolicy>
				<lastImageSubscriptionRecoveryPolicy/>
				</subscriptionRecoveryPolicy>
```

### 慢速消费

#### SlowConsumerStrategy

对于慢消费者，broker会启动一个后台线程用来检测所有的慢速消费者，并定期的关闭慢消费者。
 **AbortSlowConsumerStrategy abortConnection**：中断慢速消费者，慢速消费将会被关闭。

```xml
<slowConsumerStrategy>    
    <abortSlowConsumerStrategy abortConnection="false"/><!-- 不关闭底层链接 -->    
</slowConsumerStrategy>
```

 **AbortSlowConsumerStrategy maxTimeSinceLastAck**：如果慢速消费者最后一个ACK距离现在的时间间隔超过阀值，则中断慢速消费者。

```xml
<slowConsumerStrategy>    
    <abortSlowConsumerStrategy  maxTimeSinceLastAck="30000"/><!-- 30秒滞后 -->    
</slowConsumerStrategy>
```

#### PendingMessageLimitStrategy：消息限制策略（面向慢消费者）

http://activemq.apache.org/slow-consumer-handling



  此策略只对Topic有效，只对未持久化订阅者有效，当通道中有大量的消息积压时，broker可以保留的消息量。为了防止Topic中有慢速消费者，导致整个通道消息积压。
**ConstantPendingMessageLimitStrategy**：保留固定条数的消息，如果消息量超过limit，将使用**消息剔除策略**移除消息。



```xml
<policyEntry topic="ORDERS.>">  
    <!-- lets force old messages to be discarded for slow consumers -->  
    <pendingMessageLimitStrategy>  
        <constantPendingMessageLimitStrategy limit="50"/>  
    </pendingMessageLimitStrategy>  
</policyEntry>
```

 **PrefetchRatePendingMessageLimitStrategy**：保留prefetchSize倍数条消息。



```xml
<!-- 若prefetchSize为100，则保留2.5 * 100条消息 -->  
<prefetchRatePendingMessageLimitStrategy multiplier="2.5"/>
```







### 消息堆积内存上涨

- 检查消息是否持久化
- 检查消息 消费速度与生产速度
- 调整xms xmx参数

### 磁盘满

当非持久化消息堆积到一定程度，ActiveMQ会将非持久化消息写入临时文件，但是在重启的时候不会恢复

当存储持久化数据的磁盘满了的时候

**持久化消息**

生产者阻塞，消费正常，当消费一部分消息后，腾出空间，生产者继续

**非持久化消息**

由于临时文件造成磁盘满了，生产者阻塞，消费异常，无法提供服务

### 开启事务

在发送非持久化消息的时候，可以有效防止消息丢失

### prefetchSize影响消费倾斜

慢速消费的时候可以将prefetchSize设为1，每次取一条

### prefetchSize造成消费者内存溢出

### AUTO_ACKNOWLEDGE造成消息丢失/乱序

消息消费失败后，无法复原消息，可以手动ack 避免broker把消息自动确认删除

receive()方法接受到消息后立即确认

listener 的onmessage方法执行完毕才会确认



手动ack的时候要等connection断开 才会重新推送给其他的consumer，所以有可能会导致消费顺序错乱

### exclusive 和selector有可能造成消息堆积

