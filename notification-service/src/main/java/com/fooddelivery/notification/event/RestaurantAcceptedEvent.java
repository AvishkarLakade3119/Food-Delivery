package com.fooddelivery.notification.event;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Event published when restaurant accepts an order
 * Consumed by: order-service, notification-service
 */
public class RestaurantAcceptedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long orderId;
    private Long restaurantId;
    private Integer estimatedPrepTime;
    private LocalDateTime timestamp;

    public RestaurantAcceptedEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public RestaurantAcceptedEvent(Long orderId, Long restaurantId, 
                                  Integer estimatedPrepTime, LocalDateTime timestamp) {
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.estimatedPrepTime = estimatedPrepTime;
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

    public Integer getEstimatedPrepTime() {
        return estimatedPrepTime;
    }

    public void setEstimatedPrepTime(Integer estimatedPrepTime) {
        this.estimatedPrepTime = estimatedPrepTime;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "RestaurantAcceptedEvent{" +
                "orderId=" + orderId +
                ", restaurantId=" + restaurantId +
                ", estimatedPrepTime=" + estimatedPrepTime +
                ", timestamp=" + timestamp +
                '}';
    }
}
