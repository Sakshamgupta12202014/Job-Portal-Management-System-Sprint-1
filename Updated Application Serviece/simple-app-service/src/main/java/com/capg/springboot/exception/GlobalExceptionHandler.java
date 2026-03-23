package com.capg.springboot.exception;

import com.capg.springboot.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

// @RestControllerAdvice means this class handles exceptions from ALL controllers
// Without this, each controller would need its own try-catch blocks everywhere
// Instead, exceptions bubble up and land here automatically

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles: application not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(404, "Not Found", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Handles: recruiter accessing someone else's job/application
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex) {
        ErrorResponse error = new ErrorResponse(403, "Forbidden", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    // Handles: applying to the same job twice
    @ExceptionHandler(DuplicateApplicationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateApplicationException ex) {
        ErrorResponse error = new ErrorResponse(409, "Conflict", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // Handles: invalid status change like REJECTED -> SHORTLISTED
    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransition(InvalidStatusTransitionException ex) {
        ErrorResponse error = new ErrorResponse(400, "Bad Request", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Handles: file upload is bigger than 5MB
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleFileTooLarge(MaxUploadSizeExceededException ex) {
        ErrorResponse error = new ErrorResponse(413, "Payload Too Large",
                "Resume file must be less than 5MB");
        return new ResponseEntity<>(error, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    // Handles: bad input like missing required fields
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadInput(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(400, "Bad Request", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Handles: @Valid annotation failures like @NotNull, @NotBlank etc
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {

        // Collect all field error messages into one string
        String allErrors = "";
        for (int i = 0; i < ex.getBindingResult().getFieldErrors().size(); i++) {
            if (i > 0) {
                allErrors = allErrors + ", ";
            }
            allErrors = allErrors + ex.getBindingResult().getFieldErrors().get(i).getDefaultMessage();
        }

        ErrorResponse error = new ErrorResponse(400, "Bad Request", allErrors);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Handles: any other unexpected error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        // In production you would log ex.printStackTrace() here
        ErrorResponse error = new ErrorResponse(500, "Internal Server Error",
                "Something went wrong. Please try again.");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
