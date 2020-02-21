
package com.mashibing.mq;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;

/**
 * 消息发送
 * @author 一明哥
 *
 */
public class SenderTopic {

	public static void main(String[] args) throws Exception{

		// 1.获取连接工厂
		

		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				"admin",
				"admin",
				"tcp://localhost:5671"
				);
		// 2.获取一个向ActiveMQ的连接
		Connection connection = connectionFactory.createConnection();
		// 3.获取session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 4. 找目的地，获取destination，消费端，也会从这个目的地取消息
		// 临时节点 生命周期 隔离 connection，
		Destination topic = session.createTopic("tpk");
		
		MessageProducer producer = session.createProducer(topic);
		for (int i = 0; i < 100; i++) {
			
			TextMessage textMessage = session.createTextMessage("hi: " );
			
			producer.send(textMessage);
		}
	
		connection.close();
		
		System.out.println("System exit....");
		
	}
	
		
	
}
