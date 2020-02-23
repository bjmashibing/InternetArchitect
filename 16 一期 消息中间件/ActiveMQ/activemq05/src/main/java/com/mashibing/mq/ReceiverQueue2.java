
package com.mashibing.mq;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.apache.activemq.command.ActiveMQTextMessage;

/**
 * 消息接受
 * @author 一明哥
 *
 */
public class ReceiverQueue2 {

	public static void main(String[] args) throws Exception{

		// 1.获取连接工厂

		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				"admin",
				"admin",
				"tcp://localhost:5671"
				);
		Connection connection = connectionFactory.createConnection();
		connection.start();
		// 3.获取session
		final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 4. 找目的地，获取destination，消费端，也会从这个目的地取消息
	
		
		MessageConsumer consumer = session.createConsumer(new ActiveMQQueue("xxoo"));

		/**
		 * ActiveMQTextMessage 
		 * {commandId = 5, responseRequired = true, 
		 * messageId = ID:DESKTOP-OEK4RCQ-9773-1581339812245-1:1:1:1:1, 
		 * originalDestination = null,
		 *  originalTransactionId = null, 
		 *  producerId = ID:DESKTOP-OEK4RCQ-9773-1581339812245-1:1:1:1, 
		 *  destination = queue://xxoo, transactionId = null, expiration = 0,
		 *   
		 *    arrival = 0, 
		 *    消息生成时间
		 *    timestamp = 1581339812712,
		 *    brokerInTime = 1581339812712, 
		 *    brokerOutTime = 1581340554237
		 *    consumer的时间
		 *    , correlationId = 504d0133-d083-4bae-89da-7200fd971a7f, replyTo = null, persistent = true, type = null, priority = 4, groupID = null, groupSequence = 0, targetConsumerId = null, compressed = false, userID = null, content = org.apache.activemq.util.ByteSequence@43142445, marshalledProperties = org.apache.activemq.util.ByteSequence@4d318f20, dataStructure = null, redeliveryCounter = 0, size = 0, properties = {type=C}, readOnlyProperties = true, readOnlyBody = true, droppable = false, jmsXGroupFirstForConsumer = false, text = Message from ServerA xxx}

		 */
		consumer.setMessageListener(new MessageListener() {
			
			public void onMessage(Message message) {
				// TODO Auto-generated method stub
				System.out.println("接到一条消息。。。。");
				System.out.println("开始发送确认消息。。。");
				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSSS");
					
					String formatStr = dateFormat.format(new Date(message.getJMSTimestamp()));
					
					
					ActiveMQTextMessage tmsg = (ActiveMQTextMessage)message;
					tmsg.getBrokerInTime();
					System.out.println("Timestamp():" + formatStr);
					System.out.println("BrokerInTime():" + dateFormat.format(new Date(tmsg.getBrokerInTime())));
					System.out.println("BrokerOutTime():" + dateFormat.format(new Date(tmsg.getBrokerOutTime())));
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
			}
		});
	}
	
	
}
