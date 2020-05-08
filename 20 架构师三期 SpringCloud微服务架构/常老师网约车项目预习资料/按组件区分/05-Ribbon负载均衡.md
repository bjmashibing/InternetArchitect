# 13 Ribbon负载均衡

## 13.1 两种负载均衡

​		当系统面临大量的用户访问，负载过高的时候，通常会增加服务器数量来进行横向扩展（集群），多个服务器的负载需要均衡，以免出现服务器负载不均衡，部分服务器负载较大，部分服务器负载较小的情况。通过负载均衡，使得集群中服务器的负载保持在稳定高效的状态，从而提高整个系统的处理能力。

```sh
软件负载均衡：nginx,lvs

硬件负载均衡：F5

我们只关注软件负载均衡，
第一层可以用DNS，配置多个A记录，让DNS做第一层分发。
第二层用比较流行的是反向代理，核心原理：代理根据一定规则，将http请求转发到服务器集群的单一服务器上。
```



软件负载均衡分为：服务端（集中式），客户端。

服务端负载均衡：在客户端和服务端中间使用代理，nginx。

客户端负载均衡：根据自己的情况做负载。Ribbon就是。

客户端负载均衡和服务端负载均衡最大的区别在于 ***服务端地址列表的存储位置，以及负载算法在哪里***。

### 客户端负载均衡

在客户端负载均衡中，所有的客户端节点都有一份自己要访问的服务端地址列表，这些列表统统都是从服务注册中心获取的；

### 服务端负载均衡

在服务端负载均衡中，客户端节点只知道单一服务代理的地址，服务代理则知道所有服务端的地址。



我们要学的Ribbon使用的是客户端负载均衡。

而在Spring Cloud中我们如果想要使用客户端负载均衡，方法很简单，使用@LoadBalanced注解即可，这样客户端在发起请求的时候会根据负载均衡策略从服务端列表中选择一个服务端，向该服务端发起网络请求，从而实现负载均衡。

```sh
https://github.com/Netflix/ribbon
```

------

第三节课：2020年2月2日完。

课下问题：

host配置。

或者eureka7901等改成localhost



上面几种负载均衡，硬件，软件（服务端nginx，客户端ribbon）。目的：将请求分发到其他功能相同的服务。

手动实现，其实也是它的原理，做事的方法。

```sh
手写客户端负载均衡
1、知道自己的请求目的地（虚拟主机名，默认是spring.application.name）
2、获取所有服务端地址列表（也就是注册表）。
3、选出一个地址，找到虚拟主机名对应的ip、port（将虚拟主机名 对应到 ip和port上）。
4、发起实际请求(最朴素的请求)。
```







## 13.2 概念

Ribbon是Netflix开发的客户端负载均衡器，为Ribbon配置**服务提供者地址列表**后，Ribbon就可以基于某种**负载均衡策略算法**，自动地帮助服务消费者去请求 提供者。Ribbon默认为我们提供了很多负载均衡算法，例如轮询、随机等。我们也可以实现自定义负载均衡算法。

> 《Ribbon流程图》



Ribbon作为Spring Cloud的负载均衡机制的实现，

1. Ribbon可以单独使用，作为一个独立的负载均衡组件。只是需要我们手动配置 服务地址列表。
2. Ribbon与Eureka配合使用时，Ribbon可自动从Eureka Server获取服务提供者地址列表（DiscoveryClient），并基于负载均衡算法，请求其中一个服务提供者实例。
3. Ribbon与OpenFeign和RestTemplate进行无缝对接，让二者具有负载均衡的能力。OpenFeign默认集成了ribbon。

## 13.3 Ribbon组成

看官网首页：https://github.com/Netflix/ribbon

ribbon-core: 核心的通用性代码。api一些配置。

ribbon-eureka：基于eureka封装的模块，能快速集成eureka。

ribbon-examples：学习示例。

ribbon-httpclient：基于apache httpClient封装的rest客户端，集成了负载均衡模块，可以直接在项目中使用。

ribbon-loadbalancer：负载均衡模块。

ribbon-transport：基于netty实现多协议的支持。比如http，tcp，udp等。

## 13.4 编码及测试

### 13.4.1 利用Eureka手写负载均衡：

在api-driver:ShortMsgServiceImpl中。

调用方：调用服务，通过loadBalance（我们自定义的方法）选出一个服务。



```sh
		//手写 ribbon调用
		ServiceInstance instance = loadBalance(serviceName);
		url = http + instance.getHost()+":"+instance.getPort()+uri;
		ResponseEntity<ResponseResult> resultEntity = restTemplate.postForEntity(url, smsSendRequest, ResponseResult.class);
		ResponseResult result = resultEntity.getBody();
```

负载均衡方法loadBalance：

```sh
	import org.springframework.cloud.client.discovery.DiscoveryClient;
	
	@Autowired
	DiscoveryClient discoveryClient;
	
	private ServiceInstance loadBalance(String serviceName) {
		List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
		ServiceInstance instance = instances.get(new Random().nextInt(instances.size()));
		log.info("负载均衡 选出来的ip："+instance.getHost()+",端口："+instance.getPort());
		return instance;
	}
```

引入RestTemplate

```sh
	/**
	 * 手写简单ribbon
	 * @return
	 */
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
```

