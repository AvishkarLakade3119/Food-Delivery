package com.fooddelivery.order.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO for order item requests in order creation.
 * Represents individual items within an order with quantity, price, and optional instructions.
 */
public class OrderItemRequest {
    
    @NotNull(message = "Menu item ID is required")
    private Long menuItemId;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
    
    private String specialInstructions;
    
    // Constructors
    public OrderItemRequest() {}
    
    public OrderItemRequest(Long menuItemId, Integer quantity, BigDecimal price) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.price = price;
    }
    
    public OrderItemRequest(Long menuItemId, Integer quantity, BigDecimal price, String specialInstructions) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.price = price;
        this.specialInstructions = specialInstructions;
    }
    
    // Getters and Setters
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
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public String getSpecialInstructions() {
        return specialInstructions;
    }
    
    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
    
    /**
     * Calculate the total price for this order item (quantity * unit price).
     * 
     * @return Total price for this item
     */
    public BigDecimal getTotalPrice() {
        if (price == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}