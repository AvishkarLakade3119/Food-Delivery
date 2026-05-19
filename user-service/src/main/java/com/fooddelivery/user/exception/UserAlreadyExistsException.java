package com.fooddelivery.user.exception;

/**
 * Custom exception thrown when attempting to register a user with an email or username that already exists.
 * This exception should be mapped to HTTP 409 Conflict status.
 */
public class UserAlreadyExistsException extends RuntimeException {
    
    private final String field;
    private final String value;
    
    public UserAlreadyExistsException(String message) {
        super(message);
        this.field = "email";
        this.value = null;
    }
    
    public UserAlreadyExistsException(String field, String value) {
        super(String.format("User with %s '%s' already exists", field, value));
        this.field = field;
        this.value = value;
    }
    
    public UserAlreadyExistsException(String field, String value, String message) {
        super(message);
        this.field = field;
        this.value = value;
    }
    
    public String getField() {
        return field;
    }
    
    public String getValue() {
        return value;
    }
}