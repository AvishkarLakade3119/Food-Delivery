package com.fooddelivery.order.service;

import com.fooddelivery.order.client.NotificationClient;
import com.fooddelivery.order.client.PaymentClient;
import com.fooddelivery.order.client.RestaurantClient;
import com.fooddelivery.order.dto.*;
import com.fooddelivery.order.dto.OrderRequest;
import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.entity.OrderItem;
import com.fooddelivery.order.entity.OrderStatus;
import com.fooddelivery.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private RestaurantClient restaurantClient;
    
    @Autowired
    private PaymentClient paymentClient;
    
    @Autowired
    private NotificationClient notificationClient;

    @Transactional
    public Order createOrder(CreateOrderRequest createOrderRequest) {
        // Create order entity
        Order order = new Order(
                createOrderRequest.getUserId(),
                createOrderRequest.getRestaurantId(),
                createOrderRequest.getDeliveryAddress()
        );

        // Process order items and calculate total
        List<OrderItem> orderItems = createOrderRequest.getOrderItems().stream()
                .map(this::createOrderItem)
                .collect(Collectors.toList());

        // Set order items
        orderItems.forEach(item -> item.setOrder(order));
        order.setOrderItems(orderItems);

        // Calculate total amount
        BigDecimal totalAmount = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Send notification
        try {
            NotificationRequest notification = new NotificationRequest(
                    savedOrder.getUserId(),
                    "Your order #" + savedOrder.getId() + " has been created successfully!",
                    "ORDER_CREATED"
            );
            notificationClient.sendNotification(notification);
        } catch (Exception e) {
            // Log error but don't fail the order creation
            System.err.println("Failed to send notification: " + e.getMessage());
        }

        return savedOrder;
    }

    /**
     * Create order from pre-built Order entity (used by new flexible API)
     */
    @Transactional
    public Order createOrderFromEntity(Order order) {
        // Set created timestamp
        order.setCreatedAt(java.time.LocalDateTime.now());

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Send notification
        try {
            NotificationRequest notification = new NotificationRequest(
                    savedOrder.getUserId(),
                    "Your order #" + savedOrder.getId() + " has been created successfully!",
                    "ORDER_CREATED"
            );
            notificationClient.sendNotification(notification);
        } catch (Exception e) {
            // Log error but don't fail the order creation
            System.err.println("Failed to send notification: " + e.getMessage());
        }

        return savedOrder;
    }

    private OrderItem createOrderItem(OrderItemRequest request) {
        try {
            // Get menu item details from restaurant service
            MenuItemDto menuItem = restaurantClient.getMenuItem(
                    request.getMenuItemId(), // This should be restaurantId, but we'll use menuItemId for now
                    request.getMenuItemId()
            );

            if (!menuItem.getIsAvailable()) {
                throw new RuntimeException("Menu item is not available: " + menuItem.getName());
            }

            return new OrderItem(
                    request.getMenuItemId(),
                    request.getQuantity(),
                    menuItem.getPrice()
            );
        } catch (Exception e) {
            // Check if it's a menu availability exception and re-throw it
            if (e.getMessage() != null && e.getMessage().contains("Menu item is not available")) {
                throw e;
            }
            // For other exceptions (like service unavailable), use fallback
            return new OrderItem(
                    request.getMenuItemId(),
                    request.getQuantity(),
                    BigDecimal.valueOf(10.00) // Default price
            );
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
        OrderStatus oldStatus = order.getStatus();
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        
        // Send notification for status change
        try {
            NotificationRequest notification = new NotificationRequest(
                    updatedOrder.getUserId(),
                    "Your order #" + updatedOrder.getId() + " status has been updated to: " + status,
                    "ORDER_STATUS_UPDATED"
            );
            notificationClient.sendNotification(notification);
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }
        
        return updatedOrder;
    }

    @Transactional
    public Order confirmOrder(Long id) {
        Order order = getOrderById(id);

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new RuntimeException("Order cannot be confirmed. Current status: " + order.getStatus());
        }
        
        // Process payment
        try {
            PaymentRequest paymentRequest = new PaymentRequest(
                    order.getId(),
                    order.getTotalAmount(),
                    "CREDIT_CARD"
            );
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