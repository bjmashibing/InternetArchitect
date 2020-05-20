package com.mashibing.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class HiController {
	
	@GetMapping("/hi")
	public String hi() {
	//	UserDetailsServiceAutoConfiguration
		System.out.println("来啦老弟~！");
		return "hi";
	}
}
