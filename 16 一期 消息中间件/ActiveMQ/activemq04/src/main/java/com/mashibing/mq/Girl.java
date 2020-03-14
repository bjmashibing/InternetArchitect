package com.mashibing.mq;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Girl implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private int age;
	private double price;
	
	
	
	
	public Girl(String name, int age, double price) {
		super();
		this.name = name;
		this.age = age;
		this.price = price;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
	public static void main(String[] args) {
		
		Map<String,String> map = new HashMap<String, String>();
		
		System.out.println(map.put("1", "xx"));;
		System.out.println(map.put("1", "xxoo"));;
		System.out.println("----------");;

		System.out.println(map.putIfAbsent("2", "xx"));;
		System.out.println(map.putIfAbsent("2", "xxoo"));;
		System.out.println(map.putIfAbsent("2", "xxoo3"));;
		System.out.println(map.putIfAbsent("2", "xxoo4"));;
		// 幂等  不重启  
	}

	
}
