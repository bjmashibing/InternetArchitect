package com.mashibing.admin;

import java.io.IOException;
import java.util.Base64;
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
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.mashibing.admin.service.MyAuthProvider;
import com.mashibing.admin.service.MyDetailsService;

@Configuration
@EnableWebSecurity
public class MyConfig2 extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.
		// 哪些 地址需要登录
		authorizeRequests()
		//所有请求都需要验证
		.anyRequest().authenticated()
		.and()
		.formLogin()
		.and()
		.sessionManagement()
		// 允许同时登录的 客户端
		.maximumSessions(1)
		// 已经有用户登录后， 不允许相同用户再次登录
		.maxSessionsPreventsLogin(true).
		
		
		// cookies 不一定是浏览器 token ，集群 会话 第三方/ 无回话  只在
//		.rememberMe()
//		.and()
		
		and().and()
		.csrf().disable();
		
	}

	@Autowired
	DataSource dataSource;
	// session 登录 并发量高 -> jwt

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.
		inMemoryAuthentication()
			.withUser("123").password(new BCryptPasswordEncoder().encode("123")).roles("admin")
		.and()
			.withUser("321").password("321").roles("user")
		;

		
	}
	@Bean
	HttpSessionEventPublisher httpSessionEventPublisher() {
	    return new HttpSessionEventPublisher();
	}
	@Bean
	PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}
	
	public static void main(String[] args) {
		
		byte[] decode = Base64.getDecoder().decode("MTIzOjE1OTA3NjA2NjkxMTQ6YTc4OGNhOTJhMGE4MzYyN2MzNjhkNzkxZjA5ZGVmZDM");
	//123:1590760669114:a788ca92a0a83627c368d791f09defd3*(前面两个 值 对不对) sign
	//用户名+ 权限 + 欢乐豆 + 过期时间 + secret  = 摘要 首次登录（服务器端有 客户端没有）
	System.out.println(new String(decode));
	// 好处 不需要任何和数据源的校验
	
	}

}
