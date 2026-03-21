package com.jobportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JpmsJobServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpmsJobServiceApplication.class, args);
		System.out.println("Job service started ");
	}

}
