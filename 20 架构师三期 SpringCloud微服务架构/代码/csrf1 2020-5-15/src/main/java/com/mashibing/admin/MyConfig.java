package com.mashibing.admin;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import com.mashibing.admin.service.MyAuthProvider;
import com.mashibing.admin.service.MyDetailsService;

//@Configuration
//@EnableWebSecurity
public class MyConfig extends WebSecurityConfigurerAdapter {

	@Override
	public void configure(WebSecurity web) throws Exception {
		// TODO Auto-generated method stub
		// 不需要登录就可以访问的资源
		
		web.ignoring().antMatchers("/img/**");
	//	super.configure(web);
	}


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		String pass1 = new BCryptPasswordEncoder().encode("123");
		String pass2 = new BCryptPasswordEncoder().encode("123");
		
		System.out.println("pass1:" + pass1);
		System.out.println("pass2:" + pass2);
		http.
		// 哪些 地址需要登录
		authorizeRequests()
		//所有请求都需要验证
	//	.antMatchers("/img/**").permitAll()
		.anyRequest().authenticated()
		.and()
		
		
		//permitAll 给没登录的 用户可以访问这个地址的权限
		//自定义登录页
		.formLogin().loginPage("/login.html")
		
		.loginProcessingUrl("/login").permitAll()
		// 登录失败 页面
		.failureUrl("/login.html?error")
		// 登录成功跳转的页面
		.defaultSuccessUrl("/ok",true).permitAll()
		// 配置 登录页 的表单name   admin -> 分权限 展示页面
		.passwordParameter("oo")
		.usernameParameter("xx")
		
		
		.failureHandler(new AuthenticationFailureHandler() {
			
			@Override
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException exception) throws IOException, ServletException {
				// TODO Auto-generated method stub
				exception.printStackTrace();
				
				// 判断异常信息 跳转不同页面 比如 密码过期重置
				request.getRequestDispatcher(request.getRequestURL().toString()).forward(request, response);
				
				// 记录登录失败次数 禁止登录
				
			}
		})
		
		
		//默认 所有的post请求 都会拦截
		.and()
		.csrf()
		.csrfTokenRepository(new HttpSessionCsrfTokenRepository());
		
		;
	}
	
	
	@Autowired
	DataSource dataSource;	
	// session 登录  并发量高 -> jwt

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

//		auth.
//		inMemoryAuthentication()
//			.withUser("123").password(new BCryptPasswordEncoder().encode("123")).roles("admin")
//		.and()
//			.withUser("321").password("321").roles("user")
//		;
		
		
//		JdbcUserDetailsManager manager = 
		auth.userDetailsService(new MyDetailsService())
		
		// 开关标签
		.and()
		.authenticationProvider(new MyAuthProvider());
		
		
		;
		
//		
//		.	jdbcAuthentication()
//				.dataSource(dataSource).
//					getUserDetailsService()
//					
//					;
//					
//					
					;
		
		// 注册用户
//		boolean userExists = manager.userExists("xishi");
//		System.out.println("userExists" + userExists);
//		if(!userExists) {
//			
//			manager.createUser(User.withUsername("xishi").password(new BCryptPasswordEncoder().encode("111")).roles("admin","xxoo").build());
//		}
		
		
		// 自定义ORM
		
		
	}
	
	
//	@Bean
//	public UserDetailsService userDetailsService() {
//		
//		
//		// 基于内存存储用户
//		// UserDetails
////		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
////
////		
////		// 使用 用户名 找 user对象
////	//	manager.loadUserByUsername(username)
////		
////	//	changePassword(oldPassword, newPassword);
////		
////		User user = new User("a", new BCryptPasswordEncoder().encode("1"), true, true, true, true, Collections.singletonList(new SimpleGrantedAuthority("xx")));
////		manager.createUser(user);
////		manager.createUser(User.withUsername("yiming").password(new BCryptPasswordEncoder().encode("xx")).roles("xxz").build());
////	
//		
//		// 基于JDBC的 用户存储
//		
//		JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
//		
//		manager.createUser(User.withUsername("xishi").password(new BCryptPasswordEncoder().encode("111")).roles("admin","xxoo").build());
//		
//		return manager;
//	}
	
	
	
	
	
	@Bean
	PasswordEncoder passwordEncoder() {
		
		return new BCryptPasswordEncoder();
	}
	
	
}
