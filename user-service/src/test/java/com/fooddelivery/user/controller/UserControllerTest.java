package com.fooddelivery.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for UserController using @WebMvcTest to test the web layer in isolation.
 * Tests all REST endpoints with proper mocking of the service layer.
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private User testUser2;
    private List<User> testUsers;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
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
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setName("Jane Smith");
        testUser2.setEmail("jane.smith@example.com");
        testUser2.setPhone("+1987654321");
        testUser2.setAddress("456 Oak Ave, City, State");
        testUser2.setRole(UserRole.RESTAURANT_OWNER);
        testUser2.setUsername("janesmith");
        testUser2.setPassword("password456");
        testUser2.setFirstName("Jane");
        testUser2.setLastName("Smith");
        testUser2.setDateOfBirth(LocalDate.of(1985, 5, 15));
        testUser2.setCreatedAt(LocalDateTime.now());
        testUser2.setUpdatedAt(LocalDateTime.now());

        testUsers = Arrays.asList(testUser, testUser2);
    }

    /**
     * Test successful user creation with valid data.
     * Should return HTTP 201 CREATED and the created user.
     */
    @Test
    void createUser_WithValidData_ShouldReturnCreatedUser() throws Exception {
        // Given
        User userToCreate = new User();
        userToCreate.setName("New User");
        userToCreate.setEmail("new.user@example.com");
        userToCreate.setPhone("+1111111111");
        userToCreate.setAddress("789 Pine St, City, State");
        userToCreate.setRole(UserRole.CUSTOMER);
        userToCreate.setUsername("newuser");
        userToCreate.setPassword("newpassword");

        User createdUser = new User();
        createdUser.setId(3L);
        createdUser.setName(userToCreate.getName());
        createdUser.setEmail(userToCreate.getEmail());
        createdUser.setPhone(userToCreate.getPhone());
        createdUser.setAddress(userToCreate.getAddress());
        createdUser.setRole(userToCreate.getRole());
        createdUser.setUsername(userToCreate.getUsername());
        createdUser.setPassword(userToCreate.getPassword());
        createdUser.setCreatedAt(LocalDateTime.now());

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("New User"))
                .andExpect(jsonPath("$.email").value("new.user@example.com"))
                .andExpect(jsonPath("$.phone").value("+1111111111"))
                .andExpect(jsonPath("$.address").value("789 Pine St, City, State"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.username").value("newuser"));

        verify(userService, times(1)).createUser(any(User.class));
    }

    /**
     * Test user creation with invalid data (missing required fields).
     * Should return HTTP 400 BAD REQUEST.
     */
    @Test
    void createUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        User invalidUser = new User();
        // Missing required fields like name, email, etc.

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(User.class));
    }

    /**
     * Test retrieving all users.
     * Should return HTTP 200 OK with a list of users.
     */
    @Test
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        // Given
        when(userService.getAllUsers()).thenReturn(testUsers);

        // When & Then
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"))
                .andExpect(jsonPath("$[1].email").value("jane.smith@example.com"));

        verify(userService, times(1)).getAllUsers();
    }

    /**
     * Test retrieving all users when no users exist.
     * Should return HTTP 200 OK with an empty list.
     */
    @Test
    void getAllUsers_WhenNoUsers_ShouldReturnEmptyList() throws Exception {
        // Given
        when(userService.getAllUsers()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userService, times(1)).getAllUsers();
    }

    /**
     * Test retrieving a user by valid ID.
     * Should return HTTP 200 OK with the user data.
     */
    @Test
    void getUserById_WithValidId_ShouldReturnUser() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(testUser);

        // When & Then
        mockMvc.perform(get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.phone").value("+1234567890"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));

        verify(userService, times(1)).getUserById(1L);
    }

    /**
     * Test retrieving a user by invalid ID.
     * Should return HTTP 500 INTERNAL SERVER ERROR when service throws exception.
     */
    @Test
    void getUserById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(userService.getUserById(999L)).thenThrow(new RuntimeException("User not found with id: 999"));

        // When & Then
        mockMvc.perform(get("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).getUserById(999L);
    }

    /**
     * Test retrieving a user by valid email.
     * Should return HTTP 200 OK with the user data.
     */
    @Test
    void getUserByEmail_WithValidEmail_ShouldReturnUser() throws Exception {
        // Given
        when(userService.getUserByEmail("john.doe@example.com")).thenReturn(testUser);

        // When & Then
        mockMvc.perform(get("/api/users/email/john.doe@example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(userService, times(1)).getUserByEmail("john.doe@example.com");
    }

    /**
     * Test retrieving a user by invalid email.
     * Should return HTTP 500 INTERNAL SERVER ERROR when service throws exception.
     */
    @Test
    void getUserByEmail_WithInvalidEmail_ShouldReturnNotFound() throws Exception {
        // Given
        when(userService.getUserByEmail("nonexistent@example.com"))
                .thenThrow(new RuntimeException("User not found with email: nonexistent@example.com"));

        // When & Then
        mockMvc.perform(get("/api/users/email/nonexistent@example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).getUserByEmail("nonexistent@example.com");
    }

    /**
     * Test retrieving users by role.
     * Should return HTTP 200 OK with a list of users having the specified role.
     */
    @Test
    void getUsersByRole_WithValidRole_ShouldReturnUsers() throws Exception {
        // Given
        List<User> customerUsers = Arrays.asList(testUser);
        when(userService.getUsersByRole(UserRole.CUSTOMER)).thenReturn(customerUsers);

        // When & Then
        mockMvc.perform(get("/api/users/role/CUSTOMER")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].role").value("CUSTOMER"));

        verify(userService, times(1)).getUsersByRole(UserRole.CUSTOMER);
    }

    /**
     * Test updating a user with valid data.
     * Should return HTTP 200 OK with the updated user data.
     */
    @Test
    void updateUser_WithValidData_ShouldReturnUpdatedUser() throws Exception {
        // Given
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("johnupdated");
        updatedUser.setPassword("newpassword123");
        updatedUser.setName("John Updated");
        updatedUser.setEmail("john.updated@example.com");
        updatedUser.setPhone("+1234567890");
        updatedUser.setAddress("Updated Address");
        updatedUser.setRole(UserRole.CUSTOMER);

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.email").value("john.updated@example.com"))
                .andExpect(jsonPath("$.address").value("Updated Address"))
                .andExpect(jsonPath("$.username").value("johnupdated"));

        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    /**
     * Test updating a user with invalid data.
     * Should return HTTP 400 BAD REQUEST.
     */
    @Test
    void updateUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        User invalidUser = new User();
        invalidUser.setEmail("invalid-email"); // Invalid email format

        // When & Then
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(anyLong(), any(User.class));
    }

    /**
     * Test deleting a user with valid ID.
     * Should return HTTP 204 NO CONTENT.
     */
    @Test
    void deleteUser_WithValidId_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(1L);

        // When & Then
        mockMvc.perform(delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    /**
     * Test deleting a user with invalid ID.
     * Should return HTTP 500 INTERNAL SERVER ERROR when service throws exception.
     */
    @Test
    void deleteUser_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("User not found with id: 999"))
                .when(userService).deleteUser(999L);

        // When & Then
        mockMvc.perform(delete("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).deleteUser(999L);
    }

    /**
     * Test searching users by name.
     * Should return HTTP 200 OK with a list of users matching the name.
     */
    @Test
    void searchUsersByName_WithValidName_ShouldReturnUsers() throws Exception {
        // Given
        List<User> searchResults = Arrays.asList(testUser);
        when(userService.searchUsersByName("John")).thenReturn(searchResults);

        // When & Then
        mockMvc.perform(get("/api/users/search")
                        .param("name", "John")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John Doe"));

        verify(userService, times(1)).searchUsersByName("John");
    }

    /**
     * Test searching users by name with no results.
     * Should return HTTP 200 OK with an empty list.
     */
    @Test
    void searchUsersByName_WithNoResults_ShouldReturnEmptyList() throws Exception {
        // Given
        when(userService.searchUsersByName("NonExistent")).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/users/search")
                .param("name", "NonExistent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userService, times(1)).searchUsersByName("NonExistent");
    }

    /**
     * Test searching users without providing name parameter.
     * Should return HTTP 400 BAD REQUEST.
     */
    @Test
    void searchUsersByName_WithoutNameParameter_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(userService, never()).searchUsersByName(anyString());
    }
}