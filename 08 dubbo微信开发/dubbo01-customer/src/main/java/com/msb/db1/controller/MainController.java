package com.msb.db1.controller;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msb.db1.service.DemoService;

@RestController
@RequestMapping("/")
public class MainController {

	// api/v888/server/method
	@RequestMapping("/main")
	public String main() {
		return ("hehe");
	}
}
