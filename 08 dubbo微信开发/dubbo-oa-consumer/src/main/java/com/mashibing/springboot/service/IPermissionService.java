package com.mashibing.springboot.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.mashibing.springboot.entity.Permission;

public interface IPermissionService {

	PageInfo<Permission> findByPage(int pageNum, int pageSize);

	Permission findById(int id);

	void update(Permission permission);

	void add(Permission permission);

	List<Permission> findAll();

}