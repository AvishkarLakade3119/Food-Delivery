package com.fooddelivery.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.user.entity.User;
import com.fooddelivery.user.entity.UserRole;
import com.fooddelivery.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for UserController using @SpringBootTest and Testcontainers.
 * Tests the full request-response cycle with a real PostgreSQL database.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private User testUser;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/users";
        userRepository.deleteAll();

        testUser = new User();
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPhone("+1234567890");
        testUser.setAddress("123 Main St, City, State");
        testUser.setRole(UserRole.CUSTOMER);
        testUser.setUsername("johndoe");
        testUser.setPassword("password123");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setDateOfBirth(LocalDate.of(1990, 1, 1));

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    /**
     * Test creating a user through the full API stack.
     * Should persist the user to the database and return HTTP 201 CREATED.
     */
    @Test
    void createUser_WithValidData_ShouldPersistAndReturnCreatedUser() {
        // Given
        HttpEntity<User> request = new HttpEntity<>(testUser, headers);

        // When
        ResponseEntity<User> response = restTemplate.postForEntity(baseUrl, request, User.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("John Doe");
        assertThat(response.getBody().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(response.getBody().getRole()).isEqualTo(UserRole.CUSTOMER);
        assertThat(response.getBody().getCreatedAt()).isNotNull();

        // Verify persistence
        List<User> usersInDb = userRepository.findAll();
        assertThat(usersInDb).hasSize(1);
        assertThat(usersInDb.get(0).getEmail()).isEqualTo("john.doe@example.com");
    }

    /**
     * Test creating a user with invalid data.
     * Should return HTTP 400 BAD REQUEST and not persist the user.
     */
    @Test
    void createUser_WithInvalidData_ShouldReturnBadRequestAndNotPersist() {
        // Given
        User invalidUser = new User();
        invalidUser.setEmail("invalid-email"); // Invalid email format
        // Missing required fields

        HttpEntity<User> request = new HttpEntity<>(invalidUser, headers);

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // Verify no persistence
        List<User> usersInDb = userRepository.findAll();
        assertThat(usersInDb).isEmpty();
    }

    /**
     * Test creating a user with duplicate email.
     * Should return HTTP 500 INTERNAL SERVER ERROR due to business logic exception.
     */
    @Test
    void createUser_WithDuplicateEmail_ShouldReturnInternalServerError() {
        // Given - Create first user
        userRepository.save(testUser);

        // Create second user with same email
        User duplicateUser = new User();
        duplicateUser.setName("Jane Doe");
        duplicateUser.setEmail("john.doe@example.com"); // Duplicate email
        duplicateUser.setPhone("+1987654321");
        duplicateUser.setAddress("456 Oak Ave, City, State");
        duplicateUser.setRole(UserRole.CUSTOMER);
        duplicateUser.setUsername("janedoe");
        duplicateUser.setPassword("password456");

        HttpEntity<User> request = new HttpEntity<>(duplicateUser, headers);

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        // Verify only one user exists
        List<User> usersInDb = userRepository.findAll();
        assertThat(usersInDb).hasSize(1);
    }

    /**
     * Test retrieving all users through the API.
     * Should return HTTP 200 OK with all users from the database.
     */
    @Test
    void getAllUsers_ShouldReturnAllUsersFromDatabase() {
        // Given
        User user1 = userRepository.save(testUser);
        
        User user2 = new User();
        user2.setName("Jane Smith");
        user2.setEmail("jane.smith@example.com");
        user2.setPhone("+1987654321");
        user2.setAddress("456 Oak Ave, City, State");
        user2.setRole(UserRole.RESTAURANT_OWNER);
        user2.setUsername("janesmith");
        user2.setPassword("password456");
        user2 = userRepository.save(user2);

        // When
        ResponseEntity<User[]> response = restTemplate.getForEntity(baseUrl, User[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        
        List<User> users = List.of(response.getBody());
        assertThat(users).extracting(User::getEmail)
                .containsExactlyInAnyOrder("john.doe@example.com", "jane.smith@example.com");
    }

    /**
     * Test retrieving all users when database is empty.
     * Should return HTTP 200 OK with an empty array.
     */
    @Test
    void getAllUsers_WhenEmpty_ShouldReturnEmptyArray() {
        // When
        ResponseEntity<User[]> response = restTemplate.getForEntity(baseUrl, User[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(0);
    }

    /**
     * Test retrieving a user by ID through the API.
     * Should return HTTP 200 OK with the user data.
     */
    @Test
    void getUserById_WithValidId_ShouldReturnUser() {
        // Given
        User savedUser = userRepository.save(testUser);

        // When
        ResponseEntity<User> response = restTemplate.getForEntity(
                baseUrl + "/" + savedUser.getId(), User.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(savedUser.getId());
        assertThat(response.getBody().getName()).isEqualTo("John Doe");
        assertThat(response.getBody().getEmail()).isEqualTo("john.doe@example.com");
    }

    /**
     * Test retrieving a user by invalid ID.
     * Should return HTTP 500 INTERNAL SERVER ERROR.
     */
    @Test
    void getUserById_WithInvalidId_ShouldReturnInternalServerError() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/999", String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Test retrieving a user by email through the API.
     * Should return HTTP 200 OK with the user data.
     */
    @Test
    void getUserByEmail_WithValidEmail_ShouldReturnUser() {
        // Given
        User savedUser = userRepository.save(testUser);

        // When
        ResponseEntity<User> response = restTemplate.getForEntity(
                baseUrl + "/email/" + savedUser.getEmail(), User.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(response.getBody().getName()).isEqualTo("John Doe");
    }

    /**
     * Test retrieving users by role through the API.
     * Should return HTTP 200 OK with users having the specified role.
     */
    @Test
    void getUsersByRole_WithValidRole_ShouldReturnUsersWithRole() {
        // Given
        userRepository.save(testUser); // CUSTOMER role
        
        User restaurantOwner = new User();
        restaurantOwner.setName("Jane Smith");
        restaurantOwner.setEmail("jane.smith@example.com");
        restaurantOwner.setPhone("+1987654321");
        restaurantOwner.setAddress("456 Oak Ave, City, State");
        restaurantOwner.setRole(UserRole.RESTAURANT_OWNER);
        restaurantOwner.setUsername("janesmith");
        restaurantOwner.setPassword("password456");
        userRepository.save(restaurantOwner);

        // When
        ResponseEntity<User[]> response = restTemplate.getForEntity(
                baseUrl + "/role/CUSTOMER", User[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].getRole()).isEqualTo(UserRole.CUSTOMER);
        assertThat(response.getBody()[0].getName()).isEqualTo("John Doe");
    }

    /**
     * Test updating a user through the API.
     * Should return HTTP 200 OK with the updated user data and persist changes.
     */
    @Test
    void updateUser_WithValidData_ShouldUpdateAndReturnUser() {
        // Given
        User savedUser = userRepository.save(testUser);
        
        User updateData = new User();
        updateData.setName("John Updated");
        updateData.setEmail("john.updated@example.com");
        updateData.setPhone("+1111111111");
        updateData.setAddress("Updated Address");
        updateData.setRole(UserRole.RESTAURANT_OWNER);

        HttpEntity<User> request = new HttpEntity<>(updateData, headers);

        // When
        ResponseEntity<User> response = restTemplate.exchange(
                baseUrl + "/" + savedUser.getId(), HttpMethod.PUT, request, User.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(savedUser.getId());
        assertThat(response.getBody().getName()).isEqualTo("John Updated");
        assertThat(response.getBody().getEmail()).isEqualTo("john.updated@example.com");
        assertThat(response.getBody().getRole()).isEqualTo(UserRole.RESTAURANT_OWNER);

        // Verify persistence
        User updatedUserInDb = userRepository.findById(savedUser.getId()).orElse(null);
        assertThat(updatedUserInDb).isNotNull();
        assertThat(updatedUserInDb.getName()).isEqualTo("John Updated");
        assertThat(updatedUserInDb.getEmail()).isEqualTo("john.updated@example.com");
    }

    /**
     * Test updating a user with invalid ID.
     * Should return HTTP 500 INTERNAL SERVER ERROR.
     */
    @Test
    void updateUser_WithInvalidId_ShouldReturnInternalServerError() {
        // Given
        User updateData = new User();
        updateData.setName("Updated Name");

        HttpEntity<User> request = new HttpEntity<>(updateData, headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/999", HttpMethod.PUT, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Test deleting a user through the API.
     * Should return HTTP 204 NO CONTENT and remove user from database.
     */
    @Test
    void deleteUser_WithValidId_ShouldDeleteUserAndReturnNoContent() {
        // Given
        User savedUser = userRepository.save(testUser);
        assertThat(userRepository.findById(savedUser.getId())).isPresent();

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + savedUser.getId(), HttpMethod.DELETE, null, Void.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        
        // Verify deletion
        assertThat(userRepository.findById(savedUser.getId())).isEmpty();
        assertThat(userRepository.findAll()).isEmpty();
    }

    /**
     * Test deleting a user with invalid ID.
     * Should return HTTP 500 INTERNAL SERVER ERROR.
     */
    @Test
    void deleteUser_WithInvalidId_ShouldReturnInternalServerError() {
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/999", HttpMethod.DELETE, null, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Test searching users by name through the API.
     * Should return HTTP 200 OK with users matching the search term.
     */
    @Test
    void searchUsersByName_WithValidName_ShouldReturnMatchingUsers() {
        // Given
        userRepository.save(testUser);
        
        User user2 = new User();
        user2.setName("Jane Smith");
        user2.setEmail("jane.smith@example.com");
        user2.setPhone("+1987654321");
        user2.setAddress("456 Oak Ave, City, State");
        user2.setRole(UserRole.RESTAURANT_OWNER);
        user2.setUsername("janesmith");
        user2.setPassword("password456");
        userRepository.save(user2);

        // When
        ResponseEntity<User[]> response = restTemplate.getForEntity(
                baseUrl + "/search?name=John", User[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].getName()).contains("John");
    }

    /**
     * Test searching users by name with no results.
     * Should return HTTP 200 OK with an empty array.
     */
    @Test
    void searchUsersByName_WithNoResults_ShouldReturnEmptyArray() {
        // Given
        userRepository.save(testUser);

        // When
        ResponseEntity<User[]> response = restTemplate.getForEntity(
                baseUrl + "/search?name=NonExistent", User[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(0);
    }

    /**
     * Test searching users without name parameter.
     * Should return HTTP 400 BAD REQUEST.
     */
    @Test
    void searchUsersByName_WithoutNameParameter_ShouldReturnBadRequest() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/search", String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}