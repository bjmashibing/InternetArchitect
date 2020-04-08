package com.mashibing.eurekaserver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

	@GetMapping("/getHi")
	public String getHi() {
		
		return "Hi";
	}
	
}
