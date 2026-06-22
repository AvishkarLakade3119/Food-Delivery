package com.fooddelivery.order.event.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for Order Item in events
 * Used in OrderCreatedEvent and RestaurantOrderReceivedEvent
 */
public class OrderItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long itemId;
    private String itemName;
    private Integer quantity;
    private Double price;

    // No-args constructor
    public OrderItemDTO() {}

    // All-args constructor
    public OrderItemDTO(Long itemId, String itemName, Integer quantity, Double price) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
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

    @Override
    public String toString() {
        return "OrderItemDTO{" +
                "itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}