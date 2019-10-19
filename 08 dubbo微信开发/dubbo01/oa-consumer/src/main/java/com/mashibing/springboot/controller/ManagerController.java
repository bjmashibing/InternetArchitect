package com.mashibing.springboot.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageInfo;
import com.mashibing.springboot.entity.Account;
import com.mashibing.springboot.entity.Config;
import com.mashibing.springboot.entity.Permission;
import com.mashibing.springboot.entity.Role;
import com.mashibing.springboot.service.IAccountService;
import com.mashibing.springboot.service.IPermissionService;
import com.mashibing.springboot.service.IRoleService;


/**
 * 用户账户相关
 * @author Administrator
 *
 */

@Controller
@RequestMapping("/manager")
public class ManagerController {

	

	@Reference(version = "1.0.0")
	IAccountService accountSrv;

	@Reference(version = "1.0.0")
	IPermissionService permissionSrv;
	
	@Reference(version = "1.0.0")
	IRoleService roleSrv;

    
    @RequestMapping("accountList")
    public  String accountList(@RequestParam(defaultValue = "1") int pageNum,@RequestParam(defaultValue = "5" ) int pageSize,Model model) {
    	
    	PageInfo<Account>page = accountSrv.findByPage(pageNum,pageSize);
		model.addAttribute("page", page);
		
    	return "manager/accountList";
    }
    
    
    @RequestMapping("permissionList")
    public  String permissionList(@RequestParam(defaultValue = "1") int pageNum,@RequestParam(defaultValue = "5" ) int pageSize,Model model) {
    	
    	PageInfo<Permission>page = permissionSrv.findByPage(pageNum,pageSize);
		model.addAttribute("page", page);
    	return "manager/permissionList";
    }
    
    @RequestMapping("permissionModify")
    public  String permissionModify(@RequestParam int id,Model model) {
    	
    	Permission permission = permissionSrv.findById(id);
    	
    	model.addAttribute("p", permission);
    	return "manager/permissionModify";
    }
    
    @RequestMapping("permissionAdd")
    public  String permissionAdd(Model model) {
    	
    	
    	return "manager/permissionModify";
    }
    
    @RequestMapping("roleList")
    public  String roleList(@RequestParam(defaultValue = "1") int pageNum,@RequestParam(defaultValue = "5" ) int pageSize,Model model) {
    	
    	PageInfo<Role>page = roleSrv.findByPage(pageNum,pageSize);
    	model.addAttribute("page", page);
    	return "manager/roleList";
    }
    
    
    /**
     * 角色 添加/ 修改权限
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("rolePermission/{id}")
    public  String rolePermission(@PathVariable int id,Model model,HttpServletRequest request) {
    	
    	Role role = roleSrv.findById(id);
    	System.out.println("role:" + ToStringBuilder.reflectionToString(role));
    	List<Permission> pList =  permissionSrv.findAll();
    	model.addAttribute("pList", pList);
    	model.addAttribute("role", role);
    	Object attribute = request.getSession().getAttribute("account");
    	System.out.println("attribute:" + ToStringBuilder.reflectionToString(attribute));
    	return "manager/rolePermission";
    }
    
    
	
}
