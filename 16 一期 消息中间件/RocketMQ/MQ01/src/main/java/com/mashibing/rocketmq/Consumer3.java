package com.mashibing.rocketmq;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

public class Consumer3 {

	public static void main(String[] args)throws Exception {
		
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("xxoo0TAG-B");
		
		consumer.setNamesrvAddr("192.168.150.113:9876");
		
		
		/**
		 * a()
		 * c()
		 * d()
		 * b -> 向 rocketmq 写入一条消息()
		 * rollback()
		 * 
		 * 
		 * 
		 */
		
		// 每个consumer 关注一个topic
		
		// topic 关注的消息的地址
		// 过滤器 * 表示不过滤
		// tag selector 在一个group中的消费者，都不能随便变，要保持统一
		
		MessageSelector messageSelector = MessageSelector.bySql("age >= 18 and age <= 28");
		
		consumer.subscribe("myTopic003",messageSelector );
		
		consumer.registerMessageListener(new MessageListenerConcurrently() {
			
			public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

				for (MessageExt msg : msgs) {
					
					System.out.println(new String(msg.getBody()));;
				}
				// 默认情况下 这条消息只会被 一个consumer 消费到 点对点
				// message 状态修改
				// ack
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
		});
		
		
		// 讲道理 冲突，首先，别这么干
		consumer.setMessageModel(MessageModel.CLUSTERING);
		consumer.start();
		
		// 集群 -> 一组consumer
		// 广播
		
		System.out.println("Consumer TAG-B start...");
	}
}
