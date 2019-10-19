package com.mashibing.spring.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.mashibing.spring.dao.UserDao;
import com.mashibing.spring.entity.User;


/**
 * 一个线程 创建连接
 * 另一个线程 关闭连接
 * 
 * 对应的类 -> Connection 类
 * 
 * ThreadLocal 
 * @author Administrator
 *
 */

@Repository("daoMysql")
public class UserDaoMysqlImpl implements UserDao {

	@Autowired
	User user;
	
	public User getUserByName(String name) {
		System.out.println("用户查找中。。。");
		return user;
	}

}
