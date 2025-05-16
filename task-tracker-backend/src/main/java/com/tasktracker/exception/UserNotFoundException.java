package com.tasktracker.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
// This exception is thrown when a user is not found in the system.
