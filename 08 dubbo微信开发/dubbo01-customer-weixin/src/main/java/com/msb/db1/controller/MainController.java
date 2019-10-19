package com.msb.db1.controller;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msb.db1.entity.WxConfig;

import weixin.popular.api.MenuAPI;
import weixin.popular.api.TokenAPI;
import weixin.popular.bean.BaseResult;
import weixin.popular.bean.token.Token;
import weixin.popular.support.TokenManager;


@RestController
@RequestMapping("/")
public class MainController {

	// api/v888/server/method
	@RequestMapping("/main")
	public String main() {
		return ("hehe");
	}
	
	// api/v888/server/method
	@RequestMapping("/createMenu")
	public String createMenu() {

		Token token = TokenAPI.token(WxConfig.getAppId(), WxConfig.getAppPass());
		System.err.println(token.getAccess_token());
		System.err.println("token:" + ToStringBuilder.reflectionToString(token));

		String menu = "{" + "    \"button\": [" + "        {" + "            \"type\": \"location_select\", " + "            \"name\": \"一键叫车\", " + "            \"key\": \"shared_location\"" + "        }" + "    ]" + "}";
		
		 BaseResult result = MenuAPI.menuCreate(token.getAccess_token(), menu);
		System.out.println("result:" + result);
		return "1123";
	}
	
	
	
}
