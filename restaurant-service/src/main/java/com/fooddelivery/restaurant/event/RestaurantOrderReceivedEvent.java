package com.fooddelivery.restaurant.event;

import com.fooddelivery.restaurant.event.dto.OrderItemDTO;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Event published when restaurant receives an order
 */
public class RestaurantOrderReceivedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long orderId;
    private Long restaurantId;
    private List<OrderItemDTO> items;
    private Double totalAmount;
    private LocalDateTime timestamp;

    public RestaurantOrderReceivedEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public RestaurantOrderReceivedEvent(Long orderId, Long restaurantId, 
                                       List<OrderItemDTO> items, Double totalAmount, 
                                       LocalDateTime timestamp) {
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.items = items;
        this.totalAmount = totalAmount;
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

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "RestaurantOrderReceivedEvent{" +
                "orderId=" + orderId +
                ", restaurantId=" + restaurantId +
                ", totalAmount=" + totalAmount +
                ", timestamp=" + timestamp +
                '}';
    }
}