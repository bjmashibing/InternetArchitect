package com.mashibing.spring;

public class TestGetBean {

	
	public static void main(String[] args) {


		
		Girl girl = new Girl();
		Human proxyGirl = new ProxyGirl(girl);
		
		proxyGirl.eat();

		
	}
}
