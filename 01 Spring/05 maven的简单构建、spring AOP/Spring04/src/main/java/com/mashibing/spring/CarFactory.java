package com.mashibing.spring;

public class CarFactory {

	public CarFactory() {
		super();
		System.out.println("CarFactory");
	}
	
	
	public static Car getCar(String name) throws Exception{
		if (name.equals("audi")) {
			
			return new Audi();
		}else {
			throw new Exception("暂时没法生产这辆车");
		}
	}
}
