package com.capg.springboot.controller;

import com.capg.springboot.dto.*;
import com.capg.springboot.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs", description = "Job CRUD, search, and recruiter operations")
public class JobController {

    private final JobService jobService;

    // ── PUBLIC ENDPOINTS (no JWT needed) ────────────────────────────────────

    @GetMapping
    @Operation(summary = "List all active jobs (paginated)")
    public ResponseEntity<PagedResponse<JobResponse>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(jobService.getAllActiveJobs(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job by ID — 404 if deleted")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search jobs with filters: keyword, location, jobType, experience, salary")
    public ResponseEntity<PagedResponse<JobResponse>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) Integer experienceYears,
            @RequestParam(required = false) java.math.BigDecimal salaryMin,
            @RequestParam(required = false) java.math.BigDecimal salaryMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        JobSearchRequest req = new JobSearchRequest();
        req.setKeyword(keyword);
        req.setLocation(location);
        if (jobType != null) req.setJobType(com.capg.springboot.enums.JobType.valueOf(jobType));
        req.setExperienceYears(experienceYears);
        req.setSalaryMin(salaryMin);
        req.setSalaryMax(salaryMax);
        req.setPage(page);
        req.setSize(size);

        return ResponseEntity.ok(jobService.searchJobs(req));
    }

    // ── RECRUITER ENDPOINTS ──────────────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Post a new job [RECRUITER only]")
    public ResponseEntity<JobResponse> createJob(
            @Valid @RequestBody JobRequest request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {

        if (!"RECRUITER".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobService.createJob(request, Long.parseLong(userId)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a job [RECRUITER — own jobs only]")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobRequest request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {

        if (!"RECRUITER".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(jobService.updateJob(id, request, Long.parseLong(userId)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft-delete a job [RECRUITER — own jobs only, or ADMIN]")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {

        if ("ADMIN".equals(role)) {
            jobService.adminDeleteJob(id);
        } else if ("RECRUITER".equals(role)) {
            jobService.deleteJob(id, Long.parseLong(userId));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-jobs")
    @Operation(summary = "Get all jobs posted by the current recruiter")
    public ResponseEntity<PagedResponse<JobResponse>> getMyJobs(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (!"RECRUITER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(jobService.getMyJobs(Long.parseLong(userId), page, size));
    }

    // ── ADMIN ENDPOINTS ──────────────────────────────────────────────────────

    @GetMapping("/admin/all")
    @Operation(summary = "Admin — get all jobs including DELETED")
    public ResponseEntity<PagedResponse<JobResponse>> getAllForAdmin(
            @RequestHeader("X-User-Role") String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(jobService.getAllJobsForAdmin(page, size));
    }
}
