package com.msb.db1;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Dubbo01ProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(Dubbo01ProviderApplication.class, args);
	}

}
