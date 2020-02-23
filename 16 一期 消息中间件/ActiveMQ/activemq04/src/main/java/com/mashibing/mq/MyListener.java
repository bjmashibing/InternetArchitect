package com.mashibing.mq;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

public class MyListener implements MessageListener {

	public void onMessage(Message message) {
		
		
		if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage)message;
			try {
				System.out.println(textMessage.getText());
			} catch (JMSException e) {

				
				e.printStackTrace();
			};
		}else 
			if(message instanceof ObjectMessage) {
				ObjectMessage objectMessage = (ObjectMessage)message;
				try {
					Girl girl = (Girl)objectMessage.getObject();
					
					System.out.println(girl.getName());
					System.out.println(girl.getAge());
					System.out.println(girl.getPrice());
					
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else 
				if(message instanceof BytesMessage) {
					BytesMessage bytesMessage = (BytesMessage)message;
					try {

						  FileOutputStream out = null;
		                    try {
		                        out = new FileOutputStream("c:/dev/aa.txt");
		                    } catch (FileNotFoundException e2) {
		                        e2.printStackTrace();
		                    }
		                    
		                    byte[] by = new byte[1024];
		                    int len = 0 ;
		                    try {
		                        while((len = bytesMessage.readBytes(by))!= -1){
		                            out.write(by,0,len);
		                        }
		                    } catch (Exception e1) {
		                        e1.printStackTrace();
		                    }
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else if(message instanceof MapMessage) {
					MapMessage mapMessage = (MapMessage)message;
					System.out.println("mapMessage:" + mapMessage);
					try {
						System.out.println("mapMessage:" + mapMessage.getString("name"));
					System.out.println("mapMessage:" + mapMessage.getInt("age"));
					System.out.println("mapMessage:" + mapMessage.getDouble("price"));
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
		
		
		
	}

}
