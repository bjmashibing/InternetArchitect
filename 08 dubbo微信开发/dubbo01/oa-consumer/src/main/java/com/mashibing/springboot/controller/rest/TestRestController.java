package com.mashibing.springboot.controller.rest;

import org.apache.dubbo.config.annotation.Reference;
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
import com.mashibing.springboot.service.ITestService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;



@RestController
@RequestMapping("/api/v1/test")
public class TestRestController {
	
	@Reference(version = "1.0.0")
	ITestService testSrv;

	@GetMapping("/port")
	public int permissionadd() {

		return testSrv.getPort();
	}

	
	
}
