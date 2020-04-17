package com.mashibing.eurekaserver;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

	
	@Value("${server.port}")
	String port;
	
	@GetMapping("/getHi")
	public String getHi() {
		
		return "Hi!,我的port：" + port;
	}
	
	
	@GetMapping("/getMap")
	public Map<String, String> getMap() {
		
		return Collections.singletonMap("id", "100");
	}
	
	
	@GetMapping("/getObj")
	public Person getObj() {
		Person person = new Person(100,"xiao6");
		return person;
	}
	
	@GetMapping("/getObj2")
	public Person getObj2(String name) {
		Person person = new Person(100,name);
		return person;
	}		
	
	
	
	@PostMapping("/postLocation")
	public URI postParam(@RequestBody Person person,HttpServletResponse response) throws Exception {

		URI uri = new URI("https://www.baidu.com/s?wd="+person.getName().trim());
	
	//	response.addHeader("Location", uri.toString());
		
		return uri;
	
	}
	
	
	
	
	
	
	@Autowired
	HealthStatusService hsrv;
	
	@GetMapping("/health")
	public String health(@RequestParam("status") Boolean status) {
		
		hsrv.setStatus(status);
		return hsrv.getStatus();
	}
}
