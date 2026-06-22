package com.fooddelivery.order.saga;

import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.entity.OrderStatus;
import com.fooddelivery.order.entity.SagaState;
import com.fooddelivery.order.event.OrderCreatedEvent;
import com.fooddelivery.order.event.PaymentRefundEvent;
import com.fooddelivery.order.event.dto.OrderItemDTO;
import com.fooddelivery.order.messaging.OrderEventPublisher;
import com.fooddelivery.order.repository.OrderRepository;
import com.fooddelivery.order.repository.SagaStateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderSagaOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(OrderSagaOrchestrator.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SagaStateRepository sagaStateRepository;

    @Autowired
    private OrderEventPublisher orderEventPublisher;

    /**
     * Step 1: Start the saga when order is created
     */
    @Transactional
    public void startSaga(Order order) {
        String sagaId = UUID.randomUUID().toString();

        // Create saga state
        SagaState sagaState = new SagaState(sagaId, order.getId(), "ORDER_CREATED", "IN_PROGRESS");
        sagaStateRepository.save(sagaState);

        // Update order status to PAYMENT_PENDING
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        orderRepository.save(order);

        // Publish OrderCreatedEvent
        try {
            List<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
                    .map(item -> new OrderItemDTO(
                            item.getMenuItemId(),
                            "Item-" + item.getMenuItemId(),
                            item.getQuantity(),
                            item.getPrice().doubleValue()
                    ))
                    .collect(Collectors.toList());

            OrderCreatedEvent event = new OrderCreatedEvent(
                    order.getId(),
                    order.getUserId(),
                    order.getRestaurantId(),
                    itemDTOs,
                    order.getTotalAmount().doubleValue(),
                    order.getDeliveryAddress(),
                    order.getStatus().name(),
                    LocalDateTime.now()
            );
            event.setEventId(sagaId);
            orderEventPublisher.publishOrderCreated(event);

            log.info("Saga started for orderId: {}, sagaId: {}", order.getId(), sagaId);
        } catch (Exception e) {
            log.error("Failed to start saga for orderId: {}: {}", order.getId(), e.getMessage());
            // Compensate: revert order to CREATED
            order.setStatus(OrderStatus.CREATED);
            orderRepository.save(order);
            sagaState.setStatus("FAILED");
            sagaState.setFailureReason("Failed to publish event: " + e.getMessage());
            sagaStateRepository.save(sagaState);
        }
    }

    /**
     * Step 2: Handle payment success
     */
    @Transactional
    public void handlePaymentSuccess(Long orderId, Long paymentId) {
        log.info("Saga: Payment success for orderId: {}, paymentId: {}", orderId, paymentId);

        Order order = orderRepository.findByIdWithItems(orderId).orElse(null);
        if (order == null) {
            log.error("Saga: Order not found: {}", orderId);
            return;
        }

        // Update order status
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        // Update saga state
        SagaState sagaState = sagaStateRepository.findByOrderId(orderId).orElse(null);
        if (sagaState != null) {
            sagaState.setCurrentStep("PAYMENT_SUCCESS");
            sagaState.setPaymentId(paymentId);
            sagaState.setUpdatedAt(LocalDateTime.now());
            sagaStateRepository.save(sagaState);
        }

        // Next step: wait for restaurant confirmation
        order.setStatus(OrderStatus.RESTAURANT_PENDING);
        orderRepository.save(order);

        log.info("Saga: Order {} moved to RESTAURANT_PENDING", orderId);
    }

    /**
     * Step 3: Handle payment failure — compensate
     */
    @Transactional
    public void handlePaymentFailure(Long orderId, String reason) {
        log.info("Saga: Payment failed for orderId: {}, reason: {}", orderId, reason);

        Order order = orderRepository.findByIdWithItems(orderId).orElse(null);
        if (order == null) {
            log.error("Saga: Order not found: {}", orderId);
            return;
        }

        // Compensate: cancel the order
        order.setStatus(OrderStatus.PAYMENT_FAILED);
        orderRepository.save(order);

        // Update saga state
        SagaState sagaState = sagaStateRepository.findByOrderId(orderId).orElse(null);
        if (sagaState != null) {
            sagaState.setCurrentStep("PAYMENT_FAILED");
            sagaState.setStatus("COMPENSATING");
            sagaState.setFailureReason(reason);
            sagaState.setUpdatedAt(LocalDateTime.now());
            sagaStateRepository.save(sagaState);
        }

        // Final status: CANCELLED
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        if (sagaState != null) {
            sagaState.setStatus("COMPENSATED");
            sagaState.setCurrentStep("ORDER_CANCELLED");
            sagaState.setUpdatedAt(LocalDateTime.now());
            sagaStateRepository.save(sagaState);
        }

        log.info("Saga: Order {} cancelled due to payment failure", orderId);
    }

    /**
     * Step 4: Handle restaurant acceptance
     */
    @Transactional
    public void handleRestaurantAccepted(Long orderId) {
        log.info("Saga: Restaurant accepted orderId: {}", orderId);

        Order order = orderRepository.findByIdWithItems(orderId).orElse(null);
        if (order == null) {
            log.error("Saga: Order not found: {}", orderId);
            return;
        }

        // Update order to CONFIRMED
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        // Update saga state
        SagaState sagaState = sagaStateRepository.findByOrderId(orderId).orElse(null);
        if (sagaState != null) {
            sagaState.setCurrentStep("RESTAURANT_ACCEPTED");
            sagaState.setStatus("COMPLETED");
            sagaState.setUpdatedAt(LocalDateTime.now());
            sagaStateRepository.save(sagaState);
        }

        log.info("Saga: Order {} CONFIRMED successfully. Saga complete!", orderId);
    }

    /**
     * Step 5: Handle restaurant rejection — compensate (refund payment)
     */
    @Transactional
    public void handleRestaurantRejected(Long orderId, String reason) {
        log.info("Saga: Restaurant rejected orderId: {}, reason: {}", orderId, reason);

        Order order = orderRepository.findByIdWithItems(orderId).orElse(null);
        if (order == null) {
            log.error("Saga: Order not found: {}", orderId);
            return;
        }

        // Update order status
        order.setStatus(OrderStatus.RESTAURANT_REJECTED);
        orderRepository.save(order);

        // Update saga state
        SagaState sagaState = sagaStateRepository.findByOrderId(orderId).orElse(null);
        if (sagaState != null) {
            sagaState.setCurrentStep("RESTAURANT_REJECTED");
            sagaState.setStatus("COMPENSATING");
            sagaState.setFailureReason(reason);
            sagaState.setUpdatedAt(LocalDateTime.now());
            sagaStateRepository.save(sagaState);
        }

        // Compensate: publish refund event
        try {
            Long paymentId = sagaState != null ? sagaState.getPaymentId() : null;
            PaymentRefundEvent refundEvent = new PaymentRefundEvent(
                    orderId,
                    paymentId,
                    order.getUserId(),
                    order.getTotalAmount().doubleValue(),
                    "Restaurant rejected order: " + reason
            );
            orderEventPublisher.publishPaymentRefund(refundEvent);

            order.setStatus(OrderStatus.REFUND_PENDING);
            orderRepository.save(order);

            log.info("Saga: Refund event published for orderId: {}", orderId);
        } catch (Exception e) {
            log.error("Saga: Failed to publish refund event for orderId: {}: {}", orderId, e.getMessage());
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);
        }
    }

    /**
     * Step 6: Handle refund completed
     */
    @Transactional
    public void handleRefundCompleted(Long orderId) {
        log.info("Saga: Refund completed for orderId: {}", orderId);

        Order order = orderRepository.findByIdWithItems(orderId).orElse(null);
        if (order == null) return;

        order.setStatus(OrderStatus.REFUNDED);
        orderRepository.save(order);

        SagaState sagaState = sagaStateRepository.findByOrderId(orderId).orElse(null);
        if (sagaState != null) {
            sagaState.setCurrentStep("REFUND_COMPLETED");
            sagaState.setStatus("COMPENSATED");
            sagaState.setUpdatedAt(LocalDateTime.now());
            sagaStateRepository.save(sagaState);
        }

        // Final: cancel order
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        log.info("Saga: Order {} fully compensated and cancelled", orderId);
    }
}