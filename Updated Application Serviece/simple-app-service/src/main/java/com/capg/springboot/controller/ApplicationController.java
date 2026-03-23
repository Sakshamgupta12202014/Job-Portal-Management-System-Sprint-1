package com.capg.springboot.controller;

import com.capg.springboot.dto.ApplicationResponse;
import com.capg.springboot.dto.RecruiterApplicationResponse;
import com.capg.springboot.dto.StatusUpdateRequest;
import com.capg.springboot.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

// @RestController  -> this class handles HTTP requests and returns JSON
// @RequestMapping  -> all endpoints in this class start with /api/applications

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    // Spring automatically injects the ApplicationService here
    @Autowired
    private ApplicationService applicationService;


    // =========================================================================
    // POST /api/applications
    // Who can use: JOB_SEEKER only
    // What it does: Submit a job application with a resume file
    //
    // Request: multipart/form-data
    //   - jobId       (required)
    //   - coverLetter (optional)
    //   - resume      (required - PDF or DOCX, max 5MB)
    //
    // Response: 201 Created with application details
    // =========================================================================

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApplicationResponse> applyForJob(

            @RequestParam("jobId") Long jobId,
            @RequestParam(value = "coverLetter", required = false) String coverLetter,
            @RequestParam("resume") MultipartFile resume,

            // These headers are injected by the API Gateway after JWT validation
            @RequestHeader("X-User-Id")   String userId,
            @RequestHeader("X-User-Role") String role

    ) throws IOException {

        // Only JOB_SEEKER is allowed to apply for jobs
        if (!"JOB_SEEKER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Convert userId from String to Long
        Long seekerId = Long.parseLong(userId);

        // Call the service to handle the business logic
        ApplicationResponse response = applicationService.applyForJob(
                jobId, coverLetter, resume, seekerId
        );

        // 201 Created is the correct status when a new resource is created
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // =========================================================================
    // GET /api/applications/user
    // Who can use: JOB_SEEKER only
    // What it does: Get all applications submitted by the logged-in seeker
    //
    // Response: 200 OK with list of applications
    // =========================================================================

    @GetMapping("/user")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(

            @RequestHeader("X-User-Id")   String userId,
            @RequestHeader("X-User-Role") String role

    ) {

        // Only JOB_SEEKER can view their applications
        if (!"JOB_SEEKER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Long seekerId = Long.parseLong(userId);

        List<ApplicationResponse> applications = applicationService.getMyApplications(seekerId);

        return ResponseEntity.ok(applications);
    }


    // =========================================================================
    // GET /api/applications/{id}
    // Who can use: JOB_SEEKER only (can only see their own applications)
    // What it does: Get one specific application by its ID
    //
    // Response: 200 OK with application details
    //           403 Forbidden if trying to view another seeker's application
    // =========================================================================

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplicationById(

            @PathVariable Long id,

            @RequestHeader("X-User-Id")   String userId,
            @RequestHeader("X-User-Role") String role

    ) {

        // Only JOB_SEEKER can use this endpoint
        if (!"JOB_SEEKER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Long seekerId = Long.parseLong(userId);

        ApplicationResponse response = applicationService.getApplicationById(id, seekerId);

        return ResponseEntity.ok(response);
    }


    // =========================================================================
    // GET /api/applications/job/{jobId}
    // Who can use: RECRUITER only (only for their own jobs)
    // What it does: Get all applicants who applied for a specific job
    //
    // Response: 200 OK with list of applicants (includes recruiter notes)
    //           403 Forbidden if recruiter does not own this job
    // =========================================================================

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<RecruiterApplicationResponse>> getApplicantsForJob(

            @PathVariable Long jobId,

            @RequestHeader("X-User-Id")   String userId,
            @RequestHeader("X-User-Role") String role

    ) {

        // Only RECRUITER can view job applicants
        if (!"RECRUITER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Long recruiterId = Long.parseLong(userId);

        List<RecruiterApplicationResponse> applicants =
                applicationService.getApplicantsForJob(jobId, recruiterId);

        return ResponseEntity.ok(applicants);
    }


    // =========================================================================
    // PATCH /api/applications/{id}/status
    // Who can use: RECRUITER only (only for their own jobs)
    // What it does: Change the status of an application
    //
    // Request body:
    //   { "newStatus": "UNDER_REVIEW", "recruiterNote": "Good profile" }
    //
    // Response: 200 OK with updated application
    //           400 Bad Request if status transition is invalid
    //           403 Forbidden if recruiter does not own the job
    // =========================================================================

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> updateApplicationStatus(

            @PathVariable Long id,

            // @Valid triggers validation of the request body fields
            @Valid @RequestBody StatusUpdateRequest request,

            @RequestHeader("X-User-Id")   String userId,
            @RequestHeader("X-User-Role") String role

    ) {

        // Only RECRUITER can update application status
        if (!"RECRUITER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Long recruiterId = Long.parseLong(userId);

        ApplicationResponse response = applicationService.updateApplicationStatus(
                id, request, recruiterId
        );

        return ResponseEntity.ok(response);
    }

}
