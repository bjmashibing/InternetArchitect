package om.mashibing.springboot02.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import om.mashibing.springboot02.entity.Account;
import om.mashibing.springboot02.entity.City;
import om.mashibing.springboot02.mapper.AccountMapper;
import om.mashibing.springboot02.service.CityService;
import om.mashibing.springboot02.service.IAccountService;

@Controller
@RequestMapping("/city")
public class MainController {

	@Autowired
	CityService citySrv;
	
	
	@Autowired
	AccountMapper mapper;
	
	@Autowired
	IAccountService accountSrv;
	
	@RequestMapping("/list")
	public String list(Model map) {
		
		Account account = mapper.selectById(1);
		System.out.println("accountSrv.count() : " + accountSrv.count());
		System.out.println("account:" + account . getLoginName());
		return "list";
		// mapper.xml  @注解
	}
	

}
