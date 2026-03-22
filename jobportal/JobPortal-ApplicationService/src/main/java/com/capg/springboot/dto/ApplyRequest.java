package com.capg.springboot.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplyRequest {

    @NotNull(message = "Job ID is required")
    private Long jobId;

    private String coverLetter; // optional

    // resume file is handled as MultipartFile in the controller
}
