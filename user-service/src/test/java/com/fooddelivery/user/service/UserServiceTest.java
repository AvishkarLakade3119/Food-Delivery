package com.fooddelivery.user.service;

import com.fooddelivery.user.entity.User;
import com.fooddelivery.user.entity.UserRole;
import com.fooddelivery.user.exception.UserAlreadyExistsException;
import com.fooddelivery.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for UserService class.
 * Tests all CRUD operations, validation logic, exception handling, and edge cases.
 * Uses Mockito for mocking dependencies and AssertJ for assertions.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User existingUser;

    /**
     * Set up test data before each test method execution.
     * Initializes test users with valid data for various test scenarios.
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setName("John Doe");
        testUser.setEmail("test@example.com");
        testUser.setPhone("+1234567890");
        testUser.setAddress("123 Test Street, Test City");
        testUser.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testUser.setRole(UserRole.CUSTOMER);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        existingUser = new User();
        existingUser.setId(2L);
        existingUser.setUsername("existinguser");
        existingUser.setPassword("password456");
        existingUser.setFirstName("Jane");
        existingUser.setLastName("Smith");
        existingUser.setName("Jane Smith");
        existingUser.setEmail("existing@example.com");
        existingUser.setPhone("+9876543210");
        existingUser.setAddress("456 Existing Ave, Old City");
        existingUser.setDateOfBirth(LocalDate.of(1985, 5, 15));
        existingUser.setRole(UserRole.ADMIN);
        existingUser.setCreatedAt(LocalDateTime.now().minusDays(10));
        existingUser.setUpdatedAt(LocalDateTime.now().minusDays(5));
    }

    // ==================== CREATE USER TESTS ====================

    /**
     * Test creating a user with valid data and no duplicates.
     * Verifies that the user is saved successfully when email and username are unique.
     * Expected: User is saved and returned with all properties intact.
     */
    @Test
    void createUser_WithValidDataAndNoDuplicates_ShouldReturnSavedUser() {
        // Arrange
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(testUser)).thenReturn(testUser);

        // Act
        User result = userService.createUser(testUser);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(result.getUsername()).isEqualTo(testUser.getUsername());
        verify(userRepository).existsByEmail(testUser.getEmail());
        verify(userRepository).findByUsername(testUser.getUsername());
        verify(userRepository).save(testUser);
    }

    /**
     * Test creating a user with duplicate email.
     * Verifies that UserAlreadyExistsException is thrown when email already exists.
     * Expected: Exception with field="email" and appropriate message.
     */
    @Test
    void createUser_WithDuplicateEmail_ShouldThrowUserAlreadyExistsException() {
        // Arrange
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(testUser))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("email")
                .hasMessageContaining(testUser.getEmail());

        verify(userRepository).existsByEmail(testUser.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Test creating a user with duplicate username.
     * Verifies that UserAlreadyExistsException is thrown when username already exists.
     * Expected: Exception with field="username" and appropriate message.
     */
    @Test
    void createUser_WithDuplicateUsername_ShouldThrowUserAlreadyExistsException() {
        // Arrange
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(testUser))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("username")
                .hasMessageContaining(testUser.getUsername());

        verify(userRepository).existsByEmail(testUser.getEmail());
        verify(userRepository).findByUsername(testUser.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Test creating a user with database constraint violation for email.
     * Verifies that DataIntegrityViolationException with email constraint is handled properly.
     * Expected: UserAlreadyExistsException with email field information.
     */
    @Test
    void createUser_WithDatabaseConstraintViolationForEmail_ShouldThrowUserAlreadyExistsException() {
        // Arrange
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.empty());
        DataIntegrityViolationException dbException = new DataIntegrityViolationException(
                "Duplicate entry 'test@example.com' for key 'users.email'"
        );
        when(userRepository.save(testUser)).thenThrow(dbException);

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(testUser))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("email");

        verify(userRepository).save(testUser);
    }

    /**
     * Test creating a user with null username.
     * Verifies that username validation is skipped when username is null.
     * Expected: User is created successfully without checking username uniqueness.
     */
    @Test
    void createUser_WithNullUsername_ShouldSkipUsernameValidation() {
        // Arrange
        testUser.setUsername(null);
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(userRepository.save(testUser)).thenReturn(testUser);

        // Act
        User result = userService.createUser(testUser);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isNull();
        verify(userRepository).existsByEmail(testUser.getEmail());
        verify(userRepository, never()).findByUsername(anyString());
        verify(userRepository).save(testUser);
    }

    /**
     * Test creating a user with username constraint violation.
     * Verifies that DataIntegrityViolationException with username constraint is handled properly.
     * Expected: UserAlreadyExistsException with username field information.
     * Covers lines 51-54 for username constraint path.
     */
    @Test
    void createUser_WithUsernameConstraintViolation_ShouldThrowUserAlreadyExistsException() {
        // Arrange
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.empty());
        DataIntegrityViolationException dbException = new DataIntegrityViolationException(
                "Duplicate entry 'testuser' for key 'users.username'"
        );
        when(userRepository.save(testUser)).thenThrow(dbException);

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(testUser))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("username");

        verify(userRepository).save(testUser);
    }

    /**
     * Test creating a user with general constraint violation.
     * Verifies that DataIntegrityViolationException without specific field is handled properly.
     * Expected: UserAlreadyExistsException with generic message.
     * Covers lines 51-54 for else branch.
     */
    @Test
    void createUser_WithGeneralConstraintViolation_ShouldThrowUserAlreadyExistsException() {
        // Arrange
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.empty());
        DataIntegrityViolationException dbException = new DataIntegrityViolationException(
                "Duplicate entry for key 'users.phone_unique_constraint'"
        );
        when(userRepository.save(testUser)).thenThrow(dbException);

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(testUser))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("User with the provided information already exists");

        verify(userRepository).save(testUser);
    }

    // ==================== GET ALL USERS TESTS ====================

    /**
     * Test retrieving all users when users exist.
     * Verifies that all users are returned from the repository.
     * Expected: List containing all users.
     */
    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        List<User> users = Arrays.asList(testUser, existingUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(testUser, existingUser);
        verify(userRepository).findAll();
    }

    /**
     * Test retrieving all users when no users exist.
     * Verifies that an empty list is returned when repository is empty.
     * Expected: Empty list.
     */
    @Test
    void getAllUsers_WhenNoUsersExist_ShouldReturnEmptyList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(userRepository).findAll();
    }

    // ==================== GET USER BY ID TESTS ====================

    /**
     * Test retrieving a user by valid ID.
     * Verifies that the correct user is returned when ID exists.
     * Expected: User with matching ID.
     */
    @Test
    void getUserById_WithValidId_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getUserById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
        verify(userRepository).findById(1L);
    }

    /**
     * Test retrieving a user by invalid ID.
     * Verifies that RuntimeException is thrown when user ID does not exist.
     * Expected: RuntimeException with appropriate message.
     */
    @Test
    void getUserById_WithInvalidId_ShouldThrowRuntimeException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with id: 999");

        verify(userRepository).findById(999L);
    }

    // ==================== GET USER BY EMAIL TESTS ====================

    /**
     * Test retrieving a user by valid email.
     * Verifies that the correct user is returned when email exists.
     * Expected: User with matching email.
     */
    @Test
    void getUserByEmail_WithValidEmail_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getUserByEmail("test@example.com");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).findByEmail("test@example.com");
    }

    /**
     * Test retrieving a user by invalid email.
     * Verifies that RuntimeException is thrown when email does not exist.
     * Expected: RuntimeException with appropriate message.
     */
    @Test
    void getUserByEmail_WithInvalidEmail_ShouldThrowRuntimeException() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getUserByEmail("nonexistent@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with email: nonexistent@example.com");

        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    // ==================== GET USERS BY ROLE TESTS ====================

    /**
     * Test retrieving users by role.
     * Verifies that all users with the specified role are returned.
     * Expected: List of users with matching role.
     */
    @Test
    void getUsersByRole_WithValidRole_ShouldReturnUsersWithRole() {
        // Arrange
        List<User> customers = Arrays.asList(testUser);
        when(userRepository.findByRole(UserRole.CUSTOMER)).thenReturn(customers);

        // Act
        List<User> result = userService.getUsersByRole(UserRole.CUSTOMER);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRole()).isEqualTo(UserRole.CUSTOMER);
        verify(userRepository).findByRole(UserRole.CUSTOMER);
    }

    // ==================== UPDATE USER TESTS ====================

    /**
     * Test updating a user with valid data.
     * Verifies that all provided fields are updated successfully.
     * Expected: Updated user with all new values and preserved createdAt.
     */
    @Test
    void updateUser_WithValidData_ShouldReturnUpdatedUser() {
        // Arrange
        User updateDetails = new User();
        updateDetails.setName("Updated Name");
        updateDetails.setEmail("updated@example.com");
        updateDetails.setPhone("+9999999999");
        updateDetails.setAddress("999 Updated Blvd");
        updateDetails.setRole(UserRole.ADMIN);
        updateDetails.setUsername("updateduser");
        updateDetails.setFirstName("UpdatedFirst");
        updateDetails.setLastName("UpdatedLast");
        updateDetails.setDateOfBirth(LocalDate.of(1995, 12, 25));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.updateUser(1L, updateDetails);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        assertThat(result.getPhone()).isEqualTo("+9999999999");
        assertThat(result.getAddress()).isEqualTo("999 Updated Blvd");
        assertThat(result.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(result.getUsername()).isEqualTo("updateduser");
        assertThat(result.getFirstName()).isEqualTo("UpdatedFirst");
        assertThat(result.getLastName()).isEqualTo("UpdatedLast");
        assertThat(result.getDateOfBirth()).isEqualTo(LocalDate.of(1995, 12, 25));
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    /**
     * Test updating a user with partial data.
     * Verifies that only non-null fields are updated and null fields are preserved.
     * Expected: User with updated fields and unchanged null fields.
     */
    @Test
    void updateUser_WithPartialData_ShouldUpdateOnlyNonNullFields() {
        // Arrange
        User partialUpdate = new User();
        partialUpdate.setName("Partial Update");
        // All other fields are null

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.updateUser(1L, partialUpdate);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Partial Update");
        assertThat(result.getEmail()).isEqualTo(testUser.getEmail()); // Unchanged
        assertThat(result.getPhone()).isEqualTo(testUser.getPhone()); // Unchanged
        assertThat(result.getAddress()).isEqualTo(testUser.getAddress()); // Unchanged
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    /**
     * Test updating a user with invalid ID.
     * Verifies that RuntimeException is thrown when user ID does not exist.
     * Expected: RuntimeException with appropriate message.
     */
    @Test
    void updateUser_WithInvalidId_ShouldThrowRuntimeException() {
        // Arrange
        User updateDetails = new User();
        updateDetails.setName("Updated Name");
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.updateUser(999L, updateDetails))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with id: 999");

        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Test updating a user with null username.
     * Verifies that existing username is preserved when update contains null username.
     * Expected: User with unchanged username.
     * Covers line 102 null check for username.
     */
    @Test
    void updateUser_WithNullUsername_ShouldPreserveExistingUsername() {
        // Arrange
        User updateDetails = new User();
        updateDetails.setName("Updated Name");
        updateDetails.setUsername(null); // Explicitly null

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.updateUser(1L, updateDetails);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(testUser.getUsername()); // Preserved
        assertThat(result.getName()).isEqualTo("Updated Name"); // Updated
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    /**
     * Test updating a user with null firstName.
     * Verifies that existing firstName is preserved when update contains null firstName.
     * Expected: User with unchanged firstName.
     * Covers line 105 null check for firstName.
     */
    @Test
    void updateUser_WithNullFirstName_ShouldPreserveExistingFirstName() {
        // Arrange
        User updateDetails = new User();
        updateDetails.setName("Updated Name");
        updateDetails.setFirstName(null); // Explicitly null

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.updateUser(1L, updateDetails);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo(testUser.getFirstName()); // Preserved
        assertThat(result.getName()).isEqualTo("Updated Name"); // Updated
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    /**
     * Test updating a user with null lastName.
     * Verifies that existing lastName is preserved when update contains null lastName.
     * Expected: User with unchanged lastName.
     * Covers line 108 null check for lastName.
     */
    @Test
    void updateUser_WithNullLastName_ShouldPreserveExistingLastName() {
        // Arrange
        User updateDetails = new User();
        updateDetails.setName("Updated Name");
        updateDetails.setLastName(null); // Explicitly null

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.updateUser(1L, updateDetails);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getLastName()).isEqualTo(testUser.getLastName()); // Preserved
        assertThat(result.getName()).isEqualTo("Updated Name"); // Updated
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    /**
     * Test updating a user with null dateOfBirth.
     * Verifies that existing dateOfBirth is preserved when update contains null dateOfBirth.
     * Expected: User with unchanged dateOfBirth.
     * Covers line 111 null check for dateOfBirth.
     */
    @Test
    void updateUser_WithNullDateOfBirth_ShouldPreserveExistingDateOfBirth() {
        // Arrange
        User updateDetails = new User();
        updateDetails.setName("Updated Name");
        updateDetails.setDateOfBirth(null); // Explicitly null

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.updateUser(1L, updateDetails);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getDateOfBirth()).isEqualTo(testUser.getDateOfBirth()); // Preserved
        assertThat(result.getName()).isEqualTo("Updated Name"); // Updated
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    // ==================== DELETE USER TESTS ====================

    /**
     * Test deleting a user with valid ID.
     * Verifies that the user is deleted successfully when ID exists.
     * Expected: User is deleted from repository.
     */
    @Test
    void deleteUser_WithValidId_ShouldDeleteUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).findById(1L);
        verify(userRepository).delete(testUser);
    }

    /**
     * Test deleting a user with invalid ID.
     * Verifies that RuntimeException is thrown when user ID does not exist.
     * Expected: RuntimeException with appropriate message.
     */
    @Test
    void deleteUser_WithInvalidId_ShouldThrowRuntimeException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with id: 999");

        verify(userRepository).findById(999L);
        verify(userRepository, never()).delete(any(User.class));
    }

    // ==================== SEARCH USERS BY NAME TESTS ====================

    /**
     * Test searching users by name with valid name.
     * Verifies that all users with matching names are returned.
     * Expected: List of users with names containing the search term.
     */
    @Test
    void searchUsersByName_WithValidName_ShouldReturnMatchingUsers() {
        // Arrange
        List<User> matchingUsers = Arrays.asList(testUser);
        when(userRepository.findByNameContainingIgnoreCase("John")).thenReturn(matchingUsers);

        // Act
        List<User> result = userService.searchUsersByName("John");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).containsIgnoringCase("John");
        verify(userRepository).findByNameContainingIgnoreCase("John");
    }

    /**
     * Test searching users by name with no results.
     * Verifies that an empty list is returned when no users match the search term.
     * Expected: Empty list.
     */
    @Test
    void searchUsersByName_WithNoResults_ShouldReturnEmptyList() {
        // Arrange
        when(userRepository.findByNameContainingIgnoreCase("NonExistent")).thenReturn(Collections.emptyList());

        // Act
        List<User> result = userService.searchUsersByName("NonExistent");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(userRepository).findByNameContainingIgnoreCase("NonExistent");
    }

    // ==================== EXISTS BY EMAIL TESTS ====================

    /**
     * Test checking if user exists by email when email exists.
     * Verifies that true is returned when a user with the email exists.
     * Expected: true.
     */
    @Test
    void existsByEmail_WithExistingEmail_ShouldReturnTrue() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act
        boolean result = userService.existsByEmail("test@example.com");

        // Assert
        assertThat(result).isTrue();
        verify(userRepository).existsByEmail("test@example.com");
    }

    /**
     * Test checking if user exists by email when email does not exist.
     * Verifies that false is returned when no user with the email exists.
     * Expected: false.
     */
    @Test
    void existsByEmail_WithNonExistentEmail_ShouldReturnFalse() {
        // Arrange
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        // Act
        boolean result = userService.existsByEmail("nonexistent@example.com");

        // Assert
        assertThat(result).isFalse();
        verify(userRepository).existsByEmail("nonexistent@example.com");
    }

    // ==================== EXISTS BY USERNAME TESTS ====================

    /**
     * Test checking if user exists by username when username exists.
     * Verifies that true is returned when a user with the username exists.
     * Expected: true.
     */
    @Test
    void existsByUsername_WithExistingUsername_ShouldReturnTrue() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        boolean result = userService.existsByUsername("testuser");

        // Assert
        assertThat(result).isTrue();
        verify(userRepository).findByUsername("testuser");
    }

    /**
     * Test checking if user exists by username when username does not exist.
     * Verifies that false is returned when no user with the username exists.
     * Expected: false.
     */
    @Test
    void existsByUsername_WithNonExistentUsername_ShouldReturnFalse() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act
        boolean result = userService.existsByUsername("nonexistent");

        // Assert
        assertThat(result).isFalse();
        verify(userRepository).findByUsername("nonexistent");
    }

    // ==================== BRANCH COVERAGE TESTS ====================

    /**
     * Test createUser with null username.
     * Covers line 34 false branch: if (user.getUsername() != null && !user.getUsername().trim().isEmpty())
     */
    @Test
    void createUser_WithNullUsername_ShouldSkipUsernameCheck() {
        // Arrange
        testUser.setUsername(null);
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(userRepository.save(testUser)).thenReturn(testUser);

        // Act
        User result = userService.createUser(testUser);

        // Assert
        assertThat(result).isNotNull();
        verify(userRepository, never()).findByUsername(anyString());
        verify(userRepository).save(testUser);
    }

    /**
     * Test createUser with empty username.
     * Covers line 34 false branch: if (user.getUsername() != null && !user.getUsername().trim().isEmpty())
     */
    @Test
    void createUser_WithEmptyUsername_ShouldSkipUsernameCheck() {
        // Arrange
        testUser.setUsername("   ");
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(userRepository.save(testUser)).thenReturn(testUser);

        // Act
        User result = userService.createUser(testUser);

        // Assert
        assertThat(result).isNotNull();
        verify(userRepository, never()).findByUsername(anyString());
        verify(userRepository).save(testUser);
    }

    /**
     * Test updateUser with null name.
     * Covers line 86 false branch: if (userDetails.getName() != null)
     */
    @Test
    void updateUser_WithNullName_ShouldPreserveExistingName() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        User updateData = new User();
        updateData.setName(null);
        updateData.setEmail("newemail@example.com");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        User result = userService.updateUser(1L, updateData);

        // Assert
        assertThat(result.getName()).isEqualTo("Jane Smith");
        verify(userRepository).save(any(User.class));
    }

    /**
     * Test updateUser with null role.
     * Covers line 98 false branch: if (userDetails.getRole() != null)
     */
    @Test
    void updateUser_WithNullRole_ShouldPreserveExistingRole() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        User updateData = new User();
        updateData.setRole(null);
        updateData.setEmail("newemail@example.com");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        User result = userService.updateUser(1L, updateData);

        // Assert
        assertThat(result.getRole()).isEqualTo(UserRole.ADMIN);
        verify(userRepository).save(any(User.class));
    }
}
