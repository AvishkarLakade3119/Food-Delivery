package com.fooddelivery.order.controller;

import com.fooddelivery.order.dto.OrderRequest;
import com.fooddelivery.order.dto.OrderItemRequest;
import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.entity.OrderStatus;
import com.fooddelivery.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

/**
 * This test class specifically targets the missed branch at line 53 in OrderController:
 * if (order.getOrderItems() != null && !order.getOrderItems().isEmpty())
 * 
 * The missed branch is: orderItems != null AND isEmpty() == true
 * This happens when request.getItems() is an empty ArrayList (not null, but size = 0)
 */
@ExtendWith(MockitoExtension.class)
class OrderControllerEmptyListBranchTest {

    @Mock
    private OrderService orderService;

    private OrderController controller;

    @BeforeEach
    void setUp() {
        controller = new OrderController();
        ReflectionTestUtils.setField(controller, "orderService", orderService);
    }

    @Test
    void createOrder_WithEmptyItemsList_ShouldSetTotalToZero() {
        // SETUP: Create request with EMPTY list (not null, but size = 0)
        // This will trigger:
        // - Line 37: request.getItems() != null → TRUE
        // - Line 53: order.getOrderItems() != null → TRUE
        // - Line 53: !order.getOrderItems().isEmpty() → FALSE (MISSED BRANCH)
        // - Line 59: else branch executes → order.setTotalAmount(BigDecimal.ZERO)
        
        OrderRequest request = new OrderRequest();
        request.setUserId(100L);
        request.setRestaurantId(200L);
        request.setDeliveryAddress("123 Main St, City, State 12345");
        request.setItems(new ArrayList<>()); // KEY: Empty list, NOT null

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setUserId(100L);
        savedOrder.setRestaurantId(200L);
        savedOrder.setStatus(OrderStatus.CREATED);
        savedOrder.setTotalAmount(BigDecimal.ZERO);
        savedOrder.setCreatedAt(LocalDateTime.now());

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        when(orderService.createOrderFromEntity(any(Order.class))).thenReturn(savedOrder);

        // EXECUTE
        ResponseEntity<Order> response = controller.createOrder(request);

        // VERIFY
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Capture the order that was passed to the service
        verify(orderService, times(1)).createOrderFromEntity(orderCaptor.capture());
        Order capturedOrder = orderCaptor.getValue();
        
        // CRITICAL ASSERTIONS to ensure the empty list branch was hit:
        // 1. OrderItems should NOT be null (line 37 set it)
        assertNotNull(capturedOrder.getOrderItems(), 
            "OrderItems must not be null - line 37 should have set it");
        
        // 2. OrderItems MUST be empty (this triggers the MISSED branch at line 53)
        assertTrue(capturedOrder.getOrderItems().isEmpty(), 
            "OrderItems must be empty to hit the missed branch at line 53");
        
        // 3. TotalAmount MUST be ZERO (set by else branch at line 59)
        assertEquals(BigDecimal.ZERO, capturedOrder.getTotalAmount(), 
            "TotalAmount must be ZERO when items list is empty (line 59 else branch)");
    }

    @Test
    void createOrder_WithNullItemsList_ShouldSetTotalToZero() {
        // This test covers the case where request.getItems() == null
        // - Line 37: request.getItems() != null → FALSE
        // - order.setOrderItems() is NEVER called
        // - Line 53: order.getOrderItems() != null → FALSE
        // - Line 59: else branch executes → order.setTotalAmount(BigDecimal.ZERO)
        
        OrderRequest request = new OrderRequest();
        request.setUserId(100L);
        request.setRestaurantId(200L);
        request.setDeliveryAddress("123 Main St, City, State 12345");
        // request.setItems(null); // Explicitly null

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setUserId(100L);
        savedOrder.setRestaurantId(200L);
        savedOrder.setStatus(OrderStatus.CREATED);
        savedOrder.setTotalAmount(BigDecimal.ZERO);
        savedOrder.setCreatedAt(LocalDateTime.now());

        when(orderService.createOrderFromEntity(any(Order.class))).thenReturn(savedOrder);

        ResponseEntity<Order> response = controller.createOrder(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(orderService, times(1)).createOrderFromEntity(any(Order.class));
    }

    @Test
    void createOrder_WithItemsList_ShouldCalculateTotal() {
        // This test covers the TRUE branch of line 53
        // - Line 37: request.getItems() != null → TRUE
        // - Line 53: order.getOrderItems() != null && !isEmpty() → TRUE
        // - Lines 54-57: Calculate total from items
        
        OrderRequest request = new OrderRequest();
        request.setUserId(100L);
        request.setRestaurantId(200L);
        request.setDeliveryAddress("123 Main St, City, State 12345");
        
        OrderItemRequest item1 = new OrderItemRequest();
        item1.setMenuItemId(1L);
        item1.setQuantity(2);
        item1.setPrice(BigDecimal.valueOf(10.00));
        
        OrderItemRequest item2 = new OrderItemRequest();
        item2.setMenuItemId(2L);
        item2.setQuantity(1);
        item2.setPrice(BigDecimal.valueOf(15.00));
        
        request.setItems(List.of(item1, item2));

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setStatus(OrderStatus.CREATED);
        savedOrder.setTotalAmount(BigDecimal.valueOf(35.00)); // 2*10 + 1*15
        savedOrder.setCreatedAt(LocalDateTime.now());

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        when(orderService.createOrderFromEntity(any(Order.class))).thenReturn(savedOrder);

        ResponseEntity<Order> response = controller.createOrder(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        verify(orderService, times(1)).createOrderFromEntity(orderCaptor.capture());
        Order capturedOrder = orderCaptor.getValue();
        
        assertNotNull(capturedOrder.getOrderItems());
        assertFalse(capturedOrder.getOrderItems().isEmpty());
        assertEquals(2, capturedOrder.getOrderItems().size());
        assertEquals(BigDecimal.valueOf(35.00), capturedOrder.getTotalAmount());
    }
}