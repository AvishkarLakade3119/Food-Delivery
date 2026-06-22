package com.fooddelivery.order.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentRefundEvent implements Serializable {

    private String eventId;
    private Long orderId;
    private Long paymentId;
    private Long userId;
    private Double amount;
    private String reason;
    private LocalDateTime timestamp;

    public PaymentRefundEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }

    public PaymentRefundEvent(Long orderId, Long paymentId, Long userId, Double amount, String reason) {
        this.eventId = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.userId = userId;
        this.amount = amount;
        this.reason = reason;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}