package com.fooddelivery.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * DTO for order creation requests that supports both string and object delivery addresses.
 * This allows the API to accept deliveryAddress as:
 * 1. A simple string: "123 Main St, City, State 12345"
 * 2. A nested object: {"street": "123 Main St", "city": "City", "state": "State", "zipCode": "12345"}
 */
public class OrderRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;
    
    /**
     * Flexible delivery address field that accepts both String and Object types.
     * Jackson will deserialize this as either a String or a Map<String, Object>.
     */
    @NotNull(message = "Delivery address is required")
    private Object deliveryAddress;
    
    @NotEmpty(message = "Order items cannot be empty")
    @Valid
    private List<OrderItemRequest> items;
    
    // Constructors
    public OrderRequest() {}
    
    public OrderRequest(Long userId, Long restaurantId, Object deliveryAddress, List<OrderItemRequest> items) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.deliveryAddress = deliveryAddress;
        this.items = items;
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
    
    public Object getDeliveryAddress() {
        return deliveryAddress;
    }
    
    public void setDeliveryAddress(Object deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    
    public List<OrderItemRequest> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
    
    /**
     * Converts the deliveryAddress (String or Object) to a formatted string.
     * This method handles both input formats:
     * - If deliveryAddress is already a String, returns it as-is
     * - If deliveryAddress is an Object/Map, converts it to a formatted address string
     * 
     * @return Formatted delivery address string
     */
    @SuppressWarnings("unchecked")
    public String getDeliveryAddressAsString() {
        if (deliveryAddress == null) {
            return "";
        }
        
        // If it's already a string, return as-is
        if (deliveryAddress instanceof String) {
            return (String) deliveryAddress;
        }
        
        // If it's a Map (nested object), convert to formatted string
        if (deliveryAddress instanceof Map) {
            Map<String, Object> addr = (Map<String, Object>) deliveryAddress;
            StringBuilder sb = new StringBuilder();
            
            // Build formatted address: "street, apartmentNumber, city, state zipCode, country"
            if (addr.get("street") != null) {
                sb.append(addr.get("street"));
            }
            
            if (addr.get("apartmentNumber") != null) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(addr.get("apartmentNumber"));
            }
            
            if (addr.get("city") != null) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(addr.get("city"));
            }
            
            if (addr.get("state") != null) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(addr.get("state"));
            }
            
            if (addr.get("zipCode") != null) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(addr.get("zipCode"));
            }
            
            if (addr.get("country") != null) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(addr.get("country"));
            }
            
            return sb.toString();
        }
        
        // Fallback: convert to string
        return deliveryAddress.toString();
    }
}