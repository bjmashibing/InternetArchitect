package com.mashibing.UserConsumer;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mashibing.UserAPI.UserApi;

/*
 * 不结合eureka，就是自定义一个client名字。就用url属性指定 服务器列表。url=“http://ip:port/”
 */
@FeignClient(name = "user-provider",fallbackFactory = UserProviderBackFactory.class)
public interface ConsumerApi extends UserApi {
	
	/**
	 * 这里 getMapping 是给Feign看的 get请求 user-provider/getMap?id={1}
	 * @RequestParam("id") 也是给Feign看的
	 * 
	 * HttpClient Http协议
	 * @param id
	 * @return
	 */
	@GetMapping("/getMap")
	Map<Integer, String> getMap(@RequestParam("id") Integer id);
	
	
	
	@GetMapping("/getMap2")
	Map<Integer, String> getMap2(@RequestParam("id") Integer id,@RequestParam("name") String name);
	
	@GetMapping("/getMap3")
	Map<Integer, String> getMap3(@RequestParam Map<String, Object> map);
	
	@PostMapping("/postMap")
	Map<Integer, String> postMap(Map<String, Object> map);
	
	

	
}
