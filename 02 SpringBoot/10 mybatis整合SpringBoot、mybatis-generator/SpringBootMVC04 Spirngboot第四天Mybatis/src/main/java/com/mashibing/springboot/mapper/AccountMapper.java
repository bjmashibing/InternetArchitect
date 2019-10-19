package com.mashibing.springboot.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

public interface AccountMapper {

	@Select("select * from account")
	List<Account> findAll();

	void add(Account account);
}
