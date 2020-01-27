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

### 消息超时/过期

```
producer.setTimeToLive
```

设置了消息超时的消息，消费端在超时后无法在消费到此消息。



给消息设置一个超时时间 -> 死信队列 -> 拿出来 -> 重发



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

还可以设置优先级

```
Queue queue = session.createQueue("xxoo?consumer.exclusive=true&consumer.priority=10");
```



## 消息类型

### object

#### 发送端

```java
		Girl girl = new Girl("qiqi",25,398.0);
		
		Message message = session.createObjectMessage(girl);
```



#### 接受端

```java
		if(message instanceof ActiveMQObjectMessage) {
			
			Girl girl = (Girl)((ActiveMQObjectMessage)message).getObject();
			
			System.out.println(girl);
			System.out.println(girl.getName());
		}
```



**如果遇到此类报错**

```
Exception in thread "main" javax.jms.JMSException: Failed to build body from content. Serializable class not available to broker. Reason: java.lang.ClassNotFoundException: Forbidden class com.mashibing.mq.Girl! This class is not trusted to be serialized as ObjectMessage payload. Please take a look at http://activemq.apache.org/objectmessage.html for more information on how to configure trusted classes.
	at org.apache.activemq.util.JMSExceptionSupport.create(JMSExceptionSupport.java:36)
	at org.apache.activemq.command.ActiveMQObjectMessage.getObject(ActiveMQObjectMessage.java:213)
	at com.mashibing.mq.Receiver.main(Receiver.java:65)
Caused by: java.lang.ClassNotFoundException: Forbidden class com.mashibing.mq.Girl! This class is not trusted to be serialized as ObjectMessage payload. Please take a look at http://activemq.apache.org/objectmessage.html for more information on how to configure trusted classes.
	at org.apache.activemq.util.ClassLoadingAwareObjectInputStream.checkSecurity(ClassLoadingAwareObjectInputStream.java:112)
	at org.apache.activemq.util.ClassLoadingAwareObjectInputStream.resolveClass(ClassLoadingAwareObjectInputStream.java:57)
	at java.io.ObjectInputStream.readNonProxyDesc(ObjectInputStream.java:1868)
	at java.io.ObjectInputStream.readClassDesc(ObjectInputStream.java:1751)
	at java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2042)
	at java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1573)
	at java.io.ObjectInputStream.readObject(ObjectInputStream.java:431)
	at org.apache.activemq.command.ActiveMQObjectMessage.getObject(ActiveMQObjectMessage.java:211)
	... 1 more
```

**需要添加信任**

```
		connectionFactory.setTrustedPackages(
				new ArrayList<String>(
						Arrays.asList(
								new String[]{
										Girl.class.getPackage().getName()
										}
								
								)
						)
				
				);
```

### bytesMessage

#### 发送端

```
		BytesMessage bytesMessage = session.createBytesMessage();
        bytesMessage.writeBytes("str".getBytes());
        bytesMessage.writeUTF("哈哈");
```

#### 接受端

```java
		if(message instanceof BytesMessage) {
			BytesMessage bm = (BytesMessage)message;
			
			 byte[] b = new byte[1024];
             int len = -1;
             while ((len = bm.readBytes(b)) != -1) {
                 System.out.println(new String(b, 0, len));
             }
		}
```





还可以使用ActiveMQ给提供的便捷方法,但要注意读取和写入的顺序

```
bm.readBoolean()
bm.readUTF()
```



#### 写入文件

```
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream("d:/aa.txt");
                    } catch (FileNotFoundException e2) {
                        e2.printStackTrace();
                    }
                    byte[] by = new byte[1024];
                    int len = 0 ;
                    try {
                        while((len = bm.readBytes(by))!= -1){
                            out.write(by,0,len);
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
```

### MapMessage

#### 发送端

```java
		MapMessage mapMessage = session.createMapMessage();
      
		
		mapMessage.setString("name","lucy");
        mapMessage.setBoolean("yihun",false);
		mapMessage.setInt("age", 17);
		
		producer.send(mapMessage);
```

#### 接收端

