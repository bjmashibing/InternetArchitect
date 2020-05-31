package com.mashibing.admin;

import java.util.Random;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class HiController {
	
	@GetMapping("/hi")
	// 或的关系 不支持并且的关系
	@Secured({"ROLE_admin","ROLE_user"})
	public String hi() {
	//	UserDetailsServiceAutoConfiguration
		System.out.println("来啦老弟~！");
		return "hi";
	}
	
	@GetMapping("/admin/hi")
	// 必须要有 admin的角色才能访问
	@PreAuthorize("hasRole('ROLE_admin')")
	public String hi1() {
	//	UserDetailsServiceAutoConfiguration
		System.out.println("来啦老弟~！");
		return "hi admin";
	}
	
	
	@GetMapping("/admin/hi2")
	@PreAuthorize("hasAnyRole('ROLE_admin','ROLE_guest')")
	public String hi3() {
	//	UserDetailsServiceAutoConfiguration
		System.out.println("来啦老弟~！");
		return "hi admin";
	}
	
	@GetMapping("/admin/hi4")
	@PreAuthorize("hasRole('ROLE_admin') AND hasRole('ROLE_user')")
	public String hi4() {
	//	UserDetailsServiceAutoConfiguration
		System.out.println("来啦老弟~！");
		return "hi admin";
	}
	
	@GetMapping("/admin/hi5")
	// Spring EL表达式
	// 表达式 是true的时候 有权限访问 false 直接返回403
	@PostAuthorize("returnObject==1")
	public int hi5() {
	//	UserDetailsServiceAutoConfiguration
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		System.out.println("user" + authentication);
		System.out.println("来啦老弟~！" );
		// 访问 子系统 的service (把当前用户的角色带过去)
		return new Random().nextInt(2);
	}
	
	@GetMapping("/user/hi")
	public String hi2() {
	//	UserDetailsServiceAutoConfiguration
		System.out.println("来啦老弟~！");
		return "hi user";
	}
}
