package com.fooddelivery.user.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive unit tests for Exception classes to achieve 100% line and branch coverage.
 * Tests all constructors and methods of custom exception classes.
 */
class ExceptionTest {

    /**
     * Test UserAlreadyExistsException default constructor with message only.
     * Should create exception with message and default field values.
     */
    @Test
    void userAlreadyExistsException_DefaultConstructorWithMessage_ShouldCreateExceptionWithDefaultFieldValues() {
        // Given
        String message = "User already exists";

        // When
        UserAlreadyExistsException exception = new UserAlreadyExistsException(message);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getField()).isEqualTo("email"); // Default field
        assertThat(exception.getValue()).isNull(); // Default value
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    /**
     * Test UserAlreadyExistsException constructor with field and value.
     * Should create exception with formatted message and specified field/value.
     */
    @Test
    void userAlreadyExistsException_ConstructorWithFieldAndValue_ShouldCreateExceptionWithFormattedMessage() {
        // Given
        String field = "email";
        String value = "john.doe@example.com";

        // When
        UserAlreadyExistsException exception = new UserAlreadyExistsException(field, value);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("User with email 'john.doe@example.com' already exists");
        assertThat(exception.getField()).isEqualTo(field);
        assertThat(exception.getValue()).isEqualTo(value);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    /**
     * Test UserAlreadyExistsException constructor with field, value, and custom message.
     * Should create exception with custom message and specified field/value.
     */
    @Test
    void userAlreadyExistsException_ConstructorWithFieldValueAndMessage_ShouldCreateExceptionWithCustomMessage() {
        // Given
        String field = "username";
        String value = "johndoe";
        String customMessage = "Custom error message for duplicate user";

        // When
        UserAlreadyExistsException exception = new UserAlreadyExistsException(field, value, customMessage);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(customMessage);
        assertThat(exception.getField()).isEqualTo(field);
        assertThat(exception.getValue()).isEqualTo(value);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    /**
     * Test UserAlreadyExistsException with null message.
     * Should handle null message properly.
     */
    @Test
    void userAlreadyExistsException_WithNullMessage_ShouldHandleNullMessageProperly() {
        // Given
        String message = null;

        // When
        UserAlreadyExistsException exception = new UserAlreadyExistsException(message);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getField()).isEqualTo("email"); // Default field
        assertThat(exception.getValue()).isNull(); // Default value
    }

    /**
     * Test UserAlreadyExistsException with empty message.
     * Should handle empty message properly.
     */
    @Test
    void userAlreadyExistsException_WithEmptyMessage_ShouldHandleEmptyMessageProperly() {
        // Given
        String message = "";

        // When
        UserAlreadyExistsException exception = new UserAlreadyExistsException(message);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEmpty();
        assertThat(exception.getField()).isEqualTo("email"); // Default field
        assertThat(exception.getValue()).isNull(); // Default value
    }

    /**
     * Test UserAlreadyExistsException with whitespace message.
     * Should handle whitespace message properly.
     */
    @Test
    void userAlreadyExistsException_WithWhitespaceMessage_ShouldHandleWhitespaceMessageProperly() {
        // Given
        String message = "   ";

        // When
        UserAlreadyExistsException exception = new UserAlreadyExistsException(message);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getField()).isEqualTo("email"); // Default field
        assertThat(exception.getValue()).isNull(); // Default value
    }

    /**
     * Test UserAlreadyExistsException with null field and value.
     * Should handle null field and value properly.
     */
    @Test
    void userAlreadyExistsException_WithNullFieldAndValue_ShouldHandleNullsProperly() {
        // Given
        String field = null;
        String value = null;

        // When
        UserAlreadyExistsException exception = new UserAlreadyExistsException(field, value);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("User with null 'null' already exists");
        assertThat(exception.getField()).isNull();
        assertThat(exception.getValue()).isNull();
    }

