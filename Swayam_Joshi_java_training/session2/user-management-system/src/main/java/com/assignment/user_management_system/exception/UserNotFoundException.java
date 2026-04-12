package com.assignment.user_management_system.exception;

// Custom exception specifically for "Not Found" scenarios
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}