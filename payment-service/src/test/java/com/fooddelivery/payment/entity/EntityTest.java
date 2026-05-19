package com.fooddelivery.payment.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    @Test
    void testPaymentStatus_Values() {
        PaymentStatus[] statuses = PaymentStatus.values();
        assertEquals(4, statuses.length);
        
        // Test each enum value
        for (PaymentStatus status : statuses) {
            assertNotNull(PaymentStatus.valueOf(status.name()));
        }
        
        // Test specific values
        assertEquals(PaymentStatus.PENDING, PaymentStatus.valueOf("PENDING"));
        assertEquals(PaymentStatus.SUCCESS, PaymentStatus.valueOf("SUCCESS"));
        assertEquals(PaymentStatus.FAILED, PaymentStatus.valueOf("FAILED"));
        assertEquals(PaymentStatus.REFUNDED, PaymentStatus.valueOf("REFUNDED"));
    }

    @Test
    void testPayment_NoArgsConstructor() {
        Payment payment = new Payment();
        assertNotNull(payment);
        assertNull(payment.getId());
        assertNull(payment.getOrderId());
        assertNull(payment.getAmount());
        assertNull(payment.getPaymentMethod());
        assertNull(payment.getStatus());
        assertNull(payment.getTransactionId());
        assertNull(payment.getCreatedAt());
        assertNull(payment.getUpdatedAt());
    }

    @Test
    void testPayment_AllArgsConstructor() {
        Payment payment = new Payment(100L, new BigDecimal("250.00"), "CREDIT_CARD");
        
        assertNotNull(payment);
        assertEquals(100L, payment.getOrderId());
        assertEquals(new BigDecimal("250.00"), payment.getAmount());
        assertEquals("CREDIT_CARD", payment.getPaymentMethod());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
    }

    @Test
    void testPayment_GettersAndSetters() {
        Payment payment = new Payment();
        LocalDateTime now = LocalDateTime.now();
        
        payment.setId(1L);
        assertEquals(1L, payment.getId());
        
        payment.setOrderId(100L);
        assertEquals(100L, payment.getOrderId());
        
        payment.setAmount(new BigDecimal("150.75"));
        assertEquals(new BigDecimal("150.75"), payment.getAmount());
        
        payment.setPaymentMethod("PAYPAL");
        assertEquals("PAYPAL", payment.getPaymentMethod());
        
        payment.setStatus(PaymentStatus.PENDING);
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        
        payment.setTransactionId("txn456");
        assertEquals("txn456", payment.getTransactionId());
        
        payment.setCreatedAt(now);
        assertEquals(now, payment.getCreatedAt());
        
        payment.setUpdatedAt(now);
        assertEquals(now, payment.getUpdatedAt());
    }

    @Test
    void testPayment_PrePersist() {
        Payment payment = new Payment();
        assertNull(payment.getCreatedAt());
        
        // Call the @PrePersist method
        payment.onCreate();
        
        assertNotNull(payment.getCreatedAt());
    }

    @Test
    void testPayment_PreUpdate() {
        Payment payment = new Payment();
        LocalDateTime originalCreated = LocalDateTime.now().minusHours(1);
        payment.setCreatedAt(originalCreated);
        
        // Call the @PreUpdate method
        payment.onUpdate();
        
        assertEquals(originalCreated, payment.getCreatedAt()); // Should not change
        assertNotNull(payment.getUpdatedAt()); // Should be updated
    }

    @Test
    void testPayment_ToString() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setOrderId(100L);
        payment.setAmount(new BigDecimal("250.00"));
        payment.setPaymentMethod("CREDIT_CARD");
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId("txn123");
        
        String toString = payment.toString();
        assertNotNull(toString);
    }
}