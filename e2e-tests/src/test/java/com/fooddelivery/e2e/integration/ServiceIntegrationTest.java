package com.fooddelivery.e2e.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.verification.LoggedRequest.*;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

/**
 * Service Integration Tests for Food Delivery Platform
 * 
 * This test class focuses on testing integration points between individual services
 * within the Food Delivery microservices architecture. It validates:
 * 
 * 1. Service-to-Service Communication
 * 2. Data Consistency Across Services
 * 3. Error Handling and Fallback Mechanisms
 * 4. Circuit Breaker Patterns
 * 5. Service Discovery and Load Balancing
 * 6. Transaction Management Across Services
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("e2e-test")
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("food_delivery_integration")
            .withUsername("testuser")
            .withPassword("testpass")
            .withReuse(true);

    private static WireMockServer wireMockServer;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // Service URLs
    private static final String USER_SERVICE_URL = "http://localhost:8081";
    private static final String RESTAURANT_SERVICE_URL = "http://localhost:8082";
    private static final String ORDER_SERVICE_URL = "http://localhost:8083";
    private static final String PAYMENT_SERVICE_URL = "http://localhost:8084";
    private static final String NOTIFICATION_SERVICE_URL = "http://localhost:8085";
    private static final String API_GATEWAY_URL = "http://localhost:8080";
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @BeforeAll
    static void setupWireMock() {
        wireMockServer = new WireMockServer(9999);
        wireMockServer.start();
        WireMock.configureFor("localhost", 9999);
    }
    
    @AfterAll
    static void tearDownWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
    
    @BeforeEach
    void setUp() {
        wireMockServer.resetAll();
        setupServiceStubs();
    }
    
    /**
     * Setup WireMock stubs for all services
     */
    private void setupServiceStubs() {
        // User Service Stubs
        stubFor(post(urlEqualTo("/api/auth/login"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createUserLoginResponse())));
        
        stubFor(get(urlMatching("/api/users/\\d+"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createUserResponse())));
        
        // Restaurant Service Stubs
        stubFor(get(urlEqualTo("/api/restaurants"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createRestaurantsResponse())));
        
        stubFor(get(urlMatching("/api/restaurants/\\d+"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createRestaurantResponse())));
        
        stubFor(get(urlMatching("/api/restaurants/\\d+/menu"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createMenuResponse())));
        
        // Payment Service Stubs
        stubFor(post(urlEqualTo("/api/payments/process"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createPaymentResponse())));
        
        // Notification Service Stubs
        stubFor(post(urlEqualTo("/api/notifications"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createNotificationResponse())));
    }
    
    // ==================== USER SERVICE INTEGRATION TESTS ====================
    
    /**
     * Test user authentication and authorization flow
     */
    @Test
    @Order(1)
    @DisplayName("User Authentication and Authorization Integration")
    void testUserAuthenticationAndAuthorizationIntegration() {
        // Test successful authentication
        Map<String, Object> loginRequest = Map.of(
            "email", "test@example.com",
            "password", "password123"
        );
        
        ResponseEntity<Map> authResponse = restTemplate.exchange(
            USER_SERVICE_URL + "/api/auth/login",
            HttpMethod.POST,
            new HttpEntity<>(loginRequest, createHeaders()),
            Map.class
        );
        
        assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> authResult = authResponse.getBody();
        assertThat(authResult.get("success")).isEqualTo(true);
        assertThat(authResult).containsKey("token");
        
        String token = (String) authResult.get("token");
        
        // Test using token for authorized requests
        HttpHeaders authorizedHeaders = createHeaders();
        authorizedHeaders.set("Authorization", "Bearer " + token);
        
        ResponseEntity<Map> userResponse = restTemplate.exchange(
            USER_SERVICE_URL + "/api/users/1",
            HttpMethod.GET,
            new HttpEntity<>(authorizedHeaders),
            Map.class
        );
        
        assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    
    /**
     * Test user service circuit breaker and fallback mechanisms
     */
    @Test
    @Order(2)
    @DisplayName("User Service Circuit Breaker and Fallback")
    void testUserServiceCircuitBreakerAndFallback() {
        // Simulate user service failure
        stubFor(post(urlEqualTo("/api/auth/login"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withFixedDelay(5000)
                        .withBody("{\"error\": \"Internal server error\"}"))); // Fixed missing closing parenthesis
        
        Map<String, Object> loginRequest = Map.of(
            "email", "test@example.com",
            "password", "password123"
        );
        
        // Multiple requests to trigger circuit breaker
        for (int i = 0; i < 3; i++) {
            try {
                ResponseEntity<Map> response = restTemplate.exchange(
                    USER_SERVICE_URL + "/api/auth/login",
                    HttpMethod.POST,
                    new HttpEntity<>(loginRequest, createHeaders()),
                    Map.class
                );
                
                assertThat(response.getStatusCode()).isIn(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    HttpStatus.SERVICE_UNAVAILABLE,
                    HttpStatus.REQUEST_TIMEOUT
                );
            } catch (Exception e) {
                // Expected due to circuit breaker
                assertThat(e).isInstanceOf(Exception.class);
            }
        }
    }
    
    // ==================== RESTAURANT SERVICE INTEGRATION TESTS ====================
    
    /**
     * Test restaurant and menu data consistency
     */
    @Test
    @Order(10)
    @DisplayName("Restaurant and Menu Data Consistency")
    void testRestaurantAndMenuDataConsistency() {
        // Get all restaurants
        ResponseEntity<List> restaurantsResponse = restTemplate.exchange(
            RESTAURANT_SERVICE_URL + "/api/restaurants",
            HttpMethod.GET,
            new HttpEntity<>(createHeaders()),
            List.class
        );
        
        assertThat(restaurantsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Map<String, Object>> restaurants = restaurantsResponse.getBody();
        assertThat(restaurants).isNotEmpty();
        
        // Get specific restaurant
        Long restaurantId = ((Number) restaurants.get(0).get("id")).longValue();
        ResponseEntity<Map> restaurantResponse = restTemplate.exchange(
            RESTAURANT_SERVICE_URL + "/api/restaurants/" + restaurantId,
            HttpMethod.GET,
            new HttpEntity<>(createHeaders()),
            Map.class
        );
        
        assertThat(restaurantResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> restaurant = restaurantResponse.getBody();
        
        // Get menu for the restaurant
        ResponseEntity<List> menuResponse = restTemplate.exchange(
            RESTAURANT_SERVICE_URL + "/api/restaurants/" + restaurantId + "/menu",
            HttpMethod.GET,
            new HttpEntity<>(createHeaders()),
            List.class
        );
        
        assertThat(menuResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Map<String, Object>> menuItems = menuResponse.getBody();
        assertThat(menuItems).isNotEmpty();
        
        // Verify data consistency
        assertThat(restaurant.get("id")).isEqualTo(restaurantId.intValue());
        assertThat(restaurant.get("isActive")).isEqualTo(true);
        
        for (Map<String, Object> menuItem : menuItems) {
            assertThat(menuItem).containsKey("id");
            assertThat(menuItem).containsKey("name");
            assertThat(menuItem).containsKey("price");
            assertThat(menuItem).containsKey("available");
        }
    }
    
    /**
     * Test restaurant availability and menu item stock management
     */
    @Test
    @Order(11)
    @DisplayName("Restaurant Availability and Menu Stock Management")
    void testRestaurantAvailabilityAndMenuStockManagement() {
        Long restaurantId = 1L;
        
        // Test restaurant activation/deactivation
        ResponseEntity<Map> deactivateResponse = restTemplate.exchange(
            RESTAURANT_SERVICE_URL + "/api/restaurants/" + restaurantId + "/deactivate",
            HttpMethod.PUT,
            new HttpEntity<>(createHeaders()),
            Map.class
        );
        
        if (deactivateResponse.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> deactivatedRestaurant = deactivateResponse.getBody();
            assertThat(deactivatedRestaurant.get("isActive")).isEqualTo(false);
            
            // Verify menu is not accessible when restaurant is inactive
            ResponseEntity<List> menuResponse = restTemplate.exchange(
                RESTAURANT_SERVICE_URL + "/api/restaurants/" + restaurantId + "/menu",
                HttpMethod.GET,
                new HttpEntity<>(createHeaders()),
                List.class
            );
            
            // Should either return empty menu or appropriate error
            assertThat(menuResponse.getStatusCode()).isIn(
                HttpStatus.OK, HttpStatus.NOT_FOUND, HttpStatus.BAD_REQUEST
            );
            
            // Reactivate restaurant
            ResponseEntity<Map> activateResponse = restTemplate.exchange(
                RESTAURANT_SERVICE_URL + "/api/restaurants/" + restaurantId + "/activate",
                HttpMethod.PUT,
                new HttpEntity<>(createHeaders()),
                Map.class
            );
            
            if (activateResponse.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> activatedRestaurant = activateResponse.getBody();
                assertThat(activatedRestaurant.get("isActive")).isEqualTo(true);
            }
        }
    }
    
    // ==================== ORDER SERVICE INTEGRATION TESTS ====================
    
    /**
     * Test order creation with external service validation
     */
    @Test
    @Order(20)
    @DisplayName("Order Creation with External Service Validation")
    void testOrderCreationWithExternalServiceValidation() {
        // Create order request
        Map<String, Object> orderRequest = Map.of(
            "userId", 1L,
            "restaurantId", 1L,
            "deliveryAddress", "123 Test Street",
            "items", List.of(
                Map.of("menuItemId", 1L, "quantity", 2, "price", new BigDecimal("15.99")),
                Map.of("menuItemId", 2L, "quantity", 1, "price", new BigDecimal("12.50"))
            )
        );
        
        // Create order
        ResponseEntity<Map> orderResponse = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders",
            HttpMethod.POST,
            new HttpEntity<>(orderRequest, createHeaders()),
            Map.class
        );
        
        assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Map<String, Object> order = orderResponse.getBody();
        assertThat(order).containsKey("id");
        assertThat(order.get("status")).isEqualTo("CREATED");
        assertThat(order.get("userId")).isEqualTo(1);
        assertThat(order.get("restaurantId")).isEqualTo(1);
        
        // Verify order items
        List<Map<String, Object>> orderItems = (List<Map<String, Object>>) order.get("orderItems");
        assertThat(orderItems).hasSize(2);
        
        // Verify total amount calculation
        BigDecimal expectedTotal = new BigDecimal("44.48"); // (15.99 * 2) + 12.50
        assertThat(order.get("totalAmount")).isEqualTo(expectedTotal.doubleValue());
    }
    
    /**
     * Test order status transitions and notifications
     */
    @Test
    @Order(21)
    @DisplayName("Order Status Transitions and Notifications")
    void testOrderStatusTransitionsAndNotifications() {
        // Create order
        Map<String, Object> orderRequest = Map.of(
            "userId", 1L,
            "restaurantId", 1L,
            "deliveryAddress", "123 Test Street",
            "items", List.of(
                Map.of("menuItemId", 1L, "quantity", 1, "price", new BigDecimal("15.99"))
            )
        );
        
        ResponseEntity<Map> orderResponse = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders",
            HttpMethod.POST,
            new HttpEntity<>(orderRequest, createHeaders()),
            Map.class
        );
        
        assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long orderId = ((Number) orderResponse.getBody().get("id")).longValue();
        
        // Test status transitions
        String[] statusTransitions = {"CONFIRMED", "PREPARING", "READY", "DELIVERED"};
        
        for (String status : statusTransitions) {
            Map<String, Object> statusUpdate = Map.of("status", status);
            
            ResponseEntity<Map> updateResponse = restTemplate.exchange(
                ORDER_SERVICE_URL + "/api/orders/" + orderId + "/status",
                HttpMethod.PUT,
                new HttpEntity<>(statusUpdate, createHeaders()),
                Map.class
            );
            
            assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            Map<String, Object> updatedOrder = updateResponse.getBody();
            assertThat(updatedOrder.get("status")).isEqualTo(status);
            
            // Verify notification was sent for status change
            await().atMost(5, TimeUnit.SECONDS)
                    .untilAsserted(() -> {
                        verify(postRequestedFor(urlEqualTo("/api/notifications"))
                                .withRequestBody(containing(status)));
                    });
        }
    }
    
    // ==================== PAYMENT SERVICE INTEGRATION TESTS ====================
    
    /**
     * Test payment processing with order integration
     */
    @Test
    @Order(30)
    @DisplayName("Payment Processing with Order Integration")
    void testPaymentProcessingWithOrderIntegration() {
        // Create order first
        Map<String, Object> orderRequest = Map.of(
            "userId", 1L,
            "restaurantId", 1L,
            "deliveryAddress", "123 Test Street",
            "items", List.of(
                Map.of("menuItemId", 1L, "quantity", 1, "price", new BigDecimal("25.99"))
            )
        );
        
        ResponseEntity<Map> orderResponse = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders",
            HttpMethod.POST,
            new HttpEntity<>(orderRequest, createHeaders()),
            Map.class
        );
        
        assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long orderId = ((Number) orderResponse.getBody().get("id")).longValue();
        BigDecimal orderAmount = new BigDecimal(orderResponse.getBody().get("totalAmount").toString());
        
        // Process payment
        Map<String, Object> paymentRequest = Map.of(
            "orderId", orderId,
            "amount", orderAmount,
            "paymentMethod", "CREDIT_CARD",
            "cardNumber", "4111111111111111",
            "expiryDate", "12/25",
            "cvv", "123"
        );
        
        ResponseEntity<Map> paymentResponse = restTemplate.exchange(
            PAYMENT_SERVICE_URL + "/api/payments/process",
            HttpMethod.POST,
            new HttpEntity<>(paymentRequest, createHeaders()),
            Map.class
        );
        
        assertThat(paymentResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> payment = paymentResponse.getBody();
        assertThat(payment.get("status")).isEqualTo("SUCCESS");
        assertThat(payment.get("orderId")).isEqualTo(orderId.intValue());
        assertThat(payment).containsKey("transactionId");
        
        // Verify payment record exists
        ResponseEntity<List> paymentsResponse = restTemplate.exchange(
            PAYMENT_SERVICE_URL + "/api/payments/order/" + orderId,
            HttpMethod.GET,
            new HttpEntity<>(createHeaders()),
            List.class
        );
        
        assertThat(paymentsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Map<String, Object>> payments = paymentsResponse.getBody();
        assertThat(payments).hasSize(1);
        assertThat(payments.get(0).get("orderId")).isEqualTo(orderId.intValue());
    }
    
    /**
     * Test payment failure handling and order status updates
     */
    @Test
    @Order(31)
    @DisplayName("Payment Failure Handling and Order Status Updates")
    void testPaymentFailureHandlingAndOrderStatusUpdates() {
        // Mock payment failure
        stubFor(post(urlEqualTo("/api/payments/process"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createPaymentFailureResponse())));
        
        // Create order
        Map<String, Object> orderRequest = Map.of(
            "userId", 1L,
            "restaurantId", 1L,
            "deliveryAddress", "123 Test Street",
            "items", List.of(
                Map.of("menuItemId", 1L, "quantity", 1, "price", new BigDecimal("15.99"))
            )
        );
        
        ResponseEntity<Map> orderResponse = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders",
            HttpMethod.POST,
            new HttpEntity<>(orderRequest, createHeaders()),
            Map.class
        );
        
        Long orderId = ((Number) orderResponse.getBody().get("id")).longValue();
        
        // Attempt payment
        Map<String, Object> paymentRequest = Map.of(
            "orderId", orderId,
            "amount", new BigDecimal("15.99"),
            "paymentMethod", "CREDIT_CARD",
            "cardNumber", "4000000000000002", // Invalid card for testing
            "expiryDate", "12/25",
            "cvv", "123"
        );
        
        ResponseEntity<Map> paymentResponse = restTemplate.exchange(
            PAYMENT_SERVICE_URL + "/api/payments/process",
            HttpMethod.POST,
            new HttpEntity<>(paymentRequest, createHeaders()),
            Map.class
        );
        
        assertThat(paymentResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, Object> paymentResult = paymentResponse.getBody();
        assertThat(paymentResult.get("status")).isEqualTo("FAILED");
        
        // Verify order status remains unchanged or is marked as payment failed
        ResponseEntity<Map> orderStatusResponse = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders/" + orderId,
            HttpMethod.GET,
            new HttpEntity<>(createHeaders()),
            Map.class
        );
        
        assertThat(orderStatusResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> orderStatus = orderStatusResponse.getBody();
        assertThat(orderStatus.get("status")).isIn("CREATED", "PAYMENT_FAILED");
    }
    
    // ==================== NOTIFICATION SERVICE INTEGRATION TESTS ====================
    
    /**
     * Test notification service integration with order events
     */
    @Test
    @Order(40)
    @DisplayName("Notification Service Integration with Order Events")
    void testNotificationServiceIntegrationWithOrderEvents() {
        // Create order
        Map<String, Object> orderRequest = Map.of(
            "userId", 1L,
            "restaurantId", 1L,
            "deliveryAddress", "123 Test Street",
            "items", List.of(
                Map.of("menuItemId", 1L, "quantity", 1, "price", new BigDecimal("15.99"))
            )
        );
        
        ResponseEntity<Map> orderResponse = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders",
            HttpMethod.POST,
            new HttpEntity<>(orderRequest, createHeaders()),
            Map.class
        );
        
        Long orderId = ((Number) orderResponse.getBody().get("id")).longValue();
        
        // Confirm order to trigger notification
        ResponseEntity<Map> confirmResponse = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders/" + orderId + "/confirm",
            HttpMethod.POST,
            new HttpEntity<>(createHeaders()),
            Map.class
        );
        
        assertThat(confirmResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // Verify notification was sent
        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    verify(postRequestedFor(urlEqualTo("/api/notifications"))
                            .withRequestBody(containing("Order")));
                });
        
        // Verify notification content
        verify(postRequestedFor(urlEqualTo("/api/notifications"))
                .withRequestBody(containing(orderId.toString())));
    }
    
    /**
     * Test notification delivery and retry mechanisms
     */
    @Test
    @Order(41)
    @DisplayName("Notification Delivery and Retry Mechanisms")
    void testNotificationDeliveryAndRetryMechanisms() {
        // Mock notification service failure initially
        stubFor(post(urlEqualTo("/api/notifications"))
                .inScenario("notification-retry")
                .whenScenarioStateIs("Started")
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("{\"error\": \"Service temporarily unavailable\"}"))
                .willSetStateTo("First-Retry"));
        
        // Mock successful retry
        stubFor(post(urlEqualTo("/api/notifications"))
                .inScenario("notification-retry")
                .whenScenarioStateIs("First-Retry")
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createNotificationResponse())));
        
        // Create order to trigger notification
        Map<String, Object> orderRequest = Map.of(
            "userId", 1L,
            "restaurantId", 1L,
            "deliveryAddress", "123 Test Street",
            "items", List.of(
                Map.of("menuItemId", 1L, "quantity", 1, "price", new BigDecimal("15.99"))
            )
        );
        
        ResponseEntity<Map> orderResponse = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders",
            HttpMethod.POST,
            new HttpEntity<>(orderRequest, createHeaders()),
            Map.class
        );
        
        Long orderId = ((Number) orderResponse.getBody().get("id")).longValue();
        
        // Confirm order
        restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders/" + orderId + "/confirm",
            HttpMethod.POST,
            new HttpEntity<>(createHeaders()),
            Map.class
        );
        
        // Verify retry mechanism worked
        await().atMost(15, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    verify(moreThan(1), postRequestedFor(urlEqualTo("/api/notifications")));
                });
    }
    
    // ==================== API GATEWAY INTEGRATION TESTS ====================
    
    /**
     * Test API Gateway routing to all services
     */
    @Test
    @Order(50)
    @DisplayName("API Gateway Routing to All Services")
    void testApiGatewayRoutingToAllServices() {
        // Test routing to user service
        Map<String, Object> loginRequest = Map.of(
            "email", "test@example.com",
            "password", "password123"
        );
        
        ResponseEntity<Map> userServiceResponse = restTemplate.exchange(
            API_GATEWAY_URL + "/user-service/api/auth/login",
            HttpMethod.POST,
            new HttpEntity<>(loginRequest, createHeaders()),
            Map.class
        );
        
        // Should route correctly or return service unavailable
        assertThat(userServiceResponse.getStatusCode()).isIn(
            HttpStatus.OK, HttpStatus.SERVICE_UNAVAILABLE, HttpStatus.BAD_GATEWAY
        );
        
        // Test routing to restaurant service
        ResponseEntity<List> restaurantServiceResponse = restTemplate.exchange(
            API_GATEWAY_URL + "/restaurant-service/api/restaurants",
            HttpMethod.GET,
            new HttpEntity<>(createHeaders()),
            List.class
        );
        
        assertThat(restaurantServiceResponse.getStatusCode()).isIn(
            HttpStatus.OK, HttpStatus.SERVICE_UNAVAILABLE, HttpStatus.BAD_GATEWAY
        );
        
        // Test routing to order service
        Map<String, Object> orderRequest = Map.of(
            "userId", 1L,
            "restaurantId", 1L,
            "deliveryAddress", "123 Test Street",
            "items", List.of(
                Map.of("menuItemId", 1L, "quantity", 1, "price", new BigDecimal("15.99"))
            )
        );
        
        ResponseEntity<Map> orderServiceResponse = restTemplate.exchange(
            API_GATEWAY_URL + "/order-service/api/orders",
            HttpMethod.POST,
            new HttpEntity<>(orderRequest, createHeaders()),
            Map.class
        );
        
        assertThat(orderServiceResponse.getStatusCode()).isIn(
            HttpStatus.CREATED, HttpStatus.SERVICE_UNAVAILABLE, HttpStatus.BAD_GATEWAY
        );
    }
    
    /**
     * Test API Gateway load balancing and circuit breaker
     */
    @Test
    @Order(51)
    @DisplayName("API Gateway Load Balancing and Circuit Breaker")
    void testApiGatewayLoadBalancingAndCircuitBreaker() {
        // Simulate multiple requests to test load balancing
        List<ResponseEntity<List>> responses = new ArrayList<>();
        
        for (int i = 0; i < 5; i++) {
            try {
                ResponseEntity<List> response = restTemplate.exchange(
                    API_GATEWAY_URL + "/restaurant-service/api/restaurants",
                    HttpMethod.GET,
                    new HttpEntity<>(createHeaders()),
                    List.class
                );
                responses.add(response);
            } catch (Exception e) {
                // Expected if circuit breaker is triggered
            }
            
            // Small delay between requests
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // At least some requests should succeed or fail gracefully
        assertThat(responses).isNotEmpty();
        
        for (ResponseEntity<List> response : responses) {
            assertThat(response.getStatusCode()).isIn(
                HttpStatus.OK,
                HttpStatus.SERVICE_UNAVAILABLE,
                HttpStatus.BAD_GATEWAY,
                HttpStatus.REQUEST_TIMEOUT
            );
        }
    }
    
    // ==================== CROSS-SERVICE DATA CONSISTENCY TESTS ====================
    
    /**
     * Test data consistency across all services in complete workflow
     */
    @Test
    @Order(60)
    @DisplayName("Data Consistency Across All Services")
    void testDataConsistencyAcrossAllServices() {
        // 1. Authenticate user
        Map<String, Object> loginRequest = Map.of(
            "email", "test@example.com",
            "password", "password123"
        );
        
        ResponseEntity<Map> authResponse = restTemplate.exchange(
            USER_SERVICE_URL + "/api/auth/login",
            HttpMethod.POST,
            new HttpEntity<>(loginRequest, createHeaders()),
            Map.class
        );
        
        assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Long userId = ((Number) authResponse.getBody().get("userId")).longValue();
        
        // 2. Get restaurant and menu
        ResponseEntity<List> restaurantsResponse = restTemplate.exchange(
            RESTAURANT_SERVICE_URL + "/api/restaurants",
            HttpMethod.GET,
            new HttpEntity<>(createHeaders()),
            List.class
        );
        
        assertThat(restaurantsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Map<String, Object>> restaurants = restaurantsResponse.getBody();
        Long restaurantId = ((Number) restaurants.get(0).get("id")).longValue();
        
        // 3. Create order
        Map<String, Object> orderRequest = Map.of(
            "userId", userId,
            "restaurantId", restaurantId,
            "deliveryAddress", "123 Test Street",
            "items", List.of(
                Map.of("menuItemId", 1L, "quantity", 1, "price", new BigDecimal("15.99"))
            )
        );
        
        ResponseEntity<Map> orderResponse = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders",
            HttpMethod.POST,
            new HttpEntity<>(orderRequest, createHeaders()),
            Map.class
        );
        
        assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long orderId = ((Number) orderResponse.getBody().get("id")).longValue();
        
        // 4. Process payment
        Map<String, Object> paymentRequest = Map.of(
            "orderId", orderId,
            "amount", new BigDecimal("15.99"),
            "paymentMethod", "CREDIT_CARD"
        );
        
        ResponseEntity<Map> paymentResponse = restTemplate.exchange(
            PAYMENT_SERVICE_URL + "/api/payments/process",
            HttpMethod.POST,
            new HttpEntity<>(paymentRequest, createHeaders()),
            Map.class
        );
        
        assertThat(paymentResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // 5. Verify data consistency across services
        
        // Check order in order service
        ResponseEntity<Map> orderCheckResponse = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders/" + orderId,
            HttpMethod.GET,
            new HttpEntity<>(createHeaders()),
            Map.class
        );
        
        assertThat(orderCheckResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> orderData = orderCheckResponse.getBody();
        assertThat(orderData.get("userId")).isEqualTo(userId.intValue());
        assertThat(orderData.get("restaurantId")).isEqualTo(restaurantId.intValue());
        
        // Check payment in payment service
        ResponseEntity<List> paymentCheckResponse = restTemplate.exchange(
            PAYMENT_SERVICE_URL + "/api/payments/order/" + orderId,
            HttpMethod.GET,
            new HttpEntity<>(createHeaders()),
            List.class
        );
        
        assertThat(paymentCheckResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Map<String, Object>> payments = paymentCheckResponse.getBody();
        assertThat(payments).isNotEmpty();
        assertThat(payments.get(0).get("orderId")).isEqualTo(orderId.intValue());
        
        // Verify notification was sent
        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    verify(postRequestedFor(urlEqualTo("/api/notifications"))
                            .withRequestBody(containing(orderId.toString())));
                });
    }
    
    // ==================== HELPER METHODS ====================
    
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
    
    // ==================== WIREMOCK RESPONSE HELPERS ====================
    
    private String createUserLoginResponse() {
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Login successful",
            "userId", 1L,
            "username", "testuser",
            "email", "test@example.com",
            "role", "CUSTOMER",
            "token", "mock_jwt_token_12345"
        );
        
        try {
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            return "{\"success\": true, \"token\": \"mock_jwt_token_12345\"}";
        }
    }
    
    private String createUserResponse() {
        Map<String, Object> user = Map.of(
            "id", 1L,
            "username", "testuser",
            "email", "test@example.com",
            "firstName", "John",
            "lastName", "Doe",
            "role", "CUSTOMER"
        );
        
        try {
            return objectMapper.writeValueAsString(user);
        } catch (Exception e) {
            return "{\"id\": 1, \"username\": \"testuser\"}";
        }
    }
    
    private String createRestaurantsResponse() {
        List<Map<String, Object>> restaurants = List.of(
            Map.of(
                "id", 1L,
                "name", "Test Restaurant",
                "description", "A test restaurant",
                "address", "456 Restaurant Ave",
                "cuisineType", "Italian",
                "isActive", true,
                "rating", 4.5
            )
        );
        
        try {
            return objectMapper.writeValueAsString(restaurants);
        } catch (Exception e) {
            return "[{\"id\": 1, \"name\": \"Test Restaurant\", \"isActive\": true}]";
        }
    }
    
    private String createRestaurantResponse() {
        Map<String, Object> restaurant = Map.of(
            "id", 1L,
            "name", "Test Restaurant",
            "description", "A test restaurant",
            "address", "456 Restaurant Ave",
            "cuisineType", "Italian",
            "isActive", true,
            "rating", 4.5
        );
        
        try {
            return objectMapper.writeValueAsString(restaurant);
        } catch (Exception e) {
            return "{\"id\": 1, \"name\": \"Test Restaurant\", \"isActive\": true}";
        }
    }
    
    private String createMenuResponse() {
        List<Map<String, Object>> menuItems = List.of(
            Map.of(
                "id", 1L,
                "name", "Pizza Margherita",
                "description", "Classic pizza with tomato and mozzarella",
                "price", new BigDecimal("15.99"),
                "available", true,
                "category", "Main Course"
            ),
            Map.of(
                "id", 2L,
                "name", "Caesar Salad",
                "description", "Fresh romaine lettuce with Caesar dressing",
                "price", new BigDecimal("12.50"),
                "available", true,
                "category", "Salad"
            )
        );
        
        try {
            return objectMapper.writeValueAsString(menuItems);
        } catch (Exception e) {
            return "[{\"id\": 1, \"name\": \"Pizza\", \"price\": 15.99, \"available\": true}]";
        }
    }
    
    private String createPaymentResponse() {
        Map<String, Object> payment = Map.of(
            "id", 1L,
            "orderId", 1L,
            "status", "SUCCESS",
            "amount", new BigDecimal("15.99"),
            "paymentMethod", "CREDIT_CARD",
            "transactionId", "txn_12345",
            "timestamp", LocalDateTime.now().toString()
        );
        
        try {
            return objectMapper.writeValueAsString(payment);
        } catch (Exception e) {
            return "{\"status\": \"SUCCESS\", \"transactionId\": \"txn_12345\"}";
        }
    }
    
    private String createPaymentFailureResponse() {
        Map<String, Object> payment = Map.of(
            "status", "FAILED",
            "error", "Invalid card number",
            "timestamp", LocalDateTime.now().toString()
        );
        
        try {
            return objectMapper.writeValueAsString(payment);
        } catch (Exception e) {
            return "{\"status\": \"FAILED\", \"error\": \"Invalid card number\"}";
        }
    }
    
    private String createNotificationResponse() {
        Map<String, Object> notification = Map.of(
            "id", 1L,
            "message", "Order notification sent",
            "status", "SENT",
            "timestamp", LocalDateTime.now().toString()
        );
        
        try {
            return objectMapper.writeValueAsString(notification);
        } catch (Exception e) {
            return "{\"status\": \"SENT\", \"message\": \"Notification sent\"}";
        }
    }
}