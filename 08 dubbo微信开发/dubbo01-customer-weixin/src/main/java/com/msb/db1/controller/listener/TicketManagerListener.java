/**
 * TODO :
 * @author  : yiming 
 * 2016年8月27日 下午4:08:46 / 精致科技 copyright	
 */
package com.msb.db1.controller.listener;

import javax.servlet.ServletContextEvent;

import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.msb.db1.entity.WxConfig;

import weixin.popular.support.TicketManager;
@WebListener
public class TicketManagerListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.err.println("------------------TicketManagerListener----contextInitialized---------------");
		TicketManager.init(WxConfig.getAppId(), 15, 60 * 119);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.err.println("------------------TicketManagerListener----contextDestroyed---------------");
		TicketManager.destroyed();
	}

}
