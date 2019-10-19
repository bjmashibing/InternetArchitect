package com.mashibing.springboot.service;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Value;

@Service(version = "1.0.0" ,retries = 5,timeout = 1000,weight = 1 , interfaceClass = TestService.class,loadbalance = "roundrobin")
public class TestServiceImpl implements TestService {

	
	@Value("${dubbo.protocol.port}")
	int port;
	@Override
	public int getPort(String name) {
		// TODO Auto-generated method stub
		try {
			System.out.println("---准备睡觉~~~" + name);
			System.out.println(Thread.currentThread().getName());
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return port;
	}

}
