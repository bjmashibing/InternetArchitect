package com.mashibing.rmq;

import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RmqProducer1ApplicationTests {



	@Autowired
	MyService mySrv;
	
	@Test
	public void addTest() {
		int num = mySrv.add(6, 3);
		assert num == 9;
		System.out.println("xxoo");
		
	}
	
	
}
