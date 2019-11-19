package com.mashibing.arica.mapper;

import com.mashibing.arica.entity.Item;
import com.mashibing.arica.entity.ItemExample;
import org.springframework.stereotype.Repository;

/**
 * ItemDAO继承基类
 */
@Repository
public interface ItemDAO extends MyBatisBaseDao<Item, Integer, ItemExample> {
}