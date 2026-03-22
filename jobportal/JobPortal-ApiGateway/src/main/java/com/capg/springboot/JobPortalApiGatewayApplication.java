package com.capg.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class JobPortalApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobPortalApiGatewayApplication.class, args);
        System.out.println("Job Portal API Gateway is now live on port 8989...");
    }
}
