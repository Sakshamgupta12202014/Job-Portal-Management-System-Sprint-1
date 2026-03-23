package com.capg.springboot.dto;

import lombok.Getter;
import lombok.Setter;

// This is the response we get when we call the Job Service
// We only need a few fields from the job - not everything

@Getter
@Setter
public class JobClientResponse {

    private Long id;
    private String title;
    private String status;    // ACTIVE, CLOSED, DELETED etc
    private Long postedBy;    // recruiter who owns this job
    private String deadline;  // deadline date as string "2025-12-31"

}
