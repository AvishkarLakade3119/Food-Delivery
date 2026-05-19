package com.fooddelivery.notification.exception;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private WebRequest webRequest;

    @Mock
    private HttpMessageNotReadableException httpMessageNotReadableException;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/notifications");
    }

    // ========== HttpMessageNotReadableException Tests ==========

    @Test
    void handleHttpMessageNotReadable_GetRequest_ShouldReturnNull() {
        // Covers line 35: GET method check - TRUE path
        when(httpServletRequest.getMethod()).thenReturn("GET");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/notifications");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertThat(response).isNull();
    }

    @Test
    void handleHttpMessageNotReadable_DeleteRequest_ShouldReturnNull() {
        // Covers line 35: DELETE method check - TRUE path
        when(httpServletRequest.getMethod()).thenReturn("DELETE");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/notifications/1");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertThat(response).isNull();
    }

    @Test
    void handleHttpMessageNotReadable_HeadRequest_ShouldReturnNull() {
        // Covers line 35: HEAD method check - TRUE path
        when(httpServletRequest.getMethod()).thenReturn("HEAD");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/notifications");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertThat(response).isNull();
    }

    @Test
    void handleHttpMessageNotReadable_OptionsRequest_ShouldReturnNull() {
        // Covers line 35: OPTIONS method check - TRUE path
        when(httpServletRequest.getMethod()).thenReturn("OPTIONS");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/notifications");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertThat(response).isNull();
    }

    @Test
    void handleHttpMessageNotReadable_ActuatorPathContains_ShouldReturnNull() {
        // Covers line 43: path.contains("/actuator/") - TRUE path
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn("/actuator/health");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertThat(response).isNull();
    }

    @Test
    void handleHttpMessageNotReadable_ActuatorPathStartsWith_ShouldReturnNull() {
        // Covers line 43: path.startsWith("/actuator") - TRUE path
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn("/actuator");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertThat(response).isNull();
    }

    @Test
    void handleHttpMessageNotReadable_NullPath_ShouldProcessNormally() {
        // Covers line 43: path != null - FALSE path (path is null)
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn(null);
        when(httpMessageNotReadableException.getLocalizedMessage()).thenReturn("JSON parse error");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("message");
        assertThat(response.getBody().get("path")).isEqualTo("/api/notifications");
    }

    @Test
    void handleHttpMessageNotReadable_PostRequest_ShouldReturnBadRequest() {
        // Covers line 50: POST method check - TRUE path
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/notifications");
        when(httpMessageNotReadableException.getLocalizedMessage()).thenReturn("JSON parse error");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(400);
        assertThat(response.getBody().get("error")).isEqualTo("Bad Request");
        assertThat(response.getBody()).containsKey("timestamp");
        assertThat(response.getBody()).containsKey("message");
        assertThat(response.getBody()).containsKey("details");
    }

    @Test
    void handleHttpMessageNotReadable_PutRequest_ShouldReturnBadRequest() {
        // Covers line 50: PUT method check - TRUE path
        when(httpServletRequest.getMethod()).thenReturn("PUT");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/notifications/1");
        when(httpMessageNotReadableException.getLocalizedMessage()).thenReturn("Invalid JSON");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleHttpMessageNotReadable_PatchRequest_ShouldReturnBadRequest() {
        // Covers line 50: PATCH method check - TRUE path
        when(httpServletRequest.getMethod()).thenReturn("PATCH");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/notifications/1");
        when(httpMessageNotReadableException.getLocalizedMessage()).thenReturn("Malformed JSON");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleHttpMessageNotReadable_OtherMethod_ShouldReturnNull() {
        // Covers line 50: FALSE path (method is not POST/PUT/PATCH)
        when(httpServletRequest.getMethod()).thenReturn("TRACE");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/notifications");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertThat(response).isNull();
    }

    // ========== MethodArgumentNotValidException Tests ==========

    @Test
    void handleValidationExceptions_ShouldReturnValidationErrors() {
        FieldError fieldError1 = new FieldError("notificationRequest", "userId", "User ID is required");
        FieldError fieldError2 = new FieldError("notificationRequest", "message", "Message is required");
        
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleValidationExceptions(methodArgumentNotValidException, webRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(400);
        assertThat(response.getBody().get("error")).isEqualTo("Validation Failed");
        assertThat(response.getBody().get("message")).isEqualTo("Input validation failed");
        assertThat(response.getBody()).containsKey("validationErrors");
        assertThat(response.getBody()).containsKey("timestamp");
        assertThat(response.getBody()).containsKey("path");
        
        @SuppressWarnings("unchecked")
        Map<String, String> validationErrors = (Map<String, String>) response.getBody().get("validationErrors");
        assertThat(validationErrors).hasSize(2);
        assertThat(validationErrors).containsKey("userId");
        assertThat(validationErrors).containsKey("message");
    }

    // ========== IllegalArgumentException Tests ==========

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequest() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument provided");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleIllegalArgumentException(exception, webRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(400);
        assertThat(response.getBody().get("error")).isEqualTo("Bad Request");
        assertThat(response.getBody().get("message")).isEqualTo("Invalid argument provided");
        assertThat(response.getBody()).containsKey("timestamp");
        assertThat(response.getBody()).containsKey("path");
    }

    // ========== RuntimeException Tests ==========

    @Test
    void handleRuntimeException_ShouldReturnInternalServerError() {
        RuntimeException exception = new RuntimeException("Database connection failed");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleRuntimeException(exception, webRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(500);
        assertThat(response.getBody().get("error")).isEqualTo("Internal Server Error");
        assertThat(response.getBody().get("message")).isEqualTo("An unexpected error occurred: Database connection failed");
        assertThat(response.getBody()).containsKey("timestamp");
        assertThat(response.getBody()).containsKey("path");
    }

    // ========== Generic Exception Tests ==========

    @Test
    void handleGenericException_ShouldReturnInternalServerError() {
        Exception exception = new Exception("Unexpected error occurred");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleGenericException(exception, webRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(500);
        assertThat(response.getBody().get("error")).isEqualTo("Internal Server Error");
        assertThat(response.getBody().get("message")).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().get("details")).isEqualTo("Unexpected error occurred");
        assertThat(response.getBody()).containsKey("timestamp");
        assertThat(response.getBody()).containsKey("path");
    }
}