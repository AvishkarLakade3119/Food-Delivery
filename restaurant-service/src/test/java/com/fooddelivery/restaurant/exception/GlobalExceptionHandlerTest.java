package com.fooddelivery.restaurant.exception;

import jakarta.servlet.http.HttpServletRequest;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for GlobalExceptionHandler to achieve 100% line and branch coverage.
 * Tests cover all @ExceptionHandler methods and all conditional branches within each handler.
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    // ===============================
    // HttpMessageNotReadableException Tests
    // ===============================

    /**
     * Test HttpMessageNotReadableException handler returns null for GET request
     */
    @Test
    void testHandleHttpMessageNotReadable_GetRequest_ReturnsNull() {
        // Arrange
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(httpServletRequest.getMethod()).thenReturn("GET");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/restaurants");
        
        // Act
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleHttpMessageNotReadable(ex, webRequest, httpServletRequest);
        
        // Assert
        assertNull(result);
    }

    /**
     * Test HttpMessageNotReadableException handler returns null for DELETE request
     */
    @Test
    void testHandleHttpMessageNotReadable_DeleteRequest_ReturnsNull() {
        // Arrange
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(httpServletRequest.getMethod()).thenReturn("DELETE");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/restaurants/1");
        
        // Act
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleHttpMessageNotReadable(ex, webRequest, httpServletRequest);
        
        // Assert
        assertNull(result);
    }

    /**
     * Test HttpMessageNotReadableException handler returns null for HEAD request
     */
    @Test
    void testHandleHttpMessageNotReadable_HeadRequest_ReturnsNull() {
        // Arrange
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(httpServletRequest.getMethod()).thenReturn("HEAD");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/restaurants");
        
        // Act
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleHttpMessageNotReadable(ex, webRequest, httpServletRequest);
        
        // Assert
        assertNull(result);
    }

    /**
     * Test HttpMessageNotReadableException handler returns null for OPTIONS request
     */
    @Test
    void testHandleHttpMessageNotReadable_OptionsRequest_ReturnsNull() {
        // Arrange
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(httpServletRequest.getMethod()).thenReturn("OPTIONS");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/restaurants");
        
        // Act
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleHttpMessageNotReadable(ex, webRequest, httpServletRequest);
        
        // Assert
        assertNull(result);
    }

    /**
     * Test HttpMessageNotReadableException handler returns null for actuator endpoint with contains check
     */
    @Test
    void testHandleHttpMessageNotReadable_ActuatorEndpoint_Contains_ReturnsNull() {
        // Arrange
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/actuator/health");
        
        // Act
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleHttpMessageNotReadable(ex, webRequest, httpServletRequest);
        
        // Assert
        assertNull(result);
    }

    /**
     * Test HttpMessageNotReadableException handler returns null for actuator endpoint with startsWith check
     */
    @Test
    void testHandleHttpMessageNotReadable_ActuatorEndpoint_StartsWith_ReturnsNull() {
        // Arrange
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn("/actuator");
        
        // Act
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleHttpMessageNotReadable(ex, webRequest, httpServletRequest);
        
        // Assert
        assertNull(result);
    }

    /**
     * Test HttpMessageNotReadableException handler returns error response for POST request
     */
    @Test
    void testHandleHttpMessageNotReadable_PostRequest_ReturnsErrorResponse() {
        // Arrange
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMessage()).thenReturn("JSON parse error");
        when(ex.getLocalizedMessage()).thenReturn("Localized JSON parse error");
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/restaurants");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/restaurants");
        
        // Act
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleHttpMessageNotReadable(ex, webRequest, httpServletRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        
        Map<String, Object> body = result.getBody();
        assertNotNull(body);
        assertNotNull(body.get("timestamp"));
        assertEquals(400, body.get("status"));
        assertEquals("Bad Request", body.get("error"));
        assertEquals("Invalid JSON format in request body. Please ensure the request body contains valid JSON and Content-Type is 'application/json'", body.get("message"));
        assertEquals("JSON parse error: Localized JSON parse error", body.get("details"));
        assertEquals("/api/restaurants", body.get("path"));
    }

    /**
     * Test HttpMessageNotReadableException handler returns error response for PUT request
     */
    @Test
    void testHandleHttpMessageNotReadable_PutRequest_ReturnsErrorResponse() {
        // Arrange
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMessage()).thenReturn("JSON parse error");
        when(ex.getLocalizedMessage()).thenReturn("Localized JSON parse error");
        when(httpServletRequest.getMethod()).thenReturn("PUT");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/restaurants/1");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/restaurants/1");
        
        // Act
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleHttpMessageNotReadable(ex, webRequest, httpServletRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        
        Map<String, Object> body = result.getBody();
        assertNotNull(body);
        assertEquals("/api/restaurants/1", body.get("path"));
    }

    /**
     * Test HttpMessageNotReadableException handler returns error response for PATCH request
     */
    @Test
    void testHandleHttpMessageNotReadable_PatchRequest_ReturnsErrorResponse() {
        // Arrange
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMessage()).thenReturn("JSON parse error");
        when(ex.getLocalizedMessage()).thenReturn("Localized JSON parse error");
        when(httpServletRequest.getMethod()).thenReturn("PATCH");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/restaurants/1");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/restaurants/1");
        
        // Act
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleHttpMessageNotReadable(ex, webRequest, httpServletRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    /**
     * Test HttpMessageNotReadableException handler uses WebRequest description when path is null
     */
    @Test
    void testHandleHttpMessageNotReadable_NullPath_UsesWebRequestDescription() {
        // Arrange
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMessage()).thenReturn("JSON parse error");
        when(ex.getLocalizedMessage()).thenReturn("JSON parse error");
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn(null);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/restaurants");
        
        // Act
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleHttpMessageNotReadable(ex, webRequest, httpServletRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        
        Map<String, Object> body = result.getBody();
        assertNotNull(body);
        assertEquals("/api/restaurants", body.get("path")); // From webRequest.getDescription(false).replace("uri=", "")
    }

    /**
     * Test HttpMessageNotReadableException handler returns null for other HTTP methods
     */
    @Test
    void testHandleHttpMessageNotReadable_OtherMethod_ReturnsNull() {
        // Arrange
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(httpServletRequest.getMethod()).thenReturn("TRACE");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/restaurants");
        
        // Act
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleHttpMessageNotReadable(ex, webRequest, httpServletRequest);
        
        // Assert
        assertNull(result);
    }

    // ===============================
    // MethodArgumentNotValidException Tests
    // ===============================

    /**
     * Test MethodArgumentNotValidException handler returns validation error response
     */
    @Test
    void testHandleValidationExceptions_ReturnsValidationErrorResponse() {
        // Arrange
        FieldError fieldError1 = new FieldError("restaurantRequest", "name", "Restaurant name is required");
        FieldError fieldError2 = new FieldError("restaurantRequest", "cuisine", "Cuisine type is required");
        
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));
        when(webRequest.getDescription(false)).thenReturn("uri=/api/restaurants");
        
        // Act
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleValidationExceptions(methodArgumentNotValidException, webRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        
        Map<String, Object> body = result.getBody();
        assertNotNull(body);
        assertNotNull(body.get("timestamp"));
        assertEquals(400, body.get("status"));
        assertEquals("Validation Failed", body.get("error"));
        assertEquals("Input validation failed", body.get("message"));
        assertEquals("/api/restaurants", body.get("path"));
        
        @SuppressWarnings("unchecked")
        Map<String, String> validationErrors = (Map<String, String>) body.get("validationErrors");
        assertNotNull(validationErrors);
        assertEquals("Restaurant name is required", validationErrors.get("name"));
        assertEquals("Cuisine type is required", validationErrors.get("cuisine"));
    }

    /**
     * Test MethodArgumentNotValidException handler with empty validation errors
     */
    @Test
    void testHandleValidationExceptions_EmptyErrors() {
        // Arrange
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList());
        when(webRequest.getDescription(false)).thenReturn("uri=/api/restaurants");
        
        // Act
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleValidationExceptions(methodArgumentNotValidException, webRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        
        Map<String, Object> body = result.getBody();
        assertNotNull(body);
        
        @SuppressWarnings("unchecked")
        Map<String, String> validationErrors = (Map<String, String>) body.get("validationErrors");
        assertNotNull(validationErrors);
        assertTrue(validationErrors.isEmpty());
    }

    // ===============================
    // IllegalArgumentException Tests
    // ===============================

    /**
     * Test IllegalArgumentException handler returns bad request response
     */
    @Test
    void testHandleIllegalArgumentException_ReturnsBadRequestResponse() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("Invalid restaurant ID");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/restaurants");
        
        // Act
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleIllegalArgumentException(ex, webRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        
        Map<String, Object> body = result.getBody();
        assertNotNull(body);
        assertNotNull(body.get("timestamp"));
        assertEquals(400, body.get("status"));
        assertEquals("Bad Request", body.get("error"));
        assertEquals("Invalid restaurant ID", body.get("message"));
        assertEquals("/api/restaurants", body.get("path"));
    }

    /**
     * Test IllegalArgumentException handler with null message
     */
    @Test
    void testHandleIllegalArgumentException_NullMessage() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException((String) null);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/restaurants");
        
        // Act
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleIllegalArgumentException(ex, webRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        
        Map<String, Object> body = result.getBody();
        assertNotNull(body);
        assertNull(body.get("message"));
    }

    // ===============================
    // DataIntegrityViolationException Tests
    // ===============================

    /**
     * Test DataIntegrityViolationException handler returns conflict response
     */
    @Test
    void testHandleDataIntegrityViolation_ReturnsConflictResponse() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Unique constraint violation");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/restaurants");
        
        // Act
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleDataIntegrityViolation(ex, httpServletRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        
        Map<String, Object> body = result.getBody();
        assertNotNull(body);
        assertNotNull(body.get("timestamp"));
        assertEquals(409, body.get("status"));
        assertEquals("Conflict", body.get("error"));
        assertEquals("A record with this ID already exists. Do not send 'id' in POST requests.", body.get("message"));
        assertEquals("Database constraint violation: Unique constraint violation", body.get("details"));
        assertEquals("/api/restaurants", body.get("path"));
    }

    // ===============================
    // RuntimeException Tests
    // ===============================

    /**
     * Test RuntimeException handler returns conflict response for "already exists" message
     */
    @Test
    void testHandleRuntimeException_AlreadyExists_ReturnsConflictResponse() {
        // Arrange
        RuntimeException ex = new RuntimeException("Restaurant already exists");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/restaurants");
        
        // Act
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleRuntimeException(ex, webRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        
        Map<String, Object> body = result.getBody();
        assertNotNull(body);
        assertNotNull(body.get("timestamp"));
        assertEquals(409, body.get("status"));
        assertEquals("Conflict", body.get("error"));
        assertEquals("A record with this information already exists.", body.get("message"));
        assertEquals("Restaurant already exists", body.get("details"));
        assertEquals("/api/restaurants", body.get("path"));
    }

    /**
     * Test RuntimeException handler returns conflict response for "duplicate" message (case sensitive)
     */
    @Test
    void testHandleRuntimeException_Duplicate_ReturnsConflictResponse() {
        // Arrange - using lowercase "duplicate" as the handler checks for case-sensitive match
        RuntimeException ex = new RuntimeException("duplicate entry found");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/restaurants");
        
        // Act
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleRuntimeException(ex, webRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        
        Map<String, Object> body = result.getBody();
        assertNotNull(body);
        assertEquals("A record with this information already exists.", body.get("message"));
    }

    /**
     * Test RuntimeException handler returns conflict response for "unique constraint" message
     */
    @Test
    void testHandleRuntimeException_UniqueConstraint_ReturnsConflictResponse() {
        // Arrange
        RuntimeException ex = new RuntimeException("unique constraint violation");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/restaurants");
        
        // Act
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleRuntimeException(ex, webRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        
        Map<String, Object> body = result.getBody();
        assertNotNull(body);
        assertEquals("A record with this information already exists.", body.get("message"));
    }

    /**
     * Test RuntimeException handler returns internal server error for other messages
     */
    @Test
    void testHandleRuntimeException_OtherMessage_ReturnsInternalServerError() {
        // Arrange
        RuntimeException ex = new RuntimeException("Some other runtime error");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/restaurants");
        
        // Act
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleRuntimeException(ex, webRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        
        Map<String, Object> body = result.getBody();
        assertNotNull(body);
        assertNotNull(body.get("timestamp"));
        assertEquals(500, body.get("status"));
        assertEquals("Internal Server Error", body.get("error"));
        assertEquals("An unexpected error occurred: Some other runtime error", body.get("message"));
        assertEquals("/api/restaurants", body.get("path"));
    }

    /**
     * Test RuntimeException handler with null message returns internal server error
     */
    @Test
    void testHandleRuntimeException_NullMessage_ReturnsInternalServerError() {
        // Arrange
        RuntimeException ex = new RuntimeException((String) null);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/restaurants");
        
        // Act
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleRuntimeException(ex, webRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        
        Map<String, Object> body = result.getBody();
        assertNotNull(body);
        assertEquals("An unexpected error occurred: null", body.get("message"));
    }

    // ===============================
    // Generic Exception Tests
    // ===============================

    /**
     * Test generic Exception handler returns internal server error response
     */
    @Test
    void testHandleGenericException_ReturnsInternalServerErrorResponse() {
        // Arrange
        Exception ex = new Exception("Generic error occurred");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/restaurants");
        
        // Act
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleGenericException(ex, webRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        
        Map<String, Object> body = result.getBody();
        assertNotNull(body);
        assertNotNull(body.get("timestamp"));
        assertEquals(500, body.get("status"));
        assertEquals("Internal Server Error", body.get("error"));
        assertEquals("An unexpected error occurred", body.get("message"));
        assertEquals("Generic error occurred", body.get("details"));
        assertEquals("/api/restaurants", body.get("path"));
    }

    /**
     * Test generic Exception handler with null message
     */
    @Test
    void testHandleGenericException_NullMessage() {
        // Arrange
        Exception ex = new Exception((String) null);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/restaurants");
        
        // Act
        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleGenericException(ex, webRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        
        Map<String, Object> body = result.getBody();
        assertNotNull(body);
        assertNull(body.get("details"));
    }
}