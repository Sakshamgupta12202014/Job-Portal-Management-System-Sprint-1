package com.jobportal.controller;

import com.jobportal.dto.ApplicationStats;
import com.jobportal.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Internal endpoint only reachable from inside the Docker network (not exposed via API Gateway)
@RestController
@RequestMapping("/api/internal/applications")
public class InternalApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @GetMapping("/stats")
    public ResponseEntity<ApplicationStats> getStats() {
        return ResponseEntity.ok(applicationService.getStats());
    }
}
