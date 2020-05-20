package com.mashibing.eurekaserver;

import java.util.List;

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
public class MainController {

	@Autowired
	// 抽象
	DiscoveryClient client;
	
	@Autowired
	EurekaClient client2;
	
	@Autowired
	LoadBalancerClient lb;
	
	
	@GetMapping("/getHi")
	public String getHi() {
		
		return "Hi";
	}
	
	
	@GetMapping("/client")
	public String client() {
		List<String> services = client.getServices();
		
		for (String str : services) {
			System.out.println(str);
			
		}
		return "Hi";
	}
	@GetMapping("/client2")
	public Object client2() {
		return client.getInstances("provider");
	}
	
	
	@GetMapping("/client3")
	public Object client3() {
		
		List<ServiceInstance> instances = client.getInstances("provider");
		for (ServiceInstance ins : instances) {
			System.out.println(ToStringBuilder.reflectionToString(ins));
		}
		
		return "xxoo";
	}
	
	@GetMapping("/client4")
	public Object client4() {
		
		// 具体服务
	//	List<InstanceInfo> instances = client2.getInstancesById("localhost:provider:80");
		
		// 使用服务名 ，找列表
		List<InstanceInfo> instances = client2.getInstancesByVipAddress("provider", false);
		
		
		for (InstanceInfo ins : instances) {
			System.out.println(ToStringBuilder.reflectionToString(ins));
		}
		
		if(instances.size()>0) {
			// 服务
			InstanceInfo instanceInfo = instances.get(0);
			if(instanceInfo.getStatus() == InstanceStatus.UP) {
				
			   String url =	"http://" + instanceInfo.getHostName() +":"+ instanceInfo.getPort() + "/getHi";
			
			   System.out.println("url:" + url);
			   
			   RestTemplate restTemplate = new RestTemplate();
			   
			   String respStr = restTemplate.getForObject(url, String.class);
			   
			   System.out.println("respStr"  + respStr);
			}
			
		}
		return "xxoo";
	}
	
	
	
	
	@GetMapping("/client5")
	public Object client5() {
		
		// ribbon 完成客户端的负载均衡，过滤掉down了的节点
		ServiceInstance instance = lb.choose("provider");
		
		String url ="http://" + instance.getHost() +":"+ instance.getPort() + "/getHi";
		   
		RestTemplate restTemplate = new RestTemplate();
		   
		String respStr = restTemplate.getForObject(url, String.class);
		   
		System.out.println("respStr"  + respStr);

		return "xxoo";
	}
}
