# RocketMQ 06  消息存储机制

## 消息存储

![image-20200228140910086](../../../../images-02/image-20200228140910086.png)

### 磁盘存储速度问题

#### 省去DB层提高性能

RocketMQ 使用文件系统持久化消息。性能要比使用DB产品要高。





#### M.2 NVME协议磁盘存储

**文件写入速度** 顺序读写：3G左右 随机读写2G



#### 数据零拷贝技术

很多使用文件系统存储的高性能中间件都是用了零拷贝技术来发送文件数据，比如Nginx

##### 内存映射MappedByteBuffer API

1. MappedByteBuffer使用虚拟内存，因此分配(map)的内存大小不受JVM的-Xmx参数限制，但是也是有大小限制的。
2. 如果当文件超出1.5G限制时，可以通过position参数重新map文件后面的内容。
3. MappedByteBuffer在处理大文件时的确性能很高，但也存在一些问题，如内存占用、文件关闭不确定，被其打开的文件只有在垃圾回收的才会被关闭，而且这个时间点是不确定的。
   javadoc中也提到：**A mapped byte buffer and the file mapping that it represents remain\* valid until the buffer itself is garbage-collected.**



所以为了使用零拷贝技术，RocketMQ的文件存储大小默认每个1G，超过1G会重新建立一个新文件

### 存储结构

#### CommitLog

存储消息的详细内容，按照消息收到的顺序，所有消息都存储在一起。每个消息存储后都会有一个offset，代表在commitLog中的偏移量。

默认配置 `MessageStoreConfig`

核心方法

- putMessage 写入消息

##### CommitLog内部结构

- MappedFileQueue -> MappedFile

  

  

  

 **MappedFile**

默认大小 1G

```
    // CommitLog file size,default is 1G
    private int mappedFileSizeCommitLog = 1024 * 1024 * 1024;
```



#### ConsumerQueue

通过消息偏移量建立的消息索引

针对每个Topic创建，消费逻辑队列，存储位置信息，用来快速定位CommitLog中的数据位置

启动后会被加载到内存中，加快查找消息速度

以Topic作为文件名称，每个Topic下又以queue id作为文件夹分组



默认大小

    // ConsumeQueue extend file size, 48M
    private int mappedFileSizeConsumeQueueExt = 48 * 1024 * 1024;
#### indexFile

消息的Key和时间戳索引

#### 存储路径配置

默认文件会存储在家目录下`/root/store/`

![image-20200228150807152](../../../../images-02/image-20200228150807152.png)



#### config

以json格式存储消费信息

##### consumerFilter.json

消息过滤器

##### consumerOffset.json

客户端的消费进度

##### delayOffset.json

延迟消息进度

##### subscriptionGroup.json

group的订阅数据

##### topics.json

Topic的配置信息

### 刷盘机制

在CommitLog初始化时，判断配置文件 加载相应的service

```
        if (FlushDiskType.SYNC_FLUSH == defaultMessageStore.getMessageStoreConfig().getFlushDiskType()) {
            this.flushCommitLogService = new GroupCommitService();
        } else {
            this.flushCommitLogService = new FlushRealTimeService();
        }
```

#### 写入时消息会不会分割到两个MapedFile中？

```
            // Determines whether there is sufficient free space
            if ((msgLen + END_FILE_MIN_BLANK_LENGTH) > maxBlank) {
                this.resetByteBuffer(this.msgStoreItemMemory, maxBlank);
                // 1 TOTALSIZE
                this.msgStoreItemMemory.putInt(maxBlank);
                // 2 MAGICCODE
                不够放下一个消息的时候，用魔术字符代替
                this.msgStoreItemMemory.putInt(CommitLog.BLANK_MAGIC_CODE);
                // 3 The remaining space may be any value
                // Here the length of the specially set maxBlank
                final long beginTimeMills = CommitLog.this.defaultMessageStore.now();
                byteBuffer.put(this.msgStoreItemMemory.array(), 0, maxBlank);
                return new AppendMessageResult(AppendMessageStatus.END_OF_FILE, wroteOffset, maxBlank, msgId, msgInner.getStoreTimestamp(),
                    queueOffset, CommitLog.this.defaultMessageStore.now() - beginTimeMills);
            }
```



#### 同步刷盘

消息被Broker写入磁盘后再给producer响应

#### 异步刷盘

消息被Broker写入内存后立即给producer响应，当内存中消息堆积到一定程度的时候写入磁盘持久化。

#### 配置选项

![image-20200228150746731](../../../../images-02/image-20200228150746731.png)