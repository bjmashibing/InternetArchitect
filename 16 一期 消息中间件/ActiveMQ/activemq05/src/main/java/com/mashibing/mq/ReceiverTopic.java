
package com.mashibing.mq;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * 消息接受
 * @author 一明哥
 *
 */
public class ReceiverTopic {

	public static void main(String[] args) throws Exception{

		// 1.获取连接工厂
		

		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				"admin",
				"admin",
				"tcp://localhost:5671"
				);
		// 2.获取一个向ActiveMQ的连接
		Connection connection = connectionFactory.createConnection();
		
		connection.start();
		// 3.获取session
		Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
		// 4. 找目的地，获取destination，消费端，也会从这个目的地取消息
		
		Destination topic = session.createTopic("tpk?consumer.retroactive=true");
		
		// 5.获取消息
	
		MessageConsumer consumer = session.createConsumer(topic);
		consumer.setMessageListener(new MessageListener() {
			
			public void onMessage(Message message) {
				// TODO Auto-generated method stub
			
				xx(*);
				
				
				message.acknowledge();
				
				System.out.println("收到消息。。。");
			}
			
			
			
		});
	}
	
}
