package com.mashibing.admin;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.util.StringUtils;

import com.google.code.kaptcha.Constants;

public class CodeFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		
		// 当前用户请求的URL
		String uri = req.getServletPath();


		if(uri.equals("/login") && req.getMethod().equalsIgnoreCase("post")) {

		
			String sessionCode = req.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY).toString();
			String formCode = req.getParameter("code").trim();
				
			if(StringUtils.isEmpty(formCode)) {
				throw new RuntimeException("验证码不能为空");
			}
			if(sessionCode.equalsIgnoreCase(formCode)) {
				
				System.out.println("验证通过");
				
			}else {
				
				throw new RuntimeException("验证码错误");
			}		
					
		//	throw new AuthenticationServiceException("xx");
		}

		chain.doFilter(request, response);
		
		
	}

}
