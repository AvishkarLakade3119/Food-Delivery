package com.fooddelivery.notification.event;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Event for notifications
 * Can be published by any service to trigger notifications
 */
public class NotificationEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long notificationId;
    private Long userId;
    private Long orderId;
    private String type; // ORDER_CREATED, PAYMENT_SUCCESS, PAYMENT_FAILED, ORDER_CONFIRMED, ORDER_CANCELLED
    private String message;
    private String channel; // EMAIL, SMS, PUSH
    private LocalDateTime timestamp;

    public NotificationEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public NotificationEvent(Long notificationId, Long userId, Long orderId, 
                           String type, String message, String channel, 
                           LocalDateTime timestamp) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.orderId = orderId;
        this.type = type;
        this.message = message;
        this.channel = channel;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "NotificationEvent{" +
                "notificationId=" + notificationId +
                ", userId=" + userId +
                ", orderId=" + orderId +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", channel='" + channel + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}