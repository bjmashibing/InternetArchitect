
package com.msb.db1.controller;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.dubbo.config.annotation.Reference;

import com.mashibing.springboot.service.IAccountService;
import com.msb.db1.entity.Account;
import com.msb.db1.entity.WxConfig;

import weixin.popular.bean.user.User;

@WebFilter(filterName = "authFilter", urlPatterns = "/h5/account/*")
public class AuthorizationFilter implements Filter {

	
	@Reference(version = "1.0.0")
	IAccountService accountSrv;
	
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		System.err.println("AuthorizationFilter init~");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		// 从session取user，判断是否已经登录
		User user = (User) request.getSession().getAttribute("user");
		if (null == user || null == user.getNickname()) {
			// 未登录，跳转授权页面
			
			String uri =( (HttpServletRequest)req).getRequestURI();
			String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + WxConfig.getAppId() + "&redirect_uri=" + WxConfig.getDomain() + "/auth?uri="+uri+"&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
			response.sendRedirect(url);

		} else {
			System.err.println("user:" + ToStringBuilder.reflectionToString(user));
			// 检查用户是否已注册
			// 检查注册标记
			String isCheckReg = (String) request.getSession().getAttribute("isCheckReg");

			if (null == isCheckReg || !"yes".equals(isCheckReg)) {
//				ServletContext context = request.getSession().getServletContext();
//				ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);
//				
//				AccountWxService accountWxService  = (AccountWxService) ctx.getBean(AccountWxService.class);
				
				
				Account account = accountSrv.findByOpendid(user.getOpenid());
				if (null != account) {
					// 已注册用户写入session
					request.getSession().setAttribute("member", account);
				}
				// 检查完成写入标记
				request.getSession().setAttribute("isCheckReg", "yes");
			}

			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
