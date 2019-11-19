package com.mashibing.arica.controller;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mashibing.arica.entity.Item;
import com.mashibing.arica.service.ItemService;

@Controller
public class MainController {

	
	@Autowired
	ItemService itemSrv;
	
	
	@RequestMapping("")
	public String index() {
		System.out.println("xxoo");
		return "arica";
	}
	
	/**
	 * 显示添加 item 表单
	 * @return
	 */
	@RequestMapping("addtor")
	public String addtor() {
		return "add";
	}	
	
	/**
	 * 表单接受，入库
	 * @param item
	 * @param model
	 * @return
	 */
	@RequestMapping("add")
	public String add(Item item,Model model) {
		
		Item itemRecord = itemSrv.insert(item);
		
		model.addAttribute("msg", "添加商品成功, <a href = 'view?id="
		+itemRecord.getId()+"' target='_blank' class=\"layui-btn\">预览一下</a>");
		
		return "success";
	}
	
	/**
	 * 临时预览，动态
	 * @param item
	 * @param model
	 * @return
	 */
	@RequestMapping("view")
	public String view(Model model,int id) {
		
		Item item = itemSrv.findById(id);
		model.addAttribute("item", item);

		return "view";
	}
	
}
