package com.mashibing.arica.controller;

import java.util.List;

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
		System.out.println("xxoo11");
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

		return "item";
	}
	/**
	 * item 列表，可以生成html文件，修改item信息
	 * @param item
	 * @param model
	 * @return
	 */
	@RequestMapping("itemList")
	public String itemList(Model model) {
		
		List<Item> items = itemSrv.findAll();
		model.addAttribute("items", items);

		return "item_list";
	}
	
	/**
	 * item 列表，可以生成html文件，修改item信息
	 * @param item
	 * @param model
	 * @return
	 */
	@RequestMapping("generate")
	public String generate(Model model,int id) {
		
		itemSrv.generateHtml(id);

		String msg = "文件生成成功，<a href='item"+id+".html' target='_blank'>查看</a>";
		model.addAttribute("msg", msg);
		return "success";
	}
	
	
	
	/**
	 * 模板管理
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping("templates")
	public String templates(Model model) {

		return "templates";
	}
	
	/**
	 * 修改模板
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping("editTemplate")
	public String editTemplate(Model model)throws Exception {

		String tplStr = itemSrv.getFileTemplateString();
		
		model.addAttribute("tplStr",tplStr);
		return "edit_template";
	}
	
	/**
	 * 保存模板
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping("saveTemplate")
	public String saveTemplate(Model model,String content)throws Exception {

		itemSrv.saveFileTemplateString(content);
		
		String msg = "保存成功。";
		model.addAttribute("msg", msg);
		return "success";
	}
	
}
