package com.fooddelivery.order.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;
    
    @Mock
    private WebRequest webRequest;
    
    @Mock
    private HttpServletRequest httpServletRequest;
    
    @Mock
    private BindingResult bindingResult;
    
    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;
    
    @Mock
    private HttpMessageNotReadableException httpMessageNotReadableException;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleHttpMessageNotReadable_PostRequest() {
        // Arrange
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/orders");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/orders");
        when(httpMessageNotReadableException.getMessage()).thenReturn("Invalid JSON");
        when(httpMessageNotReadableException.getLocalizedMessage()).thenReturn("JSON parse error");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertEquals("Bad Request", body.get("error"));
        assertTrue(body.get("message").toString().contains("Invalid JSON format"));
        assertTrue(body.get("details").toString().contains("JSON parse error"));
        assertEquals("/api/orders", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void testHandleHttpMessageNotReadable_PutRequest() {
        // Arrange
        when(httpServletRequest.getMethod()).thenReturn("PUT");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/orders/1");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/orders/1");
        when(httpMessageNotReadableException.getMessage()).thenReturn("Invalid JSON");
        when(httpMessageNotReadableException.getLocalizedMessage()).thenReturn("JSON parse error");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testHandleHttpMessageNotReadable_PatchRequest() {
        // Arrange
        when(httpServletRequest.getMethod()).thenReturn("PATCH");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/orders/1");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/orders/1");
        when(httpMessageNotReadableException.getMessage()).thenReturn("Invalid JSON");
        when(httpMessageNotReadableException.getLocalizedMessage()).thenReturn("JSON parse error");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testHandleHttpMessageNotReadable_GetRequest() {
        // Arrange
        when(httpServletRequest.getMethod()).thenReturn("GET");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/orders");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        // Assert
        assertNull(response); // Should return null for GET requests
    }

    @Test
    void testHandleHttpMessageNotReadable_DeleteRequest() {
        // Arrange
        when(httpServletRequest.getMethod()).thenReturn("DELETE");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/orders/1");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        // Assert
        assertNull(response); // Should return null for DELETE requests
    }

    @Test
    void testHandleHttpMessageNotReadable_HeadRequest() {
        // Arrange
        when(httpServletRequest.getMethod()).thenReturn("HEAD");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/orders");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        // Assert
        assertNull(response); // Should return null for HEAD requests
    }

    @Test
    void testHandleHttpMessageNotReadable_OptionsRequest() {
        // Arrange
        when(httpServletRequest.getMethod()).thenReturn("OPTIONS");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/orders");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        // Assert
        assertNull(response); // Should return null for OPTIONS requests
    }

    @Test
    void testHandleHttpMessageNotReadable_ActuatorEndpoint() {
        // Arrange
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn("/actuator/health");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        // Assert
        assertNull(response); // Should return null for actuator endpoints
    }

    @Test
    void testHandleHttpMessageNotReadable_ActuatorEndpointWithSlash() {
        // Arrange
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn("/actuator/metrics");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        // Assert
        assertNull(response); // Should return null for actuator endpoints
    }

    @Test
    void testHandleHttpMessageNotReadable_NullPath() {
        // Arrange
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn(null);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/orders");
        when(httpMessageNotReadableException.getMessage()).thenReturn("Invalid JSON");
        when(httpMessageNotReadableException.getLocalizedMessage()).thenReturn("JSON parse error");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("/api/orders", body.get("path"));
    }

    @Test
    void testHandleHttpMessageNotReadable_OtherMethod() {
        // Arrange
        when(httpServletRequest.getMethod()).thenReturn("TRACE");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/orders");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        // Assert
        assertNull(response); // Should return null for other methods
    }

    @Test
    void testHandleValidationExceptions() {
        // Arrange
        FieldError fieldError1 = new FieldError("orderRequest", "userId", "User ID is required");
        FieldError fieldError2 = new FieldError("orderRequest", "restaurantId", "Restaurant ID is required");
        
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));
        when(webRequest.getDescription(false)).thenReturn("uri=/api/orders");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleValidationExceptions(methodArgumentNotValidException, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertEquals("Validation Failed", body.get("error"));
        assertEquals("Input validation failed", body.get("message"));
        assertEquals("/api/orders", body.get("path"));
        assertNotNull(body.get("timestamp"));
        
        @SuppressWarnings("unchecked")
        Map<String, String> validationErrors = (Map<String, String>) body.get("validationErrors");
        assertNotNull(validationErrors);
        assertEquals("User ID is required", validationErrors.get("userId"));
        assertEquals("Restaurant ID is required", validationErrors.get("restaurantId"));
    }

    @Test
    void testHandleIllegalArgumentException() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument provided");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/orders");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleIllegalArgumentException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertEquals("Bad Request", body.get("error"));
        assertEquals("Invalid argument provided", body.get("message"));
        assertEquals("/api/orders", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void testHandleRuntimeException() {
        // Arrange
        RuntimeException exception = new RuntimeException("Runtime error occurred");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/orders");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleRuntimeException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.get("status"));
        assertEquals("Internal Server Error", body.get("error"));
        assertTrue(body.get("message").toString().contains("Runtime error occurred"));
        assertEquals("/api/orders", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void testHandleDataIntegrityViolationException() {
        // Arrange
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Constraint violation");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/orders");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleDataIntegrityViolationException(exception, httpServletRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(409, body.get("status"));
        assertEquals("Conflict", body.get("error"));
        assertEquals("A record with this ID already exists. Do not send 'id' in POST requests.", body.get("message"));
        assertEquals("Constraint violation", body.get("details"));
        assertEquals("/api/orders", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void testHandleGenericException() {
        // Arrange
        Exception exception = new Exception("Generic error occurred");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/orders");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleGenericException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.get("status"));
        assertEquals("Internal Server Error", body.get("error"));
        assertEquals("An unexpected error occurred", body.get("message"));
        assertEquals("Generic error occurred", body.get("details"));
        assertEquals("/api/orders", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void testHandleHttpMessageNotReadable_WithNullPath_ShouldHandleGracefully() {
        // Arrange
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn(null); // Test null path branch
        when(webRequest.getDescription(false)).thenReturn("uri=/api/orders");
        when(httpMessageNotReadableException.getMessage()).thenReturn("Invalid JSON");
        when(httpMessageNotReadableException.getLocalizedMessage()).thenReturn("JSON parse error");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertEquals("Bad Request", body.get("error"));
        assertEquals("/api/orders", body.get("path"));
    }

    @Test
    void testHandleHttpMessageNotReadable_WithNonActuatorPath_ShouldProcessNormally() {
        // This test covers the FALSE branch of the actuator check at line 43:
        // if (path != null && (path.contains("/actuator/") || path.startsWith("/actuator")))
        // When path is NOT null AND does NOT contain "/actuator/", the condition is false

        // Arrange
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/orders/create"); // Non-actuator path
        when(webRequest.getDescription(false)).thenReturn("uri=/api/orders/create");
        when(httpMessageNotReadableException.getMessage()).thenReturn("Invalid JSON");
        when(httpMessageNotReadableException.getLocalizedMessage()).thenReturn("Malformed JSON");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertEquals("Bad Request", body.get("error"));
        assertTrue(body.get("message").toString().contains("Invalid JSON format"));
        assertEquals("/api/orders/create", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }
}