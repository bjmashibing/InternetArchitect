package com.mashibing.rmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
 
	@Autowired
	MyService mySrv;
	
	@GetMapping("/add")
	public int add(int a,int b) {
		
		return mySrv.add(a, b);
	}
}
