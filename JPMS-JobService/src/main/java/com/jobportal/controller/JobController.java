package com.jobportal.controller;

import com.jobportal.dto.JobRequest;
import com.jobportal.dto.JobResponse;
import com.jobportal.service.JobService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    // Recruiter identity extracted from Gateway-injected headers
    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            @RequestBody @Valid JobRequest req,
            @RequestHeader("X-User-Email") String recruiterEmail,
            @RequestHeader("X-User-Role") String role) {
        if (!"RECRUITER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(jobService.createJob(req, recruiterEmail));
    }

    @GetMapping
    public ResponseEntity<Page<JobResponse>> getAllJobs(Pageable pageable) {
        return ResponseEntity.ok(jobService.getAllJobs(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<JobResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String experience,
            Pageable pageable) {
        return ResponseEntity.ok(jobService.search(keyword, location, experience, pageable));
    }

    @GetMapping("/my")
    public ResponseEntity<List<JobResponse>> getMyJobs(
            @RequestHeader("X-User-Email") String recruiterEmail,
            @RequestHeader("X-User-Role") String role) {
        if (!"RECRUITER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(jobService.getJobsByRecruiter(recruiterEmail));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable Long id,
            @RequestBody @Valid JobRequest req,
            @RequestHeader("X-User-Email") String recruiterEmail,
            @RequestHeader("X-User-Role") String role) {
        if (!"RECRUITER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(jobService.updateJob(id, req, recruiterEmail));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long id,
            @RequestHeader("X-User-Email") String recruiterEmail,
            @RequestHeader("X-User-Role") String role) {
        if (!"RECRUITER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        jobService.deleteJob(id, recruiterEmail);
        return ResponseEntity.noContent().build();
    }
}
