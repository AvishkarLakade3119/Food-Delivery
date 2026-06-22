package com.fooddelivery.restaurant.messaging;

import com.fooddelivery.restaurant.config.RabbitMQConfig;
import com.fooddelivery.restaurant.event.RestaurantAcceptedEvent;
import com.fooddelivery.restaurant.event.RestaurantRejectedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publisher for Restaurant Events
 * Publishes restaurant-related events to RabbitMQ exchanges
 */
@Component
public class RestaurantEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public RestaurantEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publish RestaurantAcceptedEvent to restaurant.exchange
     * @param event RestaurantAcceptedEvent
     */
    public void publishRestaurantAccepted(RestaurantAcceptedEvent event) {
        try {
            logger.info("Publishing RestaurantAcceptedEvent for orderId: {}, restaurantId: {}", 
                    event.getOrderId(), event.getRestaurantId());
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.RESTAURANT_EXCHANGE,
                    RabbitMQConfig.RESTAURANT_ACCEPTED_KEY,
                    event
            );
            logger.info("Successfully published RestaurantAcceptedEvent for orderId: {}, restaurantId: {}", 
                    event.getOrderId(), event.getRestaurantId());
        } catch (Exception e) {
            logger.error("Failed to publish RestaurantAcceptedEvent for orderId: {}, restaurantId: {}", 
                    event.getOrderId(), event.getRestaurantId(), e);
            // Service should continue even if message publishing fails
        }
    }

    /**
     * Publish RestaurantRejectedEvent to restaurant.exchange
     * @param event RestaurantRejectedEvent
     */
    public void publishRestaurantRejected(RestaurantRejectedEvent event) {
        try {
            logger.info("Publishing RestaurantRejectedEvent for orderId: {}, restaurantId: {}", 
                    event.getOrderId(), event.getRestaurantId());
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.RESTAURANT_EXCHANGE,
                    RabbitMQConfig.RESTAURANT_REJECTED_KEY,
                    event
            );
            logger.info("Successfully published RestaurantRejectedEvent for orderId: {}, restaurantId: {}", 
                    event.getOrderId(), event.getRestaurantId());
        } catch (Exception e) {
            logger.error("Failed to publish RestaurantRejectedEvent for orderId: {}, restaurantId: {}", 
                    event.getOrderId(), event.getRestaurantId(), e);
            // Service should continue even if message publishing fails
        }
    }
}
