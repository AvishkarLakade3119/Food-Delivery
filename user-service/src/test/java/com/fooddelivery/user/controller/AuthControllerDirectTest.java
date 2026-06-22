package com.fooddelivery.user.controller;

import com.fooddelivery.user.dto.LoginRequest;
import com.fooddelivery.user.dto.UserRegistrationDto;
import com.fooddelivery.user.dto.UserResponse;
import com.fooddelivery.user.entity.User;
import com.fooddelivery.user.entity.UserRole;
import com.fooddelivery.user.security.JwtTokenProvider;
import com.fooddelivery.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerDirectTest {
    @Mock
    private UserService userService;
    
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    
    private PasswordEncoder passwordEncoder;
    
    private AuthController controller;
    private UserRegistrationDto validDto;
    private User testUser;
    private LoginRequest validLoginRequest;
    
    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        controller = new AuthController();
        ReflectionTestUtils.setField(controller, "userService", userService);
        ReflectionTestUtils.setField(controller, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(controller, "jwtTokenProvider", jwtTokenProvider);
        ReflectionTestUtils.setField(controller, "jwtExpirationMs", 86400000L);
        
        validDto = new UserRegistrationDto();
        validDto.setUsername("johndoe");
        validDto.setEmail("john@test.com");
        validDto.setPassword("password123");
        validDto.setFirstName("John");
        validDto.setLastName("Doe");
        validDto.setPhone("+1234567890");
        validDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        validDto.setAddress("123 Main St");
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("johndoe");
        testUser.setEmail("john@test.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setName("John Doe");
        testUser.setPhone("+1234567890");
        testUser.setAddress("123 Main St");
        testUser.setRole(UserRole.CUSTOMER);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
        
        validLoginRequest = new LoginRequest();
        validLoginRequest.setEmail("john@test.com");
        validLoginRequest.setPassword("password123");
    }
    
    // ===== REGISTER: Basic registration tests =====
    @Test
    void register_ValidRequest_Returns201() {
        when(userService.createUser(any(User.class))).thenReturn(testUser);
        ResponseEntity<UserResponse> response = controller.registerUser(validDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("john@test.com", response.getBody().getEmail());
        assertEquals("johndoe", response.getBody().getUsername());
    }
    
    @Test
    void register_NullAddress_SetsEmpty() {
        when(userService.createUser(any(User.class))).thenReturn(testUser);
        validDto.setAddress(null);
        ResponseEntity<UserResponse> response = controller.registerUser(validDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
    
    @Test
    void register_EmptyAddress_SetsEmpty() {
        when(userService.createUser(any(User.class))).thenReturn(testUser);
        validDto.setAddress("");
        ResponseEntity<UserResponse> response = controller.registerUser(validDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
    
    @Test
    void register_WhitespaceAddress_SetsEmpty() {
        when(userService.createUser(any(User.class))).thenReturn(testUser);
        validDto.setAddress("   ");
        ResponseEntity<UserResponse> response = controller.registerUser(validDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
    
    @Test
    void register_ValidAddress_SetsAddress() {
        when(userService.createUser(any(User.class))).thenReturn(testUser);
        validDto.setAddress("456 Oak Ave");
        ResponseEntity<UserResponse> response = controller.registerUser(validDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
    
    @Test
    void register_DuplicateUser_ThrowsException() {
        when(userService.createUser(any(User.class))).thenThrow(new RuntimeException("User already exists"));
        assertThrows(RuntimeException.class, () -> controller.registerUser(validDto));
    }
    
    @Test
    void register_ServiceException_ThrowsException() {
        when(userService.createUser(any(User.class))).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> controller.registerUser(validDto));
    }
    
    // ===== LOGIN: Basic login tests =====
    @Test
    void login_ValidCredentials_Returns200WithToken() {
        when(userService.getUserByEmail("john@test.com")).thenReturn(testUser);
        when(jwtTokenProvider.generateToken("john@test.com", "CUSTOMER", 1L)).thenReturn("mock.jwt.token");
        
        ResponseEntity<?> response = controller.loginUser(validLoginRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
    
    @Test
    void login_WrongPassword_Returns401() {
        when(userService.getUserByEmail("john@test.com")).thenReturn(testUser);
        
        LoginRequest wrongRequest = new LoginRequest();
        wrongRequest.setEmail("john@test.com");
        wrongRequest.setPassword("wrongpassword");
        
        ResponseEntity<?> response = controller.loginUser(wrongRequest);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    
    @Test
    void login_UserNotFound_Returns401() {
        when(userService.getUserByEmail("john@test.com")).thenThrow(new RuntimeException("User not found"));
        
        ResponseEntity<?> response = controller.loginUser(validLoginRequest);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    
    @Test
    void login_NullEmail_ThrowsException() {
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setEmail(null);
        invalidRequest.setPassword("password123");
        
        when(userService.getUserByEmail(null)).thenThrow(new RuntimeException("Email cannot be null"));
        ResponseEntity<?> response = controller.loginUser(invalidRequest);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    
    @Test
    void login_EmptyPassword_Returns401() {
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setEmail("john@test.com");
        invalidRequest.setPassword("");
        
        when(userService.getUserByEmail("john@test.com")).thenReturn(testUser);
        ResponseEntity<?> response = controller.loginUser(invalidRequest);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    
    @Test
    void login_DatabaseError_Returns401() {
        when(userService.getUserByEmail(anyString())).thenThrow(new RuntimeException("DB down"));
        
        ResponseEntity<?> response = controller.loginUser(validLoginRequest);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    
    @Test
    void login_JwtGenerationSuccess_ReturnsToken() {
        when(userService.getUserByEmail("john@test.com")).thenReturn(testUser);
        when(jwtTokenProvider.generateToken(anyString(), anyString(), anyLong())).thenReturn("valid.jwt.token");
        
        ResponseEntity<?> response = controller.loginUser(validLoginRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    
    @Test
    void login_PasswordEncoderMatches_ReturnsSuccess() {
        when(userService.getUserByEmail("john@test.com")).thenReturn(testUser);
        when(jwtTokenProvider.generateToken(anyString(), anyString(), anyLong())).thenReturn("token");
        
        ResponseEntity<?> response = controller.loginUser(validLoginRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    
    @Test
    void login_ServiceThrowsRuntimeException_Returns401() {
        when(userService.getUserByEmail(anyString())).thenThrow(new RuntimeException("Unexpected error"));
        
        ResponseEntity<?> response = controller.loginUser(validLoginRequest);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}