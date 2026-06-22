package com.fooddelivery.order.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentSuccessEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private Long paymentId;
    private Long orderId;
    private Long userId;
    private Double amount;
    private String transactionId;
    private LocalDateTime timestamp;

    public PaymentSuccessEvent() {
        this.eventId = UUID.randomUUID().toString();
    }

    public PaymentSuccessEvent(Long paymentId, Long orderId, Long userId,
                               Double amount, String transactionId, LocalDateTime timestamp) {
        this.eventId = UUID.randomUUID().toString();
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.transactionId = transactionId;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "PaymentSuccessEvent{" +
                "eventId='" + eventId + '\'' +
                ", paymentId=" + paymentId +
                ", orderId=" + orderId +
                ", userId=" + userId +
                ", amount=" + amount +
                ", transactionId='" + transactionId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}