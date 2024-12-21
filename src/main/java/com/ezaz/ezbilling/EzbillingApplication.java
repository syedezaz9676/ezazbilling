package com.ezaz.ezbilling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(value = "com.ezaz.ezbilling")
//@EntityScan("com.ezaz.ezbilling")
@EnableScheduling
public class EzbillingApplication {

	public static void main(String[] args) {
		SpringApplication.run(EzbillingApplication.class, args);
	}

}
