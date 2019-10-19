package com.mashibing.springboot.mapper;

import com.mashibing.springboot.entity.Permission;

import org.springframework.stereotype.Repository;

/**
 * PermissionMapper继承基类
 */
@Repository
public interface PermissionMapper extends MyBatisBaseDao<Permission, Integer, PermissionExample> {
}