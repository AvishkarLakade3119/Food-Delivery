package com.fooddelivery.order.service.resilience;

import com.fooddelivery.order.client.NotificationClient;
import com.fooddelivery.order.dto.NotificationRequest;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ResilientNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(ResilientNotificationService.class);
    private static final String INSTANCE = "notificationService";

    private final NotificationClient notificationClient;

    public ResilientNotificationService(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    @CircuitBreaker(name = INSTANCE, fallbackMethod = "sendNotificationFallback")
    @Retry(name = INSTANCE)
    @TimeLimiter(name = INSTANCE)
    @Bulkhead(name = INSTANCE, type = Bulkhead.Type.SEMAPHORE)
    public CompletableFuture<Void> sendNotification(NotificationRequest request) {
        return CompletableFuture.runAsync(() -> {
            logger.info("Calling notification-service for userId: {}", request.getUserId());
            notificationClient.sendNotification(request);
        });
    }

    public CompletableFuture<Void> sendNotificationFallback(NotificationRequest request, Throwable ex) {
        logger.warn("Notification fallback - notification dropped for userId: {} (reason: {})",
                request.getUserId(), ex.getMessage());
        // Could queue to RabbitMQ for later retry
        return CompletableFuture.completedFuture(null);
    }
}