package com.fooddelivery.user.controller;

import com.fooddelivery.user.dto.LoginRequest;
import com.fooddelivery.user.dto.LoginResponse;
import com.fooddelivery.user.dto.UserRegistrationDto;
import com.fooddelivery.user.dto.UserResponse;
import com.fooddelivery.user.entity.User;
import com.fooddelivery.user.entity.UserRole;
import com.fooddelivery.user.security.JwtTokenProvider;
import com.fooddelivery.user.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        logger.info("Registration request received for user: {}", registrationDto.getUsername());
        
        // Convert DTO to User entity
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setPhone(registrationDto.getPhone());
        user.setDateOfBirth(registrationDto.getDateOfBirth());
        user.setName(registrationDto.getFirstName() + " " + registrationDto.getLastName());

        // Set address
        if (registrationDto.getAddress() != null && !registrationDto.getAddress().trim().isEmpty()) {
            user.setAddress(registrationDto.getAddress());
        } else {
            user.setAddress("");
        }

        // Set default role
        user.setRole(UserRole.CUSTOMER);

        User registeredUser = userService.createUser(user);
        logger.info("User registered successfully with ID: {}", registeredUser.getId());
        
        // Create response without password
        UserResponse response = new UserResponse(
            registeredUser.getId(),
            registeredUser.getUsername(),
            registeredUser.getEmail(),
            registeredUser.getFirstName(),
            registeredUser.getLastName(),
            registeredUser.getName(),
            registeredUser.getPhone(),
            registeredUser.getAddress(),
            registeredUser.getRole(),
            registeredUser.getCreatedAt(),
            registeredUser.getUpdatedAt()
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Login request received for email: {}", loginRequest.getEmail());
        
        try {
            User user = userService.getUserByEmail(loginRequest.getEmail());
            
            // Verify password using BCrypt
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                logger.warn("Invalid login attempt for email: {}", loginRequest.getEmail());
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Invalid email or password");
                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
            }
            
            // Generate JWT token
            String token = jwtTokenProvider.generateToken(
                user.getEmail(), 
                user.getRole().name(), 
                user.getId()
            );
            
            logger.info("Login successful for user: {}", loginRequest.getEmail());
            
            // Create login response with token
            LoginResponse response = new LoginResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                jwtExpirationMs / 1000  // Convert to seconds
            );
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (RuntimeException e) {
            logger.error("Login failed for email {}: {}", loginRequest.getEmail(), e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Invalid email or password");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
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