package com.mashibing.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;

@SpringBootApplication
@EnableAdminServer
public class AdminTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminTestApplication.class, args);
	}

	@Bean
	public DingDingNotifier dingDingNotifier(InstanceRepository repository) {
		return new DingDingNotifier(repository);
	}

}
