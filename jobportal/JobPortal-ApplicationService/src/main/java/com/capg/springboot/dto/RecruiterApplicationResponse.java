package com.capg.springboot.dto;

import com.capg.springboot.enums.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterApplicationResponse {
    private Long id;
    private Long userId;
    private Long jobId;
    private String resumeUrl;
    private String coverLetter;
    private ApplicationStatus status;
    private String recruiterNote; // visible to recruiter only
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
}
