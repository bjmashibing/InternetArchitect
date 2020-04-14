package com.mashibing.UserConsumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.mashibing.UserAPI.UserApi;

/*
 * 不结合eureka，就是自定义一个client名字。就用url属性指定 服务器列表。url=“http://ip:port/”
 */
@FeignClient(name = "user-provider")
public interface ConsumerApi extends UserApi {
}
