package com.mashibing.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DubboOaProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(DubboOaProviderApplication.class, args);
	}

}
