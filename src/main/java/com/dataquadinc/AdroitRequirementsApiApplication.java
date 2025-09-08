package com.dataquadinc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class AdroitRequirementsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdroitRequirementsApiApplication.class, args);
	}

}
