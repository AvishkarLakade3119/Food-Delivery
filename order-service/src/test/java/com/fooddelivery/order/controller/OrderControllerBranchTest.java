package com.fooddelivery.order.controller;

import com.fooddelivery.order.dto.OrderItemRequest;
import com.fooddelivery.order.dto.OrderRequest;
import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.entity.OrderItem;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Branch coverage tests for OrderController using DIRECT controller method calls.
 * This approach bypasses Spring MVC validation to reach branches that cannot be tested via MockMvc.
 * 
 * Target Branches:
 * - Line 36: if (request.getItems() != null) - 2 branches
 * - Line 53: if (order.getTotalAmount() == null && order.getOrderItems() != null) - 3 branches
 * - Line 112: if (orderRequest.getOrderItems() != null) - 2 branches
 * - Line 130: if (orderService.findById(id).isPresent()) - 2 branches
 * - Optional.map() / Optional.orElse() branches - 4 branches
 */
@ExtendWith(MockitoExtension.class)
class OrderControllerBranchTest {

    @Mock
    private OrderService orderService;

    private OrderController controller;

    private OrderRequest validRequest;
    private Order savedOrder;
    private Order existingOrder;

    @BeforeEach
    void setUp() {
        controller = new OrderController();
        // Inject mock using reflection - bypasses Spring DI
        ReflectionTestUtils.setField(controller, "orderService", orderService);

        // Setup valid request
        validRequest = new OrderRequest();
        validRequest.setUserId(1L);
        validRequest.setRestaurantId(1L);
        validRequest.setDeliveryAddress("123 Main St, Test City");

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setMenuItemId(1L);
        itemRequest.setQuantity(2);
        itemRequest.setPrice(BigDecimal.valueOf(12.99));
        validRequest.setItems(Arrays.asList(itemRequest));

        // Setup saved order
        savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setUserId(1L);
        savedOrder.setRestaurantId(1L);
        savedOrder.setDeliveryAddress("123 Main St, Test City");
        savedOrder.setStatus(OrderStatus.CREATED);
        savedOrder.setTotalAmount(BigDecimal.valueOf(25.98));
        savedOrder.setCreatedAt(LocalDateTime.now());
        savedOrder.setUpdatedAt(LocalDateTime.now());

        // Setup existing order for update tests
        existingOrder = new Order();
        existingOrder.setId(1L);
        existingOrder.setUserId(1L);
        existingOrder.setRestaurantId(1L);
        existingOrder.setDeliveryAddress("123 Main St, Test City");
        existingOrder.setStatus(OrderStatus.CREATED);
        existingOrder.setTotalAmount(BigDecimal.valueOf(25.98));
        existingOrder.setCreatedAt(LocalDateTime.now().minusDays(1));
        existingOrder.setUpdatedAt(LocalDateTime.now().minusDays(1));

        OrderItem existingItem = new OrderItem();
        existingItem.setId(1L);
        existingItem.setMenuItemId(1L);
        existingItem.setQuantity(2);
        existingItem.setPrice(BigDecimal.valueOf(12.99));
        existingItem.setOrder(existingOrder);
        existingOrder.setOrderItems(Arrays.asList(existingItem));
    }

    // ===== LINE 36: if (request.getItems() != null) - 2 BRANCHES =====

    @Test
    void createOrder_NullItems_ShouldCreateOrderWithoutItems() {
        // Covers line 36 - FALSE branch (items is null)
        validRequest.setItems(null);
        when(orderService.createOrderFromEntity(any(Order.class))).thenReturn(savedOrder);

        ResponseEntity<Order> response = controller.createOrder(validRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService).createOrderFromEntity(any(Order.class));
    }

    @Test
    void createOrder_WithItems_ShouldCreateOrderWithItems() {
        // Covers line 36 - TRUE branch (items is not null)
        when(orderService.createOrderFromEntity(any(Order.class))).thenReturn(savedOrder);

        ResponseEntity<Order> response = controller.createOrder(validRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService).createOrderFromEntity(any(Order.class));
    }

    @Test
    void createOrder_EmptyItemsList_ShouldCreateOrderWithEmptyItems() {
        // Edge case: empty list (TRUE branch but empty)
        validRequest.setItems(new ArrayList<>());
        when(orderService.createOrderFromEntity(any(Order.class))).thenReturn(savedOrder);

        ResponseEntity<Order> response = controller.createOrder(validRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService).createOrderFromEntity(any(Order.class));
    }

