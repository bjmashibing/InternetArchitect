
package com.mashibing.mq;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * 消息发送
 * @author 一明哥
 *
 */
public class Sender {

	public static void main(String[] args) throws Exception{

		// 1.获取连接工厂
		

		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				"admin",
				"admin",
				"tcp://localhost:61616"
				);
		// 2.获取一个向ActiveMQ的连接
		Connection connection = connectionFactory.createConnection();
		// 3.获取session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 4. 找目的地，获取destination，消费端，也会从这个目的地取消息
		
		Queue queue = session.createQueue("user");
		
		// 51.消息创建者
		
		MessageProducer producer = session.createProducer(queue);
	//	producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		// consumer -> 消费者
		// producer -> 创建者
		// 5.2. 创建消息
		
		for (int i = 0; i < 1000; i++) {
			
			TextMessage textMessage = session.createTextMessage("hi: " + i);
			
			// 5.3 向目的地写入消息
			
			if(i % 4 == 0) {
				// 设置消息的优先级
				// 对producer 整体设置
			//	producer.setPriority(9);
			//	producer.send(textMessage,DeliveryMode.PERSISTENT,9,1000 * 100);
				textMessage.setJMSPriority(9);
			}
			
				producer.send(textMessage);
	//	Thread.sleep(3000);
		}
		
		// 6.关闭连接
		connection.close();
		
		System.out.println("System exit....");
		
	}
	
		
	
}