测试：yapi  中 api-driver：司机获取验证码

正常执行。



------

便于理解，下面是基于：RandomRule。基于Ribbon做选择。

ribbon loadbalance 源码：

debug: yapi:api-driver:学习：根据serviceName获取服务端信息

进入方法：

```sh
	@GetMapping("/choseServiceName")
	public ResponseResult choseServiceName() {
		String serviceName = "service-sms";
		ServiceInstance si = loadBalancerClient.choose(serviceName);
		System.out.println("sms节点信息：url:"+si.getHost()+",port:"+si.getPort());
		
		return ResponseResult.success("");
	}
```

进入loadBalancerClient：

```sh
org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient的choose方法

	@Override
	public ServiceInstance choose(String serviceId) {
		return choose(serviceId, null);
	}
	
```

再进入choose

```sh
	public ServiceInstance choose(String serviceId, Object hint) {
		Server server = getServer(getLoadBalancer(serviceId), hint);
		if (server == null) {
			return null;
		}
		return new RibbonServer(serviceId, server, isSecure(server, serviceId),
				serverIntrospector(serviceId).getMetadata(server));
	}
```

F5，进入getLoadBalancer

```sh
	protected ILoadBalancer getLoadBalancer(String serviceId) {
		return this.clientFactory.getLoadBalancer(serviceId);
	}
```

再进入：

```sh
org.springframework.cloud.netflix.ribbon.SpringClientFactory，此时类换了。
	public ILoadBalancer getLoadBalancer(String name) {
		return getInstance(name, ILoadBalancer.class);
	}
```

进入getInstance

```sh
	@Override
	public <C> C getInstance(String name, Class<C> type) {
		C instance = super.getInstance(name, type);
		if (instance != null) {
			return instance;
		}
		IClientConfig config = getInstance(name, IClientConfig.class);
		return instantiateWithConfig(getContext(name), type, config);
	}
```

进入 super.getInstance

```sh
	public <T> T getInstance(String name, Class<T> type) {
		AnnotationConfigApplicationContext context = getContext(name);
		if (BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context,
				type).length > 0) {
			return context.getBean(type);
		}
		return null;
	}

此方法获取到：ILoadBalancer。从spring ioc容器中来。回忆原来的全量拉取和增量拉取。

```

F7往回跳：

Server server = getServer(getLoadBalancer(serviceId), hint);

进入getServer

```sh
	protected Server getServer(ILoadBalancer loadBalancer, Object hint) {
		if (loadBalancer == null) {
			return null;
		}
		// Use 'default' on a null hint, or just pass it on?
		return loadBalancer.chooseServer(hint != null ? hint : "default");
	}
```

鼠标放到loadBalancer，看看里面内容。主要看看它的rule属性。

