package om.mashibing.springboot02.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import om.mashibing.springboot02.entity.Account;

public interface AccountMapper extends BaseMapper<Account> {

	// Nginx 动静分离 _ URI伪静态    -> 动态代理 7层  |   lvs 4层代理  | 缓存 memcached|Ehcached |
	// lua
	
	// 分布式 kafka，FastDFS dubbo  -》 spring cloud
}
