package com.capg.springboot.dto;

import com.capg.springboot.enums.JobType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class JobRequest {

    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Location is required")
    private String location;

    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private Integer experienceYears;

    @NotNull(message = "Job type is required")
    private JobType jobType;

    private String skillsRequired;

    @NotBlank(message = "Description is required")
    private String description;

    private LocalDate deadline;
}
