package com.mashibing.spring;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestGetBean {

	public static void main(String[] args) {
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		Person person1 = ctx.getBean("person",Person.class);
		System.out.println(ToStringBuilder.reflectionToString(person1));
	}
}
