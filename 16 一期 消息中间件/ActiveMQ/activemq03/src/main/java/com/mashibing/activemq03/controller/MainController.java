package com.mashibing.activemq03.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mashibing.activemq03.service.SenderService;

@RestController
public class MainController {

	
	@Autowired
	SenderService senderSrv;
	
	@RequestMapping("send")
	public String send() {
		
		
		senderSrv.send("springboot","hello~!");
	
		return "ok";
	}
}
