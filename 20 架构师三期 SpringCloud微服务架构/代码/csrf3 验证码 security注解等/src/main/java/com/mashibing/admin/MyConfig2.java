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
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.mashibing.admin.service.MyAuthProvider;
import com.mashibing.admin.service.MyDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true ,prePostEnabled = true)
public class MyConfig2 extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.addFilterBefore(new CodeFilter(), UsernamePasswordAuthenticationFilter.class);
		
		
		
		http.
		// 哪些 地址需要登录
		authorizeRequests()
		// 角色  -> URL
		// Role = 角色
		// Authority = 权限
//		.antMatchers("/admin/**").hasRole("admin")
//		.antMatchers("/user/**").hasRole("user")
//		
		
		//所有请求都需要验证
//		.anyRequest().authenticated()
		
		// 不需要登录 不常用
	//	.antMatchers("url").hasIpAddress("127.0.0.1")
		// ip地址访问的黑白名单，封ip操作
		
		.and()
	
		
		// 登录界面 和退出界面
	
		.formLogin().loginPage("/login.html")
		
		.loginProcessingUrl("/login").permitAll()
		// 登录失败 页面
		.failureUrl("/login.html?error")
		// 登录成功跳转的页面
		.defaultSuccessUrl("/ok",true).permitAll()
		// 配置 登录页 的表单name   admin -> 分权限 展示页面
		.passwordParameter("oo")
		.usernameParameter("xx")
		
		// 在登录成功后会被调起
		// 用来锁定资源 初始化资源等
		.successHandler(new AuthenticationSuccessHandler() {
			
			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				// TODO Auto-generated method stub
				Object user = authentication.getPrincipal();
				System.out.println(user);
			}
		})
		
		
		.failureHandler(new AuthenticationFailureHandler() {
			// 登录失败的异常信息
			// 分析失败原因，统计失败次数
			@Override
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException exception) throws IOException, ServletException {
				// TODO Auto-generated method stub
				
				// 登录失败异常信息 exception
				
				exception.printStackTrace();
			}
		})
		
		
		.and().logout()
		// 退出处理器
		// 用于清理用户资源
		.addLogoutHandler(new LogoutHandler() {
			
			@Override
			public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
				// TODO Auto-generated method stub
				System.out.println("我滚了。。。");
			}
		})
		
		.addLogoutHandler(new LogoutHandler() {
			
			@Override
			public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
				// TODO Auto-generated method stub
				System.out.println("我又滚了");
			}
		})
		
		
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
			.withUser("111").password(new BCryptPasswordEncoder().encode("123")).roles("admin")
		.and()
			.withUser("112").password(new BCryptPasswordEncoder().encode("123")).roles("user")
	
		.and()
			.withUser("113").password(new BCryptPasswordEncoder().encode("123")).roles("guest")
		
		.and()
			.withUser("114").password(new BCryptPasswordEncoder().encode("123")).roles("admin","user")
		
			;
	}
	
	@Bean
	RoleHierarchy roleHierarchy() {
		
		RoleHierarchyImpl impl = new RoleHierarchyImpl();
		impl.setHierarchy("ROLE_admin > ROLE_user");
		
		return impl;
		
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
