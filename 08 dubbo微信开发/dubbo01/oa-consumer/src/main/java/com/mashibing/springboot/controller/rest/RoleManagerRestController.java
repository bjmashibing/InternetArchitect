package com.mashibing.springboot.controller.rest;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.mashibing.springboot.entity.Permission;
import com.mashibing.springboot.entity.RespStat;
import com.mashibing.springboot.entity.Role;
import com.mashibing.springboot.service.IRoleService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


/**
 * restful风格URI的controller
 * 只和用户交换JSON数据
 * @author Administrator
 *
 */

@RestController
@RequestMapping("/api/v1/manager/role")
@Api(value = "权限管理", tags = {"角色管理"})
public class RoleManagerRestController {
	
	@Reference(version = "1.0.0")
	IRoleService roleSrv;

	@GetMapping("Permission/add")
	public RespStat permissionadd(
			@RequestParam int[] permissions,
			@RequestParam int id
			) {
		//insert into role_permission (role_id,permission_id) values (6,1)
	
		
		roleSrv.addPermission(id,permissions);
		System.out.println("permissionids:" + ToStringBuilder.reflectionToString(permissions));
		return RespStat.build(200);
		
	}
	
	@GetMapping("Permission/list")
	@ApiOperation(value = "获取用户列表",notes ="获取用户列表" )
	public PageInfo<Role> list(@ApiParam(name = "pageNum",type = "integer",required = true,example = "1", value = "第几页") int pageNum,
			
			@ApiParam(name = "pageSize", required = true,type = "integer",example = "1", value = "一页显示多少个")int pageSize) {
		//insert into role_permission (role_id,permission_id) values (6,1)
	
		
		return roleSrv.findByPage(pageNum, pageSize);
		
	}
	
	
}
