package com.ezaz.ezbilling;

import com.ezaz.ezbilling.repository.mysql.JpaCustomerRepo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(value = "com.ezaz.ezbilling")
@EntityScan("com.ezaz.ezbilling")
@EnableJpaRepositories (basePackageClasses = JpaCustomerRepo.class)
public class EzbillingApplication {

	public static void main(String[] args) {
		SpringApplication.run(EzbillingApplication.class, args);
	}

}
