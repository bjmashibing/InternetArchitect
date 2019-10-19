package com.mashibing.springboot.controller.rest;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomUtils;
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
import com.mashibing.springboot.service.TestService;

/**
 * restful风格URI的controller
 * 只和用户交换JSON数据
 * @author Administrator
 *
 */

@RestController
@RequestMapping("/api/v1/test")
public class TestManagerRestController {
	
	@Reference(version = "1.0.0")
	IRoleService roleSrv;
	@Reference(version = "1.0.0",actives = 1)
	    TestService testSrv;	
	  
	@RequestMapping("/role")
	public int permissionadd() {
		//insert into role_permission (role_id,permission_id) values (6,1)
		long startTime = System.currentTimeMillis();    //获取开始时间
		System.out.println("action--");
		int port=0;
		try {
			
			port = testSrv.getPort(RandomUtils.nextInt() +" hehe");
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();    //获取结束时间

		System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
		return port;
	}
	
	
}
