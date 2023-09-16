package com.ezaz.ezbilling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "com.ezaz.ezbilling")
public class EzbillingApplication {

	public static void main(String[] args) {
		SpringApplication.run(EzbillingApplication.class, args);
	}

}
