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
	
	@Override
	public PageInfo<Permission> findByPage(int pageNum, int pageSize) {

		PageHelper.startPage(pageNum, pageSize);
		
		PermissionExample example = new PermissionExample();
		List<Permission> list = pMapper.selectByExample(example );
		
		return new PageInfo<>(list);
	}

	@Override
	public Permission findById(int id) {

		return pMapper.selectByPrimaryKey(id);
	}

	@Override
	public void update(Permission permission) {
		// TODO Auto-generated method stub
		pMapper.updateByPrimaryKeySelective(permission);
		
	}

	@Override
	public void add(Permission permission) {
		// TODO Auto-generated method stub
		
		
		pMapper.insert(permission);
	}

	@Override
	public List<Permission> findAll() {

		PermissionExample example = new PermissionExample();
		return pMapper.selectByExample(example);
	}

}
