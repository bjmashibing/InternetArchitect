package com.mashibing.spring;


import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestGetBean {

	public static void main(String[] args) {
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		Person person1 = ctx.getBean("person",Person.class);
		Person person2 = ctx.getBean("star",Person.class);
		Person person3 = ctx.getBean("human",Person.class);
		Person person4 = ctx.getBean("person2",Person.class);
		System.out.println(person1);
		System.out.println(person2);
		System.out.println(person3);
		System.out.println(person4);
	}
}
