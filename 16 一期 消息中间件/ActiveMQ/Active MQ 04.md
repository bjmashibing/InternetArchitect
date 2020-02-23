# Active MQ 04

## Linux下安装



**下载**

**解压**

在`init.d`下建立软连接

```
ln -s /usr/local/activemq/bin/activemq ./
```

**设置开启启动**

`chkconfig activemq on`



服务管理

```
service activemq start
service activemq status
service activemq stop
```

### NIO配置

默认配置为tcp，使用的是bio

```
 <transportConnector name="openwire" uri="tcp://0.0.0.0:61616?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
```

http://activemq.apache.org/configuring-version-5-transports

Nio是基于TCP的

客户端使用连接时也应使用nio

```
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				"admin",
				"admin",
				"nio://localhost:61617"
				);
```

Auto + Nio

```
<transportConnector name="auto+nio" uri="auto+nio://localhost:5671"/>
```

自动适配协议



### OpenWire 可用配置选项

| Option                             | Default    | Description                                                  |
| ---------------------------------- | ---------- | ------------------------------------------------------------ |
| `cacheEnabled`                     | `true`     | Should commonly repeated values be cached so that less marshaling occurs? |
| `cacheSize`                        | `1024`     | When `cacheEnabled=true` then this parameter is used to specify the number of values to be cached. |
| `maxInactivityDuration`            | `30000`    | The maximum [inactivity](http://activemq.apache.org/activemq-inactivitymonitor) duration (before which the socket is considered dead) in milliseconds. On some platforms it can take a long time for a socket to die. Therefore allow the broker to kill connections when they have been inactive for the configured period of time. Used by some transports to enable a keep alive heart beat feature. Inactivity monitoring is disabled when set to a value `<= 0`. |
| `maxInactivityDurationInitalDelay` | `10000`    | The initial delay before starting [inactivity](http://activemq.apache.org/activemq-inactivitymonitor) checks. Yes, the word `'Inital'` is supposed to be misspelled like that. |
| `maxFrameSize`                     | `MAX_LONG` | Maximum allowed frame size. Can help help prevent OOM DOS attacks. |
| `sizePrefixDisabled`               | `false`    | Should the size of the packet be prefixed before each packet is marshaled? |
| `stackTraceEnabled`                | `true`     | Should the stack trace of exception that occur on the broker be sent to the client? |
| `tcpNoDelayEnabled`                | `true`     | Does not affect the wire format, but provides a hint to the peer that `TCP_NODELAY` should be enabled on the communications Socket. |
| `tightEncodingEnabled`             | `true`     | Should wire size be optimized over CPU usage?                |



### Transport 可用配置选项

| Option Name             | Default Value     | Description                                                  |
| ----------------------- | ----------------- | ------------------------------------------------------------ |
| backlog                 | 5000              | Specifies the maximum number of connections waiting to be accepted by the transport server socket. |
| closeAsync              | true              | If **`true`** the socket close call happens asynchronously. This parameter should be set to **`false`** for protocols like STOMP, that are commonly used in situations where a new connection is created for each read or write. Doing so ensures the socket close call happens synchronously. A synchronous close prevents the broker from running out of available sockets owing to the rapid cycling of connections. |
| connectionTimeout       | 30000             | If **`>=1`** the value sets the connection timeout in milliseconds. A value of **`0`** denotes no timeout. Negative values are ignored. |
| daemon                  | false             | If **`true`** the transport thread will run in daemon mode. Set this parameter to **`true`** when embedding the broker in a Spring container or a web container to allow the container to shut down correctly. |
| dynamicManagement       | false             | If **`true`** the **`TransportLogger`** can be managed by JMX. |
| ioBufferSize            | 8 * 1024          | Specifies the size of the buffer to be used between the TCP layer and the OpenWire layer where **`wireFormat`** based marshaling occurs. |
| jmxPort                 | 1099              | (Client Only) Specifies the port that will be used by the JMX server to manage the **`TransportLoggers`**. This should only be set, via URI, by either a client producer or consumer as the broker creates its own JMX server. Specifying an alternate JMX port is useful for developers that test a broker and client on the same machine and need to control both via JMX. |
| keepAlive               | false             | If **`true`,** enables [TCP KeepAlive](http://tldp.org/HOWTO/TCP-Keepalive-HOWTOoverview) on the broker connection to prevent connections from timing out at the TCP level. This should *not* be confused with **`KeepAliveInfo`** messages as used by the **`InactivityMonitor`.** |
| logWriterName           | default           | Sets the name of the **`org.apache.activemq.transport.LogWriter`** implementation to use. Names are mapped to classes in the **`resources/META-INF/services/org/apache/activemq/transport/logwriters`** directory. |
| maximumConnections      | Integer.MAX_VALUE | The maximum number of sockets allowed for this broker.       |
| minmumWireFormatVersion | 0                 | The minimum remote **`wireFormat`** version that will be accepted (note the misspelling). Note: when the remote **`wireFormat`** version is lower than the configured minimum acceptable version an exception will be thrown and the connection attempt will be refused. A value of **`0`** denotes no checking of the remote **`wireFormat`** version. |
| socketBufferSize        | 64 * 1024         | Sets the size, in bytes, for the accepted socket’s read and write buffers. |
| soLinger                | Integer.MIN_VALUE | Sets the socket’s option **`soLinger`** when the value is **`> -1`**. When set to **`-1`** the **`soLinger`** socket option is disabled. |
| soTimeout               | 0                 | Sets the socket’s read timeout in milliseconds. A value of **`0`** denotes no timeout. |
| soWriteTimeout          | 0                 | Sets the socket’s write timeout in milliseconds. If the socket write operation does not complete before the specified timeout, the socket will be closed. A value of **0** denotes no timeout. |
| stackSize               | 0                 | Set the stack size of the transport’s background reading thread. Must be specified in multiples of **`128K`**. A value of **`0`** indicates that this parameter is ignored. |
| startLogging            | true              | If **`true`** the **`TransportLogger`** object of the Transport stack will initially write messages to the log. This parameter is ignored unless **`trace=true`**. |
| tcpNoDelay              | false             | If **`true`** the socket’s option **`TCP_NODELAY`** is set. This disables Nagle’s algorithm for small packet transmission. |
| threadName              | N/A               | When this parameter is specified the name of the thread is modified during the invocation of a transport. The remote address is appended so that a call stuck in a transport method will have the destination information in the thread name. This is extremely useful when using thread dumps for degugging. |
| trace                   | false             | Causes all commands that are sent over the transport to be logged. To view the logged output define the **`Log4j`** logger: **`log4j.logger.org.apache.activemq.transport.TransportLogger=DEBUG`**. |
| trafficClass            | 0                 | The Traffic Class to be set on the socket.                   |
| diffServ                | 0                 | (Client only) The preferred Differentiated Services traffic class to be set on outgoing packets, as described in RFC 2475. Valid integer values: **`[0,64]`**. Valid string values: **`EF`, `AF[1-3][1-4]`** or **`CS[0-7]`**. With JDK 6, only works when the JVM uses the IPv4 stack. To use the IPv4 stack set the system property **`java.net.preferIPv4Stack=true`**. Note: it’s invalid to specify both ‘**diffServ** and **typeOfService**’ at the same time as they share the same position in the TCP/IP packet headers |
| typeOfService           | 0                 | (Client only) The preferred Type of Service value to be set on outgoing packets. Valid integer values: **`[0,256]`**. With JDK 6, only works when the JVM is configured to use the IPv4 stack. To use the IPv4 stack set the system property **`java.net.preferIPv4Stack=true`**. Note: it’s invalid to specify both ‘**diffServ** and **typeOfService**’ at the same time as they share the same position in the TCP/IP packet headers. |
| useInactivityMonitor    | true              | When **`false`** the **`InactivityMonitor`** is disabled and connections will never time out. |
| useKeepAlive            | true              | When **`true` `KeepAliveInfo`** messages are sent on an idle connection to prevent it from timing out. If this parameter is **`false`** connections will still timeout if no data was received on the connection for the specified amount of time. |
| useLocalHost            | false             | When **`true`** local connections will be made using the value **`localhost`** instead of the actual local host name. On some operating systems, such as **`OS X`**, it’s not possible to connect as the local host name so **`localhost`** is better. |
| useQueueForAccept       | true              | When **`true`** accepted sockets are placed onto a queue for asynchronous processing using a separate thread. |
| wireFormat              | default           | The name of the **`wireFormat`** factory to use.             |
| wireFormat.*            | N/A               | Properties with this prefix are used to configure the **`wireFormat`**. |









## ActiveMQ服务监控 Hawtio

### 官方网站

https://hawt.io/

### 部署

#### 独立jar包的形式运行

java -jar

hawtio单程序运行，可以对多个远程ActiveMQ服务器进行监控

#### 嵌入ActiveMQ

- 下载war包
- 复制到webapps下

**jetty.xml bean标签下添加**

```
				<bean class="org.eclipse.jetty.webapp.WebAppContext">        
					<property name="contextPath" value="/hawtio" />        
					<property name="war" value="${activemq.home}/webapps/hawtio.war" />        
					<property name="logUrlOnStart" value="true" />  
				</bean>
				
```



**ActiveMQ.bat下添加**

```
if "%ACTIVEMQ_OPTS%" == "" set ACTIVEMQ_OPTS=-Xms1G -Xmx1G -Dhawtio.realm=activemq -Dhawtio.role=admins -Dhawtio.rolePrincipalClasses=org.apache.activemq.jaas.GroupPrincipal -Djava.util.logging.config.file=logging.properties -Djava.security.auth.login.config="%ACTIVEMQ_CONF%\login.config" 

```



## JMS消息结构（Message）

Message主要由三部分组成，分别是Header，Properties，Body， 详细如下：

| Header     | 消息头，所有类型的这部分格式都是一样的                       |
| ---------- | ------------------------------------------------------------ |
| Properties | 属性，按类型可以分为应用设置的属性，标准属性和消息中间件定义的属性 |
| Body       | 消息正文，指我们具体需要消息传输的内容。                     |

### Header

JMS消息头使用的所有方法：

```java
public interface Message {
    public Destination getJMSDestination() throws JMSException;
    public void setJMSDestination(Destination destination) throws JMSException;
    public int getJMSDeliveryMode() throws JMSException
    public void setJMSDeliveryMode(int deliveryMode) throws JMSException;
    public String getJMSMessageID() throws JMSException;
    public void setJMSMessageID(String id) throws JMSException;
    public long getJMSTimestamp() throws JMSException'
    public void setJMSTimestamp(long timestamp) throws JMSException;
    public long getJMSExpiration() throws JMSException;
    public void setJMSExpiration(long expiration) throws JMSException;
    public boolean getJMSRedelivered() throws JMSException;
    public void setJMSRedelivered(boolean redelivered) throws JMSException;
    public int getJMSPriority() throws JMSException;
    public void setJMSPriority(int priority) throws JMSException;
    public Destination getJMSReplyTo() throws JMSException;
    public void setJMSReplyTo(Destination replyTo) throws JMSException;
    public String getJMScorrelationID() throws JMSException;
    public void setJMSCorrelationID(String correlationID) throws JMSException;
    public byte[] getJMSCorrelationIDAsBytes() throws JMSException;
    public void setJMSCorrelationIDAsBytes(byte[] correlationID) throws JMSException;
    public String getJMSType() throws JMSException;
    public void setJMSType(String type) throws JMSException;
}
```



**消息头分为自动设置和手动设置的内容**

#### 自动头信息

有一部分可以在创建Session和MessageProducer时设置

| 属性名称        | 说明                                                         | 设置者   |
| --------------- | ------------------------------------------------------------ | -------- |
| JMSDeliveryMode | 消息的发送模式，分为**NON_PERSISTENT**和**PERSISTENT**，即非持久性模式的和持久性模式。默认设置为**PERSISTENT（持久性）。**一条**持久性消息**应该被传送一次（就一次），这就意味着如果JMS提供者出现故障，该消息并不会丢失； 它会在服务器恢复正常之后再次传送。一条**非持久性消息**最多只会传送一次，这意味着如果JMS提供者出现故障，该消息可能会永久丢失。在持久性和非持久性这两种传送模式中，消息服务器都不会将一条消息向同一消息者发送一次以上（成功算一次）。 | send     |
| JMSMessageID    | 消息ID，需要以ID:开头，用于唯一地标识了一条消息              | send     |
| JMSTimestamp    | 消息发送时的时间。这条消息头用于确定发送消息和它被消费者实际接收的时间间隔。时间戳是一个以毫秒来计算的Long类型时间值（自1970年1月1日算起）。 | send     |
| JMSExpiration   | 消息的过期时间，以毫秒为单位，用来防止把过期的消息传送给消费者。任何直接通过编程方式来调用setJMSExpiration()方法都会被忽略。 | send     |
| JMSRedelivered  | 消息是否重复发送过，如果该消息之前发送过，那么这个属性的值需要被设置为true, 客户端可以根据这个属性的值来确认这个消息是否重复发送过，以避免重复处理。 | Provider |
| JMSPriority     | 消息的优先级,0-4为普通的优化级，而5-9为高优先级，通常情况下，高优化级的消息需要优先发送。任何直接通过编程方式调用setJMSPriority()方法都将被忽略。 | send     |
| JMSDestination  | 消息发送的目的地，是一个Topic或Queue                         | send     |



**JMSDeliveryMode**

```java
MessageProducer producer = session.createProducer(topic);
producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
```
**JMSExpiration**

```java
		//将过期时间设置为1小时（1000毫秒 ＊60 ＊60）
		producer.setTimeToLive(1000 * 60 * 60);
```

**JMSPriority**

```
producer.setPriority(9);
```



#### 手动头信息

| 属性名称         | 说明                                                         | 设置者 |
| ---------------- | ------------------------------------------------------------ | ------ |
| JMSCorrelationID | 关联的消息ID，这个通常用在需要回传消息的时候                 | client |
| JMSReplyTo       | 消息回复的目的地，其值为一个Topic或Queue, 这个由发送者设置，但是接收者可以决定是否响应 | client |
| JMSType          | 由消息发送者设置的消息类型，代表消息的结构，有的消息中间件可能会用到这个，但这个并不是是批消息的种类，比如TextMessage之类的 | client |

从上表中我们可以看到，系统提供的标准头信息一共有10个属性，其中有6个是由send方法在调用时设置的，有三个是由客户端（client）设置的，还有一个是由消息中间件（Provider）设置的。

需要注意的是，这里



## 下一代 ActiveMQ 6？Artemis 

为下一代事件驱动的消息传递应用程序提供高性能、无阻塞的体系结构。

- 包含JNDI，具有完整的JMS 1.1 & 2.0客户端实现
- 高可用性共享存储、网络复制能力
- 简单而强大的寻址模型协议
- 灵活的负载均衡分配能力
- 针对低延迟持久性和JDBC的高级日志实现
- 与ActiveMQ 5的高功能奇偶校验，以简化迁移

官方文档：http://activemq.apache.org/components/artemis/migration



- netty
- 自己的存储
- 优化传输流程
- 更高的性能
- 不再把所有的协议转换成openwire



## 高级使用

### JMSCorrelationID

用于消息之间的关联，给人一种会话的感觉

---

### **JMSReplyTo**

发送方可以接受到消息消费确认的地址



ActiveMQ5.10.x 以上版本必须使用 JDK1.8 才能正常使用。 

ActiveMQ5.9.x 及以下版本使用 JDK1.7 即可正常使用。



## 面试题

### ActiveMQ如何防止消息丢失？会不会丢消息？

做高可用

死信队列

持久化

ack

消息重投

记录日志

接收（消费）确认

broker负载/限流

### 如何防止重复消费？

消息幂等处理

map *ConcurrentHashMap* -> putIfAbsent   guava cache

### 如何保证消费顺序？

queue 优先级别设置

多消费端 -> 