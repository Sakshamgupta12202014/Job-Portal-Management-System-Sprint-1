package com.jobportal.client;

import com.jobportal.dto.ApplicationStats;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "application-service")
public interface AdminAppClient {

    @GetMapping("/api/internal/applications/stats")
    ApplicationStats getStats();
}
