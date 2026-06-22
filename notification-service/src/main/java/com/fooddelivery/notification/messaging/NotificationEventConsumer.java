package com.fooddelivery.notification.messaging;

import com.fooddelivery.notification.config.RabbitMQConfig;
import com.fooddelivery.notification.entity.Notification;
import com.fooddelivery.notification.event.OrderCreatedEvent;
import com.fooddelivery.notification.event.PaymentSuccessEvent;
import com.fooddelivery.notification.event.RestaurantAcceptedEvent;
import com.fooddelivery.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Consumer for all events that require notifications
 * Creates notification records for order, payment, and restaurant events
 */
@Component
public class NotificationEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationEventConsumer.class);
    private final NotificationRepository notificationRepository;

    public NotificationEventConsumer(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Listen to order.created.queue
     * Create notification when a new order is created
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATED_QUEUE)
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        try {
            logger.info("Received OrderCreatedEvent for orderId: {}, userId: {}", 
                    event.getOrderId(), event.getUserId());
            
            Notification notification = new Notification();
            notification.setUserId(event.getUserId());
            notification.setTitle("Order Created");
            notification.setMessage("Your order #" + event.getOrderId() + " has been created successfully. Total: $" + event.getTotalAmount());
            notification.setType("ORDER_CREATED");
            notification.setCreatedAt(LocalDateTime.now());
            notification.setIsRead(false);
            
            notificationRepository.save(notification);
            logger.info("Notification created for OrderCreatedEvent, orderId: {}", event.getOrderId());
            
        } catch (Exception e) {
            logger.error("Error creating notification for OrderCreatedEvent, orderId: {}", event.getOrderId(), e);
            // Don't re-throw - notification failure should not block the flow
        }
    }

    /**
     * Listen to payment.success.queue
     * Create notification when payment is successful
     */
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_SUCCESS_QUEUE)
    @Transactional
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        try {
            logger.info("Received PaymentSuccessEvent for orderId: {}, userId: {}", 
                    event.getOrderId(), event.getUserId());
            
            Notification notification = new Notification();
            notification.setUserId(event.getUserId());
            notification.setTitle("Payment Successful");
            notification.setMessage("Payment of $" + event.getAmount() + " for order #" + event.getOrderId() + " was successful. Transaction ID: " + event.getTransactionId());
            notification.setType("PAYMENT_SUCCESS");
            notification.setCreatedAt(LocalDateTime.now());
            notification.setIsRead(false);
            
            notificationRepository.save(notification);
            logger.info("Notification created for PaymentSuccessEvent, orderId: {}", event.getOrderId());
            
        } catch (Exception e) {
            logger.error("Error creating notification for PaymentSuccessEvent, orderId: {}", event.getOrderId(), e);
            // Don't re-throw - notification failure should not block the flow
        }
    }

    /**
     * Listen to restaurant.accepted.queue
     * Create notification when restaurant accepts the order
     */
    @RabbitListener(queues = RabbitMQConfig.RESTAURANT_ACCEPTED_QUEUE)
    @Transactional
    public void handleRestaurantAccepted(RestaurantAcceptedEvent event) {
        try {
            logger.info("Received RestaurantAcceptedEvent for orderId: {}, restaurantId: {}", 
                    event.getOrderId(), event.getRestaurantId());
            
            // Note: We need to fetch userId from order service or include it in the event
            // For now, we'll create a generic notification without userId
            Notification notification = new Notification();
            notification.setUserId(null); // TODO: Get userId from order or include in event
            notification.setTitle("Restaurant Accepted Order");
            notification.setMessage("Restaurant has accepted your order #" + event.getOrderId() + ". Estimated preparation time: " + event.getEstimatedPrepTime() + " minutes.");
            notification.setType("ORDER_CONFIRMED");
            notification.setCreatedAt(LocalDateTime.now());
            notification.setIsRead(false);
            
            notificationRepository.save(notification);
            logger.info("Notification created for RestaurantAcceptedEvent, orderId: {}", event.getOrderId());
            
        } catch (Exception e) {
            logger.error("Error creating notification for RestaurantAcceptedEvent, orderId: {}", event.getOrderId(), e);
            // Don't re-throw - notification failure should not block the flow
        }
    }
}
