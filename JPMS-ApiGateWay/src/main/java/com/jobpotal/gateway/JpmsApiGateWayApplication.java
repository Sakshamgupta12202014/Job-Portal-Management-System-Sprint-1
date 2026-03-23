package com.jobpotal.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.jobpotal.gateway", "com.jobportal.gateway"})
public class JpmsApiGateWayApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpmsApiGateWayApplication.class, args);
		System.out.println("JPMS-ApiGateWay service is running on port 9090...");
	}

}
