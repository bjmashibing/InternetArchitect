package om.mashibing.springboot02.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import om.mashibing.springboot02.entity.Account;
import om.mashibing.springboot02.mapper.AccountMapper;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account>  implements IAccountService {

}
