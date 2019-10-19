package com.mashibing.spring.service;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.mashibing.spring.dao.UserDao;
import com.mashibing.spring.entity.User;

/**
 * 处理具体业务逻辑
 * 比如：校验账号密码是否正确
 * @author Administrator
 *
 */

// @Component 注册bean  相当于 <bean id=""

@Service
public class MainService {

	@Autowired
	@Qualifier("daoMysql")
	UserDao dao;
	
	public User login(String loginName, String password) {

		System.out.println("loginName:" + loginName);
		System.out.println("Service 接到请求 ，开始处理");
		User user = dao.getUserByName(loginName);
		
		System.out.println(ToStringBuilder.reflectionToString(user));
		
		return user;
	}

}
