package com.fooddelivery.order.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class RestaurantAcceptedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private Long orderId;
    private Long restaurantId;
    private Integer estimatedPrepTime;
    private LocalDateTime timestamp;

    public RestaurantAcceptedEvent() {
        this.eventId = UUID.randomUUID().toString();
    }

    public RestaurantAcceptedEvent(Long orderId, Long restaurantId,
                                   Integer estimatedPrepTime, LocalDateTime timestamp) {
        this.eventId = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.estimatedPrepTime = estimatedPrepTime;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
    public Integer getEstimatedPrepTime() { return estimatedPrepTime; }
    public void setEstimatedPrepTime(Integer estimatedPrepTime) { this.estimatedPrepTime = estimatedPrepTime; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "RestaurantAcceptedEvent{" +
                "eventId='" + eventId + '\'' +
                ", orderId=" + orderId +
                ", restaurantId=" + restaurantId +
                ", estimatedPrepTime=" + estimatedPrepTime +
                ", timestamp=" + timestamp +
                '}';
    }
}