    /**
     * Test UserAlreadyExistsException with empty field and value.
     * Should handle empty field and value properly.
     */
    @Test
    void userAlreadyExistsException_WithEmptyFieldAndValue_ShouldHandleEmptyStringsProperly() {
        // Given
        String field = "";
        String value = "";

        // When
        UserAlreadyExistsException exception = new UserAlreadyExistsException(field, value);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("User with  '' already exists");
        assertThat(exception.getField()).isEmpty();
        assertThat(exception.getValue()).isEmpty();
    }

    /**
     * Test UserAlreadyExistsException with whitespace field and value.
     * Should handle whitespace field and value properly.
     */
    @Test
    void userAlreadyExistsException_WithWhitespaceFieldAndValue_ShouldHandleWhitespacesProperly() {
        // Given
        String field = "   ";
        String value = "   ";

        // When
        UserAlreadyExistsException exception = new UserAlreadyExistsException(field, value);

        // Then
        assertThat(exception).isNotNull();
        // The actual message format includes the field and value as-is
        assertThat(exception.getMessage()).contains("User with");
        assertThat(exception.getMessage()).contains("already exists");
        assertThat(exception.getField()).isEqualTo(field);
        assertThat(exception.getValue()).isEqualTo(value);
    }

    /**
     * Test UserAlreadyExistsException with special characters in field and value.
     * Should handle special characters properly.
     */
    @Test
    void userAlreadyExistsException_WithSpecialCharacters_ShouldHandleSpecialCharactersProperly() {
        // Given
        String field = "email@field";
        String value = "user@example.com";

        // When
        UserAlreadyExistsException exception = new UserAlreadyExistsException(field, value);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("User with email@field 'user@example.com' already exists");
        assertThat(exception.getField()).isEqualTo(field);
        assertThat(exception.getValue()).isEqualTo(value);
    }

    /**
     * Test UserAlreadyExistsException with very long field and value.
     * Should handle long strings properly.
     */
    @Test
    void userAlreadyExistsException_WithLongFieldAndValue_ShouldHandleLongStringsProperly() {
        // Given
        String field = "a".repeat(1000); // Very long field
        String value = "b".repeat(1000); // Very long value

        // When
        UserAlreadyExistsException exception = new UserAlreadyExistsException(field, value);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains(field);
        assertThat(exception.getMessage()).contains(value);
        assertThat(exception.getField()).isEqualTo(field);
        assertThat(exception.getValue()).isEqualTo(value);
    }

    /**
     * Test UserAlreadyExistsException with different field types.
     * Should handle various field names properly.
     */
    @Test
    void userAlreadyExistsException_WithDifferentFieldTypes_ShouldHandleVariousFieldNamesProperly() {
        // Test email field
        UserAlreadyExistsException emailException = new UserAlreadyExistsException("email", "test@example.com");
        assertThat(emailException.getField()).isEqualTo("email");
        assertThat(emailException.getValue()).isEqualTo("test@example.com");
        assertThat(emailException.getMessage()).contains("email");

        // Test username field
        UserAlreadyExistsException usernameException = new UserAlreadyExistsException("username", "testuser");
        assertThat(usernameException.getField()).isEqualTo("username");
        assertThat(usernameException.getValue()).isEqualTo("testuser");
        assertThat(usernameException.getMessage()).contains("username");

        // Test phone field
        UserAlreadyExistsException phoneException = new UserAlreadyExistsException("phone", "+1234567890");
        assertThat(phoneException.getField()).isEqualTo("phone");
        assertThat(phoneException.getValue()).isEqualTo("+1234567890");
        assertThat(phoneException.getMessage()).contains("phone");
    }

