package com.mashibing.spring;

public class A {

	private String name;
	
	
	public A() {
		super();
		System.out.println("A Init.");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
