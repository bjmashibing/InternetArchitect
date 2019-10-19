package com.mashibing.spring;

public class B {
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public B() {
		super();
		System.out.println("B Init.");
	}
}
