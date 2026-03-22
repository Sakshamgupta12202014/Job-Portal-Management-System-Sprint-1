package com.capg.springboot.dto;

import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private long totalUsers;
    private long totalJobs;
    private long totalApplications;
    private Map<String, Long> usersByRole;       // JOB_SEEKER, RECRUITER, ADMIN counts
    private Map<String, Long> usersByStatus;     // ACTIVE, INACTIVE, BANNED counts
    private Map<String, Long> jobsByStatus;      // ACTIVE, CLOSED, DRAFT, DELETED counts
    private Map<String, Long> applicationsByStatus; // APPLIED, UNDER_REVIEW, etc.
}
