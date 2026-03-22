package com.capg.springboot.feign;

import com.capg.springboot.dto.JobClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * FeignClient calls the Job Service via Eureka (lb://JOB-SERVICE).
 * Used to:
 *  1. Verify a job exists and is ACTIVE before applying
 *  2. Verify the recruiter owns a job before allowing status update
 */
@FeignClient(name = "JOB-SERVICE", path = "/api/jobs")
public interface JobServiceClient {

    @GetMapping("/{id}")
    JobClientResponse getJobById(
            @PathVariable("id") Long id,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String role);
}
