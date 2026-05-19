package com.fooddelivery.order.client;

import com.fooddelivery.order.dto.PaymentRequest;
import com.fooddelivery.order.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentClient {
    
    @PostMapping("/payments/process")
    PaymentResponse processPayment(@RequestBody PaymentRequest paymentRequest);
}