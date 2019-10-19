package com.mashibing.springboot.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.mashibing.springboot.RespStat;
import com.mashibing.springboot.entity.Account;

public interface IAccountService {

	Account findByLoginNameAndPassword(String loginName, String password);

	List<Account> findAll();

	PageInfo<Account> findByPage(int pageNum, int pageSize);

	RespStat deleteById(int id);

	void update(Account account);

}