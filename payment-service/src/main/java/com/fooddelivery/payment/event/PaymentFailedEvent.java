package com.fooddelivery.payment.event;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Event published when payment fails
 * Consumed by: order-service, notification-service
 */
public class PaymentFailedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long paymentId;
    private Long orderId;
    private Long userId;
    private Double amount;
    private String failureReason;
    private LocalDateTime timestamp;

    // No-args constructor
    public PaymentFailedEvent() {
        this.timestamp = LocalDateTime.now();
    }

    // All-args constructor
    public PaymentFailedEvent(Long paymentId, Long orderId, Long userId, 
                             Double amount, String failureReason, LocalDateTime timestamp) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.failureReason = failureReason;
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

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "PaymentFailedEvent{" +
                "paymentId=" + paymentId +
                ", orderId=" + orderId +
                ", userId=" + userId +
                ", amount=" + amount +
                ", failureReason='" + failureReason + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}