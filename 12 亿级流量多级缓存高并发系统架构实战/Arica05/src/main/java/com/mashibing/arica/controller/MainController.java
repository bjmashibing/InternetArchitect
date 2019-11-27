package com.mashibing.arica.controller;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.Page;
import com.mashibing.arica.entity.Item;
import com.mashibing.arica.entity.ItemHtml;
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
	public String add(Item item,Model model) throws Exception {
		
		Item itemRecord = itemSrv.add(item);
		
		model.addAttribute("msg", "添加商品成功, <a href = 'view?id="
		+itemRecord.getId()+"' target='_blank' class=\"layui-btn\">预览一下</a>");
		
		return "success";
	}
	
	
	/**
	 * 编辑item页面
	 * @param item
	 * @param model
	 * @return
	 */
	@RequestMapping("editor")
	public String editor(Integer id,Model model) throws Exception {
		
		Item itemRecord = itemSrv.findById(id);
		
		Boolean canWrite = itemSrv.getLock(id);
		
		
		model.addAttribute("canWrite", canWrite);
		model.addAttribute("item", itemRecord);
		return "editor";
	}
	

	/**
	 * 保存item
	 * @param item
	 * @param model
	 * @return
	 */
	@RequestMapping("editSave")
	public String editSave(Item item,Model model) throws Exception {
		
		Item itemRecord = itemSrv.update(item);
		model.addAttribute("msg", "添加商品成功, <a href = 'view?id="
				+itemRecord.getId()+"' target='_blank' class=\"layui-btn\">预览一下</a>");
		
		itemSrv.releaseLock(item.getId());
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
	

	/**
	 * 生成所有静态页面
	 * @return
	 */
	@RequestMapping("generateAll")
	public String generateAll(Model model) {
		
		List<ItemHtml> list = itemSrv.generateAll();
		model.addAttribute("result", list);
		return "generateAll";
	}
	
	// 电商分类页
	@RequestMapping("main")
	public String main(Model model,
			@RequestParam(defaultValue = "1") int pageNum,
			@RequestParam(defaultValue = "5")int pageSize) {
		
		// category 参数没穿
		Page<Item> items = itemSrv.findByPage(pageNum,pageSize);
		
		
		System.out.println(ToStringBuilder.reflectionToString(items));
		
		model.addAttribute("items", items);
		return "item_page";
	}
	// 生成电商系统首页html
	@RequestMapping("generateMain")
	public String generateMain(Model model) {
		
		itemSrv.generatemain();
		String msg = "文件生成成功，<a href='main.html' target='_blank'>查看</a>";
		model.addAttribute("msg", msg);
		return "success";
	}
	
	
	
	// 生成电商系统首页html
	@RequestMapping("generateCategory")
	public String generateCategory(Model model) {
		
		itemSrv.generateCategory();
		String msg = "文件生成成功，<a href='list_1.html' target='_blank'>查看</a>";
		model.addAttribute("msg", msg);
		return "success";
	}
	
	
	
	// 集群状态
	@RequestMapping("health")
	public String health(Model model) throws Exception{
		
		HashMap<String, Boolean> map = itemSrv.health();
		model.addAttribute("map", map);
		return "health";
	}
	
	// 集群状态
	@RequestMapping("check")
	public String check(Model model) throws Exception{
		
		List<Item> errorList = itemSrv.checkFile();
		model.addAttribute("errorList", errorList);
		return "check";
	}
	
}
