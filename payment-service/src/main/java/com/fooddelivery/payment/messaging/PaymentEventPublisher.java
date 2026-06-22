package com.fooddelivery.payment.messaging;

import com.fooddelivery.payment.config.RabbitMQConfig;
import com.fooddelivery.payment.event.PaymentFailedEvent;
import com.fooddelivery.payment.event.PaymentSuccessEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publisher for Payment Events
 * Publishes payment-related events to RabbitMQ exchanges
 */
@Component
public class PaymentEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public PaymentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publish PaymentSuccessEvent to payment.exchange
     * @param event PaymentSuccessEvent
     */
    public void publishPaymentSuccess(PaymentSuccessEvent event) {
        try {
            logger.info("Publishing PaymentSuccessEvent for orderId: {}, paymentId: {}", 
                    event.getOrderId(), event.getPaymentId());
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PAYMENT_EXCHANGE,
                    RabbitMQConfig.PAYMENT_SUCCESS_KEY,
                    event
            );
            logger.info("Successfully published PaymentSuccessEvent for orderId: {}, paymentId: {}", 
                    event.getOrderId(), event.getPaymentId());
        } catch (Exception e) {
            logger.error("Failed to publish PaymentSuccessEvent for orderId: {}, paymentId: {}", 
                    event.getOrderId(), event.getPaymentId(), e);
            // Service should continue even if message publishing fails
        }
    }

    /**
     * Publish PaymentFailedEvent to payment.exchange
     * @param event PaymentFailedEvent
     */
    public void publishPaymentFailed(PaymentFailedEvent event) {
        try {
            logger.info("Publishing PaymentFailedEvent for orderId: {}, paymentId: {}", 
                    event.getOrderId(), event.getPaymentId());
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PAYMENT_EXCHANGE,
                    RabbitMQConfig.PAYMENT_FAILED_KEY,
                    event
            );
            logger.info("Successfully published PaymentFailedEvent for orderId: {}, paymentId: {}", 
                    event.getOrderId(), event.getPaymentId());
        } catch (Exception e) {
            logger.error("Failed to publish PaymentFailedEvent for orderId: {}, paymentId: {}", 
                    event.getOrderId(), event.getPaymentId(), e);
            // Service should continue even if message publishing fails
        }
    }
}
