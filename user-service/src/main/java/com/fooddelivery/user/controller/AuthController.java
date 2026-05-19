package com.fooddelivery.user.controller;

import com.fooddelivery.user.dto.UserRegistrationDto;
import com.fooddelivery.user.entity.User;
import com.fooddelivery.user.entity.UserRole;
import com.fooddelivery.user.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://localhost:4200",
        "http://localhost:8080",
        "http://localhost:8081",
        "http://localhost:8082",
        "http://localhost:8083",
        "http://localhost:8084",
        "http://localhost:8085",
        "http://localhost:8086"
}, allowCredentials = "true")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(
            @Valid @RequestBody UserRegistrationDto registrationDto,
            @RequestHeader(value = "Content-Type", required = false) String contentType) {
        
        logger.info("Registration request received for user: {}", registrationDto.getUsername());
        logger.debug("Content-Type header: {}", contentType);
        logger.debug("Registration DTO: {}", registrationDto);
        
        try {
            // Validate Content-Type header
            if (contentType == null || !contentType.contains("application/json")) {
                logger.warn("Invalid or missing Content-Type header: {}", contentType);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Missing or invalid Content-Type header");
                errorResponse.put("message", "Please set Content-Type header to 'application/json'");
                errorResponse.put("expectedContentType", "application/json");
                errorResponse.put("receivedContentType", contentType);
                
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            
            // Convert DTO to User entity
            User user = new User();
            user.setUsername(registrationDto.getUsername());
            user.setEmail(registrationDto.getEmail());
            user.setPassword(registrationDto.getPassword());
            user.setFirstName(registrationDto.getFirstName());
            user.setLastName(registrationDto.getLastName());
            user.setPhone(registrationDto.getPhone());
            user.setDateOfBirth(registrationDto.getDateOfBirth());
            user.setName(registrationDto.getFirstName() + " " + registrationDto.getLastName());

            // Set address directly as string
            if (registrationDto.getAddress() != null && !registrationDto.getAddress().trim().isEmpty()) {
                user.setAddress(registrationDto.getAddress());
                logger.debug("Address set: {}", user.getAddress());
            } else {
                logger.warn("Address is null or empty for user: {}", registrationDto.getUsername());
                user.setAddress("");
            }

            // Set default role
            user.setRole(UserRole.CUSTOMER);

            User registeredUser = userService.createUser(user);
            logger.info("User registered successfully with ID: {}", registeredUser.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("userId", registeredUser.getId());
            response.put("username", registeredUser.getUsername());
            response.put("email", registeredUser.getEmail());
            response.put("firstName", registeredUser.getFirstName());
            response.put("lastName", registeredUser.getLastName());
            response.put("role", registeredUser.getRole());
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error during registration: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Validation Error");
            errorResponse.put("message", e.getMessage());

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            // Let GlobalExceptionHandler handle UserAlreadyExistsException and other specific exceptions
            logger.error("Error during registration for user {}: {}",
                    registrationDto.getUsername(), e.getMessage());

            // Re-throw to let GlobalExceptionHandler handle it properly
            throw e;
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(
            @RequestBody Map<String, String> loginRequest,
            @RequestHeader(value = "Content-Type", required = false) String contentType) {
        
        logger.info("Login request received for email: {}", loginRequest.get("email"));
        logger.debug("Content-Type header: {}", contentType);
        
        try {
            // Validate Content-Type header
            if (contentType == null || !contentType.contains("application/json")) {
                logger.warn("Invalid or missing Content-Type header: {}", contentType);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Missing or invalid Content-Type header");
                errorResponse.put("message", "Please set Content-Type header to 'application/json'");
                errorResponse.put("expectedContentType", "application/json");
                errorResponse.put("receivedContentType", contentType);
                
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");
            
            if (email == null || password == null) {
                logger.warn("Missing email or password in login request");
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Email and password are required");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            
            User user = userService.getUserByEmail(email);
            
            // Simple password check (in production, use proper password encoding)
            if (user != null && password.equals(user.getPassword())) {
                logger.info("Login successful for user: {}", email);
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Login successful");
                response.put("userId", user.getId());
                response.put("username", user.getUsername());
                response.put("email", user.getEmail());
                response.put("role", user.getRole());
                
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                logger.warn("Invalid login attempt for email: {}", email);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Invalid email or password");
                
                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            logger.error("Unexpected error during login for email {}: {}", 
                        loginRequest.get("email"), e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", "Login failed: " + e.getMessage());
            errorResponse.put("timestamp", java.time.LocalDateTime.now());
            
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "user-service-auth");
        response.put("timestamp", java.time.LocalDateTime.now());
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}