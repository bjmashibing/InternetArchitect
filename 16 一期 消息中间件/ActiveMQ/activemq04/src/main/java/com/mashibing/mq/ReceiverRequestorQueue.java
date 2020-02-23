
package com.mashibing.mq;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

/**
 * 消息接受
 * @author 一明哥
 *
 */
public class ReceiverRequestorQueue {

	public static void main(String[] args) throws Exception{

		// 1.获取连接工厂

		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				"admin",
				"admin",
				"tcp://localhost:5671"
				);
		// 1.1 添加信任的 持久化类型
		
		ArrayList<String> list = new ArrayList<String>();
		list.add(Girl.class.getPackage().getName());

		connectionFactory.setTrustedPackages(list);
		
		// 2.获取一个向ActiveMQ的连接
		Connection connection = connectionFactory.createConnection();
		System.out.println("ReceiverQueue-1 Started");
		connection.start();
		// 3.获取session
		final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 4. 找目的地，获取destination，消费端，也会从这个目的地取消息
	
		
		MessageConsumer consumer = session.createConsumer(new ActiveMQQueue("xxoo"));

		consumer.setMessageListener(new MessageListener() {
			
			public void onMessage(Message message) {
				// TODO Auto-generated method stub
				System.out.println("接到一条消息。。。。");
				System.out.println("开始发送确认消息。。。");
				
				try {
					Destination replyTo = message.getJMSReplyTo();
					System.out.println("replyTo:" + replyTo);
					MessageProducer producer = session.createProducer(replyTo);
					producer.send(session.createTextMessage("xxxx..."));
					
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
			

	}
	
	
}
