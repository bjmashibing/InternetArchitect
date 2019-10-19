package com.msb.db1.controller.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.msb.db1.entity.WxConfig;

import weixin.popular.support.TokenManager;

/**
 * 初始化Token监听器
 * @author yiming
 *
 */
@WebListener
public class TokenManagerListener implements ServletContextListener{

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.err.println("------------------TokenManagerListener----contextInitialized---------------");
		
		TokenManager.init(WxConfig.getAppId(), WxConfig.getAppPass());
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

		System.err.println("------------------TokenManagerListener----contextInitialized---------------");
		TokenManager.destroyed();
	}
}
