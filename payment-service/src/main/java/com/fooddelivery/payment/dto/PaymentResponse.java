package com.fooddelivery.payment.dto;

public class PaymentResponse {
    private String status;
    private String transactionId;
    private String message;
    
    // Constructors
    public PaymentResponse() {}
    
    public PaymentResponse(String status, String transactionId, String message) {
        this.status = status;
        this.transactionId = transactionId;
        this.message = message;
    }
    
    // Getters and Setters
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}