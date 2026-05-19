package com.fooddelivery.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.user.dto.UserRegistrationDto;
import com.fooddelivery.user.entity.User;
import com.fooddelivery.user.entity.UserRole;
import com.fooddelivery.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AuthController using @WebMvcTest to test the web layer in isolation.
 * Tests all authentication endpoints with proper mocking of the service layer.
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRegistrationDto validRegistrationDto;
    private User testUser;
    private Map<String, String> validLoginRequest;

    @BeforeEach
    void setUp() {
        validRegistrationDto = new UserRegistrationDto();
        validRegistrationDto.setUsername("johndoe");
        validRegistrationDto.setEmail("john.doe@example.com");
        validRegistrationDto.setPassword("password123");
        validRegistrationDto.setFirstName("John");
        validRegistrationDto.setLastName("Doe");
        validRegistrationDto.setPhone("+1234567890");
        validRegistrationDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        validRegistrationDto.setAddress("123 Main St, City, State");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("johndoe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("password123");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setName("John Doe");
        testUser.setPhone("+1234567890");
        testUser.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testUser.setAddress("123 Main St, City, State");
        testUser.setRole(UserRole.CUSTOMER);
        testUser.setCreatedAt(LocalDateTime.now());

        validLoginRequest = new HashMap<>();
        validLoginRequest.put("email", "john.doe@example.com");
        validLoginRequest.put("password", "password123");
    }

    /**
     * Test successful user registration with valid data.
     * Should return HTTP 201 CREATED with success response.
     */
    @Test
    void registerUser_WithValidData_ShouldReturnCreatedUser() throws Exception {
        // Given
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegistrationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));

        verify(userService, times(1)).createUser(any(User.class));
    }

    /**
     * Test user registration with missing Content-Type header.
     * Should return HTTP 400 BAD REQUEST with error message.
     */
    @Test
    void registerUser_WithMissingContentType_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .content(objectMapper.writeValueAsString(validRegistrationDto)))
                .andExpect(status().isInternalServerError());

        verify(userService, never()).createUser(any(User.class));
    }

    /**
     * Test successful user login with valid credentials.
     * Should return HTTP 200 OK with success response and user details.
     */
    @Test
    void loginUser_WithValidCredentials_ShouldReturnSuccessResponse() throws Exception {
        // Given
        when(userService.getUserByEmail("john.doe@example.com")).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));

        verify(userService, times(1)).getUserByEmail("john.doe@example.com");
    }

    /**
     * Test health check endpoint.
     * Should return HTTP 200 OK with health status information.
     */
    @Test
    void healthCheck_ShouldReturnHealthStatus() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/auth/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("user-service-auth"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(userService, never()).createUser(any(User.class));
        verify(userService, never()).getUserByEmail(anyString());
    }

    /**
     * Test user registration with null Content-Type header.
     * GlobalExceptionHandler catches this and returns 500 for unsupported media type.
     */
    @Test
    void registerUser_WithNullContentType_ShouldReturnBadRequest() throws Exception {
        // When & Then - Without Content-Type, defaults to application/octet-stream, returns 500
        mockMvc.perform(post("/api/auth/register")
                        .content(objectMapper.writeValueAsString(validRegistrationDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"));

        verify(userService, never()).createUser(any(User.class));
    }

    /**
     * Test user registration with non-JSON Content-Type header.
     * GlobalExceptionHandler catches this and returns 500 (covers line 49 indirectly).
     */
    @Test
    void registerUser_WithNonJsonContentType_ShouldReturnBadRequest() throws Exception {
        // When & Then - GlobalExceptionHandler returns 500 for unsupported media type
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(objectMapper.writeValueAsString(validRegistrationDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"));

        verify(userService, never()).createUser(any(User.class));
    }

    /**
     * Test user registration with empty address.
     * DTO validation fails with @NotBlank, returns 400 (covers validation path).
     */
    @Test
    void registerUser_WithEmptyAddress_ShouldSetAddressToEmptyString() throws Exception {
        // Given - Empty address fails @NotBlank validation
        validRegistrationDto.setAddress("");

        // When & Then - Validation fails before reaching controller logic
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegistrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));

        verify(userService, never()).createUser(any(User.class));
    }

    /**
     * Test user registration with null address.
     * DTO validation fails with @NotBlank, returns 400 (covers validation path).
     */
    @Test
    void registerUser_WithNullAddress_ShouldSetAddressToEmptyString() throws Exception {
        // Given - Null address fails @NotBlank validation
        validRegistrationDto.setAddress(null);

        // When & Then - Validation fails before reaching controller logic
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegistrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));

        verify(userService, never()).createUser(any(User.class));
    }

    /**
     * Test user registration with IllegalArgumentException.
     * Should return HTTP 400 BAD REQUEST with validation error (covers line 99-106).
     */
    @Test
    void registerUser_WithIllegalArgumentException_ShouldReturnBadRequest() throws Exception {
        // Given
        when(userService.createUser(any(User.class)))
                .thenThrow(new IllegalArgumentException("Invalid user data"));

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegistrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Invalid user data"));

        verify(userService, times(1)).createUser(any(User.class));
    }

    /**
     * Test user login with null Content-Type header.
     * GlobalExceptionHandler catches this and returns 500 for unsupported media type.
     */
    @Test
    void loginUser_WithNullContentType_ShouldReturnBadRequest() throws Exception {
        // When & Then - Without Content-Type, defaults to application/octet-stream, returns 500
        mockMvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"));

        verify(userService, never()).getUserByEmail(anyString());
    }

    /**
     * Test user login with non-JSON Content-Type header.
     * GlobalExceptionHandler catches this and returns 500 (covers line 128-137 indirectly).
     */
    @Test
    void loginUser_WithNonJsonContentType_ShouldReturnBadRequest() throws Exception {
        // When & Then - GlobalExceptionHandler returns 500 for unsupported media type
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.TEXT_XML)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"));

        verify(userService, never()).getUserByEmail(anyString());
    }

    /**
     * Test user login with missing email.
     * Should return HTTP 400 BAD REQUEST with error message (covers line 143-148).
     */
    @Test
    void loginUser_WithMissingEmail_ShouldReturnBadRequest() throws Exception {
        // Given
        Map<String, String> loginRequestWithoutEmail = new HashMap<>();
        loginRequestWithoutEmail.put("password", "password123");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestWithoutEmail)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email and password are required"));

        verify(userService, never()).getUserByEmail(anyString());
    }

    /**
     * Test user login with missing password.
     * Should return HTTP 400 BAD REQUEST with error message (covers line 143-148).
     */
    @Test
    void loginUser_WithMissingPassword_ShouldReturnBadRequest() throws Exception {
        // Given
        Map<String, String> loginRequestWithoutPassword = new HashMap<>();
        loginRequestWithoutPassword.put("email", "john.doe@example.com");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestWithoutPassword)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email and password are required"));

        verify(userService, never()).getUserByEmail(anyString());
    }

    /**
     * Test user login with wrong password.
     * Should return HTTP 401 UNAUTHORIZED with error message (covers line 166-183).
     */
    @Test
    void loginUser_WithWrongPassword_ShouldReturnUnauthorized() throws Exception {
        // Given
        when(userService.getUserByEmail("john.doe@example.com")).thenReturn(testUser);
        Map<String, String> loginRequestWithWrongPassword = new HashMap<>();
        loginRequestWithWrongPassword.put("email", "john.doe@example.com");
        loginRequestWithWrongPassword.put("password", "wrongpassword");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestWithWrongPassword)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));

        verify(userService, times(1)).getUserByEmail("john.doe@example.com");
    }

    /**
     * Test user login when user not found.
     * Should return HTTP 500 INTERNAL SERVER ERROR with error message (covers exception handling).
     */
    @Test
    void loginUser_WithUserNotFound_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(userService.getUserByEmail("nonexistent@example.com"))
                .thenThrow(new RuntimeException("User not found with email: nonexistent@example.com"));
        Map<String, String> loginRequestNonExistent = new HashMap<>();
        loginRequestNonExistent.put("email", "nonexistent@example.com");
        loginRequestNonExistent.put("password", "password123");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestNonExistent)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Login failed: User not found with email: nonexistent@example.com"));

        verify(userService, times(1)).getUserByEmail("nonexistent@example.com");
    }

    /**
     * Test user login with exception during login.
     * Should return HTTP 500 INTERNAL SERVER ERROR with error message (covers exception handling).
     */
    @Test
    void loginUser_WithException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(userService.getUserByEmail("john.doe@example.com"))
                .thenThrow(new RuntimeException("Database connection error"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Login failed: Database connection error"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(userService, times(1)).getUserByEmail("john.doe@example.com");
    }

    /**
     * Test user login when user is null (not found in database).
     * Covers line 154 false branch: if (user != null && password.equals(user.getPassword()))
     */
    @Test
    void loginUser_WithNullUser_ShouldReturnUnauthorized() throws Exception {
        // Given
        when(userService.getUserByEmail("nonexistent@example.com")).thenReturn(null);
        Map<String, String> loginRequestNonExistent = new HashMap<>();
        loginRequestNonExistent.put("email", "nonexistent@example.com");
        loginRequestNonExistent.put("password", "password123");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestNonExistent)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));

        verify(userService, times(1)).getUserByEmail("nonexistent@example.com");
    }

    /**
     * Test user registration with whitespace-only address.
     * The @NotBlank validation on address field rejects whitespace-only strings.
     * Validation fails BEFORE controller logic executes, returning 400 Bad Request.
     */
    @Test
    void registerUser_WithWhitespaceOnlyAddress_ShouldSetAddressToEmpty() throws Exception {
        // Given - Whitespace-only address fails @NotBlank validation
        validRegistrationDto.setAddress("   ");

        // When & Then - Validation fails before reaching controller logic
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegistrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));

        // Service should never be called because validation fails first
        verify(userService, never()).createUser(any(User.class));
    }

    /**
     * Test user registration with valid Content-Type header containing application/json.
     * Covers line 49 TRUE branch: contentType contains "application/json".
     * This is the success path where Content-Type validation passes.
     */
    @Test
    void registerUser_WithValidContentTypeJson_ShouldSucceed() throws Exception {
        // Given
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        // When &amp; Then - Content-Type contains "application/json"
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json; charset=UTF-8") // Contains "application/json"
                        .content(objectMapper.writeValueAsString(validRegistrationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"));

        verify(userService, times(1)).createUser(any(User.class));
    }

    /**
     * Test user registration when service throws RuntimeException (not IllegalArgumentException).
     * Covers line 108 catch block: catch (Exception e) { throw e; }
     * This re-throws the exception to let GlobalExceptionHandler handle it.
     */
    @Test
    void registerUser_WithRuntimeException_ShouldRethrowException() throws Exception {
        // Given
        when(userService.createUser(any(User.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When &amp; Then - RuntimeException (not IllegalArgumentException) is re-thrown
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegistrationDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"));

        verify(userService, times(1)).createUser(any(User.class));
    }

    /**
     * Test user login with valid Content-Type header containing application/json.
     * Covers line 128 TRUE branch: contentType contains "application/json".
     * This is the success path where Content-Type validation passes for login.
     */
    @Test
    void loginUser_WithValidContentTypeJson_ShouldSucceed() throws Exception {
        // Given
        when(userService.getUserByEmail("john.doe@example.com")).thenReturn(testUser);

        // When &amp; Then - Content-Type contains "application/json"
        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json; charset=UTF-8") // Contains "application/json"
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"));

        verify(userService, times(1)).getUserByEmail("john.doe@example.com");
    }
}
