package com.mashibing.spring;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestGetBean {

	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

		
		A a = ctx.getBean("A",A.class);

		System.out.println(a.getB());

	}
}
