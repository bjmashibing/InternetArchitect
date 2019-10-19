package com.mashibing.springboot.service;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mashibing.springboot.entity.Role;
import com.mashibing.springboot.mapper.RoleExample;
import com.mashibing.springboot.mapper.RoleMapper;

@Component
@Service(version = "1.0.0" ,timeout = 10000, interfaceClass = IRoleService.class)

public class RoleServiceImpl implements IRoleService {

	@Autowired
	RoleMapper roleMapper;
	
	@Override
	public PageInfo<Role> findByPage(int pageNum, int pageSize) {

		PageHelper.startPage(pageNum, pageSize);
		RoleExample example = new RoleExample();
		PageInfo<Role> pageInfo = new PageInfo<>(roleMapper.selectByExample(example ));
		;
		
		return pageInfo;
	}

	@Override
	public Role findById(int id) {
		// TODO Auto-generated method stub
		
		return roleMapper.findById(id);
	}

	@Override
	public void addPermission(int id, int[] permissions) {
		// TODO Auto-generated method stub
		
//		for (int p : permissions) {
//			
//			roleMapper.addPermission(id,p);
//		}
		
		roleMapper.addPermissions(id,permissions);
		
		
		
	}

}
