package com.fooddelivery.payment.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Event published when a new order is created
 * Consumed by: payment-service, notification-service
 */
public class OrderCreatedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long orderId;
    private Long userId;
    private Long restaurantId;
    private List<OrderItemDTO> items;
    private Double totalAmount;
    private String deliveryAddress;
    private String orderStatus;
    private LocalDateTime timestamp;

    // No-args constructor
    public OrderCreatedEvent() {
        this.timestamp = LocalDateTime.now();
    }

    // All-args constructor
    public OrderCreatedEvent(Long orderId, Long userId, Long restaurantId, 
                            List<OrderItemDTO> items, Double totalAmount, 
                            String deliveryAddress, String orderStatus, LocalDateTime timestamp) {
        this.orderId = orderId;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.deliveryAddress = deliveryAddress;
        this.orderStatus = orderStatus;
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

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "OrderCreatedEvent{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", restaurantId=" + restaurantId +
                ", totalAmount=" + totalAmount +
                ", orderStatus='" + orderStatus + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
    
    // Inner class for OrderItemDTO
    public static class OrderItemDTO implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private Long menuItemId;
        private Integer quantity;
        private Double price;
        
        public OrderItemDTO() {}
        
        public OrderItemDTO(Long menuItemId, Integer quantity, Double price) {
            this.menuItemId = menuItemId;
            this.quantity = quantity;
            this.price = price;
        }
        
        public Long getMenuItemId() {
            return menuItemId;
        }
        
        public void setMenuItemId(Long menuItemId) {
            this.menuItemId = menuItemId;
        }
        
        public Integer getQuantity() {
            return quantity;
        }
        
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
        
        public Double getPrice() {
            return price;
        }
        
        public void setPrice(Double price) {
            this.price = price;
        }
    }
}
