package com.mashibing.springboot.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mashibing.springboot.config.WxConfig;

import weixin.popular.bean.user.User;

@WebFilter(filterName = "WxAuthFilter",urlPatterns = "/profile/*")
public class WxAuthFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(WxAuthFilter.class);
	@Autowired
	WxConfig wxConf;
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		logger.info("WxAuthFilter Init...");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {


		// 判断用户 登录状态 检查session 里有没有 User对象
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)resp;
		 	// 框架 封装
		User user = (User)request.getSession().getAttribute("user");
		
		
		if(null == user) {
			
			//如果没有
			// 获取 用户的 openid 跳转登录 ，去数据库比对
			String uri =request.getRequestURI();
			String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + wxConf.getAppID() + 
					"&redirect_uri=http://weixin.duozuiyu.com/auth?uri="
			+uri+"&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";


			response.sendRedirect(url);
		}else {
			logger.info("user:" + ToStringBuilder.reflectionToString(user));
		}
		        // 没有的话 写数据库
		
		chain.doFilter(request, response);
		
	}

}
