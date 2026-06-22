package com.fooddelivery.restaurant.event;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Event published when an order is confirmed after restaurant acceptance
 * Consumed by: notification-service
 */
public class OrderConfirmedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long orderId;
    private Long userId;
    private Long restaurantId;
    private Double totalAmount;
    private String estimatedDeliveryTime;
    private LocalDateTime timestamp;

    // No-args constructor
    public OrderConfirmedEvent() {
        this.timestamp = LocalDateTime.now();
    }

    // All-args constructor
    public OrderConfirmedEvent(Long orderId, Long userId, Long restaurantId, 
                              Double totalAmount, String estimatedDeliveryTime, 
                              LocalDateTime timestamp) {
        this.orderId = orderId;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.totalAmount = totalAmount;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    // Getters and Setters
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

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(String estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "OrderConfirmedEvent{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", restaurantId=" + restaurantId +
                ", totalAmount=" + totalAmount +
                ", estimatedDeliveryTime='" + estimatedDeliveryTime + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
