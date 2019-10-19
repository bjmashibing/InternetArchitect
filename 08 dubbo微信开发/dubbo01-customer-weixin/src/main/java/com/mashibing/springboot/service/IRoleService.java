package com.mashibing.springboot.service;

import com.github.pagehelper.PageInfo;
import com.msb.db1.entity.Role;

public interface IRoleService {

	PageInfo<Role> findByPage(int pageNum, int pageSize);

	Role findById(int id);

	void addPermission(int id, int[] permissions);

}