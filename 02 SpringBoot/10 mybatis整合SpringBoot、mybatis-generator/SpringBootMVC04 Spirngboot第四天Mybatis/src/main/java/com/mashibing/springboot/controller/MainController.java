package com.mashibing.springboot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mashibing.springboot.mapper.Account;
import com.mashibing.springboot.mapper.Menu;
import com.mashibing.springboot.service.AccountService;
import com.mashibing.springboot.service.MenuService;

@Controller
public class MainController {

	
	@Autowired
	AccountService accSrv;
	
	
	@Autowired
	MenuService menuSrv;
	
	
	@RequestMapping("/list")
	@ResponseBody
	public Object list () {
		List<Account>  accounts = accSrv.findAll();
		
		
		List<Menu> menus =  menuSrv.findAll();
		
		
		return menus;
	}
	
	
	@RequestMapping("/add")
	@ResponseBody
	public Object add () {
		
		accSrv.add();
		return "ok";
	}
	
	
	@RequestMapping("/addMenu")
	@ResponseBody
	public Object addMenu () {
		
		menuSrv.add();
		return "ok";
	}
	
	
	@RequestMapping("/menuQuery")
	@ResponseBody
	public Object menuQuery (@RequestParam Integer id) {
		
		Menu menu = menuSrv.findById(id);
		return menu;
	}
	
	
	
	@RequestMapping("/page")
	@ResponseBody
	public Object page (@RequestParam(required = false) Integer pageNum,@RequestParam(required = false) Integer pageSize) {
		
		List<Menu> menus = menuSrv.findByPage(pageNum,pageSize);
		return menus;
	}
	
	
	
	
	
}
