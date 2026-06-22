package com.fooddelivery.order.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrderConfirmedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private Long orderId;
    private Long userId;
    private Long restaurantId;
    private Double totalAmount;
    private String estimatedDeliveryTime;
    private LocalDateTime timestamp;

    public OrderConfirmedEvent() {
        this.eventId = UUID.randomUUID().toString();
    }

    public OrderConfirmedEvent(Long orderId, Long userId, Long restaurantId,
                               Double totalAmount, String estimatedDeliveryTime,
                               LocalDateTime timestamp) {
        this.eventId = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.totalAmount = totalAmount;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public String getEstimatedDeliveryTime() { return estimatedDeliveryTime; }
    public void setEstimatedDeliveryTime(String estimatedDeliveryTime) { this.estimatedDeliveryTime = estimatedDeliveryTime; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "OrderConfirmedEvent{" +
                "eventId='" + eventId + '\'' +
                ", orderId=" + orderId +
                ", userId=" + userId +
                ", restaurantId=" + restaurantId +
                ", totalAmount=" + totalAmount +
                ", timestamp=" + timestamp +
                '}';
    }
}