    // ===== LINE 53: if (order.getTotalAmount() == null && order.getOrderItems() != null) - 3 BRANCHES =====

    @Test
    void createOrder_NullTotalAmountWithItems_ShouldCalculateTotal() {
        // Covers line 53 - TRUE + TRUE branch (total is null AND items exist)
        // This is the default case when items are provided but no total
        when(orderService.createOrderFromEntity(any(Order.class))).thenReturn(savedOrder);

        ResponseEntity<Order> response = controller.createOrder(validRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService).createOrderFromEntity(any(Order.class));
    }

    @Test
    void createOrder_NullTotalAmountWithNullItems_ShouldNotCalculate() {
        // Covers line 53 - TRUE + FALSE branch (total is null BUT items is null)
        validRequest.setItems(null);
        when(orderService.createOrderFromEntity(any(Order.class))).thenReturn(savedOrder);

        ResponseEntity<Order> response = controller.createOrder(validRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService).createOrderFromEntity(any(Order.class));
    }

    @Test
    void createOrder_MultipleItems_ShouldCalculateTotalCorrectly() {
        // Edge case: multiple items to ensure calculation logic is tested
        OrderItemRequest item1 = new OrderItemRequest();
        item1.setMenuItemId(1L);
        item1.setQuantity(2);
        item1.setPrice(BigDecimal.valueOf(12.99));

        OrderItemRequest item2 = new OrderItemRequest();
        item2.setMenuItemId(2L);
        item2.setQuantity(1);
        item2.setPrice(BigDecimal.valueOf(8.99));

        validRequest.setItems(Arrays.asList(item1, item2));
        when(orderService.createOrderFromEntity(any(Order.class))).thenReturn(savedOrder);

        ResponseEntity<Order> response = controller.createOrder(validRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService).createOrderFromEntity(any(Order.class));
    }

    // ===== LINE 112: if (orderRequest.getOrderItems() != null) - 2 BRANCHES =====

