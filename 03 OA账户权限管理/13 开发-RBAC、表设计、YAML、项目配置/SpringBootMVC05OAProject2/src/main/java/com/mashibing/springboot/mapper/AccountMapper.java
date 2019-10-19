package com.mashibing.springboot.mapper;

import com.mashibing.springboot.entity.Account;
import org.springframework.stereotype.Repository;

/**
 * AccountMapper继承基类
 */
@Repository
public interface AccountMapper extends MyBatisBaseDao<Account, Integer, AccountExample> {
}