package com.mashibing.mq;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueRequestor;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class SenderQueueRequestor {
	public static void main(String[] args) throws Exception{

		// 1.获取连接工厂
		

		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				"admin",
				"admin",
				"nio://localhost:5671"
				);
		ActiveMQConnection connection = (ActiveMQConnection)connectionFactory.createConnection();
		QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		
		connection.start();
		Queue queue = session.createQueue("xxoo");
		
		MessageProducer producer = session.createProducer(queue);
		
		QueueRequestor queueRequestor = new QueueRequestor(session, queue);
		// 想 broker发送请求，等待响应
		System.out.println("=== 准备发请求");
		TextMessage responseMsg = (TextMessage)queueRequestor.request(session.createTextMessage("xxx"));
		System.out.println("=== 请求发完了");
		
		System.out.println("responseMsg:" + responseMsg.getText());
		        
		System.out.println("System exit....");
		
	}
	
}
