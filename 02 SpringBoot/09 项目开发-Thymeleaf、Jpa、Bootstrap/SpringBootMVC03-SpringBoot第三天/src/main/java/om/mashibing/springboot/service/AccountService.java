package om.mashibing.springboot.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import om.mashibing.springboot.RespStat;
import om.mashibing.springboot.entity.Account;
import om.mashibing.springboot.repository.AccountRepository;


@Service
public class AccountService {

	
	@Autowired
	AccountRepository accRep;
	
	public RespStat save(Account account) {
		// TODO Auto-generated method stub
		
		/**
		 * 返回的实体类，id带回来。
		 */
		
		try {
			Account entity = accRep.save(account);
		} catch (Exception e) {
			return new RespStat(500, "发生错误", e.getMessage());
		}
		return RespStat.build(200);
	}

	public List<Account> findAll() {
		// TODO Auto-generated method stub
		
		// 查询所有数据

		// 自定义方法
		return accRep.findByIdBetween(1,6);
	//	return accRep.findAll();
		
		
	}

	public Optional<Account> findById(int id) {
		// 接口自带方法
		return accRep.findById(id);
		
	}

	public Object findxxx() {

	//	Account account = accRep.findByLoginNameAndPassword("yimingge", "123");
		List<Account> findbyxx = accRep.findbyxx2(2);
		return findbyxx;
	}

}
