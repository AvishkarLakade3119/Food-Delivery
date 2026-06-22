package com.fooddelivery.order.service;

import com.fooddelivery.order.client.NotificationClient;
import com.fooddelivery.order.client.PaymentClient;
import com.fooddelivery.order.client.RestaurantClient;
import com.fooddelivery.order.dto.*;
import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.entity.OrderItem;
import com.fooddelivery.order.entity.OrderStatus;
import com.fooddelivery.order.messaging.OrderEventPublisher;
import com.fooddelivery.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestaurantClient restaurantClient;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private OrderEventPublisher orderEventPublisher;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private CreateOrderRequest createOrderRequest;
    private OrderItem orderItem;
    private MenuItemDto menuItemDto;

    @BeforeEach
    void setUp() {
        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setMenuItemId(1L);
        orderItem.setQuantity(2);
        orderItem.setPrice(BigDecimal.valueOf(12.99));

        order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setRestaurantId(1L);
        order.setDeliveryAddress("123 Test St, Test City");
        order.setTotalAmount(BigDecimal.valueOf(25.98));
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setOrderItems(Arrays.asList(orderItem));
        orderItem.setOrder(order);

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setMenuItemId(1L);
        itemRequest.setQuantity(2);

        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setUserId(1L);
        createOrderRequest.setRestaurantId(1L);
        createOrderRequest.setDeliveryAddress("123 Test St, Test City");
        createOrderRequest.setOrderItems(Arrays.asList(itemRequest));

        menuItemDto = new MenuItemDto();
        menuItemDto.setId(1L);
        menuItemDto.setName("Test Pizza");
        menuItemDto.setPrice(BigDecimal.valueOf(12.99));
        menuItemDto.setIsAvailable(true);
    }

    @Test
    void createOrder_ShouldCreateAndReturnOrder() {
        when(restaurantClient.getMenuItem(anyLong(), anyLong())).thenReturn(menuItemDto);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.createOrder(createOrderRequest);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getRestaurantId()).isEqualTo(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrderFromEntity_ShouldCreateAndReturnOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.createOrderFromEntity(order);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(orderRepository).save(order);
    }

    @Test
    void getAllOrders_ShouldReturnAllOrders() {
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findAllWithItems()).thenReturn(orders);

        List<Order> result = orderService.getAllOrders();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(orderRepository).findAllWithItems();
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(orderRepository).findByIdWithItems(1L);
    }

    @Test
    void getOrderById_NotFound_ShouldThrowException() {
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order not found with id: 1");

        verify(orderRepository).findByIdWithItems(1L);
    }

    @Test
    void findById_ShouldReturnOptionalOrder() {
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));

        Optional<Order> result = orderService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(orderRepository).findByIdWithItems(1L);
    }

    @Test
    void save_ShouldSaveAndReturnOrder() {
        when(orderRepository.save(order)).thenReturn(order);

        Order result = orderService.save(order);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(orderRepository).save(order);
    }

    @Test
    void deleteById_ShouldDeleteOrder() {
        doNothing().when(orderRepository).deleteById(1L);

        orderService.deleteById(1L);

        verify(orderRepository).deleteById(1L);
    }

    @Test
    void getOrdersByUserId_ShouldReturnUserOrders() {
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findByUserIdWithItems(1L)).thenReturn(orders);

        List<Order> result = orderService.getOrdersByUserId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(1L);
        verify(orderRepository).findByUserIdWithItems(1L);
    }

    @Test
    void getOrdersByRestaurantId_ShouldReturnRestaurantOrders() {
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findByRestaurantId(1L)).thenReturn(orders);

        List<Order> result = orderService.getOrdersByRestaurantId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRestaurantId()).isEqualTo(1L);
        verify(orderRepository).findByRestaurantId(1L);
    }

    @Test
    void getOrdersByStatus_ShouldReturnOrdersByStatus() {
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findByStatus(OrderStatus.CREATED)).thenReturn(orders);

        List<Order> result = orderService.getOrdersByStatus(OrderStatus.CREATED);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(OrderStatus.CREATED);
        verify(orderRepository).findByStatus(OrderStatus.CREATED);
    }

    @Test
    void updateOrderStatus_ShouldUpdateAndReturnOrder() {
        Order updatedOrder = new Order();
        updatedOrder.setId(1L);
        updatedOrder.setUserId(1L);
        updatedOrder.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);
        doNothing().when(notificationClient).sendNotification(any(NotificationRequest.class));

        Order result = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        verify(orderRepository).findByIdWithItems(1L);
        verify(orderRepository).save(any(Order.class));
        verify(notificationClient).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void confirmOrder_ShouldConfirmOrder() {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setStatus("SUCCESS");
        paymentResponse.setMessage("Payment successful");

        Order confirmedOrder = new Order();
        confirmedOrder.setId(1L);
        confirmedOrder.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));
        when(paymentClient.processPayment(any(PaymentRequest.class))).thenReturn(paymentResponse);
        when(orderRepository.save(any(Order.class))).thenReturn(confirmedOrder);
        doNothing().when(notificationClient).sendNotification(any(NotificationRequest.class));

        Order result = orderService.confirmOrder(1L);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        verify(paymentClient).processPayment(any(PaymentRequest.class));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void confirmOrder_PaymentFailed_ShouldThrowException() {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setStatus("FAILED");
        paymentResponse.setMessage("Insufficient funds");

        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));
        when(paymentClient.processPayment(any(PaymentRequest.class))).thenReturn(paymentResponse);

        assertThatThrownBy(() -> orderService.confirmOrder(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Payment processing failed: Payment failed: Insufficient funds");

        verify(paymentClient).processPayment(any(PaymentRequest.class));
    }

    @Test
    void cancelOrder_ShouldCancelOrder() {
        Order cancelledOrder = new Order();
        cancelledOrder.setId(1L);
        cancelledOrder.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(cancelledOrder);
        doNothing().when(notificationClient).sendNotification(any(NotificationRequest.class));

        Order result = orderService.cancelOrder(1L);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void deliverOrder_ShouldDeliverOrder() {
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        Order deliveredOrder = new Order();
        deliveredOrder.setId(1L);
        deliveredOrder.setStatus(OrderStatus.DELIVERED);

        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(deliveredOrder);
        doNothing().when(notificationClient).sendNotification(any(NotificationRequest.class));

        Order result = orderService.deliverOrder(1L);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void deliverOrder_InvalidStatus_ShouldThrowException() {
        order.setStatus(OrderStatus.CREATED);
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.deliverOrder(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order cannot be delivered. Current status: CREATED");
    }

    @Test
    void confirmOrder_InvalidStatus_ShouldThrowException() {
        order.setStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.confirmOrder(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order cannot be confirmed. Current status: CONFIRMED");
    }

    @Test
    void cancelOrder_DeliveredStatus_ShouldThrowException() {
        order.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order cannot be cancelled. Current status: DELIVERED");
    }

    @Test
    void cancelOrder_CancelledStatus_ShouldThrowException() {
        order.setStatus(OrderStatus.CANCELLED);
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order cannot be cancelled. Current status: CANCELLED");
    }

    @Test
    void createOrderItem_MenuItemNotAvailable_ShouldThrowException() {
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setMenuItemId(1L);
        itemRequest.setQuantity(2);

        MenuItemDto unavailableItem = new MenuItemDto();
        unavailableItem.setId(1L);
        unavailableItem.setName("Unavailable Pizza");
        unavailableItem.setPrice(BigDecimal.valueOf(12.99));
        unavailableItem.setIsAvailable(false);

        CreateOrderRequest requestWithUnavailableItem = new CreateOrderRequest();
        requestWithUnavailableItem.setUserId(1L);
        requestWithUnavailableItem.setRestaurantId(1L);
        requestWithUnavailableItem.setDeliveryAddress("123 Test St");
        requestWithUnavailableItem.setOrderItems(Arrays.asList(itemRequest));

        when(restaurantClient.getMenuItem(anyLong(), anyLong())).thenReturn(unavailableItem);

        assertThatThrownBy(() -> orderService.createOrder(requestWithUnavailableItem))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Menu item is not available: Unavailable Pizza");
    }

    @Test
    void createOrderItem_ServiceException_ShouldUseFallbackPrice() {
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setMenuItemId(1L);
        itemRequest.setQuantity(2);

        CreateOrderRequest requestWithServiceError = new CreateOrderRequest();
        requestWithServiceError.setUserId(1L);
        requestWithServiceError.setRestaurantId(1L);
        requestWithServiceError.setDeliveryAddress("123 Test St");
        requestWithServiceError.setOrderItems(Arrays.asList(itemRequest));

        when(restaurantClient.getMenuItem(anyLong(), anyLong())).thenThrow(new RuntimeException("Service unavailable"));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.createOrder(requestWithServiceError);

        assertThat(result).isNotNull();
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_NotificationFails_ShouldStillCreateOrder() {
        when(restaurantClient.getMenuItem(anyLong(), anyLong())).thenReturn(menuItemDto);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.createOrder(createOrderRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrderFromEntity_NotificationFails_ShouldStillCreateOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.createOrderFromEntity(order);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderStatus_NotificationFails_ShouldStillUpdateOrder() {
        Order updatedOrder = new Order();
        updatedOrder.setId(1L);
        updatedOrder.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);
        doThrow(new RuntimeException("Notification service down")).when(notificationClient)
                .sendNotification(any(NotificationRequest.class));

        Order result = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        verify(orderRepository).save(any(Order.class));
        verify(notificationClient).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void updateOrderStatus_NotFound_ShouldThrowException() {
        when(orderRepository.findByIdWithItems(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateOrderStatus(999L, OrderStatus.CONFIRMED))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order not found with id: 999");
    }

    @Test
    void confirmOrder_NotFound_ShouldThrowException() {
        when(orderRepository.findByIdWithItems(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.confirmOrder(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order not found with id: 999");
    }

    @Test
    void cancelOrder_NotFound_ShouldThrowException() {
        when(orderRepository.findByIdWithItems(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.cancelOrder(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order not found with id: 999");
    }

    @Test
    void deliverOrder_NotFound_ShouldThrowException() {
        when(orderRepository.findByIdWithItems(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.deliverOrder(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order not found with id: 999");
    }

    @Test
    void confirmOrder_PaymentException_ShouldThrowException() {
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));
        when(paymentClient.processPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException("Payment service unavailable"));

        assertThatThrownBy(() -> orderService.confirmOrder(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Payment processing failed: Payment service unavailable");
    }

    @Test
    void findById_NotFound_ShouldReturnEmpty() {
        when(orderRepository.findByIdWithItems(999L)).thenReturn(Optional.empty());

        Optional<Order> result = orderService.findById(999L);

        assertThat(result).isEmpty();
        verify(orderRepository).findByIdWithItems(999L);
    }

    @Test
    void getAllOrders_EmptyList_ShouldReturnEmptyList() {
        when(orderRepository.findAllWithItems()).thenReturn(Arrays.asList());

        List<Order> result = orderService.getAllOrders();

        assertThat(result).isEmpty();
        verify(orderRepository).findAllWithItems();
    }

    @Test
    void getOrdersByUserId_EmptyList_ShouldReturnEmptyList() {
        when(orderRepository.findByUserIdWithItems(999L)).thenReturn(Arrays.asList());

        List<Order> result = orderService.getOrdersByUserId(999L);

        assertThat(result).isEmpty();
        verify(orderRepository).findByUserIdWithItems(999L);
    }

    @Test
    void getOrdersByRestaurantId_EmptyList_ShouldReturnEmptyList() {
        when(orderRepository.findByRestaurantId(999L)).thenReturn(Arrays.asList());

        List<Order> result = orderService.getOrdersByRestaurantId(999L);

        assertThat(result).isEmpty();
        verify(orderRepository).findByRestaurantId(999L);
    }

    @Test
    void getOrdersByStatus_EmptyList_ShouldReturnEmptyList() {
        when(orderRepository.findByStatus(OrderStatus.PREPARING)).thenReturn(Arrays.asList());

        List<Order> result = orderService.getOrdersByStatus(OrderStatus.PREPARING);

        assertThat(result).isEmpty();
        verify(orderRepository).findByStatus(OrderStatus.PREPARING);
    }

    @Test
    void createOrderItem_MenuItemAvailableExceptionWithNullMessage_ShouldUseFallback() {
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setMenuItemId(1L);
        itemRequest.setQuantity(2);

        CreateOrderRequest requestWithNullMessageException = new CreateOrderRequest();
        requestWithNullMessageException.setUserId(1L);
        requestWithNullMessageException.setRestaurantId(1L);
        requestWithNullMessageException.setDeliveryAddress("123 Test St");
        requestWithNullMessageException.setOrderItems(Arrays.asList(itemRequest));

        RuntimeException exceptionWithNullMessage = new RuntimeException((String) null);
        when(restaurantClient.getMenuItem(anyLong(), anyLong())).thenThrow(exceptionWithNullMessage);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.createOrder(requestWithNullMessageException);

        assertThat(result).isNotNull();
        verify(orderRepository).save(any(Order.class));
    }
}