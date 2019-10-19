package com.mashibing.springboot.service;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Service(version = "1.0.0" , interfaceClass = ITestService.class,
	loadbalance = "roundrobin",executes = 1
	,timeout = 1000 ,retries = 5
		)

// 幂等   update maxiaoliu = 598  ++ 不行
// retries = 5 + 第一次调用1 
public class TestServiceImpl implements ITestService{
	
	@Value("${dubbo.protocol.port}")
	private int port;
	
	@Override
	public int getPort() {

		long start = System.currentTimeMillis();
		
		System.out.println("---- action");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long end = System.currentTimeMillis();
		System.out.println(Thread.currentThread().getName() + " ：线程  运行时间" + (end-start) + "ms");
		return port;
	}

}
