package com.fooddelivery.restaurant.messaging;

import com.fooddelivery.restaurant.config.RabbitMQConfig;
import com.fooddelivery.restaurant.event.OrderConfirmedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumer for Order Events
 * Listens to order.confirmed.queue and notifies restaurant of new order
 */
@Component
public class RestaurantEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantEventConsumer.class);

    /**
     * Listen to order.confirmed.queue
     * Notify restaurant when an order is confirmed (after payment success)
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_CONFIRMED_QUEUE)
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        try {
            logger.info("Received OrderConfirmedEvent for orderId: {}, restaurantId: {}", 
                    event.getOrderId(), event.getRestaurantId());
            
            // In real scenario, this would:
            // 1. Notify restaurant dashboard/app
            // 2. Send push notification to restaurant
            // 3. Update restaurant order management system
            // 4. Trigger kitchen display system
            
            logger.info("Restaurant {} notified about new order {}", 
                    event.getRestaurantId(), event.getOrderId());
            
            // For now, just log the event
            logger.info("Order details - Total: {}, Estimated Delivery: {}", 
                    event.getTotalAmount(), event.getEstimatedDeliveryTime());
            
        } catch (Exception e) {
            logger.error("Error processing OrderConfirmedEvent for orderId: {}", event.getOrderId(), e);
            // Don't re-throw - restaurant notification failure should not block the flow
        }
    }
}
