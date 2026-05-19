package com.fooddelivery.payment.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    @Test
    void testPaymentNotFoundException_NoArgConstructor() {
        PaymentNotFoundException exception = new PaymentNotFoundException();
        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    @Test
    void testPaymentNotFoundException_MessageConstructor() {
        String message = "Payment not found";
        PaymentNotFoundException exception = new PaymentNotFoundException(message);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testPaymentNotFoundException_MessageAndCauseConstructor() {
        String message = "Payment not found";
        Throwable cause = new RuntimeException("Root cause");
        PaymentNotFoundException exception = new PaymentNotFoundException(message, cause);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testPaymentNotFoundException_CauseConstructor() {
        Throwable cause = new RuntimeException("Root cause");
        PaymentNotFoundException exception = new PaymentNotFoundException(cause);
        assertNotNull(exception);
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testInvalidPaymentException_NoArgConstructor() {
        InvalidPaymentException exception = new InvalidPaymentException();
        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    @Test
    void testInvalidPaymentException_MessageConstructor() {
        String message = "Invalid payment data";
        InvalidPaymentException exception = new InvalidPaymentException(message);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testInvalidPaymentException_MessageAndCauseConstructor() {
        String message = "Invalid payment data";
        Throwable cause = new IllegalArgumentException("Validation error");
        InvalidPaymentException exception = new InvalidPaymentException(message, cause);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testInvalidPaymentException_CauseConstructor() {
        Throwable cause = new IllegalArgumentException("Validation error");
        InvalidPaymentException exception = new InvalidPaymentException(cause);
        assertNotNull(exception);
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testPaymentProcessingException_NoArgConstructor() {
        PaymentProcessingException exception = new PaymentProcessingException();
        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    @Test
    void testPaymentProcessingException_MessageConstructor() {
        String message = "Payment processing failed";
        PaymentProcessingException exception = new PaymentProcessingException(message);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testPaymentProcessingException_MessageAndCauseConstructor() {
        String message = "Payment processing failed";
        Throwable cause = new RuntimeException("External service error");
        PaymentProcessingException exception = new PaymentProcessingException(message, cause);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testPaymentProcessingException_CauseConstructor() {
        Throwable cause = new RuntimeException("External service error");
        PaymentProcessingException exception = new PaymentProcessingException(cause);
        assertNotNull(exception);
        assertEquals(cause, exception.getCause());
    }
}