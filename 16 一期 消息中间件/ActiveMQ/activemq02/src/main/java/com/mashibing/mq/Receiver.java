
package com.mashibing.mq;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
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
public class Receiver {

	public static void main(String[] args) throws Exception{

		// 1.获取连接工厂
		

		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				"admin",
				"admin",
				"tcp://localhost:61616"
				);
		// 2.获取一个向ActiveMQ的连接
		Connection connection = connectionFactory.createConnection();
		
		connection.start();
		// 3.获取session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 4. 找目的地，获取destination，消费端，也会从这个目的地取消息
		
		Destination queue = session.createQueue("user");
		
		// 5.获取消息
	
		MessageConsumer consumer = session.createConsumer(queue);
		
		for(int i=0;;i++) {
			
			TextMessage message = (TextMessage)consumer.receive();
			System.out.println("-----");
			System.out.println("message2:" + message.getText());
		//	System.out.println("metadata:" + message);
	//ActiveMQTextMessage {commandId = 303, responseRequired = false, messageId = ID:DESKTOP-OEK4RCQ-12451-1578750463269-1:1:1:1:100, originalDestination = null, originalTransactionId = null, producerId = ID:DESKTOP-OEK4RCQ-12451-1578750463269-1:1:1:1, destination = queue://user, transactionId = TX:ID:DESKTOP-OEK4RCQ-12451-1578750463269-1:1:100, expiration = 0, timestamp = 1578750760787, arrival = 0, brokerInTime = 1578750760787, brokerOutTime = 1578750981647, correlationId = null, replyTo = null, persistent = true, type = null, priority = 4, groupID = null, groupSequence = 0, targetConsumerId = null, compressed = false, userID = null, content = null, marshalledProperties = null, dataStructure = null, redeliveryCounter = 2, size = 0, properties = null, readOnlyProperties = true, readOnlyBody = true, droppable = false, jmsXGroupFirstForConsumer = false, text = hi: 99}

			// 事务中的一个操作
		//	message.acknowledge();
			// commit()
		//	if(i % 4 == 0)
		//		session.commit();
			
		}
	}
	/*
	 * 还有个问题就是receiver端createsession时是true，后面的参数为啥不会被覆盖？
	 * 
	 * 老师有点不理解，你未确认，另外一个消费者不重复消费？但你再重开消费者一个就能消费
	 */
	
}
