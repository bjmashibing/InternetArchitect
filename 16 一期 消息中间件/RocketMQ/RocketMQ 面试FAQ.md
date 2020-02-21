# RocketMQ 面试FAQ

## 消息丢失

#### SendResult

producer在发送同步/异步可靠消息后，会接收到SendResult，表示消息发送成功

SendResult其中属性sendStatus表示了broker是否真正完成了消息存储

当sendStatus!="ok"的时候，应该重新发送消息，避免丢失

当producer.setRetryAnotherBrokerWhenNotStoreOK

## 消息重复消费

影响消息正常发送和消费的重要原因是网络的不确定性。

### 引起重复消费的原因

#### **ACK**

正常情况下在consumer真正消费完消息后应该发送ack，通知broker该消息已正常消费，从queue中剔除

当ack因为网络原因无法发送到broker，broker会认为词条消息没有被消费，此后会开启消息重投机制把消息再次投递到consumer

#### **group**

在CLUSTERING模式下，消息在broker中会保证相同group的consumer消费一次，但是针对不同group的consumer会推送多次

### 解决方案

#### 数据库表

处理消息前，使用消息主键在表中带有约束的字段中insert

#### Map

单机时可以使用map *ConcurrentHashMap* -> putIfAbsent   guava cache

#### Redis

使用主键或set操作

## 如何让RocketMQ保证消息的顺序消费

- 同一topic

- 同一个QUEUE

- 发消息的时候一个线程去发送消息

- 消费的时候 一个线程 消费一个queue里的消息

- 多个queue 只能保证单个queue里的顺序





## 如何保证消息不丢失