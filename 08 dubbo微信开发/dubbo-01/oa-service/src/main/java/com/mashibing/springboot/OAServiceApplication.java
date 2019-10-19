package com.mashibing.springboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@SpringBootApplication
@MapperScan(value = "com.mashibing.springboot.mapper")
@EnableHystrix
public class OAServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OAServiceApplication.class, args);
	}

}
