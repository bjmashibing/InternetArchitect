package com.mashibing.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *     展示 修改 个人信息 微信
 * @author Administrator
 *
 */

@Controller
@RequestMapping("/profile")
public class WxProfileController {

	@RequestMapping("/my")
	public String my(Model model) {
		
		
		return "profile/my";
	}
}
