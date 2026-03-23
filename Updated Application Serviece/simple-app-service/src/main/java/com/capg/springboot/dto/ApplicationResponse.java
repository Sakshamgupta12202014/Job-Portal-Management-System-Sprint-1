package com.capg.springboot.dto;

import com.capg.springboot.enums.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// This DTO is what we send back to the JOB SEEKER
// Notice: recruiterNote is NOT here - seekers cannot see internal recruiter notes

@Getter
@Setter
public class ApplicationResponse {

    private Long id;
    private Long userId;
    private Long jobId;
    private String resumeUrl;
    private String coverLetter;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;

    // recruiterNote is intentionally missing - it is a private recruiter field

}
