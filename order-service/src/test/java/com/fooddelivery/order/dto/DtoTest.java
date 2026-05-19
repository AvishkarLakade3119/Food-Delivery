package com.fooddelivery.order.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    @Test
    void testCreateOrderRequest_NoArgsConstructor() {
        // Act
        CreateOrderRequest request = new CreateOrderRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getUserId());
        assertNull(request.getRestaurantId());
        assertNull(request.getDeliveryAddress());
        assertNull(request.getOrderItems());
    }

    @Test
    void testCreateOrderRequest_AllArgsConstructor() {
        // Arrange
        Long userId = 1L;
        Long restaurantId = 2L;
        String deliveryAddress = "123 Main St";
        List<OrderItemRequest> orderItems = Arrays.asList(new OrderItemRequest());

        // Act
        CreateOrderRequest request = new CreateOrderRequest(userId, restaurantId, deliveryAddress, orderItems);

        // Assert
        assertEquals(userId, request.getUserId());
        assertEquals(restaurantId, request.getRestaurantId());
        assertEquals(deliveryAddress, request.getDeliveryAddress());
        assertEquals(orderItems, request.getOrderItems());
    }

    @Test
    void testCreateOrderRequest_GettersAndSetters() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest();
        Long userId = 1L;
        Long restaurantId = 2L;
        String deliveryAddress = "123 Main St";
        List<OrderItemRequest> orderItems = Arrays.asList(new OrderItemRequest());

        // Act
        request.setUserId(userId);
        request.setRestaurantId(restaurantId);
        request.setDeliveryAddress(deliveryAddress);
        request.setOrderItems(orderItems);

        // Assert
        assertEquals(userId, request.getUserId());
        assertEquals(restaurantId, request.getRestaurantId());
        assertEquals(deliveryAddress, request.getDeliveryAddress());
        assertEquals(orderItems, request.getOrderItems());
    }

    @Test
    void testCreateOrderRequest_NullValues() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest();

        // Act
        request.setUserId(null);
        request.setRestaurantId(null);
        request.setDeliveryAddress(null);
        request.setOrderItems(null);

        // Assert
        assertNull(request.getUserId());
        assertNull(request.getRestaurantId());
        assertNull(request.getDeliveryAddress());
        assertNull(request.getOrderItems());
    }

    @Test
    void testMenuItemDto_NoArgsConstructor() {
        // Act
        MenuItemDto dto = new MenuItemDto();

        // Assert
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getDescription());
        assertNull(dto.getPrice());
        assertNull(dto.getIsAvailable());
    }

    @Test
    void testMenuItemDto_AllArgsConstructor() {
        // Arrange
        Long id = 1L;
        String name = "Pizza";
        String description = "Delicious pizza";
        BigDecimal price = new BigDecimal("15.99");
        Boolean isAvailable = true;

        // Act
        MenuItemDto dto = new MenuItemDto(id, name, description, price, isAvailable);

        // Assert
        assertEquals(id, dto.getId());
        assertEquals(name, dto.getName());
        assertEquals(description, dto.getDescription());
        assertEquals(price, dto.getPrice());
        assertEquals(isAvailable, dto.getIsAvailable());
    }

    @Test
    void testMenuItemDto_GettersAndSetters() {
        // Arrange
        MenuItemDto dto = new MenuItemDto();
        Long id = 1L;
        String name = "Pizza";
        String description = "Delicious pizza";
        BigDecimal price = new BigDecimal("15.99");
        Boolean isAvailable = true;

        // Act
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setPrice(price);
        dto.setIsAvailable(isAvailable);

        // Assert
        assertEquals(id, dto.getId());
        assertEquals(name, dto.getName());
        assertEquals(description, dto.getDescription());
        assertEquals(price, dto.getPrice());
        assertEquals(isAvailable, dto.getIsAvailable());
    }

    @Test
    void testMenuItemDto_NullValues() {
        // Arrange
        MenuItemDto dto = new MenuItemDto();

        // Act
        dto.setId(null);
        dto.setName(null);
        dto.setDescription(null);
        dto.setPrice(null);
        dto.setIsAvailable(null);

        // Assert
        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getDescription());
        assertNull(dto.getPrice());
        assertNull(dto.getIsAvailable());
    }

    @Test
    void testNotificationRequest_NoArgsConstructor() {
        // Act
        NotificationRequest request = new NotificationRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getUserId());
        assertNull(request.getMessage());
        assertNull(request.getType());
    }

    @Test
    void testNotificationRequest_AllArgsConstructor() {
        // Arrange
        Long userId = 1L;
        String message = "Order confirmed";
        String type = "ORDER_CONFIRMATION";

        // Act
        NotificationRequest request = new NotificationRequest(userId, message, type);

        // Assert
        assertEquals(userId, request.getUserId());
        assertEquals(message, request.getMessage());
        assertEquals(type, request.getType());
    }

    @Test
    void testNotificationRequest_GettersAndSetters() {
        // Arrange
        NotificationRequest request = new NotificationRequest();
        Long userId = 1L;
        String message = "Order confirmed";
        String type = "ORDER_CONFIRMATION";

        // Act
        request.setUserId(userId);
        request.setMessage(message);
        request.setType(type);

        // Assert
        assertEquals(userId, request.getUserId());
        assertEquals(message, request.getMessage());
        assertEquals(type, request.getType());
    }

    @Test
    void testOrderItemRequest_NoArgsConstructor() {
        // Act
        OrderItemRequest request = new OrderItemRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getMenuItemId());
        assertNull(request.getQuantity());
        assertNull(request.getPrice());
        assertNull(request.getSpecialInstructions());
    }

    @Test
    void testOrderItemRequest_ThreeArgsConstructor() {
        // Arrange
        Long menuItemId = 1L;
        Integer quantity = 2;
        BigDecimal price = new BigDecimal("10.99");

        // Act
        OrderItemRequest request = new OrderItemRequest(menuItemId, quantity, price);

        // Assert
        assertEquals(menuItemId, request.getMenuItemId());
        assertEquals(quantity, request.getQuantity());
        assertEquals(price, request.getPrice());
        assertNull(request.getSpecialInstructions());
    }

    @Test
    void testOrderItemRequest_FourArgsConstructor() {
        // Arrange
        Long menuItemId = 1L;
        Integer quantity = 2;
        BigDecimal price = new BigDecimal("10.99");
        String specialInstructions = "Extra cheese";

        // Act
        OrderItemRequest request = new OrderItemRequest(menuItemId, quantity, price, specialInstructions);

        // Assert
        assertEquals(menuItemId, request.getMenuItemId());
        assertEquals(quantity, request.getQuantity());
        assertEquals(price, request.getPrice());
        assertEquals(specialInstructions, request.getSpecialInstructions());
    }

    @Test
    void testOrderItemRequest_GettersAndSetters() {
        // Arrange
        OrderItemRequest request = new OrderItemRequest();
        Long menuItemId = 1L;
        Integer quantity = 2;
        BigDecimal price = new BigDecimal("10.99");
        String specialInstructions = "Extra cheese";

        // Act
        request.setMenuItemId(menuItemId);
        request.setQuantity(quantity);
        request.setPrice(price);
        request.setSpecialInstructions(specialInstructions);

        // Assert
        assertEquals(menuItemId, request.getMenuItemId());
        assertEquals(quantity, request.getQuantity());
        assertEquals(price, request.getPrice());
        assertEquals(specialInstructions, request.getSpecialInstructions());
    }

    @Test
    void testOrderItemRequest_GetTotalPrice() {
        // Arrange
        OrderItemRequest request = new OrderItemRequest();
        request.setPrice(new BigDecimal("10.99"));
        request.setQuantity(2);

        // Act
        BigDecimal totalPrice = request.getTotalPrice();

        // Assert
        assertEquals(new BigDecimal("21.98"), totalPrice);
    }

    @Test
    void testOrderItemRequest_GetTotalPrice_NullPrice() {
        // Arrange
        OrderItemRequest request = new OrderItemRequest();
        request.setPrice(null);
        request.setQuantity(2);

        // Act
        BigDecimal totalPrice = request.getTotalPrice();

        // Assert
        assertEquals(BigDecimal.ZERO, totalPrice);
    }

    @Test
    void testOrderItemRequest_GetTotalPrice_NullQuantity() {
        // Arrange
        OrderItemRequest request = new OrderItemRequest();
        request.setPrice(new BigDecimal("10.99"));
        request.setQuantity(null);

        // Act
        BigDecimal totalPrice = request.getTotalPrice();

        // Assert
        assertEquals(BigDecimal.ZERO, totalPrice);
    }

    @Test
    void testOrderItemRequest_GetTotalPrice_BothNull() {
        // Arrange
        OrderItemRequest request = new OrderItemRequest();
        request.setPrice(null);
        request.setQuantity(null);

        // Act
        BigDecimal totalPrice = request.getTotalPrice();

        // Assert
        assertEquals(BigDecimal.ZERO, totalPrice);
    }

    @Test
    void testOrderRequest_NoArgsConstructor() {
        // Act
        OrderRequest request = new OrderRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getUserId());
        assertNull(request.getRestaurantId());
        assertNull(request.getDeliveryAddress());
        assertNull(request.getItems());
    }

    @Test
    void testOrderRequest_AllArgsConstructor() {
        // Arrange
        Long userId = 1L;
        Long restaurantId = 2L;
        Object deliveryAddress = "123 Main St";
        List<OrderItemRequest> items = Arrays.asList(new OrderItemRequest());

        // Act
        OrderRequest request = new OrderRequest(userId, restaurantId, deliveryAddress, items);

        // Assert
        assertEquals(userId, request.getUserId());
        assertEquals(restaurantId, request.getRestaurantId());
        assertEquals(deliveryAddress, request.getDeliveryAddress());
        assertEquals(items, request.getItems());
    }

    @Test
    void testOrderRequest_GettersAndSetters() {
        // Arrange
        OrderRequest request = new OrderRequest();
        Long userId = 1L;
        Long restaurantId = 2L;
        Object deliveryAddress = "123 Main St";
        List<OrderItemRequest> items = Arrays.asList(new OrderItemRequest());

        // Act
        request.setUserId(userId);
        request.setRestaurantId(restaurantId);
        request.setDeliveryAddress(deliveryAddress);
        request.setItems(items);

        // Assert
        assertEquals(userId, request.getUserId());
        assertEquals(restaurantId, request.getRestaurantId());
        assertEquals(deliveryAddress, request.getDeliveryAddress());
        assertEquals(items, request.getItems());
    }

    @Test
    void testOrderRequest_GetDeliveryAddressAsString_StringInput() {
        // Arrange
        OrderRequest request = new OrderRequest();
        String address = "123 Main St";
        request.setDeliveryAddress(address);

        // Act
        String result = request.getDeliveryAddressAsString();

        // Assert
        assertEquals(address, result);
    }

    @Test
    void testOrderRequest_GetDeliveryAddressAsString_NullInput() {
        // Arrange
        OrderRequest request = new OrderRequest();
        request.setDeliveryAddress(null);

        // Act
        String result = request.getDeliveryAddressAsString();

        // Assert
        assertEquals("", result);
    }

    @Test
    void testOrderRequest_GetDeliveryAddressAsString_MapInput() {
        // Arrange
        OrderRequest request = new OrderRequest();
        Map<String, Object> address = new HashMap<>();
        address.put("street", "123 Main St");
        address.put("city", "New York");
        address.put("state", "NY");
        address.put("zipCode", "10001");
        address.put("country", "USA");
        request.setDeliveryAddress(address);

        // Act
        String result = request.getDeliveryAddressAsString();

        // Assert
        assertEquals("123 Main St, New York, NY 10001, USA", result);
    }

    @Test
    void testOrderRequest_GetDeliveryAddressAsString_MapInputWithApartment() {
        // Arrange
        OrderRequest request = new OrderRequest();
        Map<String, Object> address = new HashMap<>();
        address.put("street", "123 Main St");
        address.put("apartmentNumber", "Apt 4B");
        address.put("city", "New York");
        address.put("state", "NY");
        address.put("zipCode", "10001");
        request.setDeliveryAddress(address);

        // Act
        String result = request.getDeliveryAddressAsString();

        // Assert
        assertEquals("123 Main St, Apt 4B, New York, NY 10001", result);
    }

    @Test
    void testOrderRequest_GetDeliveryAddressAsString_MapInputPartial() {
        // Arrange
        OrderRequest request = new OrderRequest();
        Map<String, Object> address = new HashMap<>();
        address.put("street", "123 Main St");
        address.put("city", "New York");
        request.setDeliveryAddress(address);

        /* Act */
        String result = request.getDeliveryAddressAsString();

        // Assert
        assertEquals("123 Main St, New York", result);
    }

    @Test
    void testOrderRequest_GetDeliveryAddressAsString_MapInputEmpty() {
        // Arrange
        OrderRequest request = new OrderRequest();
        Map<String, Object> address = new HashMap<>();
        request.setDeliveryAddress(address);

        // Act
        String result = request.getDeliveryAddressAsString();

        // Assert
        assertEquals("", result);
    }

    @Test
    void testOrderRequest_GetDeliveryAddressAsString_MapInputWithNulls() {
        // Arrange
        OrderRequest request = new OrderRequest();
        Map<String, Object> address = new HashMap<>();
        address.put("street", null);
        address.put("city", "New York");
        address.put("state", null);
        address.put("zipCode", "10001");
        request.setDeliveryAddress(address);

        // Act
        String result = request.getDeliveryAddressAsString();

        // Assert
        assertEquals("New York 10001", result);
    }

    @Test
    void testOrderRequest_GetDeliveryAddressAsString_NonStringNonMap() {
        // Arrange
        OrderRequest request = new OrderRequest();
        Integer address = 12345;
        request.setDeliveryAddress(address);

        // Act
        String result = request.getDeliveryAddressAsString();

        // Assert
        assertEquals("12345", result);
    }

    @Test
    void testPaymentRequest_NoArgsConstructor() {
        // Act
        PaymentRequest request = new PaymentRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getOrderId());
        assertNull(request.getAmount());
        assertNull(request.getPaymentMethod());
    }

    @Test
    void testPaymentRequest_AllArgsConstructor() {
        // Arrange
        Long orderId = 1L;
        BigDecimal amount = new BigDecimal("25.99");
        String paymentMethod = "CREDIT_CARD";

        // Act
        PaymentRequest request = new PaymentRequest(orderId, amount, paymentMethod);

        // Assert
        assertEquals(orderId, request.getOrderId());
        assertEquals(amount, request.getAmount());
        assertEquals(paymentMethod, request.getPaymentMethod());
    }

    @Test
    void testPaymentRequest_GettersAndSetters() {
        // Arrange
        PaymentRequest request = new PaymentRequest();
        Long orderId = 1L;
        BigDecimal amount = new BigDecimal("25.99");
        String paymentMethod = "CREDIT_CARD";

        // Act
        request.setOrderId(orderId);
        request.setAmount(amount);
        request.setPaymentMethod(paymentMethod);

        // Assert
        assertEquals(orderId, request.getOrderId());
        assertEquals(amount, request.getAmount());
        assertEquals(paymentMethod, request.getPaymentMethod());
    }

    @Test
    void testPaymentResponse_NoArgsConstructor() {
        // Act
        PaymentResponse response = new PaymentResponse();

        // Assert
        assertNotNull(response);
        assertNull(response.getStatus());
        assertNull(response.getTransactionId());
        assertNull(response.getMessage());
    }

    @Test
    void testPaymentResponse_AllArgsConstructor() {
        // Arrange
        String status = "SUCCESS";
        String transactionId = "TXN123";
        String message = "Payment processed successfully";

        // Act
        PaymentResponse response = new PaymentResponse(status, transactionId, message);

        // Assert
        assertEquals(status, response.getStatus());
        assertEquals(transactionId, response.getTransactionId());
        assertEquals(message, response.getMessage());
    }

    @Test
    void testPaymentResponse_GettersAndSetters() {
        // Arrange
        PaymentResponse response = new PaymentResponse();
        String status = "SUCCESS";
        String transactionId = "TXN123";
        String message = "Payment processed successfully";

        // Act
        response.setStatus(status);
        response.setTransactionId(transactionId);
        response.setMessage(message);

        // Assert
        assertEquals(status, response.getStatus());
        assertEquals(transactionId, response.getTransactionId());
        assertEquals(message, response.getMessage());
    }

    @Test
    void testOrderRequestGetDeliveryAddressAsString_WithMissingFields() {
        OrderRequest request = new OrderRequest();
        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("street", "123 Main St");
        // Missing city, state, zipCode to test all branches
        request.setDeliveryAddress(addressMap);

        String result = request.getDeliveryAddressAsString();

        assertEquals("123 Main St", result);
    }

    @Test
    void testOrderRequestGetDeliveryAddressAsString_WithNullValues() {
        OrderRequest request = new OrderRequest();
        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("street", null);
        addressMap.put("city", null);
        addressMap.put("state", null);
        addressMap.put("zipCode", null);
        request.setDeliveryAddress(addressMap);

        String result = request.getDeliveryAddressAsString();

        assertNotNull(result);
        assertEquals("", result);
    }

    @Test
    void testOrderRequestGetDeliveryAddressAsString_WithEmptyMap() {
        OrderRequest request = new OrderRequest();
        Map<String, Object> addressMap = new HashMap<>();
        request.setDeliveryAddress(addressMap);

        String result = request.getDeliveryAddressAsString();

        assertNotNull(result);
        assertEquals("", result);
    }

    @Test
    void testOrderRequestGetDeliveryAddressAsString_WithAllNullFields() {
        OrderRequest request = new OrderRequest();
        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("street", null);
        addressMap.put("apartmentNumber", null);
        addressMap.put("city", null);
        addressMap.put("state", null);
        addressMap.put("zipCode", null);
        addressMap.put("country", null);
        request.setDeliveryAddress(addressMap);

        String result = request.getDeliveryAddressAsString();

        assertNotNull(result);
        assertEquals("", result);
    }

    // ========== EQUALS AND HASHCODE TESTS FOR BRANCH COVERAGE ==========

    /**
     * Test equals() method for MenuItemDto with null fields to cover branches.
     * This covers the branch where one object has a null field and another has a non-null field.
     */
    @Test
    void testMenuItemDto_EqualsWithNullId() {
        MenuItemDto dto1 = new MenuItemDto();
        dto1.setId(null);
        dto1.setName("Pizza");
        dto1.setDescription("Delicious");
        dto1.setPrice(BigDecimal.valueOf(10.00));
        dto1.setIsAvailable(true);

        MenuItemDto dto2 = new MenuItemDto();
        dto2.setId(1L);
        dto2.setName("Pizza");
        dto2.setDescription("Delicious");
        dto2.setPrice(BigDecimal.valueOf(10.00));
        dto2.setIsAvailable(true);

        assertNotEquals(dto1, dto2);
        assertNotEquals(dto2, dto1);
    }

    /**
     * Test equals() method for NotificationRequest with null fields.
     */
    @Test
    void testNotificationRequest_EqualsWithNullUserId() {
        NotificationRequest req1 = new NotificationRequest();
        req1.setUserId(null);
        req1.setMessage("Test message");
        req1.setType("ORDER_CONFIRMATION");

        NotificationRequest req2 = new NotificationRequest();
        req2.setUserId(1L);
        req2.setMessage("Test message");
        req2.setType("ORDER_CONFIRMATION");

        assertNotEquals(req1, req2);
        assertNotEquals(req2, req1);
    }

    /**
     * Test equals() method for PaymentRequest with null fields.
     */
    @Test
    void testPaymentRequest_EqualsWithNullOrderId() {
        PaymentRequest req1 = new PaymentRequest();
        req1.setOrderId(null);
        req1.setAmount(BigDecimal.valueOf(25.99));
        req1.setPaymentMethod("CREDIT_CARD");

        PaymentRequest req2 = new PaymentRequest();
        req2.setOrderId(1L);
        req2.setAmount(BigDecimal.valueOf(25.99));
        req2.setPaymentMethod("CREDIT_CARD");

        assertNotEquals(req1, req2);
        assertNotEquals(req2, req1);
    }

    /**
     * Test equals() method for PaymentResponse with null fields.
     */
    @Test
    void testPaymentResponse_EqualsWithNullStatus() {
        PaymentResponse resp1 = new PaymentResponse();
        resp1.setStatus(null);
        resp1.setTransactionId("TXN123");
        resp1.setMessage("Success");

        PaymentResponse resp2 = new PaymentResponse();
        resp2.setStatus("SUCCESS");
        resp2.setTransactionId("TXN123");
        resp2.setMessage("Success");

        assertNotEquals(resp1, resp2);
        assertNotEquals(resp2, resp1);
    }

}