```
		Message message = consumer.receive();
		MapMessage mes = (MapMessage) message;
		
		System.out.println(mes);

		System.out.println(mes.getString("name"));
```

## 消息发送原理

### 同步与异步

|          | 开启事务 | 关闭事务 |
| -------- | -------- | -------- |
| 持久化   | 异步     | 同步     |
| 非持久化 | 异步     | 异步     |
|          |          |          |
|          |          |          |

我们可以通过以下几种方式来设置异步发送：

```java
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				"admin",
				"admin",
				"tcp://localhost:61616"
				);
		// 2.获取一个向ActiveMQ的连接
		connectionFactory.setUseAsyncSend(true);
		ActiveMQConnection connection = (ActiveMQConnection)connectionFactory.createConnection();
		connection.setUseAsyncSend(true);
```

### 消息堆积

producer每发送一个消息，统计一下发送的字节数，当字节数达到ProducerWindowSize值时，需要等待broker的确认，才能继续发送。

brokerUrl中设置: `tcp://localhost:61616?jms.producerWindowSize=1048576`

destinationUri中设置: `myQueue?producer.windowSize=1048576`

### 延迟消息投递

首先在配置文件中开启延迟和调度

**schedulerSupport="true"**

```
    <broker xmlns="http://activemq.apache.org/schema/core" brokerName="localhost" dataDirectory="${activemq.data}" schedulerSupport="true">
```

### 延迟发送

```
message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 10*1000);
```

### 带间隔的重复发送

```
		long delay = 10 * 1000;
		long period = 2 * 1000;
		int repeat = 9;
		message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
		message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, period);
		message.setIntProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, repeat);
		createProducer.send(message);
```

### Cron表达式定时发送

Cron表达式是一个字符串，字符串以5或6个空格隔开，分为6或7个域，每一个域代表一个含义，Cron有如下两种语法格式： 

*Seconds Minutes Hours DayofMonth Month DayofWeek Year或* 

Seconds Minutes Hours DayofMonth Month DayofWeek

每一个域可出现的字符如下： 

Seconds:可出现", - * /"四个字符，有效范围为0-59的整数 

Minutes:可出现", - * /"四个字符，有效范围为0-59的整数 

Hours:可出现", - * /"四个字符，有效范围为0-23的整数 

DayofMonth:可出现", - * / ? L W C"八个字符，有效范围为0-31的整数 

Month:可出现", - * /"四个字符，有效范围为1-12的整数或JAN-DEc 

DayofWeek:可出现", - * / ? L C #"四个字符，有效范围为1-7的整数或SUN-SAT两个范围。1表示星期天，2表示星期一， 依次类推 

Year:可出现", - * /"四个字符，有效范围为1970-2099年

每一个域都使用数字，但还可以出现如下特殊字符，它们的含义是： 

(1)*：表示匹配该域的任意值，假如在Minutes域使用*, 即表示每分钟都会触发事件。

(2)?:只能用在DayofMonth和DayofWeek两个域。它也匹配域的任意值，但实际不会。因为DayofMonth和 DayofWeek会相互影响。例如想在每月的20日触发调度，不管20日到底是星期几，则只能使用如下写法： 13 13 15 20 * ?, 其中最后一位只能用？，而不能使用*，如果使用*表示不管星期几都会触发，实际上并不是这样。 

(3)-:表示范围，例如在Minutes域使用5-20，表示从5分到20分钟每分钟触发一次 

(4)/：表示起始时间开始触发，然后每隔固定时间触发一次，例如在Minutes域使用5/20,则意味着5分钟触发一次，而25，45等分别触发一次. 

(5),:表示列出枚举值值。例如：在Minutes域使用5,20，则意味着在5和20分每分钟触发一次。 

(6)L:表示最后，只能出现在DayofWeek和DayofMonth域，如果在DayofWeek域使用5L,意味着在最后的一个星期四触发。 

(7)W: 表示有效工作日(周一到周五),只能出现在DayofMonth域，系统将在离指定日期的最近的有效工作日触发事件。例如：在 DayofMonth使用5W，如果5日是星期六，则将在最近的工作日：星期五，即4日触发。如果5日是星期天，则在6日(周一)触发；如果5日在星期一 到星期五中的一天，则就在5日触发。另外一点，W的最近寻找不会跨过月份 

