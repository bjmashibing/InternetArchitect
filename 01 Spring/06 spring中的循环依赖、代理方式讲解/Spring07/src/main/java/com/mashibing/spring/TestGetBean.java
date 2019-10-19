package com.mashibing.spring;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mashibing.spring.controller.MainController;

public class TestGetBean {

	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
	
		MainController controller = ctx.getBean("mainController",MainController.class);
		controller.list();
	}
}
