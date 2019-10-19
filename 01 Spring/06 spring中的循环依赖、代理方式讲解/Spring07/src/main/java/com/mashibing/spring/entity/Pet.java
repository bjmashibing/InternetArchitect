package com.mashibing.spring.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Pet {

	@Value("ø…¿÷")
	private String name;
	
}
