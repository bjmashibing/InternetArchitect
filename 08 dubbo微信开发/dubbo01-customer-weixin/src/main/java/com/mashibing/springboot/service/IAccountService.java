package com.mashibing.springboot.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.msb.db1.entity.Account;
import com.msb.db1.entity.RespStat;

public interface IAccountService {

	Account findByLoginNameAndPassword(String loginName, String password);

	List<Account> findAll();

	PageInfo<Account> findByPage(int pageNum, int pageSize);

	RespStat deleteById(int id);

	void update(Account account);

	void insertAccount(Account account);

	Account findByOpendid(String openid);

}