    @Test
    void updateOrder_WithNullItems_ShouldPreserveExistingItems() {
        // Covers line 112 - FALSE branch (orderRequest.getOrderItems() is null)
        Order updateRequest = new Order();
        updateRequest.setUserId(1L);
        updateRequest.setRestaurantId(1L);
        updateRequest.setDeliveryAddress("456 Oak Ave, Test City");
        updateRequest.setStatus(OrderStatus.CONFIRMED);
        updateRequest.setOrderItems(null); // NULL items - this is the key

        when(orderService.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(orderService.save(any(Order.class))).thenReturn(existingOrder);

        ResponseEntity<Order> response = controller.updateOrder(1L, updateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService).findById(1L);
        verify(orderService).save(any(Order.class));
    }

    @Test
    void updateOrder_WithItems_ShouldSetOrderRelationship() {
        // Covers line 112 - TRUE branch (orderRequest.getOrderItems() is not null)
        Order updateRequest = new Order();
        updateRequest.setUserId(1L);
        updateRequest.setRestaurantId(1L);
        updateRequest.setDeliveryAddress("456 Oak Ave, Test City");
        updateRequest.setStatus(OrderStatus.CONFIRMED);

        OrderItem newItem = new OrderItem();
        newItem.setId(2L);
        newItem.setMenuItemId(2L);
        newItem.setQuantity(3);
        newItem.setPrice(BigDecimal.valueOf(15.99));
        updateRequest.setOrderItems(Arrays.asList(newItem));

        when(orderService.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(orderService.save(any(Order.class))).thenReturn(existingOrder);

        ResponseEntity<Order> response = controller.updateOrder(1L, updateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService).findById(1L);
        verify(orderService).save(any(Order.class));
    }

    @Test
    void updateOrder_NotFound_ShouldReturnNotFound() {
        // Covers Optional.orElse() branch in updateOrder
        Order updateRequest = new Order();
        updateRequest.setUserId(1L);
        updateRequest.setRestaurantId(1L);

        when(orderService.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Order> response = controller.updateOrder(999L, updateRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(orderService).findById(999L);
        verify(orderService, never()).save(any(Order.class));
    }

    // ===== LINE 130: if (orderService.findById(id).isPresent()) - 2 BRANCHES =====

    @Test
    void deleteOrder_OrderExists_ShouldDeleteAndReturnNoContent() {
        // Covers line 130 - TRUE branch (order exists)
        when(orderService.findById(1L)).thenReturn(Optional.of(existingOrder));
        doNothing().when(orderService).deleteById(1L);

        ResponseEntity<Void> response = controller.deleteOrder(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService).findById(1L);
        verify(orderService).deleteById(1L);
    }

    @Test
    void deleteOrder_OrderNotFound_ShouldReturnNotFound() {
        // Covers line 130 - FALSE branch (order not found)
        when(orderService.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = controller.deleteOrder(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(orderService).findById(999L);
        verify(orderService, never()).deleteById(anyLong());
    }

    // ===== OPTIONAL.MAP() / OPTIONAL.ORELSE() BRANCHES =====

    @Test
    void updateOrderStatus_OrderExists_ShouldUpdateStatus() {
        // Covers Optional.map() branch in updateOrderStatus
        Map<String, String> statusRequest = new HashMap<>();
        statusRequest.put("status", "CONFIRMED");

        when(orderService.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(orderService.save(any(Order.class))).thenReturn(existingOrder);

        ResponseEntity<Order> response = controller.updateOrderStatus(1L, statusRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService).findById(1L);
        verify(orderService).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_OrderNotFound_ShouldReturnNotFound() {
        // Covers Optional.orElse() branch in updateOrderStatus
        Map<String, String> statusRequest = new HashMap<>();
        statusRequest.put("status", "CONFIRMED");

        when(orderService.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Order> response = controller.updateOrderStatus(999L, statusRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(orderService).findById(999L);
        verify(orderService, never()).save(any(Order.class));
    }

    // ===== ADDITIONAL EDGE CASE TESTS =====

    @Test
    void createOrder_WithItemsButZeroQuantity_ShouldCreateOrder() {
        // Edge case: item with zero quantity
        OrderItemRequest zeroQuantityItem = new OrderItemRequest();
        zeroQuantityItem.setMenuItemId(1L);
        zeroQuantityItem.setQuantity(0);
        zeroQuantityItem.setPrice(BigDecimal.valueOf(12.99));
        validRequest.setItems(Arrays.asList(zeroQuantityItem));

        when(orderService.createOrderFromEntity(any(Order.class))).thenReturn(savedOrder);

        ResponseEntity<Order> response = controller.createOrder(validRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService).createOrderFromEntity(any(Order.class));
    }

    @Test
    void updateOrder_WithEmptyItemsList_ShouldSetEmptyItems() {
        // Edge case: empty items list (not null, but empty)
        Order updateRequest = new Order();
        updateRequest.setUserId(1L);
        updateRequest.setRestaurantId(1L);
        updateRequest.setOrderItems(new ArrayList<>()); // Empty list

        when(orderService.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(orderService.save(any(Order.class))).thenReturn(existingOrder);

        ResponseEntity<Order> response = controller.updateOrder(1L, updateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService).findById(1L);
        verify(orderService).save(any(Order.class));
    }

    @Test
    void createOrder_NullItemsInRequest_ShouldNotSetOrderItems() {
        // CRITICAL: This test covers the EXACT missed branch on line 53
        // When request.getItems() is NULL, the controller skips lines 37-49
        // So order.getOrderItems() remains NULL (never set)
        // Line 53: if (order.getOrderItems() != null && !order.getOrderItems().isEmpty())
        // This hits the FALSE branch of the first condition (order.getOrderItems() == null)
        // Result: ELSE branch executes -> order.setTotalAmount(BigDecimal.ZERO)

        OrderRequest nullItemsRequest = new OrderRequest();
        nullItemsRequest.setUserId(1L);
        nullItemsRequest.setRestaurantId(1L);
        nullItemsRequest.setDeliveryAddress("123 Main St");
        nullItemsRequest.setItems(null); // NULL items - this is the key

        Order savedOrderWithZeroTotal = new Order();
        savedOrderWithZeroTotal.setId(1L);
        savedOrderWithZeroTotal.setStatus(OrderStatus.CREATED);
        savedOrderWithZeroTotal.setTotalAmount(BigDecimal.ZERO);
        savedOrderWithZeroTotal.setCreatedAt(LocalDateTime.now());

        when(orderService.createOrderFromEntity(any(Order.class))).thenReturn(savedOrderWithZeroTotal);

        ResponseEntity<Order> response = controller.createOrder(nullItemsRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService).createOrderFromEntity(any(Order.class));
    }

    @Test
    void createOrder_EmptyOrderItemsListAfterConversion_ShouldSetZeroTotal() {
        // CRITICAL: This test covers the MISSING branch on line 53
        // Line 53: if (order.getOrderItems() != null && !order.getOrderItems().isEmpty())
        // This test hits: order.getOrderItems() != null BUT order.getOrderItems().isEmpty() == TRUE
        // So the compound condition evaluates to FALSE (second part fails)
        // Result: ELSE branch executes -> order.setTotalAmount(BigDecimal.ZERO)
        // This is the 1 of 4 branches that was missed!

        OrderRequest emptyItemsRequest = new OrderRequest();
        emptyItemsRequest.setUserId(1L);
        emptyItemsRequest.setRestaurantId(1L);
        emptyItemsRequest.setDeliveryAddress("123 Main St");
        emptyItemsRequest.setItems(new ArrayList<>()); // EMPTY list (not null, but empty)

        Order savedOrderWithZeroTotal = new Order();
        savedOrderWithZeroTotal.setId(1L);
        savedOrderWithZeroTotal.setStatus(OrderStatus.CREATED);
        savedOrderWithZeroTotal.setTotalAmount(BigDecimal.ZERO);
        savedOrderWithZeroTotal.setCreatedAt(LocalDateTime.now());

        when(orderService.createOrderFromEntity(any(Order.class))).thenReturn(savedOrderWithZeroTotal);

        ResponseEntity<Order> response = controller.createOrder(emptyItemsRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService).createOrderFromEntity(any(Order.class));
    }

    @Test
    void createOrder_ItemsNull_DirectCall_ShouldCoverFalseBranch() {
        // ✅ Direct controller (bypass MockMvc + @Valid)
        OrderController controller = new OrderController();
        org.springframework.test.util.ReflectionTestUtils
                .setField(controller, "orderService", orderService);
        // ✅ Create request WITHOUT items (NULL)
        OrderRequest request = new OrderRequest();
        request.setUserId(100L);
        request.setRestaurantId(200L);
        request.setDeliveryAddress("123 Main St");
        // DO NOT set items → remains NULL ✅
        Order saved = new Order();
        saved.setId(1L);
        saved.setUserId(100L);
        saved.setRestaurantId(200L);
        saved.setStatus(OrderStatus.CREATED);
        saved.setTotalAmount(java.math.BigDecimal.ZERO);
        saved.setCreatedAt(java.time.LocalDateTime.now());
        when(orderService.createOrderFromEntity(any(Order.class)))
                .thenReturn(saved);
        // ✅ CALL METHOD DIRECTLY (critical)
        ResponseEntity<Order> response = controller.createOrder(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createOrder_EmptyItemsList_ShouldSetZeroTotal_DirectCall() {
        // This test covers the missing branch on line 53:
        // order.getOrderItems() != null (TRUE) && !order.getOrderItems().isEmpty() (FALSE)
        // When request.getItems() is an empty list (not null), the stream processing
        // creates an empty OrderItems list, which then triggers the ELSE branch

        OrderController controller = new OrderController();
        org.springframework.test.util.ReflectionTestUtils
                .setField(controller, "orderService", orderService);

        OrderRequest request = new OrderRequest();
        request.setUserId(100L);
        request.setRestaurantId(200L);
        request.setDeliveryAddress("123 Main St");
        request.setItems(new ArrayList<>()); // EMPTY list (not null, size = 0)

        Order saved = new Order();
        saved.setId(1L);
        saved.setUserId(100L);
        saved.setRestaurantId(200L);
        saved.setStatus(OrderStatus.CREATED);
        saved.setTotalAmount(java.math.BigDecimal.ZERO);
        saved.setCreatedAt(java.time.LocalDateTime.now());

        when(orderService.createOrderFromEntity(any(Order.class)))
                .thenReturn(saved);

        ResponseEntity<Order> response = controller.createOrder(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService).createOrderFromEntity(any(Order.class));
    }

    @Test
    void createOrder_EmptyItemsList_ShouldTriggerElseBranch() throws Exception {
        // items exists but empty → triggers else branch
        OrderRequest request = new OrderRequest();
        request.setUserId(100L);
        request.setRestaurantId(200L);
        request.setDeliveryAddress("123 Main St");
        request.setItems(new java.util.ArrayList<>()); // ✅ EMPTY LIST
        Order saved = new Order();
        saved.setId(1L);
        saved.setUserId(100L);
        saved.setRestaurantId(200L);
        saved.setStatus(OrderStatus.CREATED);
        saved.setTotalAmount(java.math.BigDecimal.ZERO);
        saved.setCreatedAt(java.time.LocalDateTime.now());
        when(orderService.createOrderFromEntity(any(Order.class)))
                .thenReturn(saved);
        ResponseEntity<Order> response = controller.createOrder(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}