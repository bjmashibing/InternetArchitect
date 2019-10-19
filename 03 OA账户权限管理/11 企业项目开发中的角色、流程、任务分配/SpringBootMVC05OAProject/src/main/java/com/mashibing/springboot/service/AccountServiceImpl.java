package com.mashibing.springboot.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mashibing.springboot.entity.Account;
import com.mashibing.springboot.mapper.AccountMapper;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper,Account> implements IAccountService {

}
