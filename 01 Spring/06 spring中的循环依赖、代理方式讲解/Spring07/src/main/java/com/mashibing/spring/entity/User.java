package com.mashibing.spring.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * ORM映射
 * 线程安全，成员属性，不能被共享
 * @author Administrator
 *
 */

@Component
@Scope("prototype")
public class User {

	@Value("zhangfg")
	private String loginName;
	@Value("123123")
	private String password;
	@Autowired
	private Pet pet;
	
}
