package com.capg.springboot.feign;

import com.capg.springboot.dto.JobClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

// This is a Feign client - it lets us call the Job Service like a normal Java method
// Feign automatically makes the HTTP call using Eureka to find the Job Service address
// name = "JOB-SERVICE" must match the spring.application.name in Job Service

@FeignClient(name = "JOB-SERVICE", path = "/api/jobs")
public interface JobServiceClient {

    // This calls GET http://JOB-SERVICE/api/jobs/{id}
    // We pass the user headers so Job Service knows who is making the call
    @GetMapping("/{id}")
    JobClientResponse getJobById(
            @PathVariable("id") Long id,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String role
    );

}
