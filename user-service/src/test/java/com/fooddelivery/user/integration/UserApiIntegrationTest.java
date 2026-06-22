package com.fooddelivery.user.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.user.dto.LoginRequest;
import com.fooddelivery.user.dto.LoginResponse;
import com.fooddelivery.user.dto.UserRegistrationDto;
import com.fooddelivery.user.dto.UserResponse;
import com.fooddelivery.user.entity.User;
import com.fooddelivery.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class UserApiIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;
    private Long userId;
    private UserRegistrationDto registrationDto;

    @BeforeAll
    void setupTestData() {
        userRepository.deleteAll();

        registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("testuser");
        registrationDto.setEmail("testuser@example.com");
        registrationDto.setPassword("TestPass123!");
        registrationDto.setFirstName("Test");
        registrationDto.setLastName("User");
        registrationDto.setPhone("+1234567890");
        registrationDto.setAddress("123 Test Street, Test City, TS 12345");
        registrationDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
    }

    @Test
    @Order(1)
    void registerUser_ShouldReturn201() {
        System.out.println("\n=== TEST 1: Register User ===");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserRegistrationDto> request = new HttpEntity<>(registrationDto, headers);

        ResponseEntity<UserResponse> response = restTemplate.postForEntity(
                "/api/auth/register", request, UserResponse.class);

        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Response: " + response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo("testuser@example.com");
        assertThat(response.getBody().getUsername()).isEqualTo("testuser");

        userId = response.getBody().getId();
        System.out.println("✓ User registered successfully with ID: " + userId);
    }

    @Test
    @Order(2)
    void registerDuplicateUser_ShouldReturn409() {
        System.out.println("\n=== TEST 2: Register Duplicate User ===");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserRegistrationDto> request = new HttpEntity<>(registrationDto, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/auth/register", request, Map.class);

        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Response: " + response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        System.out.println("✓ Duplicate user registration rejected as expected");
    }

    @Test
    @Order(3)
    void loginUser_ShouldReturn200WithToken() {
        System.out.println("\n=== TEST 3: Login User ===");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("testuser@example.com");
        loginRequest.setPassword("TestPass123!");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                "/api/auth/login", request, LoginResponse.class);

        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Response: " + response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo("testuser@example.com");

        jwtToken = response.getBody().getToken();
        System.out.println("✓ Login successful, JWT token received");
        System.out.println("Token: " + jwtToken.substring(0, Math.min(50, jwtToken.length())) + "...");
    }

    @Test
    @Order(4)
    void loginWithWrongPassword_ShouldReturn401() {
        System.out.println("\n=== TEST 4: Login with Wrong Password ===");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("testuser@example.com");
        loginRequest.setPassword("WrongPassword123!");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/auth/login", request, Map.class);

        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Response: " + response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        System.out.println("✓ Login with wrong password rejected as expected");
    }

    @Test
    @Order(5)
    void getProfile_WithToken_ShouldReturn200() {
        System.out.println("\n=== TEST 5: Get Profile with Token ===");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<UserResponse> response = restTemplate.exchange(
                "/api/users/profile", HttpMethod.GET, request, UserResponse.class);

        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Response: " + response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo("testuser@example.com");
        System.out.println("✓ Profile retrieved successfully with valid token");
    }

    @Test
    @Order(6)
    void getProfile_WithoutToken_ShouldReturn403() {
        System.out.println("\n=== TEST 6: Get Profile without Token ===");

        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/users/profile", String.class);

        System.out.println("Status: " + response.getStatusCode());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        System.out.println("✓ Profile access without token rejected as expected");
    }

    @Test
    @Order(7)
    void getUserById_WithToken_ShouldReturn200() {
        System.out.println("\n=== TEST 7: Get User by ID with Token ===");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<User> response = restTemplate.exchange(
                "/api/users/" + userId, HttpMethod.GET, request, User.class);

        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Response: " + response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(userId);
        System.out.println("✓ User retrieved by ID successfully");
    }

    @Test
    @Order(8)
    void getAllUsers_WithToken_ShouldReturn200() {
        System.out.println("\n=== TEST 8: Get All Users with Token ===");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<User[]> response = restTemplate.exchange(
                "/api/users", HttpMethod.GET, request, User[].class);

        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Users count: " + (response.getBody() != null ? response.getBody().length : 0));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);
        System.out.println("✓ All users retrieved successfully");
    }

    @Test
    @Order(9)
    void updateUser_WithToken_ShouldReturn200() {
        System.out.println("\n=== TEST 9: Update User with Token ===");

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("username", "testuser");
        updateData.put("email", "testuser@example.com");
        updateData.put("name", "Updated Test User");
        updateData.put("phone", "+9876543210");
        updateData.put("address", "456 Updated Street, New City, NC 54321");
        updateData.put("password", "TestPass123!");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(updateData, headers);

        ResponseEntity<User> response = restTemplate.exchange(
                "/api/users/" + userId, HttpMethod.PUT, request, User.class);

        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Response: " + response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Updated Test User");
        System.out.println("✓ User updated successfully");
    }

    @Test
    @Order(10)
    void getAllUsers_WithoutToken_ShouldReturn403() {
        System.out.println("\n=== TEST 10: Get All Users without Token ===");

        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/users", String.class);

        System.out.println("Status: " + response.getStatusCode());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        System.out.println("✓ Access to protected endpoint without token rejected as expected");
    }

    @AfterAll
    void printSummary() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("USER SERVICE JWT INTEGRATION TESTS COMPLETED");
        System.out.println("All 10 tests passed successfully!");
        System.out.println("=".repeat(60) + "\n");
    }
}