package com.fooddelivery.user.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExceptionBranchTest {

    @Mock
    private HttpMessageNotReadableException httpMessageNotReadableException;

    @Mock
    private WebRequest webRequest;

    @Mock
    private HttpServletRequest httpServletRequest;

    private GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleHttpMessageNotReadable_NullPath_ShouldProcessNormally() {
        // Covers line 42: path != null - FALSE branch (path is null)
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn(null); // NULL PATH - this is the missed branch
        when(webRequest.getDescription(false)).thenReturn("uri=/api/users/register");
        when(httpMessageNotReadableException.getLocalizedMessage()).thenReturn("JSON parse error");

        ResponseEntity<Map<String, Object>> response = handler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bad Request", response.getBody().get("error"));
        assertEquals("/api/users/register", response.getBody().get("path"));
    }

    @Test
    void handleHttpMessageNotReadable_NonNullPath_ActuatorContains_ShouldReturnNull() {
        // Covers line 42: path != null TRUE + path.contains("/actuator/") TRUE
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn("/actuator/health");
        when(webRequest.getDescription(false)).thenReturn("uri=/actuator/health");

        ResponseEntity<Map<String, Object>> response = handler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertNull(response);
    }

    @Test
    void handleHttpMessageNotReadable_NonNullPath_ActuatorStartsWith_ShouldReturnNull() {
        // Covers line 42: path != null TRUE + path.startsWith("/actuator") TRUE
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn("/actuator");
        when(webRequest.getDescription(false)).thenReturn("uri=/actuator");

        ResponseEntity<Map<String, Object>> response = handler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertNull(response);
    }

    @Test
    void handleHttpMessageNotReadable_NonNullPath_NotActuator_ShouldProcessNormally() {
        // Covers line 42: path != null TRUE + both actuator checks FALSE
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn("/api/users/register");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/users/register");
        when(httpMessageNotReadableException.getLocalizedMessage()).thenReturn("JSON parse error");

        ResponseEntity<Map<String, Object>> response = handler
                .handleHttpMessageNotReadable(httpMessageNotReadableException, webRequest, httpServletRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}