package com.fooddelivery.order.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class RestaurantRejectedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private Long orderId;
    private Long restaurantId;
    private String rejectionReason;
    private LocalDateTime timestamp;

    public RestaurantRejectedEvent() {
        this.eventId = UUID.randomUUID().toString();
    }

    public RestaurantRejectedEvent(Long orderId, Long restaurantId,
                                   String rejectionReason, LocalDateTime timestamp) {
        this.eventId = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.rejectionReason = rejectionReason;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "RestaurantRejectedEvent{" +
                "eventId='" + eventId + '\'' +
                ", orderId=" + orderId +
                ", restaurantId=" + restaurantId +
                ", rejectionReason='" + rejectionReason + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}