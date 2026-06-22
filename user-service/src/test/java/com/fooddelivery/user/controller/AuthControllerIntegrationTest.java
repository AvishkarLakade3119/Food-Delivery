package com.fooddelivery.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.user.dto.AddressDto;
import com.fooddelivery.user.dto.UserRegistrationDto;
import com.fooddelivery.user.entity.User;
import com.fooddelivery.user.entity.UserRole;
import com.fooddelivery.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private UserRegistrationDto validRegistrationDto;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        String address = "123 Main Street, New York, NY 10001, USA";

        validRegistrationDto = new UserRegistrationDto();
        validRegistrationDto.setUsername("john_doe");
        validRegistrationDto.setEmail("john.doe@example.com");
        validRegistrationDto.setPassword("SecurePass123!");
        validRegistrationDto.setFirstName("John");
        validRegistrationDto.setLastName("Doe");
        validRegistrationDto.setPhone("+1234567890");
        validRegistrationDto.setAddress(address);
        validRegistrationDto.setDateOfBirth(LocalDate.of(1990, 5, 15));
    }

    @Test
    void registerUser_Success_Returns201() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegistrationDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void registerUser_DuplicateEmail_Returns409() throws Exception {
        // First registration - should succeed
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegistrationDto)))
                .andExpect(status().isCreated());

        // Second registration with same email - should return 409
        UserRegistrationDto duplicateEmailDto = new UserRegistrationDto();
        duplicateEmailDto.setUsername("different_username");
        duplicateEmailDto.setEmail("john.doe@example.com"); // Same email
        duplicateEmailDto.setPassword("AnotherPass123!");
        duplicateEmailDto.setFirstName("Jane");
        duplicateEmailDto.setLastName("Smith");
        duplicateEmailDto.setPhone("+1987654321");
        duplicateEmailDto.setAddress(validRegistrationDto.getAddress());
        duplicateEmailDto.setDateOfBirth(LocalDate.of(1985, 8, 22));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateEmailDto)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("User with email 'john.doe@example.com' already exists"))
                .andExpect(jsonPath("$.field").value("email"))
                .andExpect(jsonPath("$.value").value("john.doe@example.com"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void registerUser_DuplicateUsername_Returns409() throws Exception {
        // First registration - should succeed
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegistrationDto)))
                .andExpect(status().isCreated());

        // Second registration with same username - should return 409
        UserRegistrationDto duplicateUsernameDto = new UserRegistrationDto();
        duplicateUsernameDto.setUsername("john_doe"); // Same username
        duplicateUsernameDto.setEmail("different.email@example.com");
        duplicateUsernameDto.setPassword("AnotherPass123!");
        duplicateUsernameDto.setFirstName("Jane");
        duplicateUsernameDto.setLastName("Smith");
        duplicateUsernameDto.setPhone("+1987654321");
        duplicateUsernameDto.setAddress(validRegistrationDto.getAddress());
        duplicateUsernameDto.setDateOfBirth(LocalDate.of(1985, 8, 22));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateUsernameDto)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("User with username 'john_doe' already exists"))
                .andExpect(jsonPath("$.field").value("username"))
                .andExpect(jsonPath("$.value").value("john_doe"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void registerUser_MissingRequiredFields_Returns400() throws Exception {
        UserRegistrationDto invalidDto = new UserRegistrationDto();
        // Missing required fields

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    void registerUser_InvalidEmailFormat_Returns400() throws Exception {
        validRegistrationDto.setEmail("invalid-email-format");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegistrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors.email").exists());
    }

    @Test
    void registerUser_PasswordTooShort_Returns400() throws Exception {
        validRegistrationDto.setPassword("123"); // Too short

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegistrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors.password").exists());
    }

    @Test
    void registerUser_MissingContentType_Returns500() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .content(objectMapper.writeValueAsString(validRegistrationDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
    }
}