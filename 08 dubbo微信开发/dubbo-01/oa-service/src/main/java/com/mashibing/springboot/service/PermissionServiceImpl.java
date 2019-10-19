package com.mashibing.springboot.service;

import java.util.List;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mashibing.springboot.entity.Permission;
import com.mashibing.springboot.mapper.PermissionExample;
import com.mashibing.springboot.mapper.PermissionMapper;


@Component
@Service(version = "1.0.0" ,timeout = 10000, interfaceClass = IPermissionService.class)

public class PermissionServiceImpl implements IPermissionService {

	@Autowired
	PermissionMapper pMapper;

	public PageInfo<Permission> findByPage(int pageNum, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	public Permission findById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	public void update(Permission permission) {
		// TODO Auto-generated method stub
		
	}

	public void add(Permission permission) {
		// TODO Auto-generated method stub
		
	}

	public List<Permission> findAll() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
