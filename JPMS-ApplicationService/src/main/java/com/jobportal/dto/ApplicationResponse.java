package com.jobportal.dto;

import java.time.LocalDateTime;

public class ApplicationResponse {

    private Long id;
    private String applicantEmail;
    private Long jobId;
    private String resumeUrl;
    private String status;
    private LocalDateTime appliedAt;
    private String message;

    public ApplicationResponse() {}

    public ApplicationResponse(Long id, String message) {
        this.id = id;
        this.message = message;
    }

    public ApplicationResponse(Long id, String applicantEmail, Long jobId,
                               String resumeUrl, String status, LocalDateTime appliedAt) {
        this.id = id;
        this.applicantEmail = applicantEmail;
        this.jobId = jobId;
        this.resumeUrl = resumeUrl;
        this.status = status;
        this.appliedAt = appliedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getApplicantEmail() { return applicantEmail; }
    public void setApplicantEmail(String applicantEmail) { this.applicantEmail = applicantEmail; }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
