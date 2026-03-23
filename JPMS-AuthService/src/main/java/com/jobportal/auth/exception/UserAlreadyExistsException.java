package com.jobportal.auth.exception;

public class UserAlreadyExistsException extends RuntimeException {
	
	public UserAlreadyExistsException(String message) {
        super(message);
    }
	
}
