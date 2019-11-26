package com.mashibing.arica;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = "com.mashibing.arica.mapper")
public class AricaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AricaApplication.class, args);
	}

}
