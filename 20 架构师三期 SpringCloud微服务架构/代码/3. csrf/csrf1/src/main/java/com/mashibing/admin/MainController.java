package com.mashibing.admin;

import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class MainController {


	
	@GetMapping("/login.html")
	public String login() {
		return "login";
	}
	
	@GetMapping("/ok")
	public String ok() {
		return "ok";
	}
	
}
