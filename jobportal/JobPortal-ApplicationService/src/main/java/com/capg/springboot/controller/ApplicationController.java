package com.capg.springboot.controller;

import java.awt.PageAttributes.MediaType;
import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.capg.springboot.dto.ApplicationResponse;
import com.capg.springboot.dto.RecruiterApplicationResponse;
import com.capg.springboot.dto.StatusUpdateRequest;
import com.capg.springboot.service.ApplicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Applications", description = "Apply, track, and manage job applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    // ── JOB SEEKER ENDPOINTS ─────────────────────────────────────────────────

    /**
     * POST /api/applications
     * Multipart form: jobId + resume file + optional coverLetter
     * 409 if already applied, 413 if file > 5MB, 400 if wrong file type
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Apply for a job [JOB_SEEKER only]")
    public ResponseEntity<ApplicationResponse> apply(
            @RequestParam("jobId") Long jobId,
            @RequestParam(value = "coverLetter", required = false) String coverLetter,
            @RequestParam("resume") MultipartFile resume,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) throws IOException {

        if (!"JOB_SEEKER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ApplicationResponse response = applicationService.apply(
                jobId, coverLetter, resume, Long.parseLong(userId));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/applications/user
     * Returns all applications for the currently logged-in seeker only
     */
    @GetMapping("/user")
    @Operation(summary = "Get my applications [JOB_SEEKER only]")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {

        if (!"JOB_SEEKER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(applicationService.getMyApplications(Long.parseLong(userId)));
    }

    /**
     * GET /api/applications/{id}
     * Seeker can only view their own application; recruiter can view if they own the job
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get application by ID [JOB_SEEKER — own only]")
    public ResponseEntity<ApplicationResponse> getById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {

        if (!"JOB_SEEKER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(applicationService.getApplicationById(id, Long.parseLong(userId)));
    }

    // ── RECRUITER ENDPOINTS ──────────────────────────────────────────────────

    /**
     * GET /api/applications/job/{jobId}
     * 403 if recruiter does not own this job
     */
    @GetMapping("/job/{jobId}")
    @Operation(summary = "Get all applicants for a job [RECRUITER — own jobs only]")
    public ResponseEntity<List<RecruiterApplicationResponse>> getApplicantsForJob(
            @PathVariable Long jobId,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {

        if (!"RECRUITER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(
                applicationService.getApplicantsForJob(jobId, Long.parseLong(userId)));
    }

    /**
     * PATCH /api/applications/{id}/status
     * Body: { newStatus, recruiterNote }
     * Validates transition before saving — backward moves return 400
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update application status [RECRUITER — own job only]")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {

        if (!"RECRUITER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(
                applicationService.updateStatus(id, request, Long.parseLong(userId)));
    }
}