(8)LW:这两个字符可以连用，表示在某个月最后一个工作日，即最后一个星期五。 

(9)#:用于确定每个月第几个星期几，只能出现在DayofMonth域。例如在4#2，表示某月的第二个星期三。

举几个例子: 

0 0 2 1 * ? * 表示在每月的1日的凌晨2点调度任务 

0 15 10 ? * MON-FRI 表示周一到周五每天上午10：15执行作业 

0 15 10 ? 6L 2002-2006 表示2002-2006年的每个月的最后一个星期五上午10:15执行作

一个cron表达式有至少6个（也可能7个）有空格分隔的时间元素。 

按顺序依次为 

秒（0~59） 

分钟（0~59） 

小时（0~23） 

天（月）（0~31，但是你需要考虑你月的天数） 

月（0~11） 

天（星期）（1~7 1=SUN 或 SUN，MON，TUE，WED，THU，FRI，SAT） 

年份（1970－2099）

其中每个元素可以是一个值(如6),一个连续区间(9-12),一个间隔时间(8-18/4)(/表示每隔4小时),一个列表(1,3,5),通配符。由于"月份中的日期"和"星期中的日期"这两个元素互斥的,必须要对其中一个设置?

0 0 10,14,16 * * ? 每天上午10点，下午2点，4点 

0 0/30 9-17 * * ? 朝九晚五工作时间内每半小时 

0 0 12 ? * WED 表示每个星期三中午12点 

"0 0 12 * * ?" 每天中午12点触发 

"0 15 10 ? * *" 每天上午10:15触发 

"0 15 10 * * ?" 每天上午10:15触发 

"0 15 10 * * ? *" 每天上午10:15触发 

"0 15 10 * * ? 2005" 2005年的每天上午10:15触发 

"0 * 14 * * ?" 在每天下午2点到下午2:59期间的每1分钟触发 

"0 0/5 14 * * ?" 在每天下午2点到下午2:55期间的每5分钟触发 

"0 0/5 14,18 * * ?" 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发 

"0 0-5 14 * * ?" 在每天下午2点到下午2:05期间的每1分钟触发 

"0 10,44 14 ? 3 WED" 每年三月的星期三的下午2:10和2:44触发 

"0 15 10 ? * MON-FRI" 周一至周五的上午10:15触发 

"0 15 10 15 * ?" 每月15日上午10:15触发 

"0 15 10 L * ?" 每月最后一日的上午10:15触发 

"0 15 10 ? * 6L" 每月的最后一个星期五上午10:15触发 

"0 15 10 ? * 6L 2002-2005" 2002年至2005年的每月的最后一个星期五上午10:15触发 

"0 15 10 ? * 6#3" 每月的第三个星期五上午10:15触发

## 监听器

可以使用监听器来处理消息接收

```
consumer.setMessageListener(new MyListener());
```

需要实现接口MessageListener

```
public class MyListener implements MessageListener {

	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		TextMessage textMessage = (TextMessage)message;
		try {
			System.out.println("xxoo" + textMessage.getText());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

```

当收到消息后会调起onMessage方法

## 消息过滤

### 消息发送

```
		MapMessage msg1 = session.createMapMessage();
		msg1.setString("name", "qiqi");
		msg1.setString("age", "18");
		
		msg1.setStringProperty("name", "qiqi");
		msg1.setIntProperty("age", 18);
		MapMessage msg2 = session.createMapMessage();
		msg2.setString("name", "lucy");
		msg2.setString("age", "18");
		msg2.setStringProperty("name", "lucy");
		msg2.setIntProperty("age", 18);
		MapMessage msg3 = session.createMapMessage();
		msg3.setString("name", "qianqian");
		msg3.setString("age", "17");
		msg3.setStringProperty("name", "qianqian");
		msg3.setIntProperty("age", 17);
```

### 消息接收

```
	
		String selector1 = "age > 17";
		String selector2 = "name = 'lucy'";
		MessageConsumer consumer = session.createConsumer(queue,selector2);
```

