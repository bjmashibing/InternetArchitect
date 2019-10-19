package com.msb.db1.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/")
public class H5Controller {

	// api/v888/server/method
	@RequestMapping("/h5/1.0/main")
	public String main() {
		return ("h5/main");
	}
}
