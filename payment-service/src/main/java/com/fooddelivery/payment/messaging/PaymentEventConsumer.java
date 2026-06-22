package com.fooddelivery.payment.messaging;

import com.fooddelivery.payment.config.RabbitMQConfig;
import com.fooddelivery.payment.entity.Payment;
import com.fooddelivery.payment.entity.PaymentStatus;
import com.fooddelivery.payment.event.OrderCreatedEvent;
import com.fooddelivery.payment.event.PaymentFailedEvent;
import com.fooddelivery.payment.event.PaymentSuccessEvent;
import com.fooddelivery.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Consumer for Order Events
 * Listens to order.created.queue and initiates payment processing
 */
@Component
public class PaymentEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventConsumer.class);
    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher paymentEventPublisher;

    public PaymentEventConsumer(PaymentRepository paymentRepository, 
                               PaymentEventPublisher paymentEventPublisher) {
        this.paymentRepository = paymentRepository;
        this.paymentEventPublisher = paymentEventPublisher;
    }

    /**
     * Listen to order.created.queue
     * Initiate payment processing when a new order is created
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATED_QUEUE)
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        try {
            logger.info("Received OrderCreatedEvent for orderId: {}, userId: {}, amount: {}", 
                    event.getOrderId(), event.getUserId(), event.getTotalAmount());
            
            // Create payment record
            Payment payment = new Payment();
            payment.setOrderId(event.getOrderId());
            payment.setUserId(event.getUserId());
            payment.setAmount(BigDecimal.valueOf(event.getTotalAmount()));
            payment.setPaymentMethod("CREDIT_CARD"); // Default payment method
            payment.setStatus(PaymentStatus.PENDING);
            payment.setCreatedAt(LocalDateTime.now());
            
            // Save payment
            Payment savedPayment = paymentRepository.save(payment);
            logger.info("Payment record created with paymentId: {} for orderId: {}", 
                    savedPayment.getId(), event.getOrderId());
            
            // Simulate payment processing (in real scenario, this would call payment gateway)
            boolean paymentSuccessful = processPayment(savedPayment);
            
            if (paymentSuccessful) {
                // Update payment status to SUCCESS
                savedPayment.setStatus(PaymentStatus.SUCCESS);
                savedPayment.setTransactionId(UUID.randomUUID().toString());
                savedPayment.setUpdatedAt(LocalDateTime.now());
                paymentRepository.save(savedPayment);
                
                // Publish PaymentSuccessEvent
                PaymentSuccessEvent successEvent = new PaymentSuccessEvent(
                        savedPayment.getId(),
                        savedPayment.getOrderId(),
                        savedPayment.getUserId(),
                        savedPayment.getAmount().doubleValue(),
                        savedPayment.getTransactionId(),
                        LocalDateTime.now()
                );
                paymentEventPublisher.publishPaymentSuccess(successEvent);
                
                logger.info("Payment successful for orderId: {}, paymentId: {}", 
                        event.getOrderId(), savedPayment.getId());
            } else {
                // Update payment status to FAILED
                savedPayment.setStatus(PaymentStatus.FAILED);
                savedPayment.setUpdatedAt(LocalDateTime.now());
                paymentRepository.save(savedPayment);
                
                // Publish PaymentFailedEvent
                PaymentFailedEvent failedEvent = new PaymentFailedEvent(
                        savedPayment.getId(),
                        savedPayment.getOrderId(),
                        savedPayment.getUserId(),
                        savedPayment.getAmount().doubleValue(),
                        "Insufficient funds",
                        LocalDateTime.now()
                );
                paymentEventPublisher.publishPaymentFailed(failedEvent);
                
                logger.warn("Payment failed for orderId: {}, paymentId: {}", 
                        event.getOrderId(), savedPayment.getId());
            }
            
        } catch (Exception e) {
            logger.error("Error processing OrderCreatedEvent for orderId: {}", event.getOrderId(), e);
            throw e; // Re-throw to trigger retry mechanism
        }
    }
    
    /**
     * Simulate payment processing
     * In real scenario, this would integrate with payment gateway (Stripe, PayPal, etc.)
     * @param payment Payment entity
     * @return true if payment successful, false otherwise
     */
    private boolean processPayment(Payment payment) {
        // Simulate payment processing delay
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate 90% success rate
        return Math.random() < 0.9;
    }
}
