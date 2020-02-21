package com.mashibing.rocketmq;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

public class Consumer3 {

	public static void main(String[] args)throws Exception {
		
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("g02");
		
		consumer.setNamesrvAddr("192.168.150.113:9876");
		
		consumer.subscribe("xxoo007", "*");
		
		
		
		/**
		 * 
		 * 
		 * 
		 * 
		 * MessageListenerConcurrently   并发消费 / 开多个线程
		 * 
		 * 
		 */
//		consumer.registerMessageListener(new MessageListenerConcurrently() {
//			
//			public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
//
//				for (MessageExt msg : msgs) {
//					
//					System.out.println(new String(msg.getBody()));;
//				}
//				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//			}
//		});

		
		/*
		 * 
		 * MessageListenerOrderly 顺序消费 ,对一个queue 开启一个线程，多个queue 开多个线程 
		 * 
		 * 
		 */
		
		
		// 
		// 最大开启消费线程数
	//	consumer.setConsumeThreadMax(consumeThreadMax);
		//  最小线程数
	//	consumer.setConsumeThreadMin(consumeThreadMin);
		consumer.registerMessageListener(new MessageListenerOrderly() {
			
			public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
				for (MessageExt msg : msgs) {
				
				System.out.println(new String(msg.getBody()) + " Thread:" + Thread.currentThread().getName());;
			}
			return ConsumeOrderlyStatus.SUCCESS;
			}
		});
		
		consumer.start();
		System.out.println("Consumer g02 start...");
	}
}
