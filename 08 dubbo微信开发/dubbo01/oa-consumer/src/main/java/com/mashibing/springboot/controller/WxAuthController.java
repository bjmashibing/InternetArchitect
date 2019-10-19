package com.mashibing.springboot.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mashibing.springboot.config.WxConfig;
import com.mashibing.springboot.filter.WxAuthFilter;

import weixin.popular.api.SnsAPI;
import weixin.popular.bean.sns.SnsToken;
import weixin.popular.bean.user.User;

@Controller
@RequestMapping("/auth")
public class WxAuthController {
	

	private static final Logger logger = LoggerFactory.getLogger(WxAuthFilter.class);

	@Autowired
	WxConfig wxConf;
	
	@RequestMapping("")
	public String list(@RequestParam Map<String, String> param,HttpServletRequest request) {
		
		// Code
		String code = param.get("code");

		// 获取User对象
		SnsToken stoken = SnsAPI.oauth2AccessToken(wxConf.getAppID(), wxConf.getAppsecret(), code);
		User user = SnsAPI.userinfo(stoken.getAccess_token(), wxConf.getAppID(), "zh_CN");
		logger.info("user:" + user);
		
		/**
		 * 
		 * 
		 * 2019-08-14 21:20:10.313  INFO 22868 --- [nio-8080-exec-4] c.m.springboot.filter.WxAuthFilter       : 
		 * user:weixin.popular.bean.user.User@2b87a0ef
		 *  昵称 带emoji 存mysql varchar字段 存不进去 utf8mb4
V
落日┈┾余辉

		 * [subscribe=<null>,openid=oDk2XxGS1PIwmOrzEm_eFfpqqo4Q,nickname=KRider,nickname_emoji=<null>,sex=1,language=zh_CN,city=西安,province=陕西,country=中国,headimgurl=http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLYOwCV8l3cMjyxDSPpskupkQgYO1MCu66OeKjbsFibGQTBuHKLBV54UkdDvAYLo36StiaWxmP9CC6w/132,subscribe_time=<null>,privilege={},unionid=<null>,groupid=<null>,remark=<null>,tagid_list=<null>,subscribe_scene=<null>,qr_scene=<null>,qr_scene_str=<null>,errcode=<null>,errmsg=<null>]
2019-08-14 21:20:15.797  INFO 22868 --- [nio-8080-exec-1] c.m.s.controller.WxServiceController     : eventMessage:weixin.popular.bean.message.EventMessage@54e82911[toUserName=gh_a32b261573f7,fromUserName=oDk2XxG_dYUM5t6ivpN8V5tcg6uM,createTime=1565788811,msgType=image,event=<null>,eventKey=<null>,msgId=22416709247083138,content=<null>,picUrl=http://mmbiz.qpic.cn/mmbiz_jpg/DfloTsYZFCmnGfa8Rl7Mf5c2dMvCE0yUib9iandkEpGicP5pOoOMHWgQQq68ESDgW9SicGuzHMEryDiaicN7FWx5GOiag/0,mediaId=xm7lHKbgUhe_sCGaQ8kge8T4oLA-L67rTo5qrMjLmJxKQac3UQL_aqsSOd3wao9l,format=<null>,recognition=<null>,thumbMediaId=<null>,location_X=<null>,location_Y=<null>,scale=<null>,label=<null>,title=<null>,description=<null>,url=<null>,ticket=<null>,latitude=<null>,longitude=<null>,precision=<null>,status=<null>,totalCount=<null>,filterCount=<null>,sentCount=<null>,errorCount=<null>,copyrightCheckResult=<null>,expiredTime=<null>,failTime=<null>,failReason=<null>,uniqId=<null>,poiId=<null>,result=<null>,msg=<null>,chosenBeacon=<null>,aroundBeacons=<null>,lotteryId=<null>,money=<null>,bindTime=<null>,connectTime=<null>,expireTime=<null>,vendorId=<null>,shopId=<null>,deviceNo=<null>,keyStandard=<null>,keyStr=<null>,country=<null>,province=<null>,city=<null>,sex=<null>,scene=<null>,regionCode=<null>,reasonMsg=<null>,otherElements=<null>]
2019-08-14 21:20:15.797  INFO 22868 --- [nio-8080-exec-1] c.m.s.controller.WxServiceController     : 24_7mgB663yKAWJUN5qxeIvgsp4oSzqKKbzW8aEw9nHs81Vr5ERH676SO1SZLXn5e3F3ASE-WTCgb143srIBnf6qiX5wgYsZmXNQV8DF_dbY2VDbWv309Gul7lRbL2zh025cp5rmQ404VpQTqFDSBXcAFAYHD

		 */
		
		
		// User写入Session
		request.getSession().setAttribute("user", user);
		// 访问受限的那个URI
		return "redirect:" +  param.get("uri");
	}

}
