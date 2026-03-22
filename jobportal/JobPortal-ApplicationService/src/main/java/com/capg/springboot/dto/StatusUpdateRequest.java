package com.capg.springboot.dto;

import com.capg.springboot.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateRequest {

    @NotNull(message = "New status is required")
    private ApplicationStatus newStatus;

    private String recruiterNote; // optional internal note
}
