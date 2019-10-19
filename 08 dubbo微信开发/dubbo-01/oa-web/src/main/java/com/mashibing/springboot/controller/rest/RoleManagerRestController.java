package com.mashibing.springboot.controller.rest;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mashibing.springboot.entity.Permission;
import com.mashibing.springboot.entity.RespStat;
import com.mashibing.springboot.entity.Role;
import com.mashibing.springboot.service.IRoleService;

/**
 * restful风格URI的controller
 * 只和用户交换JSON数据
 * @author Administrator
 *
 */

@RestController
@RequestMapping("/api/v1/manager/role")
public class RoleManagerRestController {
	
	@Reference(version = "1.0.0")
	IRoleService roleSrv;

	@RequestMapping("Permission/add")
	public RespStat permissionadd(
			@RequestParam int[] permissions,
			@RequestParam int id
			) {
		//insert into role_permission (role_id,permission_id) values (6,1)
	
		
		roleSrv.addPermission(id,permissions);
		System.out.println("permissionids:" + ToStringBuilder.reflectionToString(permissions));
		return RespStat.build(200);
		
	}
	
	    	
	  
	@RequestMapping("/")
	public Role permissionadd(@PathVariable int id) {
		//insert into role_permission (role_id,permission_id) values (6,1)
		System.out.println("xxx");
		Role role = roleSrv.findById(id);
		return role;
		
	}
	
	
}
