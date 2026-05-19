package com.fooddelivery.restaurant.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, WebRequest request, HttpServletRequest httpRequest) {

        // Check HTTP method - GET and DELETE should not have request bodies
        String method = httpRequest.getMethod();
        String requestURI = request.getDescription(false);
        String path = httpRequest.getRequestURI();

        // For GET and DELETE requests, don't treat as JSON parsing error
        if ("GET".equals(method) || "DELETE".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method)) {
            logger.debug("Skipping JSON parsing error for {} request to {}", method, path);
            // Let Spring Boot handle this naturally - don't interfere
            return null;
        }

        // For actuator endpoints, let Spring handle the error naturally
        if (path != null && (path.contains("/actuator/") || path.startsWith("/actuator"))) {
            logger.debug("Skipping JSON parsing error for actuator endpoint: {}", path);
            // Don't interfere with actuator endpoints
            return null;
        }

        // Only handle actual JSON parsing errors for POST/PUT/PATCH requests
        if ("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method)) {
            logger.warn("JSON parsing error for {} request to {}: {}", method, path, ex.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
            errorResponse.put("error", "Bad Request");
            errorResponse.put("message", "Invalid JSON format in request body. Please ensure the request body contains valid JSON and Content-Type is 'application/json'");
            errorResponse.put("details", "JSON parse error: " + ex.getLocalizedMessage());
            errorResponse.put("path", path != null ? path : requestURI.replace("uri=", ""));

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // For all other cases, let Spring handle naturally
        logger.debug("Letting Spring handle HttpMessageNotReadableException for {} request to {}", method, path);
        return null;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, String> validationErrors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });
        
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Validation Failed");
        errorResponse.put("message", "Input validation failed");
        errorResponse.put("validationErrors", validationErrors);
        errorResponse.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Bad Request");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.CONFLICT.value());
        errorResponse.put("error", "Conflict");
        errorResponse.put("message", "A record with this ID already exists. Do not send 'id' in POST requests.");
        errorResponse.put("details", "Database constraint violation: " + ex.getLocalizedMessage());
        errorResponse.put("path", request.getRequestURI());

        logger.warn("Data integrity violation on {}: {}", request.getRequestURI(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {

        // Check if this is a duplicate key error (common pattern in error messages)
        String message = ex.getMessage();
        if (message != null && (message.contains("already exists") || message.contains("duplicate") || message.contains("unique constraint"))) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("status", HttpStatus.CONFLICT.value());
            errorResponse.put("error", "Conflict");
            errorResponse.put("message", "A record with this information already exists.");
            errorResponse.put("details", message);
            errorResponse.put("path", request.getDescription(false).replace("uri=", ""));

            logger.warn("Duplicate record error: {}", message);
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", "An unexpected error occurred: " + ex.getMessage());
        errorResponse.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, WebRequest request) {
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", "An unexpected error occurred");
        errorResponse.put("details", ex.getMessage());
        errorResponse.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}