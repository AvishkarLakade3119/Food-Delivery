package com.fooddelivery.order.messaging;

import com.fooddelivery.order.config.RabbitMQConfig;
import com.fooddelivery.order.entity.ProcessedEvent;
import com.fooddelivery.order.repository.ProcessedEventRepository;
import com.fooddelivery.order.saga.OrderSagaOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Consumer for Order-related Events from Payment and Restaurant services.
 * Implements an idempotent consumption pattern integrated with the SAGA
 * Orchestrator.
 */
@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    @Autowired
    private OrderSagaOrchestrator sagaOrchestrator;

    @Autowired
    private ProcessedEventRepository processedEventRepository;

    /**
     * Listen to payment.success.queue
     * Route to SAGA orchestrator for payment success handling
     */
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_SUCCESS_QUEUE)
    @Transactional
    public void handlePaymentSuccess(Map<String, Object> event) {
        String eventId = getEventId(event);

        // Idempotency check
        if (eventId != null && processedEventRepository.existsByEventId(eventId)) {
            log.warn("Duplicate event detected, skipping: {}", eventId);
            return;
        }

        try {
            Long orderId = getLong(event, "orderId");
            Long paymentId = getLong(event, "paymentId");

            log.info("Received PaymentSuccessEvent for orderId: {}, paymentId: {}", orderId, paymentId);

            sagaOrchestrator.handlePaymentSuccess(orderId, paymentId);

            // Mark event as processed
            if (eventId != null) {
                processedEventRepository.save(new ProcessedEvent(eventId, "PAYMENT_SUCCESS"));
            }
        } catch (Exception e) {
            log.error("Error processing PaymentSuccessEvent: {}", e.getMessage(), e);
            throw e; // Let RabbitMQ retry or send to DLQ
        }
    }

    /**
     * Listen to payment.failed.queue
     * Route to SAGA orchestrator for payment failure compensation/handling
     */
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_FAILED_QUEUE)
    @Transactional
    public void handlePaymentFailed(Map<String, Object> event) {
        String eventId = getEventId(event);

        // Idempotency check
        if (eventId != null && processedEventRepository.existsByEventId(eventId)) {
            log.warn("Duplicate event detected, skipping: {}", eventId);
            return;
        }

        try {
            Long orderId = getLong(event, "orderId");
            String reason = (String) event.getOrDefault("reason", "Payment processing failed");

            // Fallback to failureReason if key is named differently in payload
            if (event.containsKey("failureReason")) {
                reason = (String) event.get("failureReason");
            }

            log.info("Received PaymentFailedEvent for orderId: {}, reason: {}", orderId, reason);

            sagaOrchestrator.handlePaymentFailure(orderId, reason);

            // Mark event as processed
            if (eventId != null) {
                processedEventRepository.save(new ProcessedEvent(eventId, "PAYMENT_FAILED"));
            }
        } catch (Exception e) {
            log.error("Error processing PaymentFailedEvent: {}", e.getMessage(), e);
            throw e; // Let RabbitMQ retry or send to DLQ
        }
    }

    /**
     * Listen to restaurant.accepted.queue
     * Route to SAGA orchestrator for fulfillment tracking
     */
    @RabbitListener(queues = RabbitMQConfig.RESTAURANT_ACCEPTED_QUEUE)
    @Transactional
    public void handleRestaurantAccepted(Map<String, Object> event) {
        String eventId = getEventId(event);

        // Idempotency check
        if (eventId != null && processedEventRepository.existsByEventId(eventId)) {
            log.warn("Duplicate event detected, skipping: {}", eventId);
            return;
        }

        try {
            Long orderId = getLong(event, "orderId");

            log.info("Received RestaurantAcceptedEvent for orderId: {}", orderId);

            sagaOrchestrator.handleRestaurantAccepted(orderId);

            // Mark event as processed
            if (eventId != null) {
                processedEventRepository.save(new ProcessedEvent(eventId, "RESTAURANT_ACCEPTED"));
            }
        } catch (Exception e) {
            log.error("Error processing RestaurantAcceptedEvent: {}", e.getMessage(), e);
            throw e; // Let RabbitMQ retry or send to DLQ
        }
    }

    /**
     * Listen to restaurant.rejected.queue
     * Route to SAGA orchestrator for compensation tasks (e.g., triggering refunds)
     */
    @RabbitListener(queues = RabbitMQConfig.RESTAURANT_REJECTED_QUEUE)
    @Transactional
    public void handleRestaurantRejected(Map<String, Object> event) {
        String eventId = getEventId(event);

        // Idempotency check
        if (eventId != null && processedEventRepository.existsByEventId(eventId)) {
            log.warn("Duplicate event detected, skipping: {}", eventId);
            return;
        }

        try {
            Long orderId = getLong(event, "orderId");
            String reason = (String) event.getOrDefault("reason", "Restaurant rejected order");

            // Fallback to rejectionReason if key is named differently in payload
            if (event.containsKey("rejectionReason")) {
                reason = (String) event.get("rejectionReason");
            }

            log.info("Received RestaurantRejectedEvent for orderId: {}, reason: {}", orderId, reason);

            sagaOrchestrator.handleRestaurantRejected(orderId, reason);

            // Mark event as processed
            if (eventId != null) {
                processedEventRepository.save(new ProcessedEvent(eventId, "RESTAURANT_REJECTED"));
            }
        } catch (Exception e) {
            log.error("Error processing RestaurantRejectedEvent: {}", e.getMessage(), e);
            throw e; // Let RabbitMQ retry or send to DLQ
        }
    }

    // ========== HELPER METHODS ==========

    private String getEventId(Map<String, Object> event) {
        Object id = event.get("eventId");
        return id != null ? id.toString() : null;
    }

    private Long getLong(Map<String, Object> event, String key) {
        Object value = event.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }
}