package com.fooddelivery.user.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for GlobalExceptionHandler to achieve 100% line and branch coverage.
 * Tests all exception handler methods and their various branches.
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private WebRequest webRequest;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
    }

    /**
     * Test HttpMessageNotReadableException handler with POST method.
     * Should return BAD_REQUEST with JSON parsing error message.
     */
    @Test
    void handleHttpMessageNotReadable_WithPostMethod_ShouldReturnBadRequestWithJsonError() {
        // Given
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Invalid JSON");
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/users");

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleHttpMessageNotReadable(
                exception, webRequest, httpServletRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(400);
        assertThat(body.get("error")).isEqualTo("Bad Request");
        assertThat(body.get("message")).asString().contains("Invalid JSON format");
        assertThat(body.get("details")).asString().contains("Invalid JSON");
        assertThat(body.get("path")).isEqualTo("/api/users");
        assertThat(body.get("timestamp")).isInstanceOf(LocalDateTime.class);
    }

    /**
     * Test HttpMessageNotReadableException handler with PUT method.
     * Should return BAD_REQUEST with JSON parsing error message.
     */
    @Test
    void handleHttpMessageNotReadable_WithPutMethod_ShouldReturnBadRequestWithJsonError() {
        // Given
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Malformed JSON");
        when(httpServletRequest.getMethod()).thenReturn("PUT");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/users/1");

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleHttpMessageNotReadable(
                exception, webRequest, httpServletRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(400);
        assertThat(body.get("error")).isEqualTo("Bad Request");
        assertThat(body.get("message")).asString().contains("Invalid JSON format");
        assertThat(body.get("details")).asString().contains("Malformed JSON");
        assertThat(body.get("path")).isEqualTo("/api/users/1");
    }

    /**
     * Test HttpMessageNotReadableException handler with PATCH method.
     * Should return BAD_REQUEST with JSON parsing error message.
     */
    @Test
    void handleHttpMessageNotReadable_WithPatchMethod_ShouldReturnBadRequestWithJsonError() {
        // Given
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("JSON parse error");
        when(httpServletRequest.getMethod()).thenReturn("PATCH");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/users/1");

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleHttpMessageNotReadable(
                exception, webRequest, httpServletRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(400);
        assertThat(body.get("details")).asString().contains("JSON parse error");
    }

    /**
     * Test HttpMessageNotReadableException handler with GET method.
     * Should return null to let Spring handle naturally.
     */
    @Test
    void handleHttpMessageNotReadable_WithGetMethod_ShouldReturnNull() {
        // Given
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Invalid JSON");
        when(httpServletRequest.getMethod()).thenReturn("GET");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/users");

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleHttpMessageNotReadable(
                exception, webRequest, httpServletRequest);

        // Then
        assertThat(response).isNull();
    }

    /**
     * Test HttpMessageNotReadableException handler with DELETE method.
     * Should return null to let Spring handle naturally.
     */
    @Test
    void handleHttpMessageNotReadable_WithDeleteMethod_ShouldReturnNull() {
        // Given
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Invalid JSON");
        when(httpServletRequest.getMethod()).thenReturn("DELETE");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/users/1");

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleHttpMessageNotReadable(
                exception, webRequest, httpServletRequest);

        // Then
        assertThat(response).isNull();
    }

    /**
     * Test HttpMessageNotReadableException handler with HEAD method.
     * Should return null to let Spring handle naturally.
     */
    @Test
    void handleHttpMessageNotReadable_WithHeadMethod_ShouldReturnNull() {
        // Given
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Invalid JSON");
        when(httpServletRequest.getMethod()).thenReturn("HEAD");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/users");

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleHttpMessageNotReadable(
                exception, webRequest, httpServletRequest);

        // Then
        assertThat(response).isNull();
    }

    /**
     * Test HttpMessageNotReadableException handler with OPTIONS method.
     * Should return null to let Spring handle naturally.
     */
    @Test
    void handleHttpMessageNotReadable_WithOptionsMethod_ShouldReturnNull() {
        // Given
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Invalid JSON");
        when(httpServletRequest.getMethod()).thenReturn("OPTIONS");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/users");

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleHttpMessageNotReadable(
                exception, webRequest, httpServletRequest);

        // Then
        assertThat(response).isNull();
    }

    /**
     * Test HttpMessageNotReadableException handler with actuator endpoint.
     * Should return null to let Spring handle naturally.
     */
    @Test
    void handleHttpMessageNotReadable_WithActuatorEndpoint_ShouldReturnNull() {
        // Given
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Invalid JSON");
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn("/actuator/health");

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleHttpMessageNotReadable(
                exception, webRequest, httpServletRequest);

        // Then
        assertThat(response).isNull();
    }

    /**
     * Test HttpMessageNotReadableException handler with actuator path containing actuator.
     * Should return null to let Spring handle naturally.
     */
    @Test
    void handleHttpMessageNotReadable_WithActuatorPath_ShouldReturnNull() {
        // Given
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Invalid JSON");
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/actuator/metrics");

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleHttpMessageNotReadable(
                exception, webRequest, httpServletRequest);

        // Then
        assertThat(response).isNull();
    }

    /**
     * Test HttpMessageNotReadableException handler with null path.
     * Should use webRequest description as fallback.
     */
    @Test
    void handleHttpMessageNotReadable_WithNullPath_ShouldUseWebRequestDescription() {
        // Given
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Invalid JSON");
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn(null);

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleHttpMessageNotReadable(
                exception, webRequest, httpServletRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("path")).isEqualTo("/api/test"); // From webRequest.getDescription
    }

    /**
     * Test HttpMessageNotReadableException handler with unknown method.
     * Should return null to let Spring handle naturally.
     */
    @Test
    void handleHttpMessageNotReadable_WithUnknownMethod_ShouldReturnNull() {
        // Given
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Invalid JSON");
        when(httpServletRequest.getMethod()).thenReturn("UNKNOWN");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/users");

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleHttpMessageNotReadable(
                exception, webRequest, httpServletRequest);

        // Then
        assertThat(response).isNull();
    }

    /**
     * Test MethodArgumentNotValidException handler.
     * Should return BAD_REQUEST with validation errors.
     */
    @Test
    void handleValidationExceptions_WithValidationErrors_ShouldReturnBadRequestWithErrors() {
        // Given
        FieldError fieldError1 = new FieldError("user", "email", "Email is required");
        FieldError fieldError2 = new FieldError("user", "name", "Name must be between 2 and 50 characters");
        List<FieldError> fieldErrors = Arrays.asList(fieldError1, fieldError2);
        
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleValidationExceptions(
                methodArgumentNotValidException, webRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(400);
        assertThat(body.get("error")).isEqualTo("Validation Failed");
        assertThat(body.get("message")).isEqualTo("Input validation failed");
        assertThat(body.get("path")).isEqualTo("/api/test");
        assertThat(body.get("timestamp")).isInstanceOf(LocalDateTime.class);
        
        @SuppressWarnings("unchecked")
        Map<String, String> validationErrors = (Map<String, String>) body.get("validationErrors");
        assertThat(validationErrors).isNotNull();
        assertThat(validationErrors.get("email")).isEqualTo("Email is required");
        assertThat(validationErrors.get("name")).isEqualTo("Name must be between 2 and 50 characters");
    }

    /**
     * Test IllegalArgumentException handler.
     * Should return BAD_REQUEST with exception message.
     */
    @Test
    void handleIllegalArgumentException_WithMessage_ShouldReturnBadRequestWithMessage() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument provided");

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleIllegalArgumentException(
                exception, webRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(400);
        assertThat(body.get("error")).isEqualTo("Bad Request");
        assertThat(body.get("message")).isEqualTo("Invalid argument provided");
        assertThat(body.get("path")).isEqualTo("/api/test");
        assertThat(body.get("timestamp")).isInstanceOf(LocalDateTime.class);
    }

    /**
     * Test UserAlreadyExistsException handler.
     * Should return CONFLICT with exception details.
     */
    @Test
    void handleUserAlreadyExistsException_WithFieldAndValue_ShouldReturnConflictWithDetails() {
        // Given
        UserAlreadyExistsException exception = new UserAlreadyExistsException("email", "john.doe@example.com");

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleUserAlreadyExistsException(
                exception, webRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(409);
        assertThat(body.get("error")).isEqualTo("Conflict");
        assertThat(body.get("message")).asString().contains("john.doe@example.com");
        assertThat(body.get("field")).isEqualTo("email");
        assertThat(body.get("value")).isEqualTo("john.doe@example.com");
        assertThat(body.get("path")).isEqualTo("/api/test");
        assertThat(body.get("timestamp")).isInstanceOf(LocalDateTime.class);
    }

    /**
     * Test DataIntegrityViolationException handler with email duplicate.
     * Should return CONFLICT with email-specific message.
     */
    @Test
    void handleDataIntegrityViolationException_WithEmailDuplicate_ShouldReturnConflictWithEmailMessage() {
        // Given
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "Duplicate entry 'test@example.com' for key 'email'");

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleDataIntegrityViolationException(
                exception, webRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(409);
        assertThat(body.get("error")).isEqualTo("Conflict");
        assertThat(body.get("message")).isEqualTo("User with this email already exists");
        assertThat(body.get("field")).isEqualTo("email");
        assertThat(body.get("path")).isEqualTo("/api/test");
        assertThat(body.get("timestamp")).isInstanceOf(LocalDateTime.class);
    }

    /**
     * Test DataIntegrityViolationException handler with username duplicate.
     * Should return CONFLICT with username-specific message.
     */
    @Test
    void handleDataIntegrityViolationException_WithUsernameDuplicate_ShouldReturnConflictWithUsernameMessage() {
        // Given
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "Duplicate entry 'johndoe' for key 'username'");

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleDataIntegrityViolationException(
                exception, webRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("message")).isEqualTo("User with this username already exists");
        assertThat(body.get("field")).isEqualTo("username");
    }

    /**
     * Test DataIntegrityViolationException handler with general duplicate.
     * Should return CONFLICT with general duplicate message.
     */
    @Test
    void handleDataIntegrityViolationException_WithGeneralDuplicate_ShouldReturnConflictWithGeneralMessage() {
        // Given
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "Duplicate entry for some key");

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleDataIntegrityViolationException(
                exception, webRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("message")).isEqualTo("User with the provided information already exists");
    }

    /**
     * Test DataIntegrityViolationException handler with non-duplicate error.
     * Should return CONFLICT with general constraint violation message.
     */
    @Test
    void handleDataIntegrityViolationException_WithNonDuplicateError_ShouldReturnConflictWithConstraintMessage() {
        // Given
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "Foreign key constraint violation");

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleDataIntegrityViolationException(
                exception, webRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("message")).isEqualTo("Data integrity constraint violation");
    }

    /**
     * Test DataIntegrityViolationException handler with null message.
     * Should return CONFLICT with general constraint violation message.
     */
    @Test
    void handleDataIntegrityViolationException_WithNullMessage_ShouldReturnConflictWithConstraintMessage() {
        // Given
        DataIntegrityViolationException exception = new DataIntegrityViolationException(null);

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleDataIntegrityViolationException(
                exception, webRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("message")).isEqualTo("Data integrity constraint violation");
    }

    /**
     * Test RuntimeException handler with "already exists" message.
     * Should return CONFLICT when message contains "already exists".
     */
    @Test
    void handleRuntimeException_WithAlreadyExistsMessage_ShouldReturnConflict() {
        // Given
        RuntimeException exception = new RuntimeException("User already exists in the system");

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleRuntimeException(
                exception, webRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(409);
        assertThat(body.get("error")).isEqualTo("Conflict");
        assertThat(body.get("message")).isEqualTo("User already exists in the system");
        assertThat(body.get("path")).isEqualTo("/api/test");
        assertThat(body.get("timestamp")).isInstanceOf(LocalDateTime.class);
    }

    /**
     * Test RuntimeException handler with general error message.
     * Should return INTERNAL_SERVER_ERROR for general runtime exceptions.
     */
    @Test
    void handleRuntimeException_WithGeneralError_ShouldReturnInternalServerError() {
        // Given
        RuntimeException exception = new RuntimeException("General runtime error");

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleRuntimeException(
                exception, webRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(500);
        assertThat(body.get("error")).isEqualTo("Internal Server Error");
        assertThat(body.get("message")).asString().contains("General runtime error");
        assertThat(body.get("path")).isEqualTo("/api/test");
        assertThat(body.get("timestamp")).isInstanceOf(LocalDateTime.class);
    }

    /**
     * Test RuntimeException handler with null message.
     * Should return INTERNAL_SERVER_ERROR with null message handling.
     */
    @Test
    void handleRuntimeException_WithNullMessage_ShouldReturnInternalServerError() {
        // Given
        RuntimeException exception = new RuntimeException((String) null);

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleRuntimeException(
                exception, webRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(500);
        assertThat(body.get("error")).isEqualTo("Internal Server Error");
    }

    /**
     * Test generic Exception handler.
     * Should return INTERNAL_SERVER_ERROR with generic error message.
     */
    @Test
    void handleGenericException_WithAnyException_ShouldReturnInternalServerError() {
        // Given
        Exception exception = new Exception("Generic exception occurred");

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleGenericException(
                exception, webRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(500);
        assertThat(body.get("error")).isEqualTo("Internal Server Error");
        assertThat(body.get("message")).isEqualTo("An unexpected error occurred");
        assertThat(body.get("details")).isEqualTo("Generic exception occurred");
        assertThat(body.get("path")).isEqualTo("/api/test");
        assertThat(body.get("timestamp")).isInstanceOf(LocalDateTime.class);
    }

    /**
     * Test generic Exception handler with null message.
     * Should return INTERNAL_SERVER_ERROR with null details.
     */
    @Test
    void handleGenericException_WithNullMessage_ShouldReturnInternalServerErrorWithNullDetails() {
        // Given
        Exception exception = new Exception((String) null);

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleGenericException(
                exception, webRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("message")).isEqualTo("An unexpected error occurred");
        assertThat(body.get("details")).isNull();
    }

    /**
     * Test HttpMessageNotReadableException handler with null path.
     * Covers line 42 false branch: if (path != null && (path.contains("/actuator/") || path.startsWith("/actuator")))
     */
    @Test
    void handleHttpMessageNotReadable_WithNullPath_ShouldProcessNormally() {
        // Given
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Invalid JSON");
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn(null);

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleHttpMessageNotReadable(
                exception, webRequest, httpServletRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(400);
        assertThat(body.get("error")).isEqualTo("Bad Request");
        assertThat(body.get("message")).asString().contains("Invalid JSON format");
    }

    /**
     * Test HttpMessageNotReadableException handler with regular API endpoint (non-actuator).
     * Covers line 42 false branch where path is NOT null but does NOT contain "/actuator/".
     * This tests the case: path != null (TRUE) && path.contains("/actuator/") (FALSE) && path.startsWith("/actuator") (FALSE)
     */
    @Test
    void handleHttpMessageNotReadable_WithNonActuatorPath_ShouldProcessNormally() {
        // Given
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Invalid JSON");
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/users/register");

        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleHttpMessageNotReadable(
                exception, webRequest, httpServletRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(400);
        assertThat(body.get("error")).isEqualTo("Bad Request");
        assertThat(body.get("message")).asString().contains("Invalid JSON format");
        assertThat(body.get("path")).isEqualTo("/api/users/register");
        assertThat(body.get("timestamp")).isInstanceOf(LocalDateTime.class);
    }
}