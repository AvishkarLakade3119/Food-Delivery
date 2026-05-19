package com.fooddelivery.order.controller;

import com.fooddelivery.order.dto.CreateOrderRequest;
import com.fooddelivery.order.dto.OrderRequest;
import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.entity.OrderStatus;
import com.fooddelivery.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"}, allowCredentials = "true")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderRequest request) {
        Order order = new Order();
        order.setId(null);  // Force new ID generation
        order.setUserId(request.getUserId());
        order.setRestaurantId(request.getRestaurantId());
        order.setDeliveryAddress(request.getDeliveryAddressAsString());
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(java.time.LocalDateTime.now());
        order.setUpdatedAt(java.time.LocalDateTime.now());

        // Handle order items
        if (request.getItems() != null) {
            List<com.fooddelivery.order.entity.OrderItem> items = request.getItems().stream()
                    .map(itemReq -> {
                        com.fooddelivery.order.entity.OrderItem item = new com.fooddelivery.order.entity.OrderItem();
                        item.setId(null);  // Force new ID generation
                        item.setMenuItemId(itemReq.getMenuItemId());
                        item.setQuantity(itemReq.getQuantity());
                        item.setPrice(itemReq.getPrice());
                        item.setOrder(order);
                        return item;
                    })
                    .collect(Collectors.toList());
            order.setOrderItems(items);
        }

        // Calculate total from order items
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            java.math.BigDecimal total = order.getOrderItems().stream()
                    .map(i -> i.getPrice().multiply(java.math.BigDecimal.valueOf(i.getQuantity())))
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            order.setTotalAmount(total);
        } else {
            order.setTotalAmount(java.math.BigDecimal.ZERO);
        }

        Order saved = orderService.createOrderFromEntity(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Legacy endpoint for backward compatibility
    @PostMapping("/legacy")
    public ResponseEntity<Order> createOrderLegacy(@Valid @RequestBody CreateOrderRequest createOrderRequest) {
        Order createdOrder = orderService.createOrder(createOrderRequest);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable("id") Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable("userId") Long userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Order>> getOrdersByRestaurantId(@PathVariable("restaurantId") Long restaurantId) {
        List<Order> orders = orderService.getOrdersByRestaurantId(restaurantId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable("status") OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(
            @PathVariable("id") Long id,
            @RequestBody Order orderRequest) {
        return orderService.findById(id)
                .map(existingOrder -> {
                    // Preserve created_at from existing record
                    orderRequest.setId(id);
                    orderRequest.setCreatedAt(existingOrder.getCreatedAt());
                    orderRequest.setUpdatedAt(java.time.LocalDateTime.now());

                    // Preserve items relationship
                    if (orderRequest.getOrderItems() != null) {
                        for (com.fooddelivery.order.entity.OrderItem item : orderRequest.getOrderItems()) {
                            item.setOrder(orderRequest);
                        }
                    } else {
                        orderRequest.setOrderItems(existingOrder.getOrderItems());
                    }

                    Order updated = orderService.save(orderRequest);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("id") Long id) {
        if (orderService.findById(id).isPresent()) {
            orderService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Order> confirmOrder(@PathVariable("id") Long id) {
        Order confirmedOrder = orderService.confirmOrder(id);
        return ResponseEntity.ok(confirmedOrder);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable("id") Long id) {
        Order cancelledOrder = orderService.cancelOrder(id);
        return ResponseEntity.ok(cancelledOrder);
    }

    @PostMapping("/{id}/deliver")
    public ResponseEntity<Order> deliverOrder(@PathVariable("id") Long id) {
        Order deliveredOrder = orderService.deliverOrder(id);
        return ResponseEntity.ok(deliveredOrder);
    }

    @GetMapping("/{id}/tracking")
    public ResponseEntity<Order> getOrderTracking(@PathVariable("id") Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable("id") Long id,
            @RequestBody java.util.Map<String, String> statusRequest) {
        return orderService.findById(id)
                .map(existingOrder -> {
                    existingOrder.setStatus(OrderStatus.valueOf(statusRequest.get("status")));
                    existingOrder.setUpdatedAt(java.time.LocalDateTime.now());
                    // created_at is NEVER touched
                    Order updated = orderService.save(existingOrder);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}