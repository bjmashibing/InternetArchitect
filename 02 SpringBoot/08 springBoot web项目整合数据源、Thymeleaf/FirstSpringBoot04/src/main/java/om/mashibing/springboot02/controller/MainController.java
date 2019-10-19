package om.mashibing.springboot02.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import om.mashibing.springboot02.domain.City;
import om.mashibing.springboot02.service.CityService;

/**
 * 在我们访问  http://主机名：端口号/context-path/Controller的URI/方法的URI
 * http://localhost:80/boot/user/list
 * @author Administrator
 * @Controller 加入Spring容器管理,单例
 */
@Controller
public class MainController {

	
	/**
	 * String 类型的返回值，会找模板文件
	 * 
	 *   context/ + /user +  /list 
	 *   context/ + list
	 * @return
	 */
	
	@Autowired
	CityService citySrv;
	
	@RequestMapping("/list")
	public String list(Model map) {
		
		List<City> list = citySrv.findAll();
		
		map.addAttribute("list", list);
		return "list";
	}
	
	@RequestMapping("/add")
	public String add(@ModelAttribute City city,Model map) {
		 
		System.out.println(city);
		String success =citySrv.add(city);
		
//
//		String success = citySrv.add(id,name);
		map.addAttribute("success", success);
		return "add";
	}
	
	@RequestMapping("/addPage")
	public String addPage() {
	
	return "add";
	}
}
