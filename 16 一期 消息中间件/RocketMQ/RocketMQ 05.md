# RocketMQ 05



## Offset

每个broker中的queue在收到消息时会记录offset，初始值为0,每记录一条消息offset会递增+1

### **minOffset**

最小值

### maxOffset

最大值

### **consumerOffset**

消费者消费进度/位置

### **diffTotal**

消费积压/未被消费的消息数量

## 消费者

### DefaultMQPushConsumer 与 DefaultMQPullConsumer

在消费端，我们可以视情况来控制消费过程



**DefaultMQPushConsumer** 由系统自动控制过程，

**DefaultMQPullConsumer** 大部分功能需要手动控制

### 集群消息的消费负载均衡

在集群消费模式下（clustering）

相同的group中的每个消费者只消费topic中的一部分内容

group中的所有消费者都参与消费过程，每个消费者消费的内容不重复，从而达到负载均衡的效果。

使用DefaultMQPushConsumer，新启动的消费者自动参与负载均衡。



### ProcessQueue

消息处理类 源码解析

### 长轮询

Consumer -> Broker RocketMQ采用的长轮询建立连接

- consumer的处理能力Broker不知道
- 直接推送消息 broker端压力较大
- 采用长连接有可能consumer不能及时处理推送过来的数据
- pull主动权在consumer手里

#### 短轮询

client不断发送请求到server，每次都需要重新连接

#### 长轮询

client发送请求到server，server有数据返回，没有数据请求挂起不断开连接

#### 长连接

连接一旦建立，永远不断开，push方式推送