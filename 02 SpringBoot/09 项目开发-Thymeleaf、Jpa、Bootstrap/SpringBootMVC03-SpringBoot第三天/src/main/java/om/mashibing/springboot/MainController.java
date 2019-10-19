package om.mashibing.springboot;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import om.mashibing.springboot.entity.Account;
import om.mashibing.springboot.service.AccountService;

@Controller
public class MainController {

	
	@Autowired
	AccountService accSrv;
	
	@RequestMapping("/list")
	@ResponseBody
	public Object list () {
	//	List<Account> list = accSrv.findAll();
	//  SQL ->  HQL select account0_.id as id1_0_0_, account0_.age as age2_0_0_, account0_.location as location3_0_0_, account0_.login_name as login_na4_0_0_, account0_.nick_name as nick_nam5_0_0_, account0_.password as password6_0_0_ from account account0_ where account0_.id=?
		 Object  account = accSrv.findxxx();
		System.out.println("account:" + account);
		return account;
	}
	
	/**
	 * 区分 get 和post 请求
	 * 
	 * get : 展示页面
	 * post：收集数据
	 * @return
	 */
	@GetMapping("/register")
	public String register (Model map) {
		
		System.out.println("======get=====");
		
		map.addAttribute("obj", "aa");
		return "register";
	}
	
	@PostMapping("/register")
	public String registerP (HttpServletRequest request,Account account) {
		
		RespStat stat =  accSrv.save(account);
		request.setAttribute("stat", stat);
		
		return "register";
	}
	
	@RequestMapping("/login")
	public String login () {
		
		
		return "login";
	}
	
	
}
