package com.mashibing.activemq03.service;

import java.util.ArrayList;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.JmsProperties.AcknowledgeMode;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

@Service
public class SenderService {

	@Autowired
	private JmsMessagingTemplate jmsMessagingTemplate;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	

	
	public void send(String destination, String msg) {
		
		
		ConnectionFactory connectionFactory = jmsTemplate.getConnectionFactory();
		try {
			Connection connection = connectionFactory.createConnection();
			connection.start();
			
			Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jmsTemplate.send(destination,new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				
				TextMessage textMessage = session.createTextMessage("xxoo");
				textMessage.setStringProperty("hehe", "enen");
				return textMessage;
			}
		});
		
		
	}
	public void send2(String destination, String msg) {

		
		ArrayList<String> list = new ArrayList<>();
		
		list.add("malaoshi");
		list.add("lain");
		list.add("zhou");
		jmsMessagingTemplate.convertAndSend(destination, list);
	}
	public void send3(String destination, String msg) {
		
		
		ArrayList<String> list = new ArrayList<>();
		
		list.add("malaoshi");
		list.add("lain");
		list.add("zhou");
		jmsMessagingTemplate.convertAndSend(new ActiveMQQueue(destination), list);
	}
}
