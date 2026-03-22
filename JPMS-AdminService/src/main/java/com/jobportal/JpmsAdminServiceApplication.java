package com.jobportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class JpmsAdminServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpmsAdminServiceApplication.class, args);
	}

}
