package com.fooddelivery.order.service.resilience;

import com.fooddelivery.order.client.PaymentClient;
import com.fooddelivery.order.dto.PaymentRequest;
import com.fooddelivery.order.dto.PaymentResponse;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ResilientPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(ResilientPaymentService.class);
    private static final String INSTANCE = "paymentService";

    private final PaymentClient paymentClient;

    public ResilientPaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    @CircuitBreaker(name = INSTANCE, fallbackMethod = "processPaymentFallback")
    @Retry(name = INSTANCE)
    @TimeLimiter(name = INSTANCE)
    @Bulkhead(name = INSTANCE, type = Bulkhead.Type.SEMAPHORE)
    public CompletableFuture<PaymentResponse> processPayment(PaymentRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("Calling payment-service for orderId: {}", request.getOrderId());
            return paymentClient.processPayment(request);
        });
    }

    public CompletableFuture<PaymentResponse> processPaymentFallback(PaymentRequest request, Throwable ex) {
        logger.error("Payment fallback triggered for orderId: {}: {}",
                request.getOrderId(), ex.getMessage());

        PaymentResponse fallback = new PaymentResponse();
        fallback.setStatus("PENDING");
        fallback.setMessage("Payment service temporarily unavailable. Order queued for retry.");
        return CompletableFuture.completedFuture(fallback);
    }
}