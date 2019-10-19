package com.mashibing.springboot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mashibing.springboot.entity.Account;
import com.mashibing.springboot.entity.Permission;
import com.mashibing.springboot.mapper.PermissionExample;
import com.mashibing.springboot.mapper.PermissionMapper;
@Service
public class PermissionService {

	@Autowired
	PermissionMapper pMapper;
	
	public PageInfo<Permission> findByPage(int pageNum, int pageSize) {

		PageHelper.startPage(pageNum, pageSize);
		
		PermissionExample example = new PermissionExample();
		List<Permission> list = pMapper.selectByExample(example );
		
		return new PageInfo<>(list);
	}

	public Permission findById(int id) {

		return pMapper.selectByPrimaryKey(id);
	}

	public void update(Permission permission) {
		// TODO Auto-generated method stub
		pMapper.updateByPrimaryKeySelective(permission);
		
	}

	public void add(Permission permission) {
		// TODO Auto-generated method stub
		pMapper.insert(permission);
	}

}
