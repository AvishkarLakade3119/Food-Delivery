package com.fooddelivery.order.messaging;

import com.fooddelivery.order.config.RabbitMQConfig;
import com.fooddelivery.order.event.OrderCancelledEvent;
import com.fooddelivery.order.event.OrderConfirmedEvent;
import com.fooddelivery.order.event.OrderCreatedEvent;
import com.fooddelivery.order.event.PaymentRefundEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOrderCreated(OrderCreatedEvent event) {
        try {
            logger.info("Publishing OrderCreatedEvent for orderId: {}", event.getOrderId());
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_EXCHANGE,
                    RabbitMQConfig.ORDER_CREATED_KEY,
                    event);
            logger.info("Successfully published OrderCreatedEvent for orderId: {}", event.getOrderId());
        } catch (Exception e) {
            logger.error("Failed to publish OrderCreatedEvent for orderId: {}", event.getOrderId(), e);
        }
    }

    public void publishOrderConfirmed(OrderConfirmedEvent event) {
        try {
            logger.info("Publishing OrderConfirmedEvent for orderId: {}", event.getOrderId());
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_EXCHANGE,
                    RabbitMQConfig.ORDER_CONFIRMED_KEY,
                    event);
            logger.info("Successfully published OrderConfirmedEvent for orderId: {}", event.getOrderId());
        } catch (Exception e) {
            logger.error("Failed to publish OrderConfirmedEvent for orderId: {}", event.getOrderId(), e);
        }
    }

    public void publishOrderCancelled(OrderCancelledEvent event) {
        try {
            logger.info("Publishing OrderCancelledEvent for orderId: {}", event.getOrderId());
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_EXCHANGE,
                    RabbitMQConfig.ORDER_CANCELLED_KEY,
                    event);
            logger.info("Successfully published OrderCancelledEvent for orderId: {}", event.getOrderId());
        } catch (Exception e) {
            logger.error("Failed to publish OrderCancelledEvent for orderId: {}", event.getOrderId(), e);
        }
    }

    public void publishPaymentRefund(PaymentRefundEvent event) {
        try {
            logger.info("Publishing PaymentRefundEvent for orderId: {}", event.getOrderId());
            rabbitTemplate.convertAndSend("payment.exchange", "payment.refund", event);
            logger.info("Successfully published PaymentRefundEvent for orderId: {}", event.getOrderId());
        } catch (Exception e) {
            logger.error("Failed to publish PaymentRefundEvent for orderId: {}: {}", event.getOrderId(), e.getMessage(), e);
        }
    }
}