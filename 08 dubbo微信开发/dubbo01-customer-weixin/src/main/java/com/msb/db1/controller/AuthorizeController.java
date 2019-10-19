/**
 * TODO :
 * @author  : yiming 
 * 2016年8月17日 下午5:39:33 / 精致科技 copyright	
 */
package com.msb.db1.controller;

import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mashibing.springboot.service.IAccountService;
import com.msb.db1.entity.Account;
import com.msb.db1.entity.WxConfig;

import weixin.popular.api.SnsAPI;
import weixin.popular.bean.sns.SnsToken;
import weixin.popular.bean.user.User;

@Controller
@RequestMapping(value = "/auth")
public class AuthorizeController {
	
	/**
	 * 微信授权登录，通过code换区snstoken，再用snstoken取userinfo
	 * @param model
	 * @param param
	 * @param request
	 * @return
	 */
	
	
	@Reference(version = "1.0.0")
	IAccountService accountSrv;
	
	@RequestMapping(value = "")
	public String list(Model model,@RequestParam Map<String, String> param,HttpServletRequest request,HttpServletResponse response) throws Exception{
		System.err.println("xxxxx");
		System.err.println("xxxxx");
		System.err.println("xxxxx");
		System.err.println("xxxxx");
		if(null == param || null == param.get("code") ){
			//处理非法请求
			return "error-login";
		}
		
		String code = param.get("code");
		System.err.println("code:" + code);
		System.err.println("code:" + code);
		System.err.println("code:" + code); 
		System.err.println("code:" + code);
		SnsToken stoken = SnsAPI.oauth2AccessToken(WxConfig.getAppId(), WxConfig.getAppPass(), code);
		User user = SnsAPI.userinfo(stoken.getAccess_token(), WxConfig.getAppId(), "zh_CN");
		
		if(null == user || null == user.getOpenid()){
			return "redirect:/class";
		}
		//微信登录信息
		request.getSession().setAttribute("user", user);
		
		System.err.println(ToStringBuilder.reflectionToString(user));
		//会员信息
		Account account =  accountSrv.findByOpendid(user.getOpenid());
		
		//未注册用户，注册
		if(null == account){
			account = new Account();
			
			account.setHeadimgurl(user.getHeadimgurl());
			account.setOpendid(user.getOpenid());
			account.setNickName(user.getNickname_emoji());
			accountSrv.insertAccount(account);
		}
//		System.out.println("##########################"+Arrays.equals(account.getNickName(), user.getNickname().getBytes()));
//		if (!Arrays.equals(account.getNickName(), user.getNickname().getBytes())|| !account.getAvatar().equals(user.getHeadimgurl()) ){
//			account.setNickName((user.getNickname().getBytes());
//			account.setHeadimgurl(user.getHeadimgurl());
//			System.out.println("更新会员信息！！！！！！！！！！！！！！！");
//			memberService.updateMember(member.getId(), member);
//		}
		request.getSession().setAttribute("member", account);
		
		//登录前请求地址
		String uri = param.get("uri");
		
		//跳转到登录前
		return "redirect:"+uri;
	}
}
