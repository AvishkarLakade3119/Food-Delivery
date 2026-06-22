package com.fooddelivery.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.user.dto.UserRegistrationDto;
import com.fooddelivery.user.entity.User;
import com.fooddelivery.user.entity.UserRole;
import com.fooddelivery.user.security.JwtAuthenticationFilter;
import com.fooddelivery.user.security.JwtTokenProvider;
import com.fooddelivery.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserService userService;

        @MockBean
        private JwtTokenProvider jwtTokenProvider;

        @MockBean
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @MockBean
        private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

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
                testUser.setPassword("$2a$10$encodedPasswordHash");
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

        @Test
        void registerUser_WithValidData_ShouldReturnCreatedUser() throws Exception {
                when(userService.createUser(any(User.class))).thenReturn(testUser);
                when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRegistrationDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.username").value("johndoe"))
                                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                                .andExpect(jsonPath("$.firstName").value("John"))
                                .andExpect(jsonPath("$.lastName").value("Doe"))
                                .andExpect(jsonPath("$.role").value("CUSTOMER"));

                verify(userService, times(1)).createUser(any(User.class));
        }

        @Test
        void registerUser_WithMissingContentType_ShouldReturnBadRequest() throws Exception {
                mockMvc.perform(post("/api/auth/register")
                                .content(objectMapper.writeValueAsString(validRegistrationDto)))
                                .andExpect(status().isInternalServerError());

                verify(userService, never()).createUser(any(User.class));
        }

        @Test
        void loginUser_WithValidCredentials_ShouldReturnSuccessResponse() throws Exception {
                testUser.setPassword(passwordEncoder.encode("password123"));
                when(userService.getUserByEmail("john.doe@example.com")).thenReturn(testUser);
                when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
                when(jwtTokenProvider.generateToken(anyString(), anyString(), anyLong())).thenReturn("mock-jwt-token");

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validLoginRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                                .andExpect(jsonPath("$.userId").value(1L))
                                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                                .andExpect(jsonPath("$.name").value("John Doe"))
                                .andExpect(jsonPath("$.role").value("CUSTOMER"));

                verify(userService, times(1)).getUserByEmail("john.doe@example.com");
        }

        @Test
        void healthCheck_ShouldReturnHealthStatus() throws Exception {
                mockMvc.perform(get("/api/auth/health")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("UP"))
                                .andExpect(jsonPath("$.service").value("user-service-auth"))
                                .andExpect(jsonPath("$.timestamp").exists());

                verify(userService, never()).createUser(any(User.class));
                verify(userService, never()).getUserByEmail(anyString());
        }

        @Test
        void registerUser_WithNullContentType_ShouldReturnBadRequest() throws Exception {
                mockMvc.perform(post("/api/auth/register")
                                .content(objectMapper.writeValueAsString(validRegistrationDto)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.error").value("Internal Server Error"));

                verify(userService, never()).createUser(any(User.class));
        }

        @Test
        void registerUser_WithNonJsonContentType_ShouldReturnBadRequest() throws Exception {
                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.TEXT_PLAIN)
                                .content(objectMapper.writeValueAsString(validRegistrationDto)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.error").value("Internal Server Error"));

                verify(userService, never()).createUser(any(User.class));
        }

        @Test
        void registerUser_WithEmptyAddress_ShouldSetAddressToEmptyString() throws Exception {
                validRegistrationDto.setAddress("");

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRegistrationDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Validation Failed"));

                verify(userService, never()).createUser(any(User.class));
        }

        @Test
        void registerUser_WithNullAddress_ShouldSetAddressToEmptyString() throws Exception {
                validRegistrationDto.setAddress(null);

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRegistrationDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Validation Failed"));

                verify(userService, never()).createUser(any(User.class));
        }

        @Test
        void registerUser_WithIllegalArgumentException_ShouldReturnBadRequest() throws Exception {
                when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
                when(userService.createUser(any(User.class)))
                                .thenThrow(new IllegalArgumentException("Invalid user data"));

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRegistrationDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").exists())
                                .andExpect(jsonPath("$.message").value("Invalid user data"));

                verify(userService, times(1)).createUser(any(User.class));
        }

        @Test
        void loginUser_WithNullContentType_ShouldReturnBadRequest() throws Exception {
                mockMvc.perform(post("/api/auth/login")
                                .content(objectMapper.writeValueAsString(validLoginRequest)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.error").value("Internal Server Error"));

                verify(userService, never()).getUserByEmail(anyString());
        }

        @Test
        void loginUser_WithNonJsonContentType_ShouldReturnBadRequest() throws Exception {
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.TEXT_XML)
                                .content(objectMapper.writeValueAsString(validLoginRequest)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.error").value("Internal Server Error"));

                verify(userService, never()).getUserByEmail(anyString());
        }

        @Test
        void loginUser_WithMissingEmail_ShouldReturnBadRequest() throws Exception {
                Map<String, String> loginRequestWithoutEmail = new HashMap<>();
                loginRequestWithoutEmail.put("password", "password123");

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequestWithoutEmail)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Validation Failed"));

                verify(userService, never()).getUserByEmail(anyString());
        }

        @Test
        void loginUser_WithMissingPassword_ShouldReturnBadRequest() throws Exception {
                Map<String, String> loginRequestWithoutPassword = new HashMap<>();
                loginRequestWithoutPassword.put("email", "john.doe@example.com");

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequestWithoutPassword)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Validation Failed"));

                verify(userService, never()).getUserByEmail(anyString());
        }

        @Test
        void loginUser_WithWrongPassword_ShouldReturnUnauthorized() throws Exception {
                testUser.setPassword(passwordEncoder.encode("password123"));
                when(userService.getUserByEmail("john.doe@example.com")).thenReturn(testUser);
                when(passwordEncoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);
                Map<String, String> loginRequestWithWrongPassword = new HashMap<>();
                loginRequestWithWrongPassword.put("email", "john.doe@example.com");
                loginRequestWithWrongPassword.put("password", "wrongpassword");

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequestWithWrongPassword)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Invalid email or password"));

                verify(userService, times(1)).getUserByEmail("john.doe@example.com");
        }

        @Test
        void loginUser_WithUserNotFound_ShouldReturnUnauthorized() throws Exception {
                when(userService.getUserByEmail("nonexistent@example.com"))
                                .thenThrow(new RuntimeException("User not found with email: nonexistent@example.com"));
                Map<String, String> loginRequestNonExistent = new HashMap<>();
                loginRequestNonExistent.put("email", "nonexistent@example.com");
                loginRequestNonExistent.put("password", "password123");

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequestNonExistent)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Invalid email or password"));

                verify(userService, times(1)).getUserByEmail("nonexistent@example.com");
        }

        @Test
        void loginUser_WithException_ShouldReturnUnauthorized() throws Exception {
                when(userService.getUserByEmail("john.doe@example.com"))
                                .thenThrow(new RuntimeException("Database connection error"));

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validLoginRequest)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Invalid email or password"));

                verify(userService, times(1)).getUserByEmail("john.doe@example.com");
        }

        @Test
        void loginUser_WithNullUser_ShouldReturnUnauthorized() throws Exception {
                when(userService.getUserByEmail("nonexistent@example.com")).thenReturn(null);
                when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
                Map<String, String> loginRequestNonExistent = new HashMap<>();
                loginRequestNonExistent.put("email", "nonexistent@example.com");
                loginRequestNonExistent.put("password", "password123");

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequestNonExistent)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Invalid email or password"));

                verify(userService, times(1)).getUserByEmail("nonexistent@example.com");
        }

        @Test
        void registerUser_WithWhitespaceOnlyAddress_ShouldSetAddressToEmpty() throws Exception {
                validRegistrationDto.setAddress("   ");

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRegistrationDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Validation Failed"));

                verify(userService, never()).createUser(any(User.class));
        }

        @Test
        void registerUser_WithValidContentTypeJson_ShouldSucceed() throws Exception {
                when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
                when(userService.createUser(any(User.class))).thenReturn(testUser);

                mockMvc.perform(post("/api/auth/register")
                                .contentType("application/json; charset=UTF-8")
                                .content(objectMapper.writeValueAsString(validRegistrationDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.username").value("johndoe"));

                verify(userService, times(1)).createUser(any(User.class));
        }

        @Test
        void registerUser_WithRuntimeException_ShouldRethrowException() throws Exception {
                when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
                when(userService.createUser(any(User.class)))
                                .thenThrow(new RuntimeException("Database connection failed"));

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRegistrationDto)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.error").value("Internal Server Error"));

                verify(userService, times(1)).createUser(any(User.class));
        }

        @Test
        void loginUser_WithValidContentTypeJson_ShouldSucceed() throws Exception {
                testUser.setPassword(passwordEncoder.encode("password123"));
                when(userService.getUserByEmail("john.doe@example.com")).thenReturn(testUser);
                when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
                when(jwtTokenProvider.generateToken(anyString(), anyString(), anyLong())).thenReturn("mock-jwt-token");

                mockMvc.perform(post("/api/auth/login")
                                .contentType("application/json; charset=UTF-8")
                                .content(objectMapper.writeValueAsString(validLoginRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

                verify(userService, times(1)).getUserByEmail("john.doe@example.com");
        }
}