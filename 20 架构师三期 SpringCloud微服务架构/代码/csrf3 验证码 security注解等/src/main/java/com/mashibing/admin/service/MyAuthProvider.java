package com.mashibing.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MyAuthProvider implements AuthenticationProvider {

	
	@Autowired
	MyDetailsService userSrv;
	
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		// TODO Auto-generated method stub
		
		
		// 穷举密码  限制 重试次数~
		
		System.out.println("开始校验账号密码 是否匹配");
		
		// 表单提交上来的数据
		String username = authentication.getPrincipal().toString();
		String rawPassword = authentication.getCredentials().toString();
		
		
		System.out.println("authentication" + authentication);
		// 去数据库拿出来 User对象
		UserDetails userDetails = userSrv.loadUserByUsername(username);
		
		// 比对
			// 明文 ，数据库里存的密文
		// 防程序员的~
	
			if(new BCryptPasswordEncoder().matches(rawPassword, userDetails.getPassword())) {
				// 密码通过 登录成功
				
				//
				UsernamePasswordAuthenticationToken authenticationToken = 
						new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
					//JWT  签名 sign
			return authentication;
			}else {
				
				throw new BadCredentialsException("用户名 或 密码错误");
			}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		// TODO Auto-generated method stub
		return true;
	}

}
