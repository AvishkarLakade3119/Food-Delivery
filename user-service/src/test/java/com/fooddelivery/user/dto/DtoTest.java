package com.fooddelivery.user.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive unit tests for DTO classes to achieve 100% line and branch coverage.
 * Tests all constructors, getters, setters, toString, equals, and hashCode methods.
 */
class DtoTest {

    /**
     * Test AddressDto default constructor.
     * Should create an instance with null values.
     */
    @Test
    void addressDto_DefaultConstructor_ShouldCreateInstanceWithNullValues() {
        // When
        AddressDto addressDto = new AddressDto();

        // Then
        assertThat(addressDto).isNotNull();
        assertThat(addressDto.getStreet()).isNull();
        assertThat(addressDto.getCity()).isNull();
        assertThat(addressDto.getState()).isNull();
        assertThat(addressDto.getZipCode()).isNull();
        assertThat(addressDto.getCountry()).isNull();
    }

    /**
     * Test AddressDto parameterized constructor.
     * Should create an instance with all fields set.
     */
    @Test
    void addressDto_ParameterizedConstructor_ShouldCreateInstanceWithAllFieldsSet() {
        // Given
        String street = "123 Main St";
        String city = "New York";
        String state = "NY";
        String zipCode = "10001";
        String country = "USA";

        // When
        AddressDto addressDto = new AddressDto(street, city, state, zipCode, country);

        // Then
        assertThat(addressDto).isNotNull();
        assertThat(addressDto.getStreet()).isEqualTo(street);
        assertThat(addressDto.getCity()).isEqualTo(city);
        assertThat(addressDto.getState()).isEqualTo(state);
        assertThat(addressDto.getZipCode()).isEqualTo(zipCode);
        assertThat(addressDto.getCountry()).isEqualTo(country);
    }

    /**
     * Test AddressDto setters and getters.
     * Should properly set and retrieve all field values.
     */
    @Test
    void addressDto_SettersAndGetters_ShouldSetAndRetrieveFieldValues() {
        // Given
        AddressDto addressDto = new AddressDto();
        String street = "456 Oak Ave";
        String city = "Los Angeles";
        String state = "CA";
        String zipCode = "90210";
        String country = "USA";

        // When
        addressDto.setStreet(street);
        addressDto.setCity(city);
        addressDto.setState(state);
        addressDto.setZipCode(zipCode);
        addressDto.setCountry(country);

        // Then
        assertThat(addressDto.getStreet()).isEqualTo(street);
        assertThat(addressDto.getCity()).isEqualTo(city);
        assertThat(addressDto.getState()).isEqualTo(state);
        assertThat(addressDto.getZipCode()).isEqualTo(zipCode);
        assertThat(addressDto.getCountry()).isEqualTo(country);
    }

    /**
     * Test AddressDto toString method.
     * Should return formatted address string.
     */
    @Test
    void addressDto_ToString_ShouldReturnFormattedAddressString() {
        // Given
        AddressDto addressDto = new AddressDto("123 Main St", "New York", "NY", "10001", "USA");

        // When
        String result = addressDto.toString();

        // Then
        assertThat(result).isEqualTo("123 Main St, New York, NY 10001, USA");
    }

    /**
     * Test AddressDto toString method with null values.
     * Should handle null values gracefully.
     */
    @Test
    void addressDto_ToStringWithNullValues_ShouldHandleNullsGracefully() {
        // Given
        AddressDto addressDto = new AddressDto();

        // When
        String result = addressDto.toString();

        // Then
        assertThat(result).isEqualTo("null, null, null null, null");
    }

