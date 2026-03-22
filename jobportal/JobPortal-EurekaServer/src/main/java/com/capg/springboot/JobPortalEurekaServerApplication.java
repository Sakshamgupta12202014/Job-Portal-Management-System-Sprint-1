package com.capg.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class JobPortalEurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobPortalEurekaServerApplication.class, args);
        System.out.println("Job Portal Eureka Server is now live on http://localhost:8761");
    }
}
