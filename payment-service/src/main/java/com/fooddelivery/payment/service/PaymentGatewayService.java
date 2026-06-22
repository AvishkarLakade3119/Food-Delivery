package com.fooddelivery.payment.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Simulates calls to an external payment gateway (Stripe / Razorpay / PayPal).
 * In production, this would make HTTP calls to those APIs.
 */
@Service
public class PaymentGatewayService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentGatewayService.class);
    private static final String INSTANCE = "paymentGateway";

    @CircuitBreaker(name = INSTANCE, fallbackMethod = "chargeFallback")
    @Retry(name = INSTANCE)
    @TimeLimiter(name = INSTANCE)
    @Bulkhead(name = INSTANCE, type = Bulkhead.Type.SEMAPHORE)
    public CompletableFuture<String> charge(Long orderId, Double amount, String paymentMethod) {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("Calling external payment gateway for orderId={}, amount={}", orderId, amount);

            // Simulate latency
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Simulate 90% success rate
            if (Math.random() < 0.9) {
                String txnId = "txn_" + UUID.randomUUID();
                logger.info("Gateway charge SUCCESS for orderId={}, txnId={}", orderId, txnId);
                return txnId;
            } else {
                logger.warn("Gateway charge FAILED for orderId={}", orderId);
                throw new RuntimeException("Payment gateway declined the transaction");
            }
        });
    }

    public CompletableFuture<String> chargeFallback(Long orderId, Double amount, String paymentMethod, Throwable ex) {
        logger.error("Payment gateway fallback for orderId={} - reason: {}", orderId, ex.getMessage());
        return CompletableFuture.completedFuture("FALLBACK_NOT_PROCESSED");
    }
}