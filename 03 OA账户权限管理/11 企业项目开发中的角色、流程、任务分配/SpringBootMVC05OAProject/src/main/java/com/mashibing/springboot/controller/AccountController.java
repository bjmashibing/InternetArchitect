package com.mashibing.springboot.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.mashibing.springboot.entity.Account;
import com.mashibing.springboot.mapper.AccountMapper;
import com.mashibing.springboot.mapper.MenuMapper;
import com.mashibing.springboot.service.AccountService;
import com.mashibing.springboot.service.IAccountService;


/**
 * 用户账户相关
 * @author Administrator
 *
 */

@Controller
@RequestMapping("/account")
public class AccountController {

	
	@Autowired
	AccountService accountSrv;
	
	@Autowired
	MenuMapper mp;
	
	@Autowired
	AccountMapper ap;
	
	@Autowired
	IAccountService is; 
	
	@RequestMapping("login")
	public String login() {
		System.out.println(ap.selectById(1));;
		System.out.println(is.count());
		return "account/login";
	}
	

	/**
	 * 用户登录异步校验
	 * @param loginName
	 * @param password
	 * @return success 成功
	 */
	@RequestMapping("validataAccount")
	@ResponseBody
	public String validataAccount(String loginName,String password,HttpServletRequest request) {
		
		System.out.println("loginName:" + loginName);
		System.out.println("password:" + password);
		
		// 1. 直接返回是否登录成功的结果
		// 2. 返回 Account对象，对象是空的 ，在controller里做业务逻辑
		// 在公司里 统一写法
		
	
		//让service返回对象，如果登录成功 把用户的对象 
		Account account = accountSrv.findByLoginNameAndPassword(loginName, password);
		
		if (account == null) {
			return "登录失败";
		}else {
			// 登录成功
			// 写到Session里
			// 在不同的controller 或者前端页面上 都能使用 
			// 当前登录用户的Account对象
			
			request.getSession().setAttribute("account", account);
			return "success";
		}
	}
	
	
	@RequestMapping("/logOut")
	public String logOut(HttpServletRequest request) {
		
		request.getSession().removeAttribute("account");
		return "index";
	}
	@RequestMapping("/list")
	public String list(@RequestParam(defaultValue = "1") int pageNum,@RequestParam(defaultValue = "5" ) int pageSize,Model model) {
		
		PageInfo<Account>page = accountSrv.findByPage(pageNum,pageSize);
		model.addAttribute("page", page);
		return "/account/list";
	}
	
}
