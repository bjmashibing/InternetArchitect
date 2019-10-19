package com.mashibing.springboot.service;

import com.github.pagehelper.PageInfo;
import com.mashibing.springboot.entity.Role;

public interface IRoleService {

	public PageInfo<Role> findByPage(int pageNum, int pageSize);

	Role findById(int id);

	void addPermission(int id, int[] permissions);

}