package com.fooddelivery.payment.event;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Event published when payment is successful
 * Consumed by: order-service, restaurant-service, notification-service
 */
public class PaymentSuccessEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long paymentId;
    private Long orderId;
    private Long userId;
    private Double amount;
    private String transactionId;
    private LocalDateTime timestamp;

    // No-args constructor
    public PaymentSuccessEvent() {
        this.timestamp = LocalDateTime.now();
    }

    // All-args constructor
    public PaymentSuccessEvent(Long paymentId, Long orderId, Long userId, 
                              Double amount, String transactionId, LocalDateTime timestamp) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.transactionId = transactionId;
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

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "PaymentSuccessEvent{" +
                "paymentId=" + paymentId +
                ", orderId=" + orderId +
                ", userId=" + userId +
                ", amount=" + amount +
                ", transactionId='" + transactionId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}