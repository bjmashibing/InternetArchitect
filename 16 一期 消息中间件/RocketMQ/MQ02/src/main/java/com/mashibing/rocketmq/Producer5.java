package com.mashibing.rocketmq;

import java.util.ArrayList;
import java.util.List;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByRandom;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

/**
 * 消息发送者
 * @author 一明哥
 *
 */
public class Producer5 {

	public static void main(String[] args)throws Exception {
		
		DefaultMQProducer producer = new DefaultMQProducer("pgp01");
		
		producer.setNamesrvAddr("192.168.150.113:9876");
		
		
		producer.start();
		
		
		for (int i = 0; i < 20; i++) {
				
			Message message = new Message("xxoo007", ("hi!" + i).getBytes());
			
			producer.send(
					// 要发的那条消息
					message,
					// queue 选择器 ，向 topic中的哪个queue去写消息
					new MessageQueueSelector() {
						// 手动 选择一个queue
						public MessageQueue select(
								// 当前topic 里面包含的所有queue
								List<MessageQueue> mqs
								, 
								// 具体要发的那条消息
								Message msg
								
								// 对应到 send（） 里的 args
								, Object arg) {
							// TODO Auto-generated method stub
							
							// 向固定的一个queue里写消息
							MessageQueue queue = mqs.get(0);
							// 选好的queue
							return queue;
						}
					},
					// 自定义参数
					0, 2000);
		}
		
		for (int i = 0; i < 20; i++) {
				
			Message message = new Message("xxoo007", ("hi!" + i).getBytes());
			
			producer.send(
					// 要发的那条消息
					message,
					// queue 选择器 ，向 topic中的哪个queue去写消息
					new MessageQueueSelector() {
						// 手动 选择一个queue
						public MessageQueue select(
								// 当前topic 里面包含的所有queue
								List<MessageQueue> mqs
								, 
								// 具体要发的那条消息
								Message msg
								
								// 对应到 send（） 里的 args
								, Object arg) {
							// TODO Auto-generated method stub
							
							// 向固定的一个queue里写消息
							MessageQueue queue = mqs.get(1);
							// 选好的queue
							return queue;
						}
					},
					// 自定义参数
					0, 2000);
		}
		
		for (int i = 0; i < 20; i++) {
				
			Message message = new Message("xxoo007", ("hi!" + i).getBytes());
			
			producer.send(
					// 要发的那条消息
					message,
					// queue 选择器 ，向 topic中的哪个queue去写消息
					new MessageQueueSelector() {
						// 手动 选择一个queue
						public MessageQueue select(
								// 当前topic 里面包含的所有queue
								List<MessageQueue> mqs
								, 
								// 具体要发的那条消息
								Message msg
								
								// 对应到 send（） 里的 args
								, Object arg) {
							// TODO Auto-generated method stub
							
							// 向固定的一个queue里写消息
							MessageQueue queue = mqs.get(2);
							// 选好的queue
							return queue;
						}
					},
					// 自定义参数
					0, 2000);
		}
		
		for (int i = 0; i < 20; i++) {
				
			Message message = new Message("xxoo007", ("hi!" + i).getBytes());
			
			producer.send(
					// 要发的那条消息
					message,
					// queue 选择器 ，向 topic中的哪个queue去写消息
					new MessageQueueSelector() {
						// 手动 选择一个queue
						public MessageQueue select(
								// 当前topic 里面包含的所有queue
								List<MessageQueue> mqs
								, 
								// 具体要发的那条消息
								Message msg
								
								// 对应到 send（） 里的 args
								, Object arg) {
							// TODO Auto-generated method stub
							
							// 向固定的一个queue里写消息
							MessageQueue queue = mqs.get(3);
							// 选好的queue
							return queue;
						}
					},
					// 自定义参数
					0, 2000);
		}
		
	//	producer.shutdown();
		System.out.println("已经停机");
		
	}
}
