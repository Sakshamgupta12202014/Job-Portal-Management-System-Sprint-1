package com.capg.springboot.dto;

import com.capg.springboot.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

// Recruiter sends this when they want to change an application status
// Example: { "newStatus": "UNDER_REVIEW", "recruiterNote": "Good profile" }

@Getter
@Setter
public class StatusUpdateRequest {

    // newStatus is required - we cannot update without knowing the new status
    @NotNull(message = "New status is required")
    private ApplicationStatus newStatus;

    // recruiterNote is optional - recruiter may or may not add a note
    private String recruiterNote;

}
