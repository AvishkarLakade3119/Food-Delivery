package com.fooddelivery.user.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive unit tests for Entity classes to achieve 100% line and branch coverage.
 * Tests all constructors, getters, setters, lifecycle methods, and enum values.
 */
class EntityTest {

    /**
     * Test User entity default constructor.
     * Should create an instance with default values.
     */
    @Test
    void user_DefaultConstructor_ShouldCreateInstanceWithDefaultValues() {
        // When
        User user = new User();

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isNull();
        assertThat(user.getEmail()).isNull();
        assertThat(user.getPhone()).isNull();
        assertThat(user.getAddress()).isNull();
        assertThat(user.getRole()).isEqualTo(UserRole.CUSTOMER); // Default role
        assertThat(user.getUsername()).isNull();
        assertThat(user.getPassword()).isNull();
        assertThat(user.getFirstName()).isNull();
        assertThat(user.getLastName()).isNull();
        assertThat(user.getDateOfBirth()).isNull();
        assertThat(user.getCreatedAt()).isNull();
        assertThat(user.getUpdatedAt()).isNull();
    }

    /**
     * Test User entity parameterized constructor.
     * Should create an instance with specified values.
     */
    @Test
    void user_ParameterizedConstructor_ShouldCreateInstanceWithSpecifiedValues() {
        // Given
        String name = "John Doe";
        String email = "john.doe@example.com";
        String phone = "+1234567890";
        String address = "123 Main St, City, State";
        UserRole role = UserRole.RESTAURANT_OWNER;

        // When
        User user = new User(name, email, phone, address, role);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPhone()).isEqualTo(phone);
        assertThat(user.getAddress()).isEqualTo(address);
        assertThat(user.getRole()).isEqualTo(role);
        assertThat(user.getId()).isNull(); // Not set in constructor
    }

    /**
     * Test User entity setters and getters for all fields.
     * Should properly set and retrieve all field values.
     */
    @Test
    void user_SettersAndGetters_ShouldSetAndRetrieveAllFieldValues() {
        // Given
        User user = new User();
        Long id = 1L;
        String name = "Jane Smith";
        String email = "jane.smith@example.com";
        String phone = "+1987654321";
        String address = "456 Oak Ave, City, State";
        UserRole role = UserRole.ADMIN;
        String username = "janesmith";
        String password = "password123";
        String firstName = "Jane";
        String lastName = "Smith";
        LocalDate dateOfBirth = LocalDate.of(1985, 5, 15);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // When
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);
        user.setRole(role);
        user.setUsername(username);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setDateOfBirth(dateOfBirth);
        user.setCreatedAt(createdAt);
        user.setUpdatedAt(updatedAt);

        // Then
        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPhone()).isEqualTo(phone);
        assertThat(user.getAddress()).isEqualTo(address);
        assertThat(user.getRole()).isEqualTo(role);
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getFirstName()).isEqualTo(firstName);
        assertThat(user.getLastName()).isEqualTo(lastName);
        assertThat(user.getDateOfBirth()).isEqualTo(dateOfBirth);
        assertThat(user.getCreatedAt()).isEqualTo(createdAt);
        assertThat(user.getUpdatedAt()).isEqualTo(updatedAt);
    }

    /**
     * Test User entity with null values.
     * Should handle null values properly in setters and getters.
     */
    @Test
    void user_WithNullValues_ShouldHandleNullsProperly() {
        // Given
        User user = new User();

        // When
        user.setId(null);
        user.setName(null);
        user.setEmail(null);
        user.setPhone(null);
        user.setAddress(null);
        user.setRole(null);
        user.setUsername(null);
        user.setPassword(null);
        user.setFirstName(null);
        user.setLastName(null);
        user.setDateOfBirth(null);
        user.setCreatedAt(null);
        user.setUpdatedAt(null);

        // Then
        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isNull();
        assertThat(user.getEmail()).isNull();
        assertThat(user.getPhone()).isNull();
        assertThat(user.getAddress()).isNull();
        assertThat(user.getRole()).isNull();
        assertThat(user.getUsername()).isNull();
        assertThat(user.getPassword()).isNull();
        assertThat(user.getFirstName()).isNull();
        assertThat(user.getLastName()).isNull();
        assertThat(user.getDateOfBirth()).isNull();
        assertThat(user.getCreatedAt()).isNull();
        assertThat(user.getUpdatedAt()).isNull();
    }

    /**
     * Test User entity with empty string values.
     * Should handle empty strings properly.
     */
    @Test
    void user_WithEmptyStringValues_ShouldHandleEmptyStringsProperly() {
        // Given
        User user = new User();

        // When
        user.setName("");
        user.setEmail("");
        user.setPhone("");
        user.setAddress("");
        user.setUsername("");
        user.setPassword("");
        user.setFirstName("");
        user.setLastName("");

        // Then
        assertThat(user.getName()).isEmpty();
        assertThat(user.getEmail()).isEmpty();
        assertThat(user.getPhone()).isEmpty();
        assertThat(user.getAddress()).isEmpty();
        assertThat(user.getUsername()).isEmpty();
        assertThat(user.getPassword()).isEmpty();
        assertThat(user.getFirstName()).isEmpty();
        assertThat(user.getLastName()).isEmpty();
    }

    /**
     * Test User entity with whitespace values.
     * Should handle whitespace strings properly.
     */
    @Test
    void user_WithWhitespaceValues_ShouldHandleWhitespacesProperly() {
        // Given
        User user = new User();
        String whitespace = "   ";

        // When
        user.setName(whitespace);
        user.setEmail(whitespace);
        user.setPhone(whitespace);
        user.setAddress(whitespace);
        user.setUsername(whitespace);
        user.setPassword(whitespace);
        user.setFirstName(whitespace);
        user.setLastName(whitespace);

        // Then
        assertThat(user.getName()).isEqualTo(whitespace);
        assertThat(user.getEmail()).isEqualTo(whitespace);
        assertThat(user.getPhone()).isEqualTo(whitespace);
        assertThat(user.getAddress()).isEqualTo(whitespace);
        assertThat(user.getUsername()).isEqualTo(whitespace);
        assertThat(user.getPassword()).isEqualTo(whitespace);
        assertThat(user.getFirstName()).isEqualTo(whitespace);
        assertThat(user.getLastName()).isEqualTo(whitespace);
    }

    /**
     * Test User entity with special characters.
     * Should handle special characters properly.
     */
    @Test
    void user_WithSpecialCharacters_ShouldHandleSpecialCharactersProperly() {
        // Given
        User user = new User();
        String specialChars = "!@#$%^&*()_+-=[]{}|;':,.<>?";

        // When
        user.setName(specialChars);
        user.setEmail(specialChars + "@example.com");
        user.setPhone(specialChars);
        user.setAddress(specialChars);
        user.setUsername(specialChars);
        user.setPassword(specialChars);
        user.setFirstName(specialChars);
        user.setLastName(specialChars);

        // Then
        assertThat(user.getName()).isEqualTo(specialChars);
        assertThat(user.getEmail()).isEqualTo(specialChars + "@example.com");
        assertThat(user.getPhone()).isEqualTo(specialChars);
        assertThat(user.getAddress()).isEqualTo(specialChars);
        assertThat(user.getUsername()).isEqualTo(specialChars);
        assertThat(user.getPassword()).isEqualTo(specialChars);
        assertThat(user.getFirstName()).isEqualTo(specialChars);
        assertThat(user.getLastName()).isEqualTo(specialChars);
    }

    /**
     * Test User entity with maximum length values.
     * Should handle long strings properly.
     */
    @Test
    void user_WithMaximumLengthValues_ShouldHandleLongStringsProperly() {
        // Given
        User user = new User();
        String longString = "a".repeat(1000); // Very long string

        // When
        user.setName(longString);
        user.setEmail(longString + "@example.com");
        user.setPhone(longString);
        user.setAddress(longString);
        user.setUsername(longString);
        user.setPassword(longString);
        user.setFirstName(longString);
        user.setLastName(longString);

        // Then
        assertThat(user.getName()).isEqualTo(longString);
        assertThat(user.getEmail()).isEqualTo(longString + "@example.com");
        assertThat(user.getPhone()).isEqualTo(longString);
        assertThat(user.getAddress()).isEqualTo(longString);
        assertThat(user.getUsername()).isEqualTo(longString);
        assertThat(user.getPassword()).isEqualTo(longString);
        assertThat(user.getFirstName()).isEqualTo(longString);
        assertThat(user.getLastName()).isEqualTo(longString);
    }

    /**
     * Test User entity with various date values.
     * Should handle different date scenarios properly.
     */
    @Test
    void user_WithVariousDateValues_ShouldHandleDatesProperly() {
        // Given
        User user = new User();
        LocalDate pastDate = LocalDate.of(1900, 1, 1);
        LocalDate futureDate = LocalDate.of(2100, 12, 31);
        LocalDate currentDate = LocalDate.now();
        LocalDateTime pastDateTime = LocalDateTime.of(2020, 1, 1, 10, 30, 45);
        LocalDateTime futureDateTime = LocalDateTime.of(2030, 12, 31, 23, 59, 59);
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Test past date
        user.setDateOfBirth(pastDate);
        assertThat(user.getDateOfBirth()).isEqualTo(pastDate);

        // Test future date
        user.setDateOfBirth(futureDate);
        assertThat(user.getDateOfBirth()).isEqualTo(futureDate);

        // Test current date
        user.setDateOfBirth(currentDate);
        assertThat(user.getDateOfBirth()).isEqualTo(currentDate);

        // Test past datetime
        user.setCreatedAt(pastDateTime);
        assertThat(user.getCreatedAt()).isEqualTo(pastDateTime);

        // Test future datetime
        user.setUpdatedAt(futureDateTime);
        assertThat(user.getUpdatedAt()).isEqualTo(futureDateTime);

        // Test current datetime
        user.setCreatedAt(currentDateTime);
        assertThat(user.getCreatedAt()).isEqualTo(currentDateTime);
    }

    /**
     * Test User entity with all UserRole enum values.
     * Should handle all enum values properly.
     */
    @Test
    void user_WithAllUserRoleValues_ShouldHandleAllEnumValuesProperly() {
        // Given
        User user = new User();

        // Test CUSTOMER role
        user.setRole(UserRole.CUSTOMER);
        assertThat(user.getRole()).isEqualTo(UserRole.CUSTOMER);

        // Test ADMIN role
        user.setRole(UserRole.ADMIN);
        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);

        // Test DELIVERY role
        user.setRole(UserRole.DELIVERY);
        assertThat(user.getRole()).isEqualTo(UserRole.DELIVERY);

        // Test RESTAURANT_OWNER role
        user.setRole(UserRole.RESTAURANT_OWNER);
        assertThat(user.getRole()).isEqualTo(UserRole.RESTAURANT_OWNER);
    }

    /**
     * Test User entity lifecycle methods - onCreate.
     * Should set createdAt timestamp when onCreate is called.
     */
    @Test
    void user_OnCreate_ShouldSetCreatedAtTimestamp() {
        // Given
        User user = new User();
        LocalDateTime beforeCreate = LocalDateTime.now().minusSeconds(1);

        // When
        user.onCreate();

        // Then
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getCreatedAt()).isAfter(beforeCreate);
        assertThat(user.getCreatedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
    }

    /**
     * Test User entity lifecycle methods - onUpdate.
     * Should set updatedAt timestamp when onUpdate is called.
     */
    @Test
    void user_OnUpdate_ShouldSetUpdatedAtTimestamp() {
        // Given
        User user = new User();
        LocalDateTime beforeUpdate = LocalDateTime.now().minusSeconds(1);

        // When
        user.onUpdate();

        // Then
        assertThat(user.getUpdatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isAfter(beforeUpdate);
        assertThat(user.getUpdatedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
    }

    /**
     * Test User entity lifecycle methods - multiple calls.
     * Should update timestamps on each call.
     */
    @Test
    void user_LifecycleMethods_MultipleCalls_ShouldUpdateTimestampsOnEachCall() throws InterruptedException {
        // Given
        User user = new User();

        // When - First call
        user.onCreate();
        LocalDateTime firstCreatedAt = user.getCreatedAt();
        
        Thread.sleep(10); // Small delay to ensure different timestamps
        
        user.onUpdate();
        LocalDateTime firstUpdatedAt = user.getUpdatedAt();
        
        Thread.sleep(10); // Small delay to ensure different timestamps
        
        // Second call
        user.onCreate();
        LocalDateTime secondCreatedAt = user.getCreatedAt();
        
        user.onUpdate();
        LocalDateTime secondUpdatedAt = user.getUpdatedAt();

        // Then
        assertThat(secondCreatedAt).isAfter(firstCreatedAt);
        assertThat(secondUpdatedAt).isAfter(firstUpdatedAt);
    }

    /**
     * Test UserRole enum values.
     * Should contain all expected enum constants.
     */
    @Test
    void userRole_EnumValues_ShouldContainAllExpectedConstants() {
        // When
        UserRole[] roles = UserRole.values();

        // Then
        assertThat(roles).hasSize(4);
        assertThat(roles).containsExactlyInAnyOrder(
            UserRole.CUSTOMER,
            UserRole.ADMIN,
            UserRole.DELIVERY,
            UserRole.RESTAURANT_OWNER
        );
    }

    /**
     * Test UserRole enum valueOf method.
     * Should return correct enum constant for each string value.
     */
    @Test
    void userRole_ValueOf_ShouldReturnCorrectEnumConstant() {
        // When & Then
        assertThat(UserRole.valueOf("CUSTOMER")).isEqualTo(UserRole.CUSTOMER);
        assertThat(UserRole.valueOf("ADMIN")).isEqualTo(UserRole.ADMIN);
        assertThat(UserRole.valueOf("DELIVERY")).isEqualTo(UserRole.DELIVERY);
        assertThat(UserRole.valueOf("RESTAURANT_OWNER")).isEqualTo(UserRole.RESTAURANT_OWNER);
    }

    /**
     * Test UserRole enum name method.
     * Should return correct string representation for each enum constant.
     */
    @Test
    void userRole_Name_ShouldReturnCorrectStringRepresentation() {
        // When & Then
        assertThat(UserRole.CUSTOMER.name()).isEqualTo("CUSTOMER");
        assertThat(UserRole.ADMIN.name()).isEqualTo("ADMIN");
        assertThat(UserRole.DELIVERY.name()).isEqualTo("DELIVERY");
        assertThat(UserRole.RESTAURANT_OWNER.name()).isEqualTo("RESTAURANT_OWNER");
    }

    /**
     * Test UserRole enum ordinal method.
     * Should return correct ordinal position for each enum constant.
     */
    @Test
    void userRole_Ordinal_ShouldReturnCorrectOrdinalPosition() {
        // When & Then
        assertThat(UserRole.CUSTOMER.ordinal()).isEqualTo(0);
        assertThat(UserRole.ADMIN.ordinal()).isEqualTo(1);
        assertThat(UserRole.DELIVERY.ordinal()).isEqualTo(2);
        assertThat(UserRole.RESTAURANT_OWNER.ordinal()).isEqualTo(3);
    }

    /**
     * Test UserRole enum toString method.
     * Should return correct string representation (same as name() for basic enums).
     */
    @Test
    void userRole_ToString_ShouldReturnCorrectStringRepresentation() {
        // When & Then
        assertThat(UserRole.CUSTOMER.toString()).isEqualTo("CUSTOMER");
        assertThat(UserRole.ADMIN.toString()).isEqualTo("ADMIN");
        assertThat(UserRole.DELIVERY.toString()).isEqualTo("DELIVERY");
        assertThat(UserRole.RESTAURANT_OWNER.toString()).isEqualTo("RESTAURANT_OWNER");
    }

    /**
     * Test UserRole enum equality.
     * Should properly compare enum constants.
     */
    @Test
    void userRole_Equality_ShouldProperlyCompareEnumConstants() {
        // When & Then
        assertThat(UserRole.CUSTOMER).isEqualTo(UserRole.CUSTOMER);
        assertThat(UserRole.CUSTOMER).isNotEqualTo(UserRole.ADMIN);
        assertThat(UserRole.ADMIN).isNotEqualTo(UserRole.DELIVERY);
        assertThat(UserRole.DELIVERY).isNotEqualTo(UserRole.RESTAURANT_OWNER);
        assertThat(UserRole.RESTAURANT_OWNER).isEqualTo(UserRole.RESTAURANT_OWNER);
    }

    /**
     * Test User entity with extreme ID values.
     * Should handle Long.MAX_VALUE and Long.MIN_VALUE properly.
     */
    @Test
    void user_WithExtremeIdValues_ShouldHandleExtremeLongValuesProperly() {
        // Given
        User user = new User();

        // Test Long.MAX_VALUE
        user.setId(Long.MAX_VALUE);
        assertThat(user.getId()).isEqualTo(Long.MAX_VALUE);

        // Test Long.MIN_VALUE
        user.setId(Long.MIN_VALUE);
        assertThat(user.getId()).isEqualTo(Long.MIN_VALUE);

        // Test zero
        user.setId(0L);
        assertThat(user.getId()).isEqualTo(0L);

        // Test negative values
        user.setId(-1L);
        assertThat(user.getId()).isEqualTo(-1L);
    }
}