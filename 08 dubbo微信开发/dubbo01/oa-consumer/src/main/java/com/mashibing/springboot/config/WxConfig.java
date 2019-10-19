package com.mashibing.springboot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WxConfig {

	@Value(value = "${weixin.appID}")
	private String appID;
	@Value(value = "${weixin.appsecret}")
	private String appsecret;
	
	@Value(value = "${weixin.TokenString}")
	private String TokenString;
	
	
	
	
	public String getTokenString() {
		return TokenString;
	}
	public void setTokenString(String tokenString) {
		TokenString = tokenString;
	}
	public String getAppID() {
		return appID;
	}
	public void setAppID(String appID) {
		this.appID = appID;
	}
	public String getAppsecret() {
		return appsecret;
	}
	public void setAppsecret(String appsecret) {
		this.appsecret = appsecret;
	}
	
}
