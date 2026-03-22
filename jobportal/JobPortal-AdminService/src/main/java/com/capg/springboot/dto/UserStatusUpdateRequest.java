package com.capg.springboot.dto;

import com.capg.springboot.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private UserStatus status; // ACTIVE | INACTIVE | BANNED
}
