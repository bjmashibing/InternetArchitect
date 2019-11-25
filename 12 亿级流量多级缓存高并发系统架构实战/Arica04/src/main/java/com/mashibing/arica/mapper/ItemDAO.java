package com.mashibing.arica.mapper;

import com.mashibing.arica.entity.Item;
import com.mashibing.arica.entity.ItemExample;
import com.mashibing.arica.entity.ItemHtml;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * ItemDAO继承基类
 */
@Repository
public interface ItemDAO extends MyBatisBaseDao<Item, Integer, ItemExample> {

	@Select("select * from item")
	List<ItemHtml> selectAllByItemHtml();
}