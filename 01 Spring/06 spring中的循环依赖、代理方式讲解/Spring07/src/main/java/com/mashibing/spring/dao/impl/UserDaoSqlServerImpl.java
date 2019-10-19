package com.mashibing.spring.dao.impl;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.mashibing.spring.dao.UserDao;
import com.mashibing.spring.entity.User;

@Repository("daoSS")
public class UserDaoSqlServerImpl implements UserDao {

	public User getUserByName(String name) {
		// TODO Auto-generated method stub
		System.out.println("UserDaoSqlServerImpl");
		return null;
	}

}
