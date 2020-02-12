
package com.mashibing.mq;
import java.util.UUID;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQQueue;

/**
 * 消息发送
 * @author 一明哥
 *
 */
public class SenderQueue {

	public static void main(String[] args) throws Exception{

		// 1.获取连接工厂
		

		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				"admin",
				"admin",
				"nio://localhost:5671"
				);
		Connection connection = connectionFactory.createConnection();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		
		connection.start();
		Queue queue = session.createQueue("xxoo");
		
		MessageProducer producer = session.createProducer(queue);
		
		
		// 发送一条消息给指定消费者
        Message message = session.createTextMessage("Message from ServerA xxx" ); 
        
        String cid = UUID.randomUUID().toString();
        message.setJMSCorrelationID(cid);
        message.setStringProperty("type", "C");
        
        producer.send(message); 
        
        
        System.out.println("P 消息发送完成");
        // 等待消息确认的消费者
        MessageConsumer consumer = session.createConsumer(queue,"JMSCorrelationID='"+cid+"' and type='P'");
        consumer.setMessageListener(new MessageListener() {
			
			public void onMessage(Message message) {
				// TODO Auto-generated method stub
				System.out.println("P 收到消息确认...");
				try {
					System.out.println("P 消息是" + ((TextMessage)(message)).getText());
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
        
		System.out.println("System exit....");
		
	}
	
		
	
}
