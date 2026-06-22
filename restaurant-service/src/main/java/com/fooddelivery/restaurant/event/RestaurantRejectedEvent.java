package com.fooddelivery.restaurant.event;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Event published when restaurant rejects an order
 * Consumed by: order-service, notification-service
 */
public class RestaurantRejectedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long orderId;
    private Long restaurantId;
    private String rejectionReason;
    private LocalDateTime timestamp;

    public RestaurantRejectedEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public RestaurantRejectedEvent(Long orderId, Long restaurantId, 
                                  String rejectionReason, LocalDateTime timestamp) {
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.rejectionReason = rejectionReason;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "RestaurantRejectedEvent{" +
                "orderId=" + orderId +
                ", restaurantId=" + restaurantId +
                ", rejectionReason='" + rejectionReason + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}