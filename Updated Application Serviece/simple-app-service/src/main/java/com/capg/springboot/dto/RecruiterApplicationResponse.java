package com.capg.springboot.dto;

import com.capg.springboot.enums.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// This DTO is what we send back to the RECRUITER
// It includes recruiterNote which is hidden from seekers

@Getter
@Setter
public class RecruiterApplicationResponse {

    private Long id;
    private Long userId;
    private Long jobId;
    private String resumeUrl;
    private String coverLetter;
    private ApplicationStatus status;
    private String recruiterNote;   // only shown to recruiter
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;

}
