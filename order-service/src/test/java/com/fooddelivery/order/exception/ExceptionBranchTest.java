package com.fooddelivery.order.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Brand new test file to cover the missed branch in GlobalExceptionHandler.
 * This file ONLY contains the test for the branch that was not covered by existing tests.
 */
class ExceptionBranchTest {

    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private HttpMessageNotReadableException exception;

    @Mock
    private WebRequest webRequest;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        exceptionHandler = new GlobalExceptionHandler();
    }

    /**
     * Covers line 43 FALSE path: if (path != null && (path.contains("/actuator/") || path.startsWith("/actuator")))
     * When path is NULL, the condition is false and the method continues to check HTTP method.
     * 
     * This test uses a POST request with path=null to ensure the NULL check branch is covered.
     */
    @Test
    void handleHttpMessageNotReadable_NullPath_ShouldProcessNormally() {
        // Mock HTTP method as POST (to avoid early return)
        when(httpServletRequest.getMethod()).thenReturn("POST");
        
        // Mock path as NULL - this covers the FALSE branch at line 43
        when(httpServletRequest.getRequestURI()).thenReturn(null);
        
        // Mock webRequest description for error response
        when(webRequest.getDescription(false)).thenReturn("uri=/api/orders");
        
        // Mock exception message
        when(exception.getLocalizedMessage()).thenReturn("JSON parse error: Unexpected character");

        // Call the handler
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleHttpMessageNotReadable(
            exception, webRequest, httpServletRequest
        );

        // Verify response
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
        assertEquals("Bad Request", body.get("error"));
        assertTrue(body.get("message").toString().contains("Invalid JSON format"));
        
        // Path should be taken from webRequest description when httpServletRequest.getRequestURI() is null
        assertEquals("/api/orders", body.get("path"));
    }

    /**
     * Additional test: Covers the TRUE path at line 43 for completeness.
     * When path contains "/actuator/", the method returns null.
     */
    @Test
    void handleHttpMessageNotReadable_ActuatorPath_ShouldReturnNull() {
        // Mock HTTP method as POST
        when(httpServletRequest.getMethod()).thenReturn("POST");
        
        // Mock path as actuator endpoint - TRUE branch at line 43
        when(httpServletRequest.getRequestURI()).thenReturn("/actuator/health");

        // Call the handler
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleHttpMessageNotReadable(
            exception, webRequest, httpServletRequest
        );

        // Verify response is null (Spring handles it)
        assertNull(response);
    }

    /**
     * Additional test: Covers the TRUE path at line 43 for path starting with "/actuator".
     */
    @Test
    void handleHttpMessageNotReadable_ActuatorPathStartsWith_ShouldReturnNull() {
        // Mock HTTP method as POST
        when(httpServletRequest.getMethod()).thenReturn("POST");
        
        // Mock path starting with /actuator - TRUE branch at line 43
        when(httpServletRequest.getRequestURI()).thenReturn("/actuator");

        // Call the handler
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleHttpMessageNotReadable(
            exception, webRequest, httpServletRequest
        );

        // Verify response is null (Spring handles it)
        assertNull(response);
    }
}