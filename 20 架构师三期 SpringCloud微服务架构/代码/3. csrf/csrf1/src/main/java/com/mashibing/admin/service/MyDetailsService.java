package com.mashibing.admin.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// User Service
@Service
public class MyDetailsService implements UserDetailsService {
	// 完全自定义 脱离了  自动配置的数据源 查询数据库
	@Override
	
//	@Autowired
//	UserDao userDao;
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// 从redis 取，mysql 取，jpa
		
		if(new Random().nextBoolean()) {
			

			// 登录成功
			throw new CredentialsExpiredException("密码已过期，请修改密码之后 在继续操作");
			
		}else {
			
			throw new LockedException("用户已经被锁定，请联系管理员");
		}
	}
	
	

}
