package om.mashibing.springboot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import om.mashibing.springboot.entity.Account;


public interface AccountRepository extends JpaRepository<Account, Integer> {

	List<Account> findByIdBetween(int max, int min);
	
	Account findByLoginNameAndPassword(String loginName, String password);
	
	
	// 自定义 hql
	@Query("select acc from Account acc where acc.id=1 ")
	List<Account> findbyxx();
	@Query("select acc from Account acc where acc.id=?1 ")
	List<Account> findbyxx2(int id);
}
