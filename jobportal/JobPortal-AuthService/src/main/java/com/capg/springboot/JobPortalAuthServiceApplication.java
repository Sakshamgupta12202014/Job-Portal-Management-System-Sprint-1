package com.capg.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class JobPortalAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobPortalAuthServiceApplication.class, args);
        System.out.println("Job Portal Auth Service is now live on port 8081...");
    }
}
