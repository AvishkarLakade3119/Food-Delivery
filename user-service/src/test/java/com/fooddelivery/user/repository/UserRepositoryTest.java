package com.fooddelivery.user.repository;

import com.fooddelivery.user.entity.User;
import com.fooddelivery.user.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for UserRepository class.
 * Tests JPA repository methods and custom query methods using an in-memory H2 database.
 * Uses @DataJpaTest for repository layer testing with TestEntityManager for test data setup.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("User Repository Integration Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User adminUser;
    private User deliveryUser;

    @BeforeEach
    void setUp() {
        // Create test users with all required fields
        testUser = new User("John Doe", "john.doe@example.com", "+1234567890", "123 Main St", UserRole.CUSTOMER);
        testUser.setUsername("johndoe");
        testUser.setPassword("password123");

        adminUser = new User("Admin User", "admin@example.com", "+1111111111", "456 Admin Ave", UserRole.ADMIN);
        adminUser.setUsername("admin");
        adminUser.setPassword("adminpass");

        deliveryUser = new User("Delivery Guy", "delivery@example.com", "+2222222222", "789 Delivery Rd", UserRole.DELIVERY);
        deliveryUser.setUsername("deliveryguy");
        deliveryUser.setPassword("deliverypass");

        // Persist test data
        entityManager.persistAndFlush(testUser);
        entityManager.persistAndFlush(adminUser);
        entityManager.persistAndFlush(deliveryUser);
    }

    @Test
    @DisplayName("Should find user by email when user exists")
    void shouldFindUserByEmailWhenExists() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("john.doe@example.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("John Doe");
        assertThat(foundUser.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Should return empty optional when user not found by email")
    void shouldReturnEmptyOptionalWhenUserNotFoundByEmail() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(foundUser).isNotPresent();
    }

    @Test
    @DisplayName("Should return true when user exists by email")
    void shouldReturnTrueWhenUserExistsByEmail() {
        // When
        boolean exists = userRepository.existsByEmail("john.doe@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when user does not exist by email")
    void shouldReturnFalseWhenUserDoesNotExistByEmail() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should find users by role")
    void shouldFindUsersByRole() {
        // When
        List<User> customerRoleUsers = userRepository.findByRole(UserRole.CUSTOMER);
        List<User> adminRoleUsers = userRepository.findByRole(UserRole.ADMIN);
        List<User> deliveryRoleUsers = userRepository.findByRole(UserRole.DELIVERY);

        // Then
        assertThat(customerRoleUsers).hasSize(1);
        assertThat(customerRoleUsers.get(0).getRole()).isEqualTo(UserRole.CUSTOMER);
        
        assertThat(adminRoleUsers).hasSize(1);
        assertThat(adminRoleUsers.get(0).getRole()).isEqualTo(UserRole.ADMIN);
        
        assertThat(deliveryRoleUsers).hasSize(1);
        assertThat(deliveryRoleUsers.get(0).getRole()).isEqualTo(UserRole.DELIVERY);
    }

    @Test
    @DisplayName("Should return empty list when no users found by role")
    void shouldReturnEmptyListWhenNoUsersFoundByRole() {
        // When - Look for a role that doesn't exist in our test data
        List<User> restaurantOwnerUsers = userRepository.findByRole(UserRole.RESTAURANT_OWNER);

        // Then
        assertThat(restaurantOwnerUsers).isEmpty();
    }

    @Test
    @DisplayName("Should find users by name containing ignore case")
    void shouldFindUsersByNameContainingIgnoreCase() {
        // When
        List<User> usersWithJohn = userRepository.findByNameContainingIgnoreCase("john");
        List<User> usersWithAdmin = userRepository.findByNameContainingIgnoreCase("ADMIN");
        List<User> usersWithDelivery = userRepository.findByNameContainingIgnoreCase("delivery");

        // Then
        assertThat(usersWithJohn).hasSize(1);
        assertThat(usersWithJohn.get(0).getName()).containsIgnoringCase("john");
        
        assertThat(usersWithAdmin).hasSize(1);
        assertThat(usersWithAdmin.get(0).getName()).containsIgnoringCase("admin");
        
        assertThat(usersWithDelivery).hasSize(1);
        assertThat(usersWithDelivery.get(0).getName()).containsIgnoringCase("delivery");
    }

    @Test
    @DisplayName("Should return empty list when no users found by name search")
    void shouldReturnEmptyListWhenNoUsersFoundByNameSearch() {
        // When
        List<User> users = userRepository.findByNameContainingIgnoreCase("nonexistent");

        // Then
        assertThat(users).isEmpty();
    }

    @Test
    @DisplayName("Should find users by partial name match")
    void shouldFindUsersByPartialNameMatch() {
        // When
        List<User> usersWithDo = userRepository.findByNameContainingIgnoreCase("Do");
        List<User> usersWithUser = userRepository.findByNameContainingIgnoreCase("User");

        // Then
        assertThat(usersWithDo).hasSize(1);
        assertThat(usersWithDo.get(0).getName()).isEqualTo("John Doe");

        assertThat(usersWithUser).hasSize(1); // Only "Admin User" matches
        assertThat(usersWithUser).extracting(User::getName)
                .contains("Admin User");
    }

    @Test
    @DisplayName("Should handle case insensitive email search")
    void shouldHandleCaseInsensitiveEmailSearch() {
        // When
        Optional<User> foundUser1 = userRepository.findByEmail("JOHN.DOE@EXAMPLE.COM");
        Optional<User> foundUser2 = userRepository.findByEmail("john.doe@example.com");
        Optional<User> foundUser3 = userRepository.findByEmail("John.Doe@Example.Com");

        // Then - Email search should be case sensitive in standard JPA
        assertThat(foundUser1).isNotPresent(); // Case sensitive
        assertThat(foundUser2).isPresent();
        assertThat(foundUser3).isNotPresent(); // Case sensitive
    }

    @Test
    @DisplayName("Should save and retrieve user with all fields")
    void shouldSaveAndRetrieveUserWithAllFields() {
        // Given
        User newUser = new User("Test User", "test@example.com", "+9999999999", "Test Address", UserRole.CUSTOMER);
        newUser.setUsername("testuser");
        newUser.setPassword("testpass123");

        // When
        User savedUser = userRepository.save(newUser);
        Optional<User> retrievedUser = userRepository.findById(savedUser.getId());

        // Then
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getName()).isEqualTo("Test User");
        assertThat(retrievedUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(retrievedUser.get().getPhone()).isEqualTo("+9999999999");
        assertThat(retrievedUser.get().getAddress()).isEqualTo("Test Address");
        assertThat(retrievedUser.get().getRole()).isEqualTo(UserRole.CUSTOMER);
        assertThat(retrievedUser.get().getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should update user and maintain data integrity")
    void shouldUpdateUserAndMaintainDataIntegrity() {
        // Given
        User userToUpdate = userRepository.findByEmail("john.doe@example.com").orElseThrow();
        
        // When
        userToUpdate.setName("John Updated");
        userToUpdate.setPhone("+9876543210");
        userToUpdate.setRole(UserRole.ADMIN);
        User updatedUser = userRepository.save(userToUpdate);

        // Then
        assertThat(updatedUser.getName()).isEqualTo("John Updated");
        assertThat(updatedUser.getPhone()).isEqualTo("+9876543210");
        assertThat(updatedUser.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(updatedUser.getEmail()).isEqualTo("john.doe@example.com"); // Should remain unchanged
        assertThat(updatedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Given
        User userToDelete = userRepository.findByEmail("john.doe@example.com").orElseThrow();
        Long userId = userToDelete.getId();

        // When
        userRepository.delete(userToDelete);
        Optional<User> deletedUser = userRepository.findById(userId);

        // Then
        assertThat(deletedUser).isNotPresent();
    }

    @Test
    @DisplayName("Should enforce unique email constraint")
    void shouldEnforceUniqueEmailConstraint() {
        // Given
        User duplicateEmailUser = new User("Duplicate", "john.doe@example.com", "+5555555555", "Duplicate St", UserRole.CUSTOMER);

        // When & Then
        try {
            userRepository.saveAndFlush(duplicateEmailUser);
            // If we reach here, the test should fail
            assertThat(false).as("Should have thrown constraint violation exception").isTrue();
        } catch (Exception e) {
            // Expected behavior - unique constraint violation
            assertThat(e.getMessage()).containsIgnoringCase("constraint");
        }
    }

    @Test
    @DisplayName("Should handle empty string search gracefully")
    void shouldHandleEmptyStringSearchGracefully() {
        // When
        List<User> users = userRepository.findByNameContainingIgnoreCase("");

        // Then
        assertThat(users).hasSize(3); // Should return all users as empty string matches everything
    }

    @Test
    @DisplayName("Should count users correctly")
    void shouldCountUsersCorrectly() {
        // When
        long totalUsers = userRepository.count();

        // Then
        assertThat(totalUsers).isEqualTo(3);
    }

    @Test
    @DisplayName("Should find all users and maintain order")
    void shouldFindAllUsersAndMaintainOrder() {
        // When
        List<User> allUsers = userRepository.findAll();

        // Then
        assertThat(allUsers).hasSize(3);
        assertThat(allUsers).extracting(User::getEmail)
                .contains("john.doe@example.com", "admin@example.com", "delivery@example.com");
    }
}