    /**
     * Test UserAlreadyExistsException inheritance.
     * Should properly inherit from RuntimeException.
     */
    @Test
    void userAlreadyExistsException_Inheritance_ShouldProperlyInheritFromRuntimeException() {
        // Given
        UserAlreadyExistsException exception = new UserAlreadyExistsException("Test message");

        // Then
        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception).isInstanceOf(Exception.class);
        assertThat(exception).isInstanceOf(Throwable.class);
    }

    /**
     * Test UserAlreadyExistsException stack trace.
     * Should have proper stack trace information.
     */
    @Test
    void userAlreadyExistsException_StackTrace_ShouldHaveProperStackTraceInformation() {
        // Given
        UserAlreadyExistsException exception = new UserAlreadyExistsException("Test message");

        // Then
        assertThat(exception.getStackTrace()).isNotNull();
        assertThat(exception.getStackTrace()).isNotEmpty();
        assertThat(exception.getStackTrace()[0].getClassName()).contains("ExceptionTest");
        assertThat(exception.getStackTrace()[0].getMethodName()).contains("userAlreadyExistsException_StackTrace_ShouldHaveProperStackTraceInformation");
    }

    /**
     * Test UserAlreadyExistsException cause.
     * Should handle cause properly when set.
     */
    @Test
    void userAlreadyExistsException_WithCause_ShouldHandleCauseProperly() {
        // Given
        Throwable cause = new IllegalArgumentException("Root cause");
        UserAlreadyExistsException exception = new UserAlreadyExistsException("Test message");
        exception.initCause(cause);

        // Then
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getCause().getMessage()).isEqualTo("Root cause");
    }

    /**
     * Test UserAlreadyExistsException toString method.
     * Should return proper string representation.
     */
    @Test
    void userAlreadyExistsException_ToString_ShouldReturnProperStringRepresentation() {
        // Given
        UserAlreadyExistsException exception = new UserAlreadyExistsException("email", "test@example.com");

        // When
        String result = exception.toString();

        // Then
        assertThat(result).contains("UserAlreadyExistsException");
        assertThat(result).contains("User with email 'test@example.com' already exists");
    }

    /**
     * Test UserAlreadyExistsException equals and hashCode.
     * Should handle equality comparison properly (inherited from Throwable).
     */
    @Test
    void userAlreadyExistsException_EqualsAndHashCode_ShouldHandleEqualityProperly() {
        // Given
        UserAlreadyExistsException exception1 = new UserAlreadyExistsException("email", "test@example.com");
        UserAlreadyExistsException exception2 = new UserAlreadyExistsException("email", "test@example.com");
        UserAlreadyExistsException exception3 = new UserAlreadyExistsException("username", "testuser");

        // Then
        assertThat(exception1).isEqualTo(exception1); // Same instance
        assertThat(exception1).isNotEqualTo(exception2); // Different instances (Throwable default behavior)
        assertThat(exception1).isNotEqualTo(exception3); // Different field/value
        assertThat(exception1).isNotEqualTo(null);
        assertThat(exception1).isNotEqualTo("string");

        // HashCode should be consistent
        assertThat(exception1.hashCode()).isEqualTo(exception1.hashCode());
    }

    /**
     * Test UserAlreadyExistsException with null custom message.
     * Should handle null custom message in three-parameter constructor.
     */
    @Test
    void userAlreadyExistsException_WithNullCustomMessage_ShouldHandleNullCustomMessageProperly() {
        // Given
        String field = "email";
        String value = "test@example.com";
        String customMessage = null;

        // When
        UserAlreadyExistsException exception = new UserAlreadyExistsException(field, value, customMessage);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getField()).isEqualTo(field);
        assertThat(exception.getValue()).isEqualTo(value);
    }

    /**
     * Test UserAlreadyExistsException with empty custom message.
     * Should handle empty custom message in three-parameter constructor.
     */
    @Test
    void userAlreadyExistsException_WithEmptyCustomMessage_ShouldHandleEmptyCustomMessageProperly() {
        // Given
        String field = "username";
        String value = "testuser";
        String customMessage = "";

        // When
        UserAlreadyExistsException exception = new UserAlreadyExistsException(field, value, customMessage);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEmpty();
        assertThat(exception.getField()).isEqualTo(field);
        assertThat(exception.getValue()).isEqualTo(value);
    }

    /**
     * Test UserAlreadyExistsException serialization compatibility.
     * Should be serializable as it extends RuntimeException.
     */
    @Test
    void userAlreadyExistsException_Serialization_ShouldBeSerializable() {
        // Given
        UserAlreadyExistsException exception = new UserAlreadyExistsException("email", "test@example.com");

        // Then
        assertThat(exception).isInstanceOf(java.io.Serializable.class);
    }
}