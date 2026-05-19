package com.fooddelivery.payment.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    @Test
    void testPaymentRequest_AllArgsConstructor() {
        PaymentRequest request = new PaymentRequest(1L, new BigDecimal("100.00"), "CREDIT_CARD");
        assertNotNull(request);
        assertEquals(1L, request.getOrderId());
        assertEquals(new BigDecimal("100.00"), request.getAmount());
        assertEquals("CREDIT_CARD", request.getPaymentMethod());
    }

    @Test
    void testPaymentRequest_NoArgsConstructor() {
        PaymentRequest request = new PaymentRequest();
        assertNotNull(request);
        assertNull(request.getOrderId());
        assertNull(request.getAmount());
        assertNull(request.getPaymentMethod());
    }

    @Test
    void testPaymentRequest_GettersAndSetters() {
        PaymentRequest request = new PaymentRequest();
        
        request.setOrderId(2L);
        assertEquals(2L, request.getOrderId());
        
        request.setAmount(new BigDecimal("250.50"));
        assertEquals(new BigDecimal("250.50"), request.getAmount());
        
        request.setPaymentMethod("PAYPAL");
        assertEquals("PAYPAL", request.getPaymentMethod());
    }

    @Test
    void testPaymentRequest_ToString() {
        PaymentRequest request = new PaymentRequest(1L, new BigDecimal("100.00"), "CREDIT_CARD");
        String toString = request.toString();
        assertNotNull(toString);
    }

    @Test
    void testPaymentResponse_AllArgsConstructor() {
        PaymentResponse response = new PaymentResponse("COMPLETED", "txn123", "Payment successful");
        assertNotNull(response);
        assertEquals("COMPLETED", response.getStatus());
        assertEquals("txn123", response.getTransactionId());
        assertEquals("Payment successful", response.getMessage());
    }

    @Test
    void testPaymentResponse_NoArgsConstructor() {
        PaymentResponse response = new PaymentResponse();
        assertNotNull(response);
        assertNull(response.getStatus());
        assertNull(response.getTransactionId());
        assertNull(response.getMessage());
    }

    @Test
    void testPaymentResponse_GettersAndSetters() {
        PaymentResponse response = new PaymentResponse();
        
        response.setStatus("PENDING");
        assertEquals("PENDING", response.getStatus());
        
        response.setTransactionId("txn456");
        assertEquals("txn456", response.getTransactionId());
        
        response.setMessage("Payment processing");
        assertEquals("Payment processing", response.getMessage());
    }

    @Test
    void testPaymentResponse_ToString() {
        PaymentResponse response = new PaymentResponse("COMPLETED", "txn123", "Payment successful");
        String toString = response.toString();
        assertNotNull(toString);
    }
}