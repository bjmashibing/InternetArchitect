package com.mashibing.springboot.mapper;

import com.mashibing.springboot.entity.Menu;
import org.springframework.stereotype.Repository;

/**
 * MenuMapper继承基类
 */
@Repository
public interface MenuMapper extends MyBatisBaseDao<Menu, Integer, MenuExample> {
}