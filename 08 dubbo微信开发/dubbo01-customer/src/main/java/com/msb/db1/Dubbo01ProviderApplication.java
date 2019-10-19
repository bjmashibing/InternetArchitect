package com.msb.db1;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import com.spring4all.swagger.EnableSwagger2Doc;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan(value = "com.mashibing.springboot.mapper")
@EnableSwagger2Doc
public class Dubbo01ProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(Dubbo01ProviderApplication.class, args);
	}

}
