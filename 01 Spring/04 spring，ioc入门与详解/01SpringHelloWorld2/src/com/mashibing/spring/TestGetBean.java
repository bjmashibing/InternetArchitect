package com.mashibing.spring;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestGetBean {

	public static void main(String[] args) {
		
//		Person p = new Person();
//		p.setAge(1);
//		
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
//		Person person = (Person)ctx.getBean("person");
//		Food food = ctx.getBean("food",Food.class);
//		
//		food.setName("œ„Ω∂");
//		
//		person.setName("zhangsan");
//		person.setAge(18);
//		person.setFood(food);
		
		
		Person person = (Person)ctx.getBean("person");
		
		
		System.out.println(ToStringBuilder.reflectionToString(person,ToStringStyle.MULTI_LINE_STYLE));;
		System.out.println(ToStringBuilder.reflectionToString(ctx,ToStringStyle.MULTI_LINE_STYLE));;
		
	}
}
