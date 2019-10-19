package com.mashibing.springboot.service;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mashibing.springboot.entity.Account;
import com.mashibing.springboot.entity.RespStat;
import com.mashibing.springboot.mapper.AccountExample;
import com.mashibing.springboot.mapper.AccountMapper;

@Component
@Service(version = "1.0.0" ,timeout = 10000, interfaceClass = IAccountService.class)
public class AccountServiceImpl implements IAccountService {

	@Autowired
	AccountMapper accMapper;
	
	
	
	@Override
	public Account findByLoginNameAndPassword(String loginName, String password) {

//		AccountExample example = new AccountExample();
//		example.createCriteria()
//		.andLoginNameEqualTo(loginName)
//		.andPasswordEqualTo(password);
//		
//		// password
//		// 1. 没有 
//		// 2. 有一条 
//		// 3. 好几条 X
//		List<Account> list = accMapper.selectByExample(example );
		
		
		Account account = accMapper.findByLoginNameAndPassword(loginName,password);
		
		return account;
	}

	@Override
	public List<Account> findAll() {

		AccountExample example = new AccountExample();
		return accMapper.selectByExample(example );
	}

	@Override
	public PageInfo<Account> findByPage(int pageNum, int pageSize) {

		
		List<Account> alist = accMapper.selectByPermission();
		
		
		Account account = alist.get(0);
		
		System.out.println("account getPermissionList:" + account.getPermissionList().size());
		System.out.println("account getRoleList:" + account.getRoleList().size());
		
		System.out.println("alist.size() + " + alist.size());
		System.out.println(ToStringBuilder.reflectionToString(alist.get(0)));
		PageHelper.startPage(pageNum, pageSize);
		
		AccountExample example = new AccountExample();
		List<Account> list = accMapper.selectByExample(example );
		return new PageInfo<>(list,5);
	}

	@Override
	public RespStat deleteById(int id) {

		// 1. 要提示用户 
		// 2. 通过删除标记 数据永远删不掉    / update 只做增，而不是直接改表内容  // 历史数据 表（数据库）  -> 写文本log
		int row = accMapper.deleteByPrimaryKey(id);
		
		if(row == 1) {
			
			return RespStat.build(200);
		}else {
			return RespStat.build(500,"删除出错");
		}
	}

	@Override
	public void update(Account account) {
		accMapper.updateByPrimaryKeySelective(account)	;	
	}
}
