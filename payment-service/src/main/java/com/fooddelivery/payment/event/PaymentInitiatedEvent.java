package com.fooddelivery.payment.event;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Event published when payment process is initiated
 * Consumed by: notification-service (optional)
 */
public class PaymentInitiatedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long paymentId;
    private Long orderId;
    private Long userId;
    private Double amount;
    private String paymentMethod;
    private LocalDateTime timestamp;

    // No-args constructor
    public PaymentInitiatedEvent() {
        this.timestamp = LocalDateTime.now();
    }

    // All-args constructor
    public PaymentInitiatedEvent(Long paymentId, Long orderId, Long userId, 
                                Double amount, String paymentMethod, LocalDateTime timestamp) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    // Getters and Setters
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "PaymentInitiatedEvent{" +
                "paymentId=" + paymentId +
                ", orderId=" + orderId +
                ", userId=" + userId +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}