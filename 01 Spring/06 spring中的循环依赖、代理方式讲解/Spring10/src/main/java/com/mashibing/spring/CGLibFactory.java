package com.mashibing.spring;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

public class CGLibFactory implements MethodInterceptor {

	
	
	private Object target;
	
	
	public CGLibFactory () {
		super();
	}
	public CGLibFactory(Object taObject) {
		super();
		this.target = taObject;
	}
	

	public Object createXXOO() {
		//增强器
		Enhancer enhancer = new Enhancer();
		// 创建子类，作为代理类
		enhancer.setSuperclass(Girl.class);
		// 设置回调类
		enhancer.setCallback(this);
		return enhancer.create();
	}
	
	
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

		System.out.println("钱");
		method.invoke(target, args);
		System.out.println("后");
		return null;
	}



}
