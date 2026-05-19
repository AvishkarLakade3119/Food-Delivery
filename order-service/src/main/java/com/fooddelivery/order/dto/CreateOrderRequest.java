package com.fooddelivery.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateOrderRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;
    
    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;
    
    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItemRequest> orderItems;
    
    // Constructors
    public CreateOrderRequest() {}
    
    public CreateOrderRequest(Long userId, Long restaurantId, String deliveryAddress, List<OrderItemRequest> orderItems) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.deliveryAddress = deliveryAddress;
        this.orderItems = orderItems;
    }
    
    // Getters and Setters
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
    
    public String getDeliveryAddress() {
        return deliveryAddress;
    }
    
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    
    public List<OrderItemRequest> getOrderItems() {
        return orderItems;
    }
    
    public void setOrderItems(List<OrderItemRequest> orderItems) {
        this.orderItems = orderItems;
    }
}