package com.mashibing.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TestGetBean {

	
	public static void main(String[] args) {

		final Girl girl = new Girl();
		
		Girl prxyGirl = (Girl)new CGLibFactory(girl).createXXOO();
		
		
		
		prxyGirl.eat();
		System.out.println("-----");
		prxyGirl.bath();
		
	}
}
