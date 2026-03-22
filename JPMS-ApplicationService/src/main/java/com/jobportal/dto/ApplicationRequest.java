package com.jobportal.dto;

import jakarta.validation.constraints.NotNull;

public class ApplicationRequest {

    @NotNull
    private Long jobId;

    private String resumeUrl;

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }
}
