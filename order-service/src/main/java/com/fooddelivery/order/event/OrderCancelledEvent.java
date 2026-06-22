package com.fooddelivery.order.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrderCancelledEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private Long orderId;
    private Long userId;
    private String reason;
    private LocalDateTime timestamp;

    public OrderCancelledEvent() {
        this.eventId = UUID.randomUUID().toString();
    }

    public OrderCancelledEvent(Long orderId, Long userId, String reason, LocalDateTime timestamp) {
        this.eventId = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.userId = userId;
        this.reason = reason;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "OrderCancelledEvent{" +
                "eventId='" + eventId + '\'' +
                ", orderId=" + orderId +
                ", userId=" + userId +
                ", reason='" + reason + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}