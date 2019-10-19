package com.mashibing.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TestGetBean {

	
	public static void main(String[] args) {

		final Girl girl = new Girl();
		
		Human prxyGirl = (Human)Proxy.newProxyInstance(Girl.class.getClassLoader(), Girl.class.getInterfaces(), new InvocationHandler() {
			
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

				System.out.println(method.getName() + " ->> 方法被执行");
				
				if (method.getName().equals("bath")) {
					System.out.println("偷看洗澡");
					Object invoke = method.invoke(girl, args);
					System.out.println("6了6了");
					return invoke;
				}else {
					System.out.println("饭前");
					Object invoke = method.invoke(girl, args);
					System.out.println("饭后");
					return invoke;
				}
			}
		});
		
		
		
		prxyGirl.eat();
		System.out.println("-----");
		prxyGirl.bath();
		
	}
}
