package com.fooddelivery.payment.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpMessageNotReadableException httpMessageNotReadableException;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        // Remove unnecessary stubbing - only stub when needed in specific tests
        // This prevents MockitoExtension strictness violations
    }

    @Test
    void handleValidationExceptions_WithMultipleErrors_ShouldReturnAllErrors() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/payments");

        FieldError fieldError1 = new FieldError("paymentRequest", "amount", "Amount is required");
        FieldError fieldError2 = new FieldError("paymentRequest", "orderId", "Order ID is required");
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleValidationExceptions(methodArgumentNotValidException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Validation Failed", response.getBody().get("error"));
        assertTrue(response.getBody().containsKey("validationErrors"));

        @SuppressWarnings("unchecked")
        Map<String, String> validationErrors = (Map<String, String>) response.getBody().get("validationErrors");
        assertEquals(2, validationErrors.size());
        assertTrue(validationErrors.containsKey("amount"));
        assertTrue(validationErrors.containsKey("orderId"));
    }

    @Test
    void handleHttpMessageNotReadable_PostRequest_ShouldReturnBadRequest() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/payments");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/payments");
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpMessageNotReadableException.getLocalizedMessage()).thenReturn("JSON parse error");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Bad Request", response.getBody().get("error"));
        assertTrue(response.getBody().get("message").toString().contains("Invalid JSON format"));
    }

    @Test
    void handleHttpMessageNotReadable_GetRequest_ShouldReturnNull() {
        when(httpServletRequest.getMethod()).thenReturn("GET");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertNull(response);
    }

    @Test
    void handleHttpMessageNotReadable_DeleteRequest_ShouldReturnNull() {
        when(httpServletRequest.getMethod()).thenReturn("DELETE");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertNull(response);
    }

    @Test
    void handleHttpMessageNotReadable_HeadRequest_ShouldReturnNull() {
        when(httpServletRequest.getMethod()).thenReturn("HEAD");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertNull(response);
    }

    @Test
    void handleHttpMessageNotReadable_OptionsRequest_ShouldReturnNull() {
        when(httpServletRequest.getMethod()).thenReturn("OPTIONS");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertNull(response);
    }

    @Test
    void handleHttpMessageNotReadable_ActuatorEndpoint_ShouldReturnNull() {
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn("/actuator/health");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertNull(response);
    }

    @Test
    void handleHttpMessageNotReadable_PutRequest_ShouldReturnBadRequest() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/payments");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/payments");
        when(httpServletRequest.getMethod()).thenReturn("PUT");
        when(httpMessageNotReadableException.getLocalizedMessage()).thenReturn("JSON parse error");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleHttpMessageNotReadable_PatchRequest_ShouldReturnBadRequest() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/payments");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/payments");
        when(httpServletRequest.getMethod()).thenReturn("PATCH");
        when(httpMessageNotReadableException.getLocalizedMessage()).thenReturn("JSON parse error");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleHttpMessageNotReadable_NullPath_ShouldUseRequestDescription() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/payments");
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn(null);
        when(httpMessageNotReadableException.getLocalizedMessage()).thenReturn("JSON parse error");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertNotNull(response);
        assertEquals("/api/payments", response.getBody().get("path"));
    }

    @Test
    void handleValidationExceptions_ShouldReturnBadRequest() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/payments");

        FieldError fieldError = new FieldError("paymentRequest", "amount", "Amount is required");
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleValidationExceptions(methodArgumentNotValidException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Validation Failed", response.getBody().get("error"));
        assertTrue(response.getBody().containsKey("validationErrors"));
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequest() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/payments");

        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleIllegalArgumentException(ex, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Bad Request", response.getBody().get("error"));
        assertEquals("Invalid argument", response.getBody().get("message"));
    }

    @Test
    void handleRuntimeException_ShouldReturnInternalServerError() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/payments");

        RuntimeException ex = new RuntimeException("Runtime error");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleRuntimeException(ex, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().get("status"));
        assertEquals("Internal Server Error", response.getBody().get("error"));
        assertTrue(response.getBody().get("message").toString().contains("Runtime error"));
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerError() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/payments");

        Exception ex = new Exception("Generic error");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleGenericException(ex, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().get("status"));
        assertEquals("Internal Server Error", response.getBody().get("error"));
        assertEquals("An unexpected error occurred", response.getBody().get("message"));
    }

    @Test
    void handleHttpMessageNotReadable_NullPathAndNullRequestURI_ShouldUseWebRequestDescription() {
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn(null);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
        when(httpMessageNotReadableException.getLocalizedMessage()).thenReturn("JSON parse error");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertNotNull(response);
        assertEquals("/api/test", response.getBody().get("path"));
    }

    @Test
    void handleHttpMessageNotReadable_WithActuatorPathContains_ShouldReturnNull() {
        // Test actuator path branch with contains check
        when(httpServletRequest.getRequestURI()).thenReturn("/api/actuator/health");
        when(httpServletRequest.getMethod()).thenReturn("POST");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertNull(response); // Should return null for paths containing actuator
    }

    @Test
    void handleHttpMessageNotReadable_WithOtherMethod_ShouldReturnNull() {
        // Test other HTTP methods that should return null (line 62-63)
        when(httpServletRequest.getRequestURI()).thenReturn("/api/payments");
        when(httpServletRequest.getMethod()).thenReturn("TRACE"); // Not in any of the handled methods

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertNull(response); // Should return null for other methods (covers lines 62-63)
    }

    @Test
    void handleHttpMessageNotReadable_ActuatorPathStartsWith_ShouldReturnNull() {
        // Test the actuator path startsWith branch that was missed (line 41)
        when(httpServletRequest.getRequestURI()).thenReturn("/actuator");
        when(httpServletRequest.getMethod()).thenReturn("POST");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertNull(response); // Should return null for actuator paths starting with /actuator
    }
}