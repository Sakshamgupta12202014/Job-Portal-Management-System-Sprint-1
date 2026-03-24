package com.jobportal.controller;

import com.jobportal.dto.JobResponseDTO;
import com.jobportal.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internal/jobs")
public class InternalJobController {

    @Autowired
    private JobService jobService;

    @GetMapping("/all")
    public ResponseEntity<List<JobResponseDTO>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobsList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJobByAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
