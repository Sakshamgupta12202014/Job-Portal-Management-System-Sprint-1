package com.jobportal.client;

import com.jobportal.dto.JobResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Feign client to verify job exists before accepting an application (cross-service REST call via Eureka)
@FeignClient(name = "JOB-SERVICE")
public interface JobServiceClient {

    @GetMapping("/api/jobs/{jobId}")
    JobResponse getJobById(@PathVariable Long jobId);
}
