package com.fooddelivery.notification.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationEntityTest {

    @Test
    void defaultConstructor_ShouldInitializeCreatedAt() {
        Notification notification = new Notification();
        
        assertThat(notification.getCreatedAt()).isNotNull();
        assertThat(notification.getId()).isNull();
        assertThat(notification.getUserId()).isNull();
        assertThat(notification.getMessage()).isNull();
        assertThat(notification.getType()).isNull();
    }

    @Test
    void parameterizedConstructor_ShouldSetFieldsCorrectly() {
        Notification notification = new Notification(1L, "Test Title", "Test message", "ORDER_CREATED");

        assertThat(notification.getUserId()).isEqualTo(1L);
        assertThat(notification.getTitle()).isEqualTo("Test Title");
        assertThat(notification.getMessage()).isEqualTo("Test message");
        assertThat(notification.getType()).isEqualTo("ORDER_CREATED");
        assertThat(notification.getStatus()).isEqualTo("SENT");
        assertThat(notification.getCreatedAt()).isNotNull();
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        Notification notification = new Notification();
        LocalDateTime now = LocalDateTime.now();
        
        notification.setId(1L);
        notification.setUserId(100L);
        notification.setMessage("Test notification");
        notification.setType("PAYMENT_UPDATE");
        notification.setStatus("DELIVERED");
        notification.setIsRead(true);
        notification.setCreatedAt(now);
        notification.setUpdatedAt(now);
        
        assertThat(notification.getId()).isEqualTo(1L);
        assertThat(notification.getUserId()).isEqualTo(100L);
        assertThat(notification.getMessage()).isEqualTo("Test notification");
        assertThat(notification.getType()).isEqualTo("PAYMENT_UPDATE");
        assertThat(notification.getStatus()).isEqualTo("DELIVERED");
        assertThat(notification.getIsRead()).isTrue();
        assertThat(notification.getCreatedAt()).isEqualTo(now);
        assertThat(notification.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void preUpdate_ShouldSetUpdatedAt() {
        Notification notification = new Notification();
        LocalDateTime before = LocalDateTime.now();
        
        // Simulate JPA @PreUpdate lifecycle method
        notification.preUpdate();
        
        assertThat(notification.getUpdatedAt()).isNotNull();
        assertThat(notification.getUpdatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void toString_ShouldReturnFormattedString() {
        Notification notification = new Notification(1L, "Test Title", "Test message", "ORDER_CREATED");
        notification.setId(1L);
        notification.setStatus("SENT");
        notification.setIsRead(false);

        String result = notification.toString();

        assertThat(result).contains("Notification{");
        assertThat(result).contains("id=1");
        assertThat(result).contains("userId=1");
        assertThat(result).contains("title='Test Title'");
        assertThat(result).contains("message='Test message'");
        assertThat(result).contains("type='ORDER_CREATED'");
        assertThat(result).contains("status='SENT'");
        assertThat(result).contains("isRead=false");
    }

    @Test
    void isRead_DefaultValue_ShouldBeFalse() {
        Notification notification = new Notification();
        
        // Default value should be false
        assertThat(notification.getIsRead()).isFalse();
    }

    @Test
    void status_DefaultValue_ShouldBeSent() {
        Notification notification = new Notification();
        
        // Default value should be "SENT"
        assertThat(notification.getStatus()).isEqualTo("SENT");
    }
}