package com.fooddelivery.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.order.dto.CreateOrderRequest;
import com.fooddelivery.order.dto.OrderItemRequest;
import com.fooddelivery.order.dto.OrderRequest;
import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.entity.OrderItem;
import com.fooddelivery.order.entity.OrderStatus;
import com.fooddelivery.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class OrderControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private OrderRequest orderRequest;
    private CreateOrderRequest createOrderRequest;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/orders";
        orderRepository.deleteAll();

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setMenuItemId(1L);
        itemRequest.setQuantity(2);
        itemRequest.setPrice(BigDecimal.valueOf(12.99));

        orderRequest = new OrderRequest();
        orderRequest.setUserId(1L);
        orderRequest.setRestaurantId(1L);
        orderRequest.setDeliveryAddress("123 Test St");
        orderRequest.setDeliveryAddress("123 Test St, Test City, Test State 12345");
        orderRequest.setItems(Arrays.asList(itemRequest));

        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setUserId(1L);
        createOrderRequest.setRestaurantId(1L);
        createOrderRequest.setDeliveryAddress("123 Test St, Test City");
        // Total amount is calculated automatically by the service
    }

    @Test
    void createOrder_ShouldReturnCreated() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderRequest> request = new HttpEntity<>(orderRequest, headers);

        ResponseEntity<Order> response = restTemplate.postForEntity(baseUrl, request, Order.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUserId()).isEqualTo(1L);
        assertThat(response.getBody().getRestaurantId()).isEqualTo(1L);
        assertThat(response.getBody().getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    void createOrderLegacy_ShouldReturnCreated() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateOrderRequest> request = new HttpEntity<>(createOrderRequest, headers);

        ResponseEntity<Order> response = restTemplate.postForEntity(baseUrl + "/legacy", request, Order.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUserId()).isEqualTo(1L);
    }

    @Test
    void getAllOrders_ShouldReturnList() {
        // Create an order first
        Order order = createTestOrder();
        orderRepository.save(order);

        ResponseEntity<Order[]> response = restTemplate.getForEntity(baseUrl, Order[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        // Create an order first
        Order order = createTestOrder();
        Order saved = orderRepository.save(order);

        ResponseEntity<Order> response = restTemplate.getForEntity(baseUrl + "/" + saved.getId(), Order.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(saved.getId());
    }

    @Test
    void getOrdersByUserId_ShouldReturnList() {
        // Create an order first
        Order order = createTestOrder();
        orderRepository.save(order);

        ResponseEntity<Order[]> response = restTemplate.getForEntity(baseUrl + "/user/1", Order[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getOrdersByRestaurantId_ShouldReturnList() {
        // Create an order first
        Order order = createTestOrder();
        orderRepository.save(order);

        ResponseEntity<Order[]> response = restTemplate.getForEntity(baseUrl + "/restaurant/1", Order[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getOrdersByStatus_ShouldReturnList() {
        // Create an order first
        Order order = createTestOrder();
        orderRepository.save(order);

        ResponseEntity<Order[]> response = restTemplate.getForEntity(baseUrl + "/status/CREATED", Order[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void updateOrder_ShouldReturnUpdated() {
        // Create an order first
        Order order = createTestOrder();
        Order saved = orderRepository.save(order);

        Order updateRequest = new Order();
        updateRequest.setStatus(OrderStatus.CONFIRMED);
        updateRequest.setDeliveryAddress("Updated Address");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Order> request = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<Order> response = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.PUT,
                request,
                Order.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void deleteOrder_ShouldReturnNoContent() {
        // Create an order first
        Order order = createTestOrder();
        Order saved = orderRepository.save(order);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void updateOrderStatus_ShouldReturnUpdated() {
        // Create an order first
        Order order = createTestOrder();
        Order saved = orderRepository.save(order);

        Map<String, String> statusRequest = new HashMap<>();
        statusRequest.put("status", "CONFIRMED");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(statusRequest, headers);

        ResponseEntity<Order> response = restTemplate.exchange(
                baseUrl + "/" + saved.getId() + "/status",
                HttpMethod.PUT,
                request,
                Order.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    private Order createTestOrder() {
        OrderItem orderItem = new OrderItem();
        orderItem.setMenuItemId(1L);
        orderItem.setQuantity(2);
        orderItem.setPrice(BigDecimal.valueOf(12.99));

        Order order = new Order();
        order.setUserId(1L);
        order.setRestaurantId(1L);
        order.setDeliveryAddress("123 Test St, Test City");
        order.setTotalAmount(BigDecimal.valueOf(25.98));
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setOrderItems(Arrays.asList(orderItem));
        orderItem.setOrder(order);

        return order;
    }
}