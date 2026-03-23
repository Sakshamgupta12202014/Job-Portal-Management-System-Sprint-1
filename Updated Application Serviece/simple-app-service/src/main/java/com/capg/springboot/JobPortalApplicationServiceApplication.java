package com.capg.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

// @SpringBootApplication  --> starts the whole spring boot app
// @EnableDiscoveryClient  --> registers this service with Eureka
// @EnableFeignClients     --> allows us to call other services using Feign

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class JobPortalApplicationServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(JobPortalApplicationServiceApplication.class, args);

        System.out.println("==============================================");
        System.out.println("  Application Service started on port 8083   ");
        System.out.println("==============================================");
    }

}
