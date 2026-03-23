package com.capg.springboot.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// Every error from this service returns this exact shape
// Example: { "status": 409, "error": "Conflict", "message": "Already applied", "timestamp": "..." }

@Getter
@Setter
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;

    // Simple constructor to build error response easily
    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

}
