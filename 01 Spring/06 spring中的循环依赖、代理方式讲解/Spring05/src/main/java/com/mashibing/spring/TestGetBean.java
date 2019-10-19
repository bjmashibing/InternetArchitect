package com.mashibing.spring;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestGetBean {

	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

		
//		A a = ctx.getBean("A",A.class);
//		System.out.println(ToStringBuilder.reflectionToString(a));
//		System.out.println(ToStringBuilder.reflectionToString(a.getB()));
//		System.out.println(ToStringBuilder.reflectionToString(a.getB().getC()));
//		
//		
//		System.out.println("------");
//		A a1 = ctx.getBean("A",A.class);
//		System.out.println(ToStringBuilder.reflectionToString(a1));
//		System.out.println(ToStringBuilder.reflectionToString(a1.getB()));
//		System.out.println(ToStringBuilder.reflectionToString(a1.getB().getC()));
//		
//		System.out.println(a == a1);
//		System.out.println(a.getB() == a1.getB());
//		
		
		
		B b = ctx.getBean("B",B.class);
		System.out.println(ToStringBuilder.reflectionToString(b));
		
		A a = ctx.getBean("A",A.class);
		System.out.println(ToStringBuilder.reflectionToString(a));
		System.out.println(a.getName().equals(""));
		System.out.println(b.getName().equals(""));
		

	}
}
