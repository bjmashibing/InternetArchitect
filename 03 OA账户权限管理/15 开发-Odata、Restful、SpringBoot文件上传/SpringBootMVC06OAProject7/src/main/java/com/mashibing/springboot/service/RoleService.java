package com.mashibing.springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mashibing.springboot.entity.Role;
import com.mashibing.springboot.mapper.RoleExample;
import com.mashibing.springboot.mapper.RoleMapper;

@Service
public class RoleService {

	@Autowired
	RoleMapper roleMapper;
	
	public PageInfo<Role> findByPage(int pageNum, int pageSize) {

		PageHelper.startPage(pageNum, pageSize);
		RoleExample example = new RoleExample();
		PageInfo<Role> pageInfo = new PageInfo<>(roleMapper.selectByExample(example ));
		;
		
		return pageInfo;
	}

	public Role findById(int id) {
		// TODO Auto-generated method stub
		
		return roleMapper.findById(id);
	}

	public void addPermission(int id, int[] permissions) {
		// TODO Auto-generated method stub
		
//		for (int p : permissions) {
//			
//			roleMapper.addPermission(id,p);
//		}
		
		roleMapper.addPermissions(id,permissions);
		
		
		
	}

}
