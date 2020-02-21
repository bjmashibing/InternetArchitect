package com.mashibing.rocketmq;

import java.util.ArrayList;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 消息发送者
 * @author 一明哥
 *
 */
public class Producer {

	public static void main(String[] args)throws Exception {
		
		TransactionMQProducer producer = new TransactionMQProducer("xoxogp1");
		
		producer.setNamesrvAddr("192.168.150.113:9876");
		
		// 回调
		
		producer.setTransactionListener(new TransactionListener() {
			
			public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
				// 执行 本地事务
				
				
				System.out.println("=====executeLocalTransaction");
				System.out.println("msg:" + new String(msg.getBody()));
				System.out.println("msg:" + msg.getTransactionId());
				
				/**
				 * 
				 * 事务方法 写这里
				 * 
				 * 同步执行
				 * -----a----
				 * a 提交注册信息()  ;
				 * b 写入数据库();
				 * c 新用户() -> 发消息;
				 * 
				 * 事务消息的应用场景是不是适合发送多个消息要保证同时成功或失败？
				 * 
				 * ----b----
				 * 
				 * 读取消息
				 * 拿到新用户的信息 发短信
				 * 
				 * 
				 * 那如果最后一个commit发送失败，
				 * 业务的事务 异常， 然后broker等超时回调检查 发现失败，就扔掉数据是吗？
				 * 
				 * 
				 * 下订单，发半消息 尝试扣款，
					如果订单成功，就真正扣款 ，如果订单失败，则不扣款，  如果半消息扣款失败 则回滚订单   是这样的吗
					
					mysql 的事务
					
					
				 */
				try {
					
					// 业务
				} catch (Exception e) {
					// 
					return LocalTransactionState.ROLLBACK_MESSAGE;
				}
				// 真正发出去的数据 可用
				return LocalTransactionState.COMMIT_MESSAGE;
			}
			
			public LocalTransactionState checkLocalTransaction(MessageExt msg) {
				// Broker 端 回调 ，检查事务
				
				System.out.println("=====checkLocalTransaction");
				System.out.println("msg:" + new String(msg.getBody()));
				System.out.println("msg:" + msg.getTransactionId());
				
				
				
				
				// 事务执行成功
				return LocalTransactionState.COMMIT_MESSAGE;
				// 等会儿
		//		return LocalTransactionState.UNKNOW;
				// 回滚消息
		//		return LocalTransactionState.ROLLBACK_MESSAGE;
			}
		});
		
		
		
		producer.start();
		

		TransactionSendResult sendResult = producer.sendMessageInTransaction(new Message("xxoo003", "测试！这是事务消息".getBytes()), null);
		
		System.out.println("sendResult:" + sendResult);
		
	//	producer.shutdown();
		System.out.println("已经停机");
		
	}
}
