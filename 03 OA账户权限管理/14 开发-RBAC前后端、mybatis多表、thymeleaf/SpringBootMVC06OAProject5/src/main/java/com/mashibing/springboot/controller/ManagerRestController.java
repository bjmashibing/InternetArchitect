package com.mashibing.springboot.controller;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mashibing.springboot.RespStat;
import com.mashibing.springboot.entity.Permission;
import com.mashibing.springboot.service.PermissionService;

/**
 * restful风格URI的controller
 * 只和用户交换JSON数据
 * @author Administrator
 *
 */

@RestController
@RequestMapping("/api/v1/manager/permission")
public class ManagerRestController {
	
	@Autowired
	PermissionService permissionSrv;
	
	/**
	 * 更新
	 * @param permission
	 * @return
	 */
	@RequestMapping("add")
	public RespStat add(@RequestBody Permission permission) {
		
		System.out.println("permission:" + ToStringBuilder.reflectionToString(permission));
		permissionSrv.add(permission);
		return RespStat.build(200);
	}
	@RequestMapping("update")
	public RespStat update(@RequestBody Permission permission) {
		
		System.out.println("permission:" + ToStringBuilder.reflectionToString(permission));
		permissionSrv.update(permission);
		return RespStat.build(200);
	}
}
