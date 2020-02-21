package com.mashibing.activemq03.service;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class Receiver {

	@JmsListener(destination = "springboot",containerFactory = "jmsListenerContainerTopic")
	public void rece(String msg) {
		
		System.out.println("收到消息：" + msg);
	}
}
