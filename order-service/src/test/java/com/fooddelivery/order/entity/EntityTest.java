package com.fooddelivery.order.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    @Test
    void testOrder_NoArgsConstructor() {
        // Act
        Order order = new Order();
        
        // Assert
        assertNotNull(order);
        assertNull(order.getId());
        assertNull(order.getUserId());
        assertNull(order.getRestaurantId());
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertEquals(BigDecimal.ZERO, order.getTotalAmount());
        assertNull(order.getDeliveryAddress());
        assertNotNull(order.getOrderItems());
        assertTrue(order.getOrderItems().isEmpty());
        assertNull(order.getCreatedAt());
        assertNull(order.getUpdatedAt());
    }

    @Test
    void testOrder_ThreeArgsConstructor() {
        // Arrange
        Long userId = 1L;
        Long restaurantId = 2L;
        String deliveryAddress = "123 Main St";
        
        // Act
        Order order = new Order(userId, restaurantId, deliveryAddress);
        
        // Assert
        assertEquals(userId, order.getUserId());
        assertEquals(restaurantId, order.getRestaurantId());
        assertEquals(deliveryAddress, order.getDeliveryAddress());
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertEquals(BigDecimal.ZERO, order.getTotalAmount());
        assertNotNull(order.getOrderItems());
        assertTrue(order.getOrderItems().isEmpty());
    }

    @Test
    void testOrder_GettersAndSetters() {
        // Arrange
        Order order = new Order();
        Long id = 1L;
        Long userId = 2L;
        Long restaurantId = 3L;
        OrderStatus status = OrderStatus.CONFIRMED;
        BigDecimal totalAmount = new BigDecimal("25.99");
        String deliveryAddress = "456 Oak Ave";
        List<OrderItem> orderItems = new ArrayList<>();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        
        // Act
        order.setId(id);
        order.setUserId(userId);
        order.setRestaurantId(restaurantId);
        order.setStatus(status);
        order.setTotalAmount(totalAmount);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderItems(orderItems);
        order.setCreatedAt(createdAt);
        order.setUpdatedAt(updatedAt);
        
        // Assert
        assertEquals(id, order.getId());
        assertEquals(userId, order.getUserId());
        assertEquals(restaurantId, order.getRestaurantId());
        assertEquals(status, order.getStatus());
        assertEquals(totalAmount, order.getTotalAmount());
        assertEquals(deliveryAddress, order.getDeliveryAddress());
        assertEquals(orderItems, order.getOrderItems());
        assertEquals(createdAt, order.getCreatedAt());
        assertEquals(updatedAt, order.getUpdatedAt());
    }

    @Test
    void testOrder_OnCreate() {
        // Arrange
        Order order = new Order();
        
        // Act
        order.onCreate();
        
        // Assert
        assertNotNull(order.getCreatedAt());
        assertTrue(order.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testOrder_OnUpdate() {
        // Arrange
        Order order = new Order();
        
        // Act
        order.onUpdate();
        
        // Assert
        assertNotNull(order.getUpdatedAt());
        assertTrue(order.getUpdatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testOrderItem_NoArgsConstructor() {
        // Act
        OrderItem orderItem = new OrderItem();
        
        // Assert
        assertNotNull(orderItem);
        assertNull(orderItem.getId());
        assertNull(orderItem.getMenuItemId());
        assertNull(orderItem.getQuantity());
        assertNull(orderItem.getPrice());
        assertNull(orderItem.getOrder());
    }

    @Test
    void testOrderItem_ThreeArgsConstructor() {
        // Arrange
        Long menuItemId = 1L;
        Integer quantity = 2;
        BigDecimal price = new BigDecimal("12.99");
        
        // Act
        OrderItem orderItem = new OrderItem(menuItemId, quantity, price);
        
        // Assert
        assertEquals(menuItemId, orderItem.getMenuItemId());
        assertEquals(quantity, orderItem.getQuantity());
        assertEquals(price, orderItem.getPrice());
        assertNull(orderItem.getId());
        assertNull(orderItem.getOrder());
    }

    @Test
    void testOrderItem_GettersAndSetters() {
        // Arrange
        OrderItem orderItem = new OrderItem();
        Long id = 1L;
        Long menuItemId = 2L;
        Integer quantity = 3;
        BigDecimal price = new BigDecimal("15.50");
        Order order = new Order();
        
        // Act
        orderItem.setId(id);
        orderItem.setMenuItemId(menuItemId);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(price);
        orderItem.setOrder(order);
        
        // Assert
        assertEquals(id, orderItem.getId());
        assertEquals(menuItemId, orderItem.getMenuItemId());
        assertEquals(quantity, orderItem.getQuantity());
        assertEquals(price, orderItem.getPrice());
        assertEquals(order, orderItem.getOrder());
    }

    @Test
    void testOrderItem_GetSubtotal() {
        // Arrange
        OrderItem orderItem = new OrderItem();
        orderItem.setPrice(new BigDecimal("10.50"));
        orderItem.setQuantity(3);
        
        // Act
        BigDecimal subtotal = orderItem.getSubtotal();
        
        // Assert
        assertEquals(new BigDecimal("31.50"), subtotal);
    }

    @Test
    void testOrderStatus_EnumValues() {
        // Act & Assert
        assertEquals("CREATED", OrderStatus.CREATED.name());
        assertEquals("CONFIRMED", OrderStatus.CONFIRMED.name());
        assertEquals("PREPARING", OrderStatus.PREPARING.name());
        assertEquals("OUT_FOR_DELIVERY", OrderStatus.OUT_FOR_DELIVERY.name());
        assertEquals("DELIVERED", OrderStatus.DELIVERED.name());
        assertEquals("CANCELLED", OrderStatus.CANCELLED.name());
    }

    @Test
    void testOrderStatus_ValueOf() {
        // Act & Assert
        assertEquals(OrderStatus.CREATED, OrderStatus.valueOf("CREATED"));
        assertEquals(OrderStatus.CONFIRMED, OrderStatus.valueOf("CONFIRMED"));
        assertEquals(OrderStatus.PREPARING, OrderStatus.valueOf("PREPARING"));
        assertEquals(OrderStatus.OUT_FOR_DELIVERY, OrderStatus.valueOf("OUT_FOR_DELIVERY"));
        assertEquals(OrderStatus.DELIVERED, OrderStatus.valueOf("DELIVERED"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
    }

    @Test
    void testOrderStatus_Values() {
        // Act
        OrderStatus[] values = OrderStatus.values();
        
        // Assert
        assertEquals(6, values.length);
        assertEquals(OrderStatus.CREATED, values[0]);
        assertEquals(OrderStatus.CONFIRMED, values[1]);
        assertEquals(OrderStatus.PREPARING, values[2]);
        assertEquals(OrderStatus.OUT_FOR_DELIVERY, values[3]);
        assertEquals(OrderStatus.DELIVERED, values[4]);
        assertEquals(OrderStatus.CANCELLED, values[5]);
    }

    @Test
    void testOrderStatus_ValueOfInvalidValue() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            OrderStatus.valueOf("INVALID_STATUS");
        });
    }

    @Test
    void testOrder_NullValues() {
        // Arrange
        Order order = new Order();
        
        // Act
        order.setId(null);
        order.setUserId(null);
        order.setRestaurantId(null);
        order.setStatus(null);
        order.setTotalAmount(null);
        order.setDeliveryAddress(null);
        order.setOrderItems(null);
        order.setCreatedAt(null);
        order.setUpdatedAt(null);
        
        // Assert
        assertNull(order.getId());
        assertNull(order.getUserId());
        assertNull(order.getRestaurantId());
        assertNull(order.getStatus());
        assertNull(order.getTotalAmount());
        assertNull(order.getDeliveryAddress());
        assertNull(order.getOrderItems());
        assertNull(order.getCreatedAt());
        assertNull(order.getUpdatedAt());
    }

    @Test
    void testOrderItem_NullValues() {
        // Arrange
        OrderItem orderItem = new OrderItem();
        
        // Act
        orderItem.setId(null);
        orderItem.setMenuItemId(null);
        orderItem.setQuantity(null);
        orderItem.setPrice(null);
        orderItem.setOrder(null);
        
        // Assert
        assertNull(orderItem.getId());
        assertNull(orderItem.getMenuItemId());
        assertNull(orderItem.getQuantity());
        assertNull(orderItem.getPrice());
        assertNull(orderItem.getOrder());
    }

    @Test
    void testOrder_AllStatusValues() {
        // Arrange
        Order order = new Order();
        
        // Test all enum values
        for (OrderStatus status : OrderStatus.values()) {
            // Act
            order.setStatus(status);
            
            // Assert
            assertEquals(status, order.getStatus());
        }
    }

    @Test
    void testOrderItem_GetSubtotalWithZeroQuantity() {
        // Arrange
        OrderItem orderItem = new OrderItem();
        orderItem.setPrice(new BigDecimal("10.50"));
        orderItem.setQuantity(0);

        // Act
        BigDecimal subtotal = orderItem.getSubtotal();

        // Assert
        assertEquals(new BigDecimal("0.00"), subtotal);
    }

    @Test
    void testOrderItem_GetSubtotalWithZeroPrice() {
        // Arrange
        OrderItem orderItem = new OrderItem();
        orderItem.setPrice(BigDecimal.ZERO);
        orderItem.setQuantity(3);

        // Act
        BigDecimal subtotal = orderItem.getSubtotal();

        // Assert
        assertEquals(new BigDecimal("0"), subtotal);
    }

    @Test
    void testOrder_WithOrderItems() {
        // Arrange
        Order order = new Order();
        OrderItem item1 = new OrderItem(1L, 2, new BigDecimal("10.00"));
        OrderItem item2 = new OrderItem(2L, 1, new BigDecimal("15.00"));
        List<OrderItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        
        // Act
        order.setOrderItems(items);
        
        // Assert
        assertEquals(2, order.getOrderItems().size());
        assertTrue(order.getOrderItems().contains(item1));
        assertTrue(order.getOrderItems().contains(item2));
    }
}