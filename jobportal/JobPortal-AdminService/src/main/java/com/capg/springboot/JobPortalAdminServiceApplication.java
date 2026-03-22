package com.capg.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class JobPortalAdminServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobPortalAdminServiceApplication.class, args);
        System.out.println("Job Portal Admin Service is now live on port 8084...");
    }
}
