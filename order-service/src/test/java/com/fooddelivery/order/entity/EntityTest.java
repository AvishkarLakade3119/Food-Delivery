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
        Order order = new Order();
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
        Long userId = 1L;
        Long restaurantId = 2L;
        String deliveryAddress = "123 Main St";
        Order order = new Order(userId, restaurantId, deliveryAddress);
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

        order.setId(id);
        order.setUserId(userId);
        order.setRestaurantId(restaurantId);
        order.setStatus(status);
        order.setTotalAmount(totalAmount);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderItems(orderItems);
        order.setCreatedAt(createdAt);
        order.setUpdatedAt(updatedAt);

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
        Order order = new Order();
        order.onCreate();
        assertNotNull(order.getCreatedAt());
        assertTrue(order.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testOrder_OnUpdate() {
        Order order = new Order();
        order.onUpdate();
        assertNotNull(order.getUpdatedAt());
        assertTrue(order.getUpdatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testOrderItem_NoArgsConstructor() {
        OrderItem orderItem = new OrderItem();
        assertNotNull(orderItem);
        assertNull(orderItem.getId());
        assertNull(orderItem.getMenuItemId());
        assertNull(orderItem.getQuantity());
        assertNull(orderItem.getPrice());
        assertNull(orderItem.getOrder());
    }

    @Test
    void testOrderItem_ThreeArgsConstructor() {
        Long menuItemId = 1L;
        Integer quantity = 2;
        BigDecimal price = new BigDecimal("12.99");
        OrderItem orderItem = new OrderItem(menuItemId, quantity, price);
        assertEquals(menuItemId, orderItem.getMenuItemId());
        assertEquals(quantity, orderItem.getQuantity());
        assertEquals(price, orderItem.getPrice());
        assertNull(orderItem.getId());
        assertNull(orderItem.getOrder());
    }

    @Test
    void testOrderItem_GettersAndSetters() {
        OrderItem orderItem = new OrderItem();
        Long id = 1L;
        Long menuItemId = 2L;
        Integer quantity = 3;
        BigDecimal price = new BigDecimal("15.50");
        Order order = new Order();

        orderItem.setId(id);
        orderItem.setMenuItemId(menuItemId);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(price);
        orderItem.setOrder(order);

        assertEquals(id, orderItem.getId());
        assertEquals(menuItemId, orderItem.getMenuItemId());
        assertEquals(quantity, orderItem.getQuantity());
        assertEquals(price, orderItem.getPrice());
        assertEquals(order, orderItem.getOrder());
    }

    @Test
    void testOrderItem_GetSubtotal() {
        OrderItem orderItem = new OrderItem();
        orderItem.setPrice(new BigDecimal("10.50"));
        orderItem.setQuantity(3);
        BigDecimal subtotal = orderItem.getSubtotal();
        assertEquals(new BigDecimal("31.50"), subtotal);
    }

    @Test
    void testOrderStatus_EnumValues() {
        assertEquals("CREATED", OrderStatus.CREATED.name());
        assertEquals("PAYMENT_PENDING", OrderStatus.PAYMENT_PENDING.name());
        assertEquals("PAID", OrderStatus.PAID.name());
        assertEquals("PAYMENT_FAILED", OrderStatus.PAYMENT_FAILED.name());
        assertEquals("RESTAURANT_PENDING", OrderStatus.RESTAURANT_PENDING.name());
        assertEquals("RESTAURANT_CONFIRMED", OrderStatus.RESTAURANT_CONFIRMED.name());
        assertEquals("RESTAURANT_REJECTED", OrderStatus.RESTAURANT_REJECTED.name());
        assertEquals("CONFIRMED", OrderStatus.CONFIRMED.name());
        assertEquals("PREPARING", OrderStatus.PREPARING.name());
        assertEquals("OUT_FOR_DELIVERY", OrderStatus.OUT_FOR_DELIVERY.name());
        assertEquals("DELIVERED", OrderStatus.DELIVERED.name());
        assertEquals("CANCELLED", OrderStatus.CANCELLED.name());
        assertEquals("REFUND_PENDING", OrderStatus.REFUND_PENDING.name());
        assertEquals("REFUNDED", OrderStatus.REFUNDED.name());
        assertEquals("FAILED", OrderStatus.FAILED.name());
    }

    @Test
    void testOrderStatus_ValueOf() {
        assertEquals(OrderStatus.CREATED, OrderStatus.valueOf("CREATED"));
        assertEquals(OrderStatus.PAYMENT_PENDING, OrderStatus.valueOf("PAYMENT_PENDING"));
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf("PAID"));
        assertEquals(OrderStatus.PAYMENT_FAILED, OrderStatus.valueOf("PAYMENT_FAILED"));
        assertEquals(OrderStatus.RESTAURANT_PENDING, OrderStatus.valueOf("RESTAURANT_PENDING"));
        assertEquals(OrderStatus.RESTAURANT_CONFIRMED, OrderStatus.valueOf("RESTAURANT_CONFIRMED"));
        assertEquals(OrderStatus.RESTAURANT_REJECTED, OrderStatus.valueOf("RESTAURANT_REJECTED"));
        assertEquals(OrderStatus.CONFIRMED, OrderStatus.valueOf("CONFIRMED"));
        assertEquals(OrderStatus.PREPARING, OrderStatus.valueOf("PREPARING"));
        assertEquals(OrderStatus.OUT_FOR_DELIVERY, OrderStatus.valueOf("OUT_FOR_DELIVERY"));
        assertEquals(OrderStatus.DELIVERED, OrderStatus.valueOf("DELIVERED"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
        assertEquals(OrderStatus.REFUND_PENDING, OrderStatus.valueOf("REFUND_PENDING"));
        assertEquals(OrderStatus.REFUNDED, OrderStatus.valueOf("REFUNDED"));
        assertEquals(OrderStatus.FAILED, OrderStatus.valueOf("FAILED"));
    }

    @Test
    void testOrderStatus_Values() {
        OrderStatus[] values = OrderStatus.values();
        assertEquals(15, values.length);
        assertEquals(OrderStatus.CREATED, values[0]);
        assertEquals(OrderStatus.PAYMENT_PENDING, values[1]);
        assertEquals(OrderStatus.PAID, values[2]);
        assertEquals(OrderStatus.PAYMENT_FAILED, values[3]);
        assertEquals(OrderStatus.RESTAURANT_PENDING, values[4]);
        assertEquals(OrderStatus.RESTAURANT_CONFIRMED, values[5]);
        assertEquals(OrderStatus.RESTAURANT_REJECTED, values[6]);
        assertEquals(OrderStatus.CONFIRMED, values[7]);
        assertEquals(OrderStatus.PREPARING, values[8]);
        assertEquals(OrderStatus.OUT_FOR_DELIVERY, values[9]);
        assertEquals(OrderStatus.DELIVERED, values[10]);
        assertEquals(OrderStatus.CANCELLED, values[11]);
        assertEquals(OrderStatus.REFUND_PENDING, values[12]);
        assertEquals(OrderStatus.REFUNDED, values[13]);
        assertEquals(OrderStatus.FAILED, values[14]);
    }

    @Test
    void testOrderStatus_ValueOfInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            OrderStatus.valueOf("INVALID_STATUS");
        });
    }

    @Test
    void testOrder_NullValues() {
        Order order = new Order();
        order.setId(null);
        order.setUserId(null);
        order.setRestaurantId(null);
        order.setStatus(null);
        order.setTotalAmount(null);
        order.setDeliveryAddress(null);
        order.setOrderItems(null);
        order.setCreatedAt(null);
        order.setUpdatedAt(null);

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
        OrderItem orderItem = new OrderItem();
        orderItem.setId(null);
        orderItem.setMenuItemId(null);
        orderItem.setQuantity(null);
        orderItem.setPrice(null);
        orderItem.setOrder(null);

        assertNull(orderItem.getId());
        assertNull(orderItem.getMenuItemId());
        assertNull(orderItem.getQuantity());
        assertNull(orderItem.getPrice());
        assertNull(orderItem.getOrder());
    }

    @Test
    void testOrder_AllStatusValues() {
        Order order = new Order();
        for (OrderStatus status : OrderStatus.values()) {
            order.setStatus(status);
            assertEquals(status, order.getStatus());
        }
    }

    @Test
    void testOrderItem_GetSubtotalWithZeroQuantity() {
        OrderItem orderItem = new OrderItem();
        orderItem.setPrice(new BigDecimal("10.50"));
        orderItem.setQuantity(0);
        BigDecimal subtotal = orderItem.getSubtotal();
        assertEquals(new BigDecimal("0.00"), subtotal);
    }

    @Test
    void testOrderItem_GetSubtotalWithZeroPrice() {
        OrderItem orderItem = new OrderItem();
        orderItem.setPrice(BigDecimal.ZERO);
        orderItem.setQuantity(3);
        BigDecimal subtotal = orderItem.getSubtotal();
        assertEquals(new BigDecimal("0"), subtotal);
    }

    @Test
    void testOrder_WithOrderItems() {
        Order order = new Order();
        OrderItem item1 = new OrderItem(1L, 2, new BigDecimal("10.00"));
        OrderItem item2 = new OrderItem(2L, 1, new BigDecimal("15.00"));
        List<OrderItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        order.setOrderItems(items);
        assertEquals(2, order.getOrderItems().size());
        assertTrue(order.getOrderItems().contains(item1));
        assertTrue(order.getOrderItems().contains(item2));
    }
}