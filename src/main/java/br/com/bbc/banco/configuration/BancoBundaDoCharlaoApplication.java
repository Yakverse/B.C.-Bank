package br.com.bbc.banco.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({"br.com.bbc.banco"})
@EntityScan(basePackages  = "br.com.bbc.banco")
@EnableJpaRepositories(basePackages = "br.com.bbc.banco")
public class BancoBundaDoCharlaoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BancoBundaDoCharlaoApplication.class, args);
	}

}
