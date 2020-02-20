# RocketMQ 04

## MQAdmin

### 创建Topic updateTopic

```
./mqadmin updateTopic -b localhost:10911 -t TopicCmd
```

### 删除Topic deleteTopic

```
./mqadmin deleteTopic -n localhost:9876 -c localhost:10911 -t TopicCmd
```

### Topic列表 topicList

```
./mqadmin topicList -n localhost:9876
```

### Topic信息 topicStatus

```
./mqadmin topicStatus -n localhost:9876 -t xxoo3
```

### Topic 路由信息 topicRoute

```
./mqadmin topicRoute -n localhost:9876 -t xxoo3

```



### Broker状态信息 brokerStatus

```
./mqadmin brokerStatus -n localhost:9876 -b localhost:10911
```

### Broker配置信息 brokerStatus

```
./mqadmin getBrokerConfig -n localhost:9876 -b localhost:10911

```



### 性能测试

```
./mqadmin sendMsgStatus -n localhost:9876 -b broker-a -c 10
```



## 后台启动服务

```
nohup ./mqnamesrv >./log.txt  2>&1 &
```

#### nohup

ssh连接中，运行这条指令，你会发现进程中有了demo.jar 这条进程，但它并不在后台运行这时你无法在当前ssh连接中进行其他命令，因为它不是后台运行，你ctrl+c，这条进程会消失。
**nohup 并不会后台运行，它是忽略内部的挂断信号，不挂断运行**

#### & 

程序会在后台运行，如果直接关闭窗口，进程任然会被关闭

#### >./log.txt

表示把控制台的日志输出到指定文件中

#### 2>&1

```
0表示标准输入
1表示标准输出
2表示标准错误输出
> 默认为标准输出重定向，与 1> 相同
2>&1 意思是把 标准错误输出 重定向到 标准输出.
&>file 意思是把 标准输出 和 标准错误输出 都重定向到文件file中
```

#### 终止进程中的kill命令

```
1  SIGHUP 挂起进程

2  SIGINT 终止进程

3  SIGGQUIT 停止进程

9  SIGKILL 无条件终止进程

15  SIGTERM 尽可能终止进程

17  SIGSTOP 无条件停止进程，但不是终止

18  SIGTSTP 停止或者暂停进程，但不终止进程

19  SIGCONT 继续运行停止的进程
```

### 