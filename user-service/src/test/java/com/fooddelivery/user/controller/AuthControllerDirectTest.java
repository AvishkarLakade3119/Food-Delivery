package com.fooddelivery.user.controller;

import com.fooddelivery.user.dto.UserRegistrationDto;
import com.fooddelivery.user.entity.User;
import com.fooddelivery.user.entity.UserRole;
import com.fooddelivery.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerDirectTest {
    @Mock
    private UserService userService;
    
    private AuthController controller;
    private UserRegistrationDto validDto;
    private User testUser;
    private Map<String, String> validLoginRequest;
    
    @BeforeEach
    void setUp() {
        controller = new AuthController();
        ReflectionTestUtils.setField(controller, "userService", userService);
        
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
        testUser.setPassword("password123");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setName("John Doe");
        testUser.setPhone("+1234567890");
        testUser.setAddress("123 Main St");
        testUser.setRole(UserRole.CUSTOMER);
        testUser.setCreatedAt(LocalDateTime.now());
        
        validLoginRequest = new HashMap<>();
        validLoginRequest.put("email", "john@test.com");
        validLoginRequest.put("password", "password123");
    }
    
    // ===== REGISTER: Content-Type branches (line 49) =====
    @Test
    void register_NullContentType_Returns400() {
        ResponseEntity<Map<String, Object>> response = controller.registerUser(validDto, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, response.getBody().get("success"));
    }
    
    @Test
    void register_TextPlainContentType_Returns400() {
        ResponseEntity<Map<String, Object>> response = controller.registerUser(validDto, "text/plain");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    void register_JsonContentType_Returns201() {
        when(userService.createUser(any(User.class))).thenReturn(testUser);
        ResponseEntity<Map<String, Object>> response = controller.registerUser(validDto, "application/json");
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(true, response.getBody().get("success"));
    }
    
    // ===== REGISTER: Address branches (line 73) =====
    @Test
    void register_NullAddress_SetsEmpty() {
        when(userService.createUser(any(User.class))).thenReturn(testUser);
        validDto.setAddress(null);
        ResponseEntity<Map<String, Object>> response = controller.registerUser(validDto, "application/json");
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
    
    @Test
    void register_EmptyAddress_SetsEmpty() {
        when(userService.createUser(any(User.class))).thenReturn(testUser);
        validDto.setAddress("");
        ResponseEntity<Map<String, Object>> response = controller.registerUser(validDto, "application/json");
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
    
    @Test
    void register_WhitespaceAddress_SetsEmpty() {
        when(userService.createUser(any(User.class))).thenReturn(testUser);
        validDto.setAddress("   ");
        ResponseEntity<Map<String, Object>> response = controller.registerUser(validDto, "application/json");
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
    
    @Test
    void register_ValidAddress_SetsAddress() {
        when(userService.createUser(any(User.class))).thenReturn(testUser);
        validDto.setAddress("456 Oak Ave");
        ResponseEntity<Map<String, Object>> response = controller.registerUser(validDto, "application/json");
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
    
    // ===== REGISTER: Exception branches (lines 99, 108) =====
    @Test
    void register_IllegalArgException_Returns400() {
        when(userService.createUser(any(User.class))).thenThrow(new IllegalArgumentException("bad"));
        ResponseEntity<Map<String, Object>> response = controller.registerUser(validDto, "application/json");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, response.getBody().get("success"));
    }
    
    @Test
    void register_RuntimeException_Rethrows() {
        when(userService.createUser(any(User.class))).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> controller.registerUser(validDto, "application/json"));
    }
    
    // ===== LOGIN: Content-Type branches (line 128) =====
    @Test
    void login_NullContentType_Returns400() {
        ResponseEntity<Map<String, Object>> response = controller.loginUser(validLoginRequest, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, response.getBody().get("success"));
    }
    
    @Test
    void login_TextPlainContentType_Returns400() {
        ResponseEntity<Map<String, Object>> response = controller.loginUser(validLoginRequest, "text/plain");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    void login_JsonContentType_Returns200() {
        when(userService.getUserByEmail("john@test.com")).thenReturn(testUser);
        ResponseEntity<Map<String, Object>> response = controller.loginUser(validLoginRequest, "application/json");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().get("success"));
    }
    
    // ===== LOGIN: Null email/password branches (line 143) =====
    @Test
    void login_NullEmail_Returns400() {
        Map<String, String> request = new HashMap<>();
        request.put("email", null);
        request.put("password", "pass");
        ResponseEntity<Map<String, Object>> response = controller.loginUser(request, "application/json");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    void login_NullPassword_Returns400() {
        Map<String, String> request = new HashMap<>();
        request.put("email", "john@test.com");
        request.put("password", null);
        ResponseEntity<Map<String, Object>> response = controller.loginUser(request, "application/json");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    void login_BothNull_Returns400() {
        Map<String, String> request = new HashMap<>();
        request.put("email", null);
        request.put("password", null);
        ResponseEntity<Map<String, Object>> response = controller.loginUser(request, "application/json");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    // ===== LOGIN: Password check branches (line 154) =====
    @Test
    void login_CorrectPassword_Returns200() {
        when(userService.getUserByEmail("john@test.com")).thenReturn(testUser);
        ResponseEntity<Map<String, Object>> response = controller.loginUser(validLoginRequest, "application/json");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().get("success"));
    }
    
    @Test
    void login_WrongPassword_Returns401() {
        User user = new User();
        user.setPassword("differentPassword");
        when(userService.getUserByEmail("john@test.com")).thenReturn(user);
        ResponseEntity<Map<String, Object>> response = controller.loginUser(validLoginRequest, "application/json");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    
    @Test
    void login_NullUser_Returns401() {
        when(userService.getUserByEmail("john@test.com")).thenReturn(null);
        ResponseEntity<Map<String, Object>> response = controller.loginUser(validLoginRequest, "application/json");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    
    // ===== LOGIN: Exception branch (line 173) =====
    @Test
    void login_ServiceException_Returns500() {
        when(userService.getUserByEmail(anyString())).thenThrow(new RuntimeException("DB down"));
        ResponseEntity<Map<String, Object>> response = controller.loginUser(validLoginRequest, "application/json");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(false, response.getBody().get("success"));
    }
}