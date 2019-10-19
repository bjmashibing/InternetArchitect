package com.mashibing.springboot.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.mashibing.springboot.entity.Account;
import com.mashibing.springboot.entity.RespStat;

public interface IAccountService {

	Account findByLoginNameAndPassword(String loginName, String password);

	List<Account> findAll();

	PageInfo<Account> findByPage(int pageNum, int pageSize);

	RespStat deleteById(int id);

	void update(Account account);

}