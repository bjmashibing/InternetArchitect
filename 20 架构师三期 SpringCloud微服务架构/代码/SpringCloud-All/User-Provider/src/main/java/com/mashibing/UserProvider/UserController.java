package com.mashibing.UserProvider;

import org.springframework.web.bind.annotation.RestController;

import com.mashibing.UserAPI.UserApi;

@RestController
public class UserController implements UserApi {

	
	@Override
	public String alive() {


		return "ooxxoo";
	}

	@Override
	public String getById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 写一个文档  给谁看？ php看吗
	 * 
	 * 接口写了，只有java的客户端能用，php 再去写文档。。。
	 * 
	 * 
	 * 
	 * 服务名
	 * 接口名。。。。
	 * @return
	 */
	

}
