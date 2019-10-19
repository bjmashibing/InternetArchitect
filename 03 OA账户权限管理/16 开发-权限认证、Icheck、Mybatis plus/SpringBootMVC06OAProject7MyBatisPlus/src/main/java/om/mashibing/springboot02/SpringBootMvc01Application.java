package om.mashibing.springboot02;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("om.mashibing.springboot02.mapper")
public class SpringBootMvc01Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootMvc01Application.class, args);
	}

}