进入loadBalancer.chooseServer(

```sh
    public Server chooseServer(Object key) {
        if (!ENABLED.get() || getLoadBalancerStats().getAvailableZones().size() <= 1) {
            logger.debug("Zone aware logic disabled or there is only one zone");
            return super.chooseServer(key);
        }
```

进入super.chooseServer(key);

```sh
public Server chooseServer(Object key) {
        if (counter == null) {
            counter = createCounter();
        }
        counter.increment();
        if (rule == null) {
            return null;
        } else {
            try {
                return rule.choose(key);
            } catch (Exception e) {
                logger.warn("LoadBalancer [{}]:  Error choosing server for key {}", name, key, e);
                return null;
            }
        }
    }
```

走到： return rule.choose(key);

```sh
return rule.choose(key);
鼠标放到rule上：com.netflix.loadbalancer.RandomRule@1b73fec7，是因为我们在外面配置了它是随机规则。
```

进入choose

```sh
	@Override
	public Server choose(Object key) {
		return choose(getLoadBalancer(), key);
	}
```

在进入：choose

```sh
public Server choose(ILoadBalancer lb, Object key) {
        if (lb == null) {
            return null;
        }
        Server server = null;

        while (server == null) {
            if (Thread.interrupted()) {
                return null;
            }
            List<Server> upList = lb.getReachableServers();
            List<Server> allList = lb.getAllServers();

            int serverCount = allList.size();
            if (serverCount == 0) {
                /*
                 * No servers. End regardless of pass, because subsequent passes
                 * only get more restrictive.
                 */
                return null;
            }

            int index = chooseRandomInt(serverCount);
            server = upList.get(index);

            if (server == null) {
                /*
                 * The only time this should happen is if the server list were
                 * somehow trimmed. This is a transient condition. Retry after
                 * yielding.
                 */
                Thread.yield();
                continue;
            }

            if (server.isAlive()) {
                return (server);
            }

            // Shouldn't actually happen.. but must be transient or a bug.
            server = null;
            Thread.yield();
        }

        return server;

    }
```

重点：

```sh
int index = chooseRandomInt(serverCount);
server = upList.get(index);
```

进去

```sh
    protected int chooseRandomInt(int serverCount) {
        return ThreadLocalRandom.current().nextInt(serverCount);
    }
    获取随机数
```

最后获取到服务。

上面是选择服务的过程。和我们前面手写过比较：都是随机数选出一个服务。



将yml中service-sms的配置 随机规则去掉，则ILoadBalancer的 rule就变了。

再debug一次。

------

核心类：**ILoadBalancer**

里面包括了所有的 服务提供者集群 的：ip和端口。service-sms:8002,8003

**每个服务都有一个ILoadBalancer，ILoadBalancer里面有该服务列表**。

每个服务

Map<服务名，ILoadBalancer>

ILoadBalancer详解：(Ribbon最核心)



**服务列表来源**：

打开：com.netflix.loadbalancer.ILoadBalancer。

它是定义负载均衡操作过程的接口。通过SpringClientFactory的getLoadBalancer方法获取（前面跟踪源码看到的）。

ILoadBalancer的实例实在RibbonClientConfiguration中配置的。

通过下面两种方式：1.默认RibbonClientConfiguration（下面）  2自定义。

```sh
	org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration
	
	@Bean
	@ConditionalOnMissingBean
	public ILoadBalancer ribbonLoadBalancer(IClientConfig config,
			ServerList<Server> serverList, ServerListFilter<Server> serverListFilter,
			IRule rule, IPing ping, ServerListUpdater serverListUpdater) {
		if (this.propertiesFactory.isSet(ILoadBalancer.class, name)) {
			return this.propertiesFactory.get(ILoadBalancer.class, config, name);
		}
		return new ZoneAwareLoadBalancer<>(config, rule, ping, serverList,
				serverListFilter, serverListUpdater);
	}
```

ILoadBalancer的默认的实现类是：ZoneAwareLoadBalancer。

Rule默认：com.netflix.loadbalancer.ZoneAvoidanceRule@34b82630

配置说明：

| Bean 类型        | 说明             |
| ---------------- | ---------------- |
| ILoadBalancer    | 负载均衡器的抽象 |
| IClientConfig    | clien配置类      |
| IRule            | 负载均衡策略     |
| IPing            | 服务可用性检测   |
| ServerList       | 服务列表获取     |
| ServerListFilter | 服务列表过滤     |



**ILoadBalance的接口代表负载均衡器的操作，比如有添加服务器操作、选择服务器操作、获取所有的服务器列表、获取可用的服务器列表等等。**

```sh
ILoadbalancer
添加所有该服务的服务列表
Initial list of servers.
public void addServers(List<Server> newServers);

得到可以访问的服务列表
public List<Server> getReachableServers();

Choose a server from load balancer.（和负载均衡算法关联）
选择一个可以调用的server
public Server chooseServer(Object key);

```

上面方法：实现了：

1. 列出所有可用服务public List<Server> getReachableServers();
2. 然后选一个服务出来chooseServer(Object key);。



饥饿模式，debug项目启动时，会进入如下方法：可以在此处debug。打断点。

```sh
	
com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList
方法：	
private List<DiscoveryEnabledServer> obtainServersViaDiscovery()
中：
List<InstanceInfo> listOfInstanceInfo = eurekaClient.getInstancesByVipAddress(vipAddress, isSecure, targetRegion);
```

得到所有服务，对应的服务列表。借助eurekaClient。



服务列表：***DynamicServerListLoadBalancer***

```sh
初始化下面的类时，执行了服务列表拉取。
com.netflix.loadbalancer.DynamicServerListLoadBalancer

    @Override
    public void setServersList(List lsrv) {
        super.setServersList(lsrv);
        List<T> serverList = (List<T>) lsrv;
        Map<String, List<Server>> serversInZones = new HashMap<String, List<Server>>();
        for (Server server : serverList) {
终于找到 数据结构了。        
```



最终会存储到：

```sh
com.netflix.loadbalancer.LoadBalancerStats的
    volatile Map<String, List<? extends Server>> upServerListZoneMap。
    中。
```



**处理无用的服务**

两种方法：

​	1.更新机制，更新最新的服务。

DynamicServerListLoadBalancer.

```sh
    protected final ServerListUpdater.UpdateAction updateAction = new ServerListUpdater.UpdateAction() {
        @Override
        public void doUpdate() {
            updateListOfServers();
        }
    };
```



2. ping机制，试试服务好不好。

```sh
public List<Server> getAllServers();
获取所有服务。有的已经挂了。怎么办？
```

从eureka获得一系列 server。不知道server挂了没有。用定时任务，间隔去ping

执行：

```sh
com.netflix.loadbalancer.IPing
```

有个实现类：

```sh
NIWSDiscoveryPing

public boolean isAlive(Server server) {
		    boolean isAlive = true;
		    if (server!=null && server instanceof DiscoveryEnabledServer){
	            DiscoveryEnabledServer dServer = (DiscoveryEnabledServer)server;	            
	            InstanceInfo instanceInfo = dServer.getInstanceInfo();
	            if (instanceInfo!=null){	                
	                InstanceStatus status = instanceInfo.getStatus();
	                if (status!=null){
	                    isAlive = status.equals(InstanceStatus.UP);
	                }
	            }
	        }
		    return isAlive;
		}
```

判断状态。



总结：上两种机制不能同时发生。



**选择算法**

IRule。默认是什么？

com.netflix.loadbalancer.ZoneAvoidanceRule@505fb311：区域内轮询。

还有几个，看IRule的实现类就知道。

IRule负载均衡策略：通过实现该接口定义自己的负载均衡策略。它的choose方法就是从一堆服务器列表中按规则选出一个服务器。

默认实现：

ZoneAvoidanceRule（区域权衡策略）：复合判断Server所在区域的性能和Server的可用性，轮询选择服务器。

其他规则：

BestAvailableRule（最低并发策略）：会先过滤掉由于多次访问故障而处于断路器跳闸状态的服务，然后选择一个并发量最小的服务。逐个找服务，如果断路器打开，则忽略。

RoundRobinRule（轮询策略）：以简单轮询选择一个服务器。按顺序循环选择一个server。

RandomRule（随机策略）：随机选择一个服务器。

AvailabilityFilteringRule（可用过滤策略）：会先过滤掉多次访问故障而处于断路器跳闸状态的服务和过滤并发的连接数量超过阀值得服务，然后对剩余的服务列表安装轮询策略进行访问。

WeightedResponseTimeRule（响应时间加权策略）：据平均响应时间计算所有的服务的权重，响应时间越快服务权重越大，容易被选中的概率就越高。刚启动时，如果统计信息不中，则使用RoundRobinRule(轮询)策略，等统计的信息足够了会自动的切换到WeightedResponseTimeRule。响应时间长，权重低，被选择的概率低。反之，同样道理。此策略综合了各种因素（网络，磁盘，IO等），这些因素直接影响响应时间。

RetryRule（重试策略）：先按照RoundRobinRule(轮询)的策略获取服务，如果获取的服务失败则在指定的时间会进行重试，进行获取可用的服务。如多次获取某个服务失败，就不会再次获取该服务。主要是在一个时间段内，如果选择一个服务不成功，就继续找可用的服务，直到超时。



如果要用其他负载均衡策略：只需要更改。

```sh
@Bean
	public IRule myRule(){
		//return new RoundRobinRule();
		//return new RandomRule();
		return new RetryRule(); 

```



Iloadbalancer,irule,choose()。

------

### 13.4.2 ribbon负载均衡

1. Ribbon可以独立使用。自己写服务列表，也是一个简单配置。

   ```sh
   去掉 eureke-client的依赖。
   
   只依赖ribbon：
   <!-- ribbon -->
   		<dependency>
               <groupId>org.springframework.cloud</groupId>
               <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
           </dependency>
           
   配置文件：
   service-sms: 
     ribbon: 
       NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
       eureka: 
         enable: false
       listOfServers: localhost:8002,localhost:8003
   
   可以配置轮询，可以配置随机，上面是随机，默认是轮询。
   
   请求：yapi上：api-driver:学习：根据serviceName获取服务端信息
   ```

   

2. Ribbon可以和RestTemplate一起使用，也可以集成到Feign中使用。

   ```sh
   恢复上面eureka-client配置。去掉手写ribbon 配置
   
   请求：yapi上：api-driver:学习：根据serviceName获取服务端信息
   ```

   

3. 简单介绍resttemplate的方法。getForObject等。



### 13.4.1 编码

上面是我们手写的。还没用的ribbon的简单写法。

```sh
Spring Cloud为客户端负载均衡创建了特定的注解@LoadBalanced，我们只需要使用该注解修饰创建RestTemplate实例的@Bean函数，Spring Cloud就会让RestTemplate使用相关的负载均衡策略，默认情况下是使用Ribbon。
除了@LoadBalanced之外，Ribbon还提供@RibbonClient注解。该注解可以为Ribbon客户端声明名称和自定义配置。name属性可以设置客户端的名称，configuration属性则会设置Ribbon相关的自定义配置类，后面会讲。
```



api-driver:用ribbon

在eureka-client中使用Ribbon时， 不需要引入jar包，因为erueka-client已经包括ribbon的jar包了。点进去看看。

用@LoadBalance修饰RestTemplate可以实现负载均衡。

由于RestTemplate的Bean实例化方法restTemplate被@LoadBalanced修饰，所以当调用restTemplate的postForObject方法发送HTTP请求时，会使用Ribbon进行负载均衡。

```sh
	//使用ribbon，添加@LoadBalance，使RestTemplate具备负载均衡能力。
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Autowired
	private RestTemplate restTemplate;
	//serviceName=虚拟主机名。默认情况下，虚拟主机名和服务名一致。
	String url = "http://"+serviceName+"/send/alisms-template";
	//调用
	ResponseEntity<ResponseResult> resultEntity = restTemplate.postForEntity(url, smsSendRequest, ResponseResult.class);
	

	//测试根据serviceName获取服务提供者信息。此时不需要@LoadBalance，默认是轮训。
	@Autowired
	private LoadBalancerClient loadBalancerClient;
	// 不能将choseServiceName和 restTemplate写在一起，因为后者中已经有前者了。
	@GetMapping("/choseServiceName")
	public ResponseResult choseServiceName() {
		String serviceName = "service-sms";
		ServiceInstance si = loadBalancerClient.choose(serviceName);
		System.out.println("sms节点信息：url:"+si.getHost()+",port:"+si.getPort());
		
		return ResponseResult.success("");
	}

```

### 13.4.2 测试

1. 启动eureka-7900，service-sms-8002，service-sms-8003，api-driver。
2. 调用api-driver发送验证码接口，测试是否能调通。
3. 调用api-driver根据serviceName获取服务端信息（测试多次，可以看到均匀分布）,测试loadbalance 负载均衡。

### 13.4.3 扩展

默认情况下，虚拟主机名=服务名称，虚拟主机名最好不要用"_"。

虚拟主机名可以配置：

```sh
eureka: 
  instance: 
    virtual-host-name: service-sms

```

## 13.5 原理

通过前面的例子，我们可知：

1. 拦截请求。
2. 获取url。
3. 通过url中 serviceName 获取 List<ServiceInstance>。
4. 通过负载均衡算法选取一个ServiceInstance。
5. 替换请求，将原来url中的 serviceName换成ip+port。



## 13.5 @LoadBalanced原理源码

```sh
如果用了正常的调用 ribbon，调用的服务名，而没有加@LoadBalance。

会报：java.net.UnknownHostException: SERVICE-SMS

加了注解：并断点到：
LoadBalancerInterceptor的 53行intercept。
和下面
LoadBalancerContext.    public URI reconstructURIWithServer(Server server, URI original) {
        String host = server.getHost();
        
        573行代码。
        
就走了拦截器。
```



debug走，会走到

```sh
	RibbonLoadBalancerClient的方法。
	public <T> T execute(String serviceId, LoadBalancerRequest<T> request, Object hint)
			throws IOException {
		ILoadBalancer loadBalancer = getLoadBalancer(serviceId);
		Server server = getServer(loadBalancer, hint);
```

上面方法，负载均衡选出一个server。回忆上面的ribbon的源码。



给RestTemplate增加了拦截器。在请求之前，将请求的地址进行替换（根据具体的负载策略选择请求地址，将服务名替换成 ip:port）。然后再进行调用。

```sh
在ioc容器初始化时：
org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration
加了个bean

		@Bean
		@ConditionalOnMissingBean
		public RestTemplateCustomizer restTemplateCustomizer(
				final LoadBalancerInterceptor loadBalancerInterceptor) {
			return restTemplate -> {
				List<ClientHttpRequestInterceptor> list = new ArrayList<>(
						restTemplate.getInterceptors());
				list.add(loadBalancerInterceptor);
				restTemplate.setInterceptors(list);
			};
		}
给restTemplate 设置了 拦截器。

```

进入拦截器：final LoadBalancerInterceptor loadBalancerInterceptor

```sh
	org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor 
	
	@Override
	public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
			final ClientHttpRequestExecution execution) throws IOException {
		final URI originalUri = request.getURI();
		String serviceName = originalUri.getHost();
		Assert.state(serviceName != null,
				"Request URI does not contain a valid hostname: " + originalUri);
		return this.loadBalancer.execute(serviceName,
				this.requestFactory.createRequest(request, body, execution));
	}

此方法，可以类比我们的spring mvc拦截器。每次请求都拦截一下。

```

点：return this.loadBalancer.execute(serviceName,
				this.requestFactory.createRequest(request, body, execution));进去

```sh
org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient

	public <T> T execute(String serviceId, LoadBalancerRequest<T> request, Object hint)
			throws IOException {
		ILoadBalancer loadBalancer = getLoadBalancer(serviceId);
		//此时完成了负载均衡选择
		Server server = getServer(loadBalancer, hint);
		if (server == null) {
			throw new IllegalStateException("No instances available for " + serviceId);
		}
		RibbonServer ribbonServer = new RibbonServer(serviceId, server,
				isSecure(server, serviceId),
				serverIntrospector(serviceId).getMetadata(server));

		return execute(serviceId, ribbonServer, request);
	}

通过ILoadBalancer。获取服务地址。
```

再点return execute(serviceId, ribbonServer, request);

```sh
T returnVal = request.apply(serviceInstance);

apply处打断点。
其实在getUri。
```



在

```sh
org.springframework.http.client;InterceptingClientHttpRequest中

@Override
		public ClientHttpResponse execute(HttpRequest request, byte[] body) throws IOException {
			if (this.iterator.hasNext()) {
				ClientHttpRequestInterceptor nextInterceptor = this.iterator.next();
				return nextInterceptor.intercept(request, body, this);
			}
			else {
				HttpMethod method = request.getMethod();
				Assert.state(method != null, "No standard HTTP method");
				ClientHttpRequest delegate = requestFactory.createRequest(request.getURI(), method);
				request.getHeaders().forEach((key, value) -> delegate.getHeaders().addAll(key, value));
				if (body.length > 0) {
					if (delegate instanceof StreamingHttpOutputMessage) {
						StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) delegate;
						streamingOutputMessage.setBody(outputStream -> StreamUtils.copy(body, outputStream));
					}
					else {
						StreamUtils.copy(body, delegate.getBody());
					}
				}
				return delegate.execute();
			}
		}
		
		
ClientHttpRequest delegate = requestFactory.createRequest(request.getURI(), method);
		
```

最终

```sh
com.netflix.loadbalancer;LoadBalancerContext

public URI reconstructURIWithServer(Server server, URI original) {
        String host = server.getHost();
        int port = server.getPort();
        String scheme = server.getScheme();
        
        if (host.equals(original.getHost()) 
                && port == original.getPort()
                && scheme == original.getScheme()) {
            return original;
        }
        if (scheme == null) {
            scheme = original.getScheme();
        }
        if (scheme == null) {
            scheme = deriveSchemeAndPortFromPartialUri(original).first();
        }

        try {
            StringBuilder sb = new StringBuilder();
            sb.append(scheme).append("://");
            if (!Strings.isNullOrEmpty(original.getRawUserInfo())) {
                sb.append(original.getRawUserInfo()).append("@");
            }
            sb.append(host);
            if (port >= 0) {
                sb.append(":").append(port);
            }
            sb.append(original.getRawPath());
            if (!Strings.isNullOrEmpty(original.getRawQuery())) {
                sb.append("?").append(original.getRawQuery());
            }
            if (!Strings.isNullOrEmpty(original.getRawFragment())) {
                sb.append("#").append(original.getRawFragment());
            }
            URI newURI = new URI(sb.toString());
            return newURI;            
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
```



总结：由于加了@LoadBalanced注解，使用RestTemplateCustomizer对所有标注了@LoadBalanced的RestTemplate Bean添加了一个LoadBalancerInterceptor拦截器。利用RestTempllate的拦截器，spring可以对restTemplate bean进行定制，加入loadbalance拦截器进行ip:port的替换，也就是将请求的地址中的服务逻辑名转为具体的服务地址。

### 源码总结

ILoadBalancer 承接 eureka 和 ribbon。获取服务地址列表，选择一个。

每个服务都有ILoadBalancer。

选择服务用 IRule（负载均衡策略）。



## 13.6 自定义Ribbon配置

IRule



Spring Cloud默认的Ribbon配置类是：org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration。

全局：

```sh
ribbon:
  eager-load:
    enabled: true
启动拉取服务列表。
默认false：当服务调用时，采取拉取服务列表。下面有测试。


ribbon:
  http:
    client:
      enabled: true  
默认的请求发起是：HttpURLConnection，true：意思是：改成：HttpClinet.

  okhttp:
    enabled: true ,true:改成OKHttpClient。


```

单个服务配置：

org.springframework.cloud.netflix.ribbon.PropertiesFactory。中

```sh
	public PropertiesFactory() {
		classToProperty.put(ILoadBalancer.class, "NFLoadBalancerClassName");
		classToProperty.put(IPing.class, "NFLoadBalancerPingClassName");
		classToProperty.put(IRule.class, "NFLoadBalancerRuleClassName");
		classToProperty.put(ServerList.class, "NIWSServerListClassName");
		classToProperty.put(ServerListFilter.class, "NIWSServerListFilterClassName");
	}
```



相应配置如下：

```sh
service-sms: 
  ribbon: 
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
```



### Java代码定义

只看标数字的步骤。

PS：修改扫描包配置,使不扫描RibbonConfiguration所在的包com.online.taxi.passenger.ribbonconfig。

```sh
@ComponentScan({"com.online.taxi.passenger.controller",
	"com.online.taxi.passenger.dao",
	"com.online.taxi.passenger.service",
	"com.online.taxi.passenger.ribbonconfigscan"})
-----
巧妙的办法，用注解，单独排除注解修饰的类
@ComponentScan(excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ANNOTATION,value=ExcudeRibbonConfig.class)
})

```

1. 定义com.online.taxi.passenger.ribbonconfig.RibbonConfiguration

```sh
package com.online.taxi.passenger.ribbonconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
/**
 * 该类不应该在主应用程序的扫描之下，需要修改启动类的扫描配置。否则将被所有的Ribbon client共享，
 * 比如此例中：ribbonRule 对象 会共享。
 * @author yueyi2019
 *
 */
@Configuration
@ExcudeRibbonConfig
public class RibbonConfiguration {

	@Bean
	public IRule ribbonRule() {
		return new RandomRule();
	}
	
	
}


```

2. 创建一个空类，配置 service-sms 对应的 ribbon规则

   ```sh
   package com.online.taxi.passenger.ribbonconfigscan;
   
   import org.springframework.cloud.netflix.ribbon.RibbonClient;
   import org.springframework.context.annotation.Configuration;
   
   import com.online.taxi.passenger.ribbonconfig.RibbonConfiguration;
   
   @Configuration
   @RibbonClient(name = "service-sms",configuration = RibbonConfiguration.class)
   public class TestConfiguration {
   
   }
   
   ```

3. 测试

   启动eureka-7900，service-sms-8002，service-sms-8003,api-passenger。

   ut1：正常访问choseServiceName，

   ut2：注释掉如下注解，在 执行ut1.

   ```sh
   @Configuration
   @RibbonClient(name = "service-sms",configuration = RibbonConfiguration.class)
   
   ```

   可以发现：一个为 轮询，一个为随机。

4. 此方法只改变service-sms的 负载均衡策略。其他服务名没有影响。

5. 给所有client设置随机策略

   ```sh
   启动类:@RibbonClients(defaultConfiguration = RibbonConfiguration.class)
   
   ```

   



### 属性定义

针对服务定ribbon策略：

```sh
service-sms: 
  ribbon: 
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule

```

给所有服务定ribbon策略：

```sh
ribbon: 
  NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule

```

属性配置方式优先级高于Java代码。

## 13.7 Ribbon脱离Eureka

```sh
service-sms:
  ribbon:
    eureka:
    # 将Eureka关闭，则Ribbon无法从Eureka中获取服务端列表信息
    enabled: false
    # listOfServers可以设置服务端列表
    listOfServers:localhost:8090,localhost:9092,localhost:9999

```

为service-sms设置 请求的网络地址列表。

Ribbon可以和服务注册中心Eureka一起工作，从服务注册中心获取服务端的地址信息，也可以在配置文件中使用listOfServers字段来设置服务端地址。



## 13.8 饥饿加载

```sh
ribbon:
  eager-load:
    enabled: true
    clients:
    - SERVICE-SMS

```

Spring Cloud默认是懒加载，指定名称的Ribbon Client第一次请求时，对应的上下文才会被加载，所以第一次访问慢。



改成以上饥饿加载后，将在启动时加载对应的程序上下文，从而提高首次请求的访问速度。

测试：

1. 上面配置为false启动，控制台没打印服务列表。

2. 为true：打印服务列表如下。

   

   或者，用debug。也能看出。

   在private List<DiscoveryEnabledServer> obtainServersViaDiscovery()首行，打断点。

   饥饿进入此代码。

   

   ```sh
   2020-01-21 16:08:03.605  INFO [api-driver,,,] 13400 --- [           main] c.n.l.DynamicServerListLoadBalancer      : DynamicServerListLoadBalancer for client SERVICE-SMS initialized: DynamicServerListLoadBalancer:{NFLoadBalancer:name=SERVICE-SMS,current list of Servers=[30.136.133.11:8002],Load balancer stats=Zone stats: {defaultzone=[Zone:defaultzone;	Instance count:1;	Active connections count: 0;	Circuit breaker tripped count: 0;	Active connections per server: 0.0;]
   },Server stats: [[Server:30.136.133.11:8002;	Zone:defaultZone;	Total Requests:0;	Successive connection failure:0;	Total blackout seconds:0;	Last connection made:Thu Jan 01 08:00:00 CST 1970;	First connection made: Thu Jan 01 08:00:00 CST 1970;	Active Connections:0;	total failure count in last (1000) msecs:0;	average resp time:0.0;	90 percentile resp time:0.0;	95 percentile resp time:0.0;	min resp time:0.0;	max resp time:0.0;	stddev resp time:0.0]
   ]}ServerList:org.springframework.cloud.netflix.ribbon.eureka.DomainExtractingServerList@5af4328e
   2020-01-21 16:08:04.574  INFO [api-driver,,,] 13400 --- [erListUpdater-0] c.netflix.config.ChainedDynamicProperty  : Flipping property: SERVICE-SMS.ribbon.ActiveConnectionsLimit to use NEXT property: niws.loadbalancer.availabilityFilteringRule.activeConnectionsLimit = 2147483647
   
   ```

   

PS:除了和RestTemplate进行配套使用之外，Ribbon还默认被集成到了OpenFeign中，当使用@FeignClient时，OpenFeign默认使用Ribbon来进行网络请求的负载均衡。



实践，在api-passenger的yml中，添加 service-sms ribbon NFLoad



------

第4节课完。2020年2月9日。

课下问题：localhost

host：将域名，映射成IP。

```sh
C:\Windows\System32\drivers\etc

127.0.0.1       eureka-7900
127.0.0.1       eureka-7901
127.0.0.1       eureka-7902
127.0.0.1       eureka-7903
```

百度一下。



上节课问题：

***服务名：大小写。***上节课有个同学提出来的。yml中用小写。

为了排除干扰项，每次修改完配置，都测试一下是否恢复到默认。

先说知识点，后面挨个测试。



Java自定义配置（api-driver-ribbon）

```sh
通用配置：
启动类上：
@RibbonClients(defaultConfiguration = RibbonConfiguration.class)


配置类：
@Configuration
@ExcudeRibbonConfig
public class RibbonConfiguration {

	/**
	 * 修改IRule
	 * @return
	 */
	@Bean
	public IRule ribbonRule() {
		return new RandomRule();
	}
	
}

启动4个服务提供者：
service-valuation-8060,8061
service-sms-8002,8003

启动api-driver-ribbon
改写SmsController中choseServiceName方法。

	@GetMapping("/choseServiceName/{serviceName}")
	public ResponseResult choseServiceName(@PathVariable("serviceName") String serviceName) {

		ServiceInstance si = loadBalancerClient.choose(serviceName);
		System.out.println(serviceName+"节点信息：url:"+si.getHost()+",port:"+si.getPort());
		
		return ResponseResult.success("");
	}
	
看控制台：结果
结果：2个服务都是随机。

```

个性化配置

```sh
启动类换成如下：
@RibbonClient(name = "service-sms",configuration = RibbonConfiguration.class)

结果：
service-valuation轮询
service-sms随机
```



总结：一个是配置所有服务提供者的IRule，一个是配置固定服务的IRule。



配置文件：

```sh
#正常ribbon，单独配置service-sms的负载均衡策略
service-sms:  
  ribbon:  
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
    # 自定义负载策略
#    NFLoadBalancerRuleClassName: com.online.taxi.driver.ribbonconfig.MsbRandomRule

#service-valuation: 
#  ribbon: 
#    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule

```

依旧启动4个服务提供者，看日志。



测试点：

1. 什么IRule都不配。测试service-valuation,service-sms的，根据服务名获取服务端信息。看控制台，是否为轮询。
2. 测试@RibbonClients的默认配置，配置类中改成随机策略。测试是否随机。
3. 测试@RibbonClients的默认配置，配置类中改成自定义的策略（MsbRandomRule）。测试是否选2,0结尾。（下面讲完，再继续），sms ：2，valuation：0。
4. 测试@RibbonClient配置，配置类中将service-sms改成随机策略。测试是否sms是随机，而valuation为轮询。
5. 将上面配置全部注释，测试sms，valuation是否都为轮询。
6. 测试yml配置：只配置service-sms,为随机，测试，是否sms为随机，valuation为轮询。
7. 测试yml配置：配置service-sms,service-valuation为随机，测试，是否都为随机。
8. 恢复默认。
9. 测试yml配置：只配置service-sms,为自定义策略（MsbRandomRule），测试sms 端口 是否为2 结尾。



配置的内容都在：com.netflix.loadbalancer这个包下。



## 13.9 自定义负载均衡策略

1. 自定义Rule。实现：如果有端口以2结尾，则选择。没有顺序找一个。

```sh
import java.util.List;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

public class MsbRandomRule extends AbstractLoadBalancerRule{
 
	
 
	public Server choose(ILoadBalancer lb, Object key) {
		
		if (lb == null) {
			return null;
		}
		Server server = null;
 
		while (server == null) {
			if (Thread.interrupted()) {
				return null;
			}
			List<Server> upList = lb.getReachableServers();  //激活可用的服务
			List<Server> allList = lb.getAllServers();  //所有的服务
 
			int serverCount = allList.size();
			if (serverCount == 0) {
				return null;
			}
			//选自定义元数据的server，选择端口以2结尾的服务。
			for (int i = 0; i < upList.size(); i++) {
				server = upList.get(i);
				String port = server.getHostPort();
				if(port.endsWith("2") || port.endsWith("0")) {
					break;
				}
				
			}
			
                       						
			if (server == null) {
				Thread.yield();
				continue;
			}
 
			if (server.isAlive()) {
				return (server);
			}
 
			// Shouldn't actually happen.. but must be transient or a bug.
			server = null;
			Thread.yield();
		}
		return server;
	}
	@Override
	public Server choose(Object key){
		return choose(getLoadBalancer(), key);
	}
 
	@Override
	public void initWithNiwsConfig(IClientConfig clientConfig){
	}
}
```



2. yml

```sh
#正常ribbon
service-sms: 
  ribbon: 
    # 自定义负载策略
    NFLoadBalancerRuleClassName: com.online.taxi.driver.ribbonconfig.MsbRandomRule
 
```

------



或者下面配置也可以实现。

```sh
@RibbonClient(name = "service-sms",configuration = RibbonConfiguration.class)

@Configuration
@ExcudeRibbonConfig
public class RibbonConfiguration {

	
	/**
	 * 修改IRule
	 * @return
	 */
//	@Bean
//	public IRule ribbonRule() {
//		return new RandomRule();
//	}
	
	/**
	 * 自定义rule
	 * @return
	 */
	@Bean
	public IRule ribbonRule() {
		return new MsbRandomRule();
	}
	
}
```

------

依旧启动service-sms-8002,8003的提供者。

查看chooseName后，都是 8002

```sh
service-sms节点信息：url:LAPTOP-BH5NFMO1,port:8002
service-sms节点信息：url:LAPTOP-BH5NFMO1,port:8002
service-sms节点信息：url:LAPTOP-BH5NFMO1,port:8002
service-sms节点信息：url:LAPTOP-BH5NFMO1,port:8002
service-sms节点信息：url:LAPTOP-BH5NFMO1,port:8002
service-sms节点信息：url:LAPTOP-BH5NFMO1,port:8002
service-sms节点信息：url:LAPTOP-BH5NFMO1,port:8002
service-sms节点信息：url:LAPTOP-BH5NFMO1,port:8002
service-sms节点信息：url:LAPTOP-BH5NFMO1,port:8002
service-sms节点信息：url:LAPTOP-BH5NFMO1,port:8002
```



思考：如何按照流量分发（60%到A，40%到B）？答案在下面。















负载均衡实际上是做请求分发的：将60%流量分发到A，将40%到B，可以更复杂。大家发挥想象。

```sh
		Random random = new Random();
		final int number = random.nextInt(10);
		if(number<7){
			return servers.get(0);
		}
		return servers.get(1);
```



下去同学们可以跟踪一下断点：LoadBalancerInterceptor中intercept



## 13.10 小结

1. 几种负载均衡。（硬，软（服务端，客户端（Ribbon）））
2. Ribbon可以单独使用。需要提供服务地址列表。
3. 原理。拦截请求，然后替换地址（servicename到ip+port）。
4. 源码。ILoadBalancer，Map<服务名，ILoadBalancer>
5. @LoadBalanced，拦截器。（LoadBalancerInterceptor中intercept）
6. 自定义配置：java配置，yml配置。
7. 自定义负载均衡策略



微服务可以用服务端负载均衡吗？



坏处：先得找到负载均衡服务器，怎么找，需要ip和端口，和微服务 悖论了（因为首先得用客户端负载均衡，到达服务端负载均衡后，再解析后续地址，为什么不一步到位呢？ 还能减少一个服务。）。就算找到了，然后再增加一层 服务名到ip的解析。如果有服务端负载均衡的话，需要客户端先请求一个服务端负载均衡，然后负载均衡再去找具体ip，如果服务端负载均衡挂了，就瘫痪了。