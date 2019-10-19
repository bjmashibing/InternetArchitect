package com.mashibing.spring;

public class ProxyGirl implements Human{

	
	private Human human;

	public ProxyGirl() {
		super();
	}
	
	public ProxyGirl(Human human) {
		super();
		this.human = human;
	}
	
	public void eat() {
		System.out.println("chiqian");
		  human.eat();
		System.out.println("chihou");
	}

	
}
