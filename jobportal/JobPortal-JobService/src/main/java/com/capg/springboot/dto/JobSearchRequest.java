package com.capg.springboot.dto;

import com.capg.springboot.enums.JobType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class JobSearchRequest {
    private String keyword;
    private String location;
    private Integer experienceYears;
    private JobType jobType;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private int page = 0;
    private int size = 10;
}
