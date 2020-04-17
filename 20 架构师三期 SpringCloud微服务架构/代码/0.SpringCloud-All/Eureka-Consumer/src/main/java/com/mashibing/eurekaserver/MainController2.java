package com.mashibing.eurekaserver;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.eureka.EurekaServiceInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.InstanceInfo.InstanceStatus;
import com.netflix.discovery.EurekaClient;

@RestController
public class MainController2 {

	
	
	//      /User          资源 事先定义
	
	/*
	 *     http://xxx/User
	 *     
	 *     
	 *     http://xxx/User/getUserList  Get
	 *     http://xxx/users  	        Get   约定  像对于到数据的一张表
	 *     http://xxx/v1/User/getUserList
	 * 
	 * 	   http://xxx/v1/User/deleteById     Get/Post
	 * 	   
	 * 		http://xxx/v1/users/1             Get= 获取id=1的这个用户  Delete请求 = 删除 put=修改
	 * 	   http://xxx/v2/users/1
	 * 
	 *     针对单表 不再重复crud SpringData Rest
	 * 
	 * 
	 */
	
	
	@Autowired
	// 抽象
	DiscoveryClient client;
	
	@Autowired
	EurekaClient client2;
	
	@Autowired
	LoadBalancerClient lb;
	
	
	
	@Autowired
	RestTemplate restTemplate;
	
	@GetMapping("/client6")
	public Object client6() {
		
		// ribbon 完成客户端的负载均衡，过滤掉down了的节点
		ServiceInstance instance = lb.choose("provider");
		
		String url ="http://" + instance.getHost() +":"+ instance.getPort() + "/HelloWorld/getHi";
		   
		String respStr = restTemplate.getForObject(url, String.class);

		return respStr;
	}
	
	/**
	 * 手动负载均衡
	 * @return
	 */
	
	@Autowired
	DiscoveryClient discoveryClient;
	
	
	@GetMapping("/client7")
	public Object client7() {
		
		
		List<ServiceInstance> instances = discoveryClient.getInstances("provider");
		
		// 自定义轮训算法
		
		// 随机
		int nextInt = new Random().nextInt(instances.size());
		AtomicInteger atomicInteger = new AtomicInteger();
	
		// 轮训
		int i = atomicInteger.getAndIncrement();
		instances.get(i % instances.size());
		
		// 权重。。
		for (ServiceInstance serviceInstance : instances) {
	//	int quanzhong = 	serviceInstance.getMetadata(); // 权重  1-9
		
		
		}
		
		
		
		ServiceInstance instance = instances.get(nextInt);
		
		// ribbon 完成客户端的负载均衡，过滤掉down了的节点
	//	ServiceInstance instance = lb.choose("provider");
		
		String url ="http://" + instance.getHost() +":"+ instance.getPort() + "/getHi";
		   
		String respStr = restTemplate.getForObject(url, String.class);

		return respStr;
	}
	
	
	
	@GetMapping("/client8")
	public Object client8() {
		
		
		
		// ribbon 完成客户端的负载均衡，过滤掉down了的节点
		ServiceInstance instance = lb.choose("provider");
		
		String url ="http://" + instance.getHost() +":"+ instance.getPort() + "/getHi";
		   
		String respStr = restTemplate.getForObject(url, String.class);
		
		System.out.println(respStr);
		return respStr;
	}
	
	
	@GetMapping("/client9")
	public Object client9() {
		// 自动处理URL
		String url ="http://provider/getHi";
		   
		String respStr = restTemplate.getForObject(url, String.class);
		
		System.out.println(respStr);
		return respStr;
	}
		
}
