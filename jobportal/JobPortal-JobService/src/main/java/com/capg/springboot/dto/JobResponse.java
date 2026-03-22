package com.capg.springboot.dto;

import com.capg.springboot.enums.JobStatus;
import com.capg.springboot.enums.JobType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {

    private Long id;
    private String title;
    private String companyName;
    private String location;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private Integer experienceYears;
    private JobType jobType;
    private String skillsRequired;
    private String description;
    private JobStatus status;
    private LocalDate deadline;
    private Long postedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
