package com.capg.springboot.dto;

import lombok.Data;

@Data
public class JobClientResponse {
    private Long id;
    private String title;
    private String status;
    private Long postedBy;
    private String deadline;
}
