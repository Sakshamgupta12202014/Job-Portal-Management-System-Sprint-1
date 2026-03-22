package com.jobportal.client;

import com.jobportal.dto.JobResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "JOB-SERVICE")
public interface AdminJobClient {

    @GetMapping("/api/internal/jobs/all")
    List<JobResponse> getAllJobs();

    @DeleteMapping("/api/internal/jobs/{id}")
    void deleteJob(@PathVariable Long id);
}