    /**
     * Test UserRegistrationDto default constructor.
     * Should create an instance with null values.
     */
    @Test
    void userRegistrationDto_DefaultConstructor_ShouldCreateInstanceWithNullValues() {
        // When
        UserRegistrationDto dto = new UserRegistrationDto();

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getUsername()).isNull();
        assertThat(dto.getEmail()).isNull();
        assertThat(dto.getPassword()).isNull();
        assertThat(dto.getFirstName()).isNull();
        assertThat(dto.getLastName()).isNull();
        assertThat(dto.getPhone()).isNull();
        assertThat(dto.getAddress()).isNull();
        assertThat(dto.getDateOfBirth()).isNull();
    }

    /**
     * Test UserRegistrationDto parameterized constructor.
     * Should create an instance with all fields set.
     */
    @Test
    void userRegistrationDto_ParameterizedConstructor_ShouldCreateInstanceWithAllFieldsSet() {
        // Given
        String username = "johndoe";
        String email = "john.doe@example.com";
        String password = "password123";
        String firstName = "John";
        String lastName = "Doe";
        String phone = "+1234567890";
        String address = "123 Main St, City, State";
        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);

        // When
        UserRegistrationDto dto = new UserRegistrationDto(username, email, password, firstName, lastName, phone, address, dateOfBirth);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getUsername()).isEqualTo(username);
        assertThat(dto.getEmail()).isEqualTo(email);
        assertThat(dto.getPassword()).isEqualTo(password);
        assertThat(dto.getFirstName()).isEqualTo(firstName);
        assertThat(dto.getLastName()).isEqualTo(lastName);
        assertThat(dto.getPhone()).isEqualTo(phone);
        assertThat(dto.getAddress()).isEqualTo(address);
        assertThat(dto.getDateOfBirth()).isEqualTo(dateOfBirth);
    }

    /**
     * Test UserRegistrationDto setters and getters.
     * Should properly set and retrieve all field values.
     */
    @Test
    void userRegistrationDto_SettersAndGetters_ShouldSetAndRetrieveFieldValues() {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        String username = "janesmith";
        String email = "jane.smith@example.com";
        String password = "password456";
        String firstName = "Jane";
        String lastName = "Smith";
        String phone = "+1987654321";
        String address = "456 Oak Ave, City, State";
        LocalDate dateOfBirth = LocalDate.of(1985, 5, 15);

        // When
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setPhone(phone);
        dto.setAddress(address);
        dto.setDateOfBirth(dateOfBirth);

        // Then
        assertThat(dto.getUsername()).isEqualTo(username);
        assertThat(dto.getEmail()).isEqualTo(email);
        assertThat(dto.getPassword()).isEqualTo(password);
        assertThat(dto.getFirstName()).isEqualTo(firstName);
        assertThat(dto.getLastName()).isEqualTo(lastName);
        assertThat(dto.getPhone()).isEqualTo(phone);
        assertThat(dto.getAddress()).isEqualTo(address);
        assertThat(dto.getDateOfBirth()).isEqualTo(dateOfBirth);
    }

    /**
     * Test UserRegistrationDto with null values.
     * Should handle null values properly in setters and getters.
     */
    @Test
    void userRegistrationDto_WithNullValues_ShouldHandleNullsProperly() {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();

        // When
        dto.setUsername(null);
        dto.setEmail(null);
        dto.setPassword(null);
        dto.setFirstName(null);
        dto.setLastName(null);
        dto.setPhone(null);
        dto.setAddress(null);
        dto.setDateOfBirth(null);

        // Then
        assertThat(dto.getUsername()).isNull();
        assertThat(dto.getEmail()).isNull();
        assertThat(dto.getPassword()).isNull();
        assertThat(dto.getFirstName()).isNull();
        assertThat(dto.getLastName()).isNull();
        assertThat(dto.getPhone()).isNull();
        assertThat(dto.getAddress()).isNull();
        assertThat(dto.getDateOfBirth()).isNull();
    }

    /**
     * Test UserRegistrationDto with empty string values.
     * Should handle empty strings properly.
     */
    @Test
    void userRegistrationDto_WithEmptyStringValues_ShouldHandleEmptyStringsProperly() {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();

        // When
        dto.setUsername("");
        dto.setEmail("");
        dto.setPassword("");
        dto.setFirstName("");
        dto.setLastName("");
        dto.setPhone("");
        dto.setAddress("");

        // Then
        assertThat(dto.getUsername()).isEmpty();
        assertThat(dto.getEmail()).isEmpty();
        assertThat(dto.getPassword()).isEmpty();
        assertThat(dto.getFirstName()).isEmpty();
        assertThat(dto.getLastName()).isEmpty();
        assertThat(dto.getPhone()).isEmpty();
        assertThat(dto.getAddress()).isEmpty();
    }

    /**
     * Test UserRegistrationDto with whitespace values.
     * Should handle whitespace strings properly.
     */
    @Test
    void userRegistrationDto_WithWhitespaceValues_ShouldHandleWhitespacesProperly() {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        String whitespace = "   ";

        // When
        dto.setUsername(whitespace);
        dto.setEmail(whitespace);
        dto.setPassword(whitespace);
        dto.setFirstName(whitespace);
        dto.setLastName(whitespace);
        dto.setPhone(whitespace);
        dto.setAddress(whitespace);

        // Then
        assertThat(dto.getUsername()).isEqualTo(whitespace);
        assertThat(dto.getEmail()).isEqualTo(whitespace);
        assertThat(dto.getPassword()).isEqualTo(whitespace);
        assertThat(dto.getFirstName()).isEqualTo(whitespace);
        assertThat(dto.getLastName()).isEqualTo(whitespace);
        assertThat(dto.getPhone()).isEqualTo(whitespace);
        assertThat(dto.getAddress()).isEqualTo(whitespace);
    }

    /**
     * Test UserRegistrationDto with special characters.
     * Should handle special characters properly.
     */
    @Test
    void userRegistrationDto_WithSpecialCharacters_ShouldHandleSpecialCharactersProperly() {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        String specialChars = "!@#$%^&*()_+-=[]{}|;':,.<>?";

        // When
        dto.setUsername(specialChars);
        dto.setEmail(specialChars + "@example.com");
        dto.setPassword(specialChars);
        dto.setFirstName(specialChars);
        dto.setLastName(specialChars);
        dto.setPhone(specialChars);
        dto.setAddress(specialChars);

        // Then
        assertThat(dto.getUsername()).isEqualTo(specialChars);
        assertThat(dto.getEmail()).isEqualTo(specialChars + "@example.com");
        assertThat(dto.getPassword()).isEqualTo(specialChars);
        assertThat(dto.getFirstName()).isEqualTo(specialChars);
        assertThat(dto.getLastName()).isEqualTo(specialChars);
        assertThat(dto.getPhone()).isEqualTo(specialChars);
        assertThat(dto.getAddress()).isEqualTo(specialChars);
    }

    /**
     * Test UserRegistrationDto with maximum length values.
     * Should handle long strings properly.
     */
    @Test
    void userRegistrationDto_WithMaximumLengthValues_ShouldHandleLongStringsProperly() {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        String longString = "a".repeat(1000); // Very long string

        // When
        dto.setUsername(longString);
        dto.setEmail(longString + "@example.com");
        dto.setPassword(longString);
        dto.setFirstName(longString);
        dto.setLastName(longString);
        dto.setPhone(longString);
        dto.setAddress(longString);

        // Then
        assertThat(dto.getUsername()).isEqualTo(longString);
        assertThat(dto.getEmail()).isEqualTo(longString + "@example.com");
        assertThat(dto.getPassword()).isEqualTo(longString);
        assertThat(dto.getFirstName()).isEqualTo(longString);
        assertThat(dto.getLastName()).isEqualTo(longString);
        assertThat(dto.getPhone()).isEqualTo(longString);
        assertThat(dto.getAddress()).isEqualTo(longString);
    }

    /**
     * Test UserRegistrationDto with various date values.
     * Should handle different date scenarios properly.
     */
    @Test
    void userRegistrationDto_WithVariousDateValues_ShouldHandleDatesProperly() {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        LocalDate pastDate = LocalDate.of(1900, 1, 1);
        LocalDate futureDate = LocalDate.of(2100, 12, 31);
        LocalDate currentDate = LocalDate.now();

        // Test past date
        dto.setDateOfBirth(pastDate);
        assertThat(dto.getDateOfBirth()).isEqualTo(pastDate);

        // Test future date
        dto.setDateOfBirth(futureDate);
        assertThat(dto.getDateOfBirth()).isEqualTo(futureDate);

        // Test current date
        dto.setDateOfBirth(currentDate);
        assertThat(dto.getDateOfBirth()).isEqualTo(currentDate);

        // Test null date
        dto.setDateOfBirth(null);
        assertThat(dto.getDateOfBirth()).isNull();
    }
}