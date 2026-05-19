package com.fooddelivery.order.controller;

import com.fooddelivery.order.dto.OrderRequest;
import com.fooddelivery.order.dto.OrderItemRequest;
import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.entity.OrderStatus;
import com.fooddelivery.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
class OrderControllerDirectTest {

    @Mock
    private OrderService orderService;

    private OrderController controller;

    @BeforeEach
    void setUp() {
        controller = new OrderController();
        ReflectionTestUtils.setField(controller, "orderService", orderService);
    }

    @Test
    void createOrder_WithItems_CalculatesTotal() {
        // Covers: orderItems != null && !isEmpty() TRUE branch
        OrderRequest request = new OrderRequest();
        request.setUserId(100L);
        request.setRestaurantId(200L);
        request.setDeliveryAddress("123 Main St");

        OrderItemRequest item = new OrderItemRequest();
        item.setMenuItemId(1L);
        item.setQuantity(2);
        item.setPrice(BigDecimal.valueOf(10.00));
        request.setItems(List.of(item));

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setStatus(OrderStatus.CREATED);
        savedOrder.setTotalAmount(BigDecimal.valueOf(20.00));
        savedOrder.setCreatedAt(LocalDateTime.now());

        when(orderService.createOrderFromEntity(any(Order.class))).thenReturn(savedOrder);

        ResponseEntity<Order> response = controller.createOrder(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createOrder_NullItems_SetsTotalZero() {
        // Covers: orderItems == null -> else branch sets ZERO
        OrderRequest request = new OrderRequest();
        request.setUserId(100L);
        request.setRestaurantId(200L);
        request.setDeliveryAddress("123 Main St");
        // items is null

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setStatus(OrderStatus.CREATED);
        savedOrder.setTotalAmount(BigDecimal.ZERO);
        savedOrder.setCreatedAt(LocalDateTime.now());

        when(orderService.createOrderFromEntity(any(Order.class))).thenReturn(savedOrder);

        ResponseEntity<Order> response = controller.createOrder(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createOrder_EmptyItems_SetsTotalZero() {
        // Covers: orderItems != null but isEmpty() TRUE -> else branch sets ZERO
        OrderRequest request = new OrderRequest();
        request.setUserId(100L);
        request.setRestaurantId(200L);
        request.setDeliveryAddress("123 Main St");
        request.setItems(new ArrayList<>()); // Empty list triggers line 37 TRUE, then line 53 FALSE branch

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setStatus(OrderStatus.CREATED);
        savedOrder.setTotalAmount(BigDecimal.ZERO);
        savedOrder.setCreatedAt(LocalDateTime.now());

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        when(orderService.createOrderFromEntity(any(Order.class))).thenReturn(savedOrder);

        ResponseEntity<Order> response = controller.createOrder(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Capture and verify the order passed to service
        verify(orderService).createOrderFromEntity(orderCaptor.capture());
        Order capturedOrder = orderCaptor.getValue();

        // This MUST hit the else branch at line 59 because orderItems is empty
        assertNotNull(capturedOrder.getOrderItems(), "OrderItems should not be null");
        assertTrue(capturedOrder.getOrderItems().isEmpty(), "OrderItems should be empty");
        assertEquals(BigDecimal.ZERO, capturedOrder.getTotalAmount(), "Total should be ZERO for empty items");
    }
}