package com.fooddelivery.notification.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationRequestDtoTest {

    @Test
    void defaultConstructor_ShouldCreateEmptyObject() {
        NotificationRequest request = new NotificationRequest();
        
        assertThat(request.getUserId()).isNull();
        assertThat(request.getMessage()).isNull();
        assertThat(request.getType()).isNull();
    }

    @Test
    void parameterizedConstructor_ShouldSetAllFields() {
        NotificationRequest request = new NotificationRequest(1L, "Test message", "ORDER_CREATED");
        
        assertThat(request.getUserId()).isEqualTo(1L);
        assertThat(request.getMessage()).isEqualTo("Test message");
        assertThat(request.getType()).isEqualTo("ORDER_CREATED");
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        NotificationRequest request = new NotificationRequest();
        
        request.setUserId(100L);
        request.setMessage("Payment notification");
        request.setType("PAYMENT_UPDATE");
        
        assertThat(request.getUserId()).isEqualTo(100L);
        assertThat(request.getMessage()).isEqualTo("Payment notification");
        assertThat(request.getType()).isEqualTo("PAYMENT_UPDATE");
    }

    @Test
    void toString_ShouldReturnFormattedString() {
        NotificationRequest request = new NotificationRequest(1L, "Test message", "ORDER_CREATED");
        
        String result = request.toString();
        
        assertThat(result).contains("NotificationRequest{");
        assertThat(result).contains("userId=1");
        assertThat(result).contains("message='Test message'");
        assertThat(result).contains("type='ORDER_CREATED'");
    }

    @Test
    void setUserId_WithNull_ShouldAcceptNull() {
        NotificationRequest request = new NotificationRequest();
        request.setUserId(null);
        
        assertThat(request.getUserId()).isNull();
    }

    @Test
    void setMessage_WithNull_ShouldAcceptNull() {
        NotificationRequest request = new NotificationRequest();
        request.setMessage(null);
        
        assertThat(request.getMessage()).isNull();
    }

    @Test
    void setType_WithNull_ShouldAcceptNull() {
        NotificationRequest request = new NotificationRequest();
        request.setType(null);
        
        assertThat(request.getType()).isNull();
    }

    @Test
    void setMessage_WithEmptyString_ShouldAcceptEmptyString() {
        NotificationRequest request = new NotificationRequest();
        request.setMessage("");
        
        assertThat(request.getMessage()).isEmpty();
    }

    @Test
    void setType_WithEmptyString_ShouldAcceptEmptyString() {
        NotificationRequest request = new NotificationRequest();
        request.setType("");
        
        assertThat(request.getType()).isEmpty();
    }
}