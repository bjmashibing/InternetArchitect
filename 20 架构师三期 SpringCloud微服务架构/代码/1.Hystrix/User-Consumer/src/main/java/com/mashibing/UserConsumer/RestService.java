package com.mashibing.UserConsumer;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class RestService {

	
	@Autowired
	RestTemplate template;
	
	@HystrixCommand(defaultFallback = "back")
	public String alive() {
		// TODO Auto-generated method stub
		
		String url ="http://user-provider/alive";
		   
		String str = template.getForObject(url, String.class);
		
		return str;
	}
	
	
	
	public String back() {
		
		
		return "呵呵";
	}

}
