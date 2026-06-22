package com.fooddelivery.order.service;

import com.fooddelivery.order.client.NotificationClient;
import com.fooddelivery.order.client.PaymentClient;
import com.fooddelivery.order.client.RestaurantClient;
import com.fooddelivery.order.dto.*;
import com.fooddelivery.order.dto.OrderRequest;
import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.entity.OrderItem;
import com.fooddelivery.order.entity.OrderStatus;
import com.fooddelivery.order.event.OrderCreatedEvent;
import com.fooddelivery.order.messaging.OrderEventPublisher;
import com.fooddelivery.order.repository.OrderRepository;
import com.fooddelivery.order.saga.OrderSagaOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantClient restaurantClient;

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private OrderEventPublisher orderEventPublisher;

    @Autowired
    private OrderSagaOrchestrator sagaOrchestrator;

    @Transactional
    public Order createOrder(CreateOrderRequest createOrderRequest) {
        Order order = new Order(
                createOrderRequest.getUserId(),
                createOrderRequest.getRestaurantId(),
                createOrderRequest.getDeliveryAddress());

        List<OrderItem> orderItems = createOrderRequest.getOrderItems().stream()
                .map(this::createOrderItem)
                .collect(Collectors.toList());

        orderItems.forEach(item -> item.setOrder(order));
        order.setOrderItems(orderItems);

        BigDecimal totalAmount = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);

        order.setStatus(OrderStatus.PAYMENT_PENDING);

        Order savedOrder = orderRepository.save(order);
        logger.info("Order saved with id: {} for userId: {}", savedOrder.getId(), savedOrder.getUserId());

        publishOrderCreatedEvent(savedOrder);

        try {
            sagaOrchestrator.startSaga(savedOrder);
        } catch (Exception e) {
            logger.error("Failed to start saga for orderId: {}: {}", savedOrder.getId(), e.getMessage(), e);
        }

        return savedOrder;
    }

    @Transactional
    public Order createOrderFromEntity(Order order) {
        order.setCreatedAt(LocalDateTime.now());

        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PAYMENT_PENDING);
        }

        Order savedOrder = orderRepository.save(order);
        logger.info("Order (from entity) saved with id: {} for userId: {}", savedOrder.getId(), savedOrder.getUserId());

        publishOrderCreatedEvent(savedOrder);

        try {
            sagaOrchestrator.startSaga(savedOrder);
        } catch (Exception e) {
            logger.error("Failed to start saga for orderId: {}: {}", savedOrder.getId(), e.getMessage(), e);
        }

        return savedOrder;
    }

    private void publishOrderCreatedEvent(Order savedOrder) {
        try {
            OrderCreatedEvent event = new OrderCreatedEvent();

            invokeSetterIfPresent(event, "setOrderId", Long.class, savedOrder.getId());
            invokeSetterIfPresent(event, "setUserId", Long.class, savedOrder.getUserId());
            invokeSetterIfPresent(event, "setRestaurantId", Long.class, savedOrder.getRestaurantId());

            if (!invokeSetterIfPresent(event, "setTotalAmount", Double.class,
                    savedOrder.getTotalAmount().doubleValue())) {
                invokeSetterIfPresent(event, "setTotalAmount", BigDecimal.class, savedOrder.getTotalAmount());
            }

            if (!invokeSetterIfPresent(event, "setCreatedAt", LocalDateTime.class, LocalDateTime.now())) {
                invokeSetterIfPresent(event, "setTimestamp", LocalDateTime.class, LocalDateTime.now());
            }

            invokeSetterIfPresent(event, "setDeliveryAddress", String.class, savedOrder.getDeliveryAddress());
            invokeSetterIfPresent(event, "setStatus", String.class,
                    savedOrder.getStatus() != null ? savedOrder.getStatus().toString() : "PAYMENT_PENDING");

            orderEventPublisher.publishOrderCreated(event);
            logger.info("Triggered publish for OrderCreatedEvent, orderId: {}", savedOrder.getId());
        } catch (Exception e) {
            logger.error("Failed to publish OrderCreatedEvent for orderId: {}: {}", savedOrder.getId(), e.getMessage(),
                    e);
        }
    }

    private boolean invokeSetterIfPresent(Object target, String setterName, Class<?> paramType, Object value) {
        try {
            Method method = target.getClass().getMethod(setterName, paramType);
            method.invoke(target, value);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        } catch (Exception e) {
            logger.debug("Could not invoke {} on {}: {}", setterName, target.getClass().getSimpleName(),
                    e.getMessage());
            return false;
        }
    }

    private OrderItem createOrderItem(OrderItemRequest request) {
        try {
            MenuItemDto menuItem = restaurantClient
                    .getMenuItem(request.getMenuItemId(), request.getMenuItemId());

            if (!menuItem.getIsAvailable()) {
                throw new RuntimeException("Menu item is not available: " + menuItem.getName());
            }

            return new OrderItem(request.getMenuItemId(), request.getQuantity(), menuItem.getPrice());
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Menu item is not available")) {
                throw new RuntimeException(e.getMessage());
            }
            logger.warn("Restaurant call failed, using fallback price for menuItemId: {}", request.getMenuItemId());
            return new OrderItem(request.getMenuItemId(), request.getQuantity(), BigDecimal.valueOf(10.00));
        }
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAllWithItems();
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Order> findById(Long id) {
        return orderRepository.findByIdWithItems(id);
    }

    @Transactional
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdWithItems(userId);
    }

    public List<Order> getOrdersByRestaurantId(Long restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);

        try {
            NotificationRequest notification = new NotificationRequest(
                    updatedOrder.getUserId(),
                    "Order Status Updated",
                    "Your order #" + updatedOrder.getId() + " status has been updated to: " + status,
                    "ORDER_STATUS_UPDATED");
            notificationClient.sendNotification(notification);
        } catch (Exception e) {
            logger.error("Notification dispatch failed: {}", e.getMessage());
        }

        return updatedOrder;
    }

    @Transactional
    public Order confirmOrder(Long id) {
        Order order = getOrderById(id);

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new RuntimeException("Order cannot be confirmed. Current status: " + order.getStatus());
        }

        try {
            PaymentRequest paymentRequest = new PaymentRequest(
                    order.getId(),
                    order.getTotalAmount(),
                    "CREDIT_CARD");

            PaymentResponse paymentResponse = paymentClient.processPayment(paymentRequest);

            if ("SUCCESS".equals(paymentResponse.getStatus())) {
                return updateOrderStatus(id, OrderStatus.CONFIRMED);
            } else {
                throw new RuntimeException("Payment failed: " + paymentResponse.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException("Payment processing failed: " + e.getMessage());
        }
    }

    @Transactional
    public Order cancelOrder(Long id) {
        Order order = getOrderById(id);

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order cannot be cancelled. Current status: " + order.getStatus());
        }

        return updateOrderStatus(id, OrderStatus.CANCELLED);
    }

    @Transactional
    public Order deliverOrder(Long id) {
        Order order = getOrderById(id);

        if (order.getStatus() != OrderStatus.OUT_FOR_DELIVERY) {
            throw new RuntimeException("Order cannot be delivered. Current status: " + order.getStatus());
        }

        return updateOrderStatus(id, OrderStatus.DELIVERED);
    }
}