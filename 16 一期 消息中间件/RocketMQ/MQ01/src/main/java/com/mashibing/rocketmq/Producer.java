package com.mashibing.rocketmq;

import java.util.ArrayList;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * ��Ϣ������
 * @author һ����
 *
 */
public class Producer {

	public static void main(String[] args)throws Exception {
		
		DefaultMQProducer producer = new DefaultMQProducer("xoxogp");
		
		// ����nameserver��ַ
		producer.setNamesrvAddr("192.168.150.113:9876");
		producer.start();
		
		// topic ��Ϣ��Ҫ���͵��ĵ�ַ
		// body  ��Ϣ�еľ�������
		Message msg1 = new Message("myTopic001", "xxxxooo ��һ��".getBytes());
		Message msg2 = new Message("myTopic001", "xxxxooo ��2��".getBytes());
		Message msg3 = new Message("myTopic001", "xxxxooo ��3��".getBytes());
		
		ArrayList<Message> list = new ArrayList<Message>();
		list.add(msg1);
		list.add(msg2);
		list.add(msg3);
		
		// ͬ����Ϣ����  
		// for  
		// list.add
		
	
		SendResult sendResult3 = producer.send(list);
		
		System.out.println(sendResult3);
		producer.shutdown();
		System.out.println("�Ѿ�ͣ��");
		
	}
}
