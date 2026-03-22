package com.capg.springboot.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Feign client to call Job Service for admin operations.
 * Admin can view ALL jobs (including DELETED) and force-delete any job.
 */
@FeignClient(name = "JOB-SERVICE", path = "/api/jobs")
public interface JobServiceClient {

    @GetMapping("/admin/all")
    Object getAllJobsForAdmin(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);

    @DeleteMapping("/{id}")
    void deleteJob(
            @PathVariable("id") Long id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role);

    @GetMapping("/admin/count")
    Map<String, Long> getJobCounts(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role);
}
