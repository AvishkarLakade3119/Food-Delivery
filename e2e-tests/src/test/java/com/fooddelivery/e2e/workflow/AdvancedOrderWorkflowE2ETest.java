package com.fooddelivery.e2e.workflow;

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
 * Advanced End-to-End Test Suite for Complex Order Placement Workflows
 * 
 * This test class covers advanced order placement scenarios including:
 * - Multi-restaurant orders
 * - Subscription-based ordering
 * - Geolocation-based delivery
 * - Dynamic pricing
 * - Inventory management
 * - Performance testing
 * - Real-time tracking
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("e2e-test")
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdvancedOrderWorkflowE2ETest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("food_delivery_advanced_e2e")
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
    
    // Test data
    private Long testUserId = 1L;
    private Long testRestaurantId = 1L;
    private Long testMenuItemId1 = 1L;
    private Long testMenuItemId2 = 2L;
    private String authToken;
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @BeforeAll
    static void setupWireMock() {
        wireMockServer = new WireMockServer(9998);
        wireMockServer.start();
        WireMock.configureFor("localhost", 9998);
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
        setupDefaultWireMockStubs();
    }
    
    private void setupDefaultWireMockStubs() {
        // User authentication stub
        stubFor(post(urlEqualTo("/api/auth/login"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createLoginResponse())));
        
        // Restaurant service stubs
        stubFor(get(urlEqualTo("/api/restaurants"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createRestaurantListResponse())));
        
        stubFor(get(urlMatching("/api/restaurants/\\d+/menu"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createMenuItemsResponse())));
        
        // Payment service stub
        stubFor(post(urlEqualTo("/api/payments/process"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createPaymentSuccessResponse())));
        
        // Notification service stub
        stubFor(post(urlEqualTo("/api/notifications"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createNotificationResponse())));
    }
    
    // ==================== ADVANCED WORKFLOW TESTS ====================
    
    /**
     * Test order workflow from multiple restaurants (should fail)
     */
    @Test
    @Order(1)
    @DisplayName("Order from Multiple Restaurants - Should Fail")
    void testOrderFromMultipleRestaurants() {
        authenticateUser("test@example.com", "password123");
        
        Map<String, Object> invalidOrderRequest = Map.of(
            "userId", testUserId,
            "restaurantId", testRestaurantId,
            "deliveryAddress", "123 Test Street",
            "items", List.of(
                Map.of("menuItemId", testMenuItemId1, "quantity", 1, "price", new BigDecimal("15.99"), "restaurantId", testRestaurantId),
                Map.of("menuItemId", testMenuItemId2, "quantity", 1, "price", new BigDecimal("12.50"), "restaurantId", 2L) // Different restaurant
            )
        );
        
        ResponseEntity<Map> response = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders",
            HttpMethod.POST,
            new HttpEntity<>(invalidOrderRequest, createHeaders()),
            Map.class
        );
        
        assertThat(response.getStatusCode()).isIn(HttpStatus.BAD_REQUEST, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    
    /**
     * Test order workflow with dietary restrictions
     */
    @Test
    @Order(2)
    @DisplayName("Order with Dietary Restrictions")
    void testOrderWithDietaryRestrictions() {
        authenticateUser("test@example.com", "password123");
        
        Map<String, Object> orderRequest = Map.of(
            "userId", testUserId,
            "restaurantId", testRestaurantId,
            "deliveryAddress", "123 Test Street",
            "items", List.of(
                Map.of("menuItemId", testMenuItemId1, "quantity", 1, "price", new BigDecimal("19.99"))
            ),
            "dietaryRestrictions", List.of("GLUTEN_FREE", "DAIRY_FREE"),
            "allergyNotes", "Severe nut allergy - please ensure no cross-contamination"
        );
        
        Map<String, Object> order = createOrder(orderRequest);
        assertThat(((List<?>) order.get("dietaryRestrictions"))).hasSize(2);
        assertThat(order.get("allergyNotes")).isEqualTo("Severe nut allergy - please ensure no cross-contamination");
    }
    
    /**
     * Test group order workflow
     */
    @Test
    @Order(3)
    @DisplayName("Group Order Workflow")
    void testGroupOrderWorkflow() {
        authenticateUser("test@example.com", "password123");
        
        // Create group order
        Map<String, Object> groupOrderRequest = Map.of(
            "groupOrderId", "group_123",
            "organizerId", testUserId,
            "restaurantId", testRestaurantId,
            "deliveryAddress", "123 Office Building",
            "orderDeadline", LocalDateTime.now().plusHours(1).toString(),
            "participants", List.of(
                Map.of("userId", testUserId, "items", List.of(
                    Map.of("menuItemId", testMenuItemId1, "quantity", 1, "price", new BigDecimal("15.99"))
                )),
                Map.of("userId", 2L, "items", List.of(
                    Map.of("menuItemId", testMenuItemId2, "quantity", 1, "price", new BigDecimal("12.50"))
                ))
            )
        );
        
        Map<String, Object> groupOrder = createOrder(groupOrderRequest);
        assertThat(groupOrder.get("groupOrderId")).isEqualTo("group_123");
        assertThat(groupOrder.get("totalAmount")).isEqualTo(28.49);
    }
    
    /**
     * Test real-time order tracking workflow
     */
    @Test
    @Order(4)
    @DisplayName("Real-time Order Tracking Workflow")
    void testRealTimeOrderTrackingWorkflow() {
        authenticateUser("test@example.com", "password123");
        
        Map<String, Object> orderRequest = createOrderRequest();
        Map<String, Object> order = createOrder(orderRequest);
        Long orderId = ((Number) order.get("id")).longValue();
        
        // Process payment and confirm order
        processPayment(orderId, "CREDIT_CARD");
        confirmOrder(orderId);
        
        // Test different order status updates
        String[] statuses = {"PREPARING", "READY_FOR_PICKUP", "OUT_FOR_DELIVERY", "DELIVERED"};
        
        for (String status : statuses) {
            Map<String, Object> statusUpdate = Map.of("status", status);
            ResponseEntity<Map> response = restTemplate.exchange(
                ORDER_SERVICE_URL + "/api/orders/" + orderId + "/status",
                HttpMethod.PUT,
                new HttpEntity<>(statusUpdate, createHeaders()),
                Map.class
            );
            
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            Map<String, Object> updatedOrder = response.getBody();
            assertThat(updatedOrder.get("status")).isEqualTo(status);
            
            // Verify tracking endpoint
            ResponseEntity<Map> trackingResponse = restTemplate.exchange(
                ORDER_SERVICE_URL + "/api/orders/" + orderId + "/tracking",
                HttpMethod.GET,
                new HttpEntity<>(createHeaders()),
                Map.class
            );
            
            assertThat(trackingResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            Map<String, Object> trackingData = trackingResponse.getBody();
            assertThat(trackingData.get("status")).isEqualTo(status);
        }
    }
    
    /**
     * Test order workflow with delivery time estimation
     */
    @Test
    @Order(5)
    @DisplayName("Order with Delivery Time Estimation")
    void testOrderWithDeliveryTimeEstimation() {
        authenticateUser("test@example.com", "password123");
        
        Map<String, Object> orderRequest = createOrderRequest();
        Map<String, Object> order = createOrder(orderRequest);
        
        // Verify estimated delivery time is calculated
        assertThat(order.get("estimatedDeliveryTime")).isNotNull();
        
        // Check that estimated time is reasonable (between 30-90 minutes from now)
        String estimatedTimeStr = (String) order.get("estimatedDeliveryTime");
        if (estimatedTimeStr != null) {
            LocalDateTime estimatedTime = LocalDateTime.parse(estimatedTimeStr);
            LocalDateTime now = LocalDateTime.now();
            assertThat(estimatedTime).isAfter(now.plusMinutes(20));
            assertThat(estimatedTime).isBefore(now.plusMinutes(120));
        }
    }
    
    /**
     * Test order workflow with peak hour surcharge
     */
    @Test
    @Order(6)
    @DisplayName("Order with Peak Hour Surcharge")
    void testOrderWithPeakHourSurcharge() {
        authenticateUser("test@example.com", "password123");
        
        Map<String, Object> orderRequest = Map.of(
            "userId", testUserId,
            "restaurantId", testRestaurantId,
            "deliveryAddress", "123 Test Street",
            "items", List.of(
                Map.of("menuItemId", testMenuItemId1, "quantity", 1, "price", new BigDecimal("20.00"))
            ),
            "isPeakHour", true,
            "surchargeAmount", new BigDecimal("3.00")
        );
        
        Map<String, Object> order = createOrder(orderRequest);
        assertThat(order.get("totalAmount")).isEqualTo(23.00); // 20.00 + 3.00 surcharge
        assertThat(order.get("surchargeAmount")).isEqualTo(3.00);
    }
    
    /**
     * Test order workflow with delivery radius validation
     */
    @Test
    @Order(7)
    @DisplayName("Order Outside Delivery Radius - Should Fail")
    void testOrderOutsideDeliveryRadius() {
        authenticateUser("test@example.com", "password123");
        
        Map<String, Object> orderRequest = Map.of(
            "userId", testUserId,
            "restaurantId", testRestaurantId,
            "deliveryAddress", "999 Far Away Street, Distant City, 99999", // Outside delivery radius
            "items", List.of(
                Map.of("menuItemId", testMenuItemId1, "quantity", 1, "price", new BigDecimal("15.99"))
            )
        );
        
        ResponseEntity<Map> response = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders",
            HttpMethod.POST,
            new HttpEntity<>(orderRequest, createHeaders()),
            Map.class
        );
        
        assertThat(response.getStatusCode()).isIn(HttpStatus.BAD_REQUEST, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    
    /**
     * Test order workflow with restaurant closing time validation
     */
    @Test
    @Order(8)
    @DisplayName("Order After Restaurant Closing Time - Should Fail")
    void testOrderAfterRestaurantClosingTime() {
        authenticateUser("test@example.com", "password123");
        
        // Mock restaurant as closed
        stubFor(get(urlMatching("/api/restaurants/\\d+"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createClosedRestaurantResponse())));
        
        Map<String, Object> orderRequest = createOrderRequest();
        
        ResponseEntity<Map> response = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders",
            HttpMethod.POST,
            new HttpEntity<>(orderRequest, createHeaders()),
            Map.class
        );
        
        assertThat(response.getStatusCode()).isIn(HttpStatus.BAD_REQUEST, HttpStatus.CONFLICT);
    }
    
    /**
     * Test complete order lifecycle with all status transitions
     */
    @Test
    @Order(9)
    @DisplayName("Complete Order Lifecycle - All Status Transitions")
    void testCompleteOrderLifecycleAllStatusTransitions() {
        authenticateUser("test@example.com", "password123");
        
        // Create order
        Map<String, Object> orderRequest = createOrderRequest();
        Map<String, Object> order = createOrder(orderRequest);
        Long orderId = ((Number) order.get("id")).longValue();
        assertThat(order.get("status")).isEqualTo("CREATED");
        
        // Process payment and confirm
        processPayment(orderId, "CREDIT_CARD");
        Map<String, Object> confirmedOrder = confirmOrder(orderId);
        assertThat(confirmedOrder.get("status")).isEqualTo("CONFIRMED");
        
        // Simulate restaurant preparation
        updateOrderStatus(orderId, "PREPARING");
        updateOrderStatus(orderId, "READY_FOR_PICKUP");
        updateOrderStatus(orderId, "OUT_FOR_DELIVERY");
        
        // Final delivery
        ResponseEntity<Map> deliveryResponse = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders/" + orderId + "/deliver",
            HttpMethod.POST,
            new HttpEntity<>(createHeaders()),
            Map.class
        );
        
        assertThat(deliveryResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> deliveredOrder = deliveryResponse.getBody();
        assertThat(deliveredOrder.get("status")).isEqualTo("DELIVERED");
        
        // Verify notifications were sent for each status change
        await().atMost(15, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    verify(moreThanOrExactly(6), postRequestedFor(urlEqualTo("/api/notifications")));
                });
    }
    
    /**
     * Test order workflow with payment retry mechanism
     */
    @Test
    @Order(10)
    @DisplayName("Order with Payment Retry Mechanism")
    void testOrderWithPaymentRetryMechanism() {
        authenticateUser("test@example.com", "password123");
        
        Map<String, Object> orderRequest = createOrderRequest();
        Map<String, Object> order = createOrder(orderRequest);
        Long orderId = ((Number) order.get("id")).longValue();
        
        // First payment attempt fails
        stubFor(post(urlEqualTo("/api/payments/process"))
                .inScenario("Payment Retry")
                .whenScenarioStateIs("Started")
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createPaymentFailureResponse("Card declined")))
                .willSetStateTo("First Attempt Failed"));
        
        // Second payment attempt succeeds
        stubFor(post(urlEqualTo("/api/payments/process"))
                .inScenario("Payment Retry")
                .whenScenarioStateIs("First Attempt Failed")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createPaymentSuccessResponse()))
                .willSetStateTo("Payment Successful"));
        
        // First attempt should fail
        Map<String, Object> paymentRequest = Map.of(
            "orderId", orderId,
            "amount", new BigDecimal("28.49"),
            "paymentMethod", "CREDIT_CARD"
        );
        
        ResponseEntity<Map> firstAttempt = restTemplate.exchange(
            PAYMENT_SERVICE_URL + "/api/payments/process",
            HttpMethod.POST,
            new HttpEntity<>(paymentRequest, createHeaders()),
            Map.class
        );
        assertThat(firstAttempt.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        // Second attempt should succeed
        ResponseEntity<Map> secondAttempt = restTemplate.exchange(
            PAYMENT_SERVICE_URL + "/api/payments/process",
            HttpMethod.POST,
            new HttpEntity<>(paymentRequest, createHeaders()),
            Map.class
        );
        assertThat(secondAttempt.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> paymentResponse = secondAttempt.getBody();
        assertThat(paymentResponse.get("status")).isEqualTo("SUCCESS");
    }
    
    /**
     * Test end-to-end performance under load
     */
    @Test
    @Order(11)
    @DisplayName("Performance Test - Multiple Concurrent Orders")
    void testPerformanceMultipleConcurrentOrders() throws InterruptedException {
        authenticateUser("test@example.com", "password123");
        
        List<Thread> threads = new ArrayList<>();
        List<ResponseEntity<Map>> responses = Collections.synchronizedList(new ArrayList<>());
        List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());
        
        int numberOfOrders = 10;
        
        for (int i = 0; i < numberOfOrders; i++) {
            final int orderNumber = i;
            Thread thread = new Thread(() -> {
                try {
                    Map<String, Object> orderRequest = Map.of(
                        "userId", testUserId,
                        "restaurantId", testRestaurantId,
                        "deliveryAddress", "123 Test Street #" + orderNumber,
                        "items", List.of(
                            Map.of("menuItemId", testMenuItemId1, "quantity", 1, "price", new BigDecimal("15.99"))
                        )
                    );
                    
                    ResponseEntity<Map> response = restTemplate.exchange(
                        ORDER_SERVICE_URL + "/api/orders",
                        HttpMethod.POST,
                        new HttpEntity<>(orderRequest, createHeaders()),
                        Map.class
                    );
                    responses.add(response);
                } catch (Exception e) {
                    exceptions.add(e);
                }
            });
            threads.add(thread);
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join(30000); // 30 second timeout per thread
        }
        
        // Verify results
        assertThat(exceptions).isEmpty(); // No exceptions should occur
        assertThat(responses).hasSize(numberOfOrders);
        
        for (ResponseEntity<Map> response : responses) {
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }
    }
    
    /**
     * Test order workflow with geolocation-based delivery
     */
    @Test
    @Order(12)
    @DisplayName("Order with Geolocation-based Delivery")
    void testOrderWithGeolocationBasedDelivery() {
        authenticateUser("test@example.com", "password123");
        
        Map<String, Object> orderRequest = Map.of(
            "userId", testUserId,
            "restaurantId", testRestaurantId,
            "deliveryAddress", "123 Test Street",
            "items", List.of(
                Map.of("menuItemId", testMenuItemId1, "quantity", 1, "price", new BigDecimal("15.99"))
            ),
            "geolocation", Map.of(
                "latitude", 40.7128,
                "longitude", -74.0060,
                "accuracy", 10.0
            ),
            "deliveryPreferences", Map.of(
                "preferredDeliveryWindow", "18:00-20:00",
                "deliveryType", "CONTACTLESS",
                "gpsTracking", true
            )
        );
        
        Map<String, Object> order = createOrder(orderRequest);
        assertThat(order.get("geolocation")).isNotNull();
        assertThat(order.get("deliveryPreferences")).isNotNull();
        
        // Verify estimated delivery time is calculated based on location
        assertThat(order.get("estimatedDeliveryTime")).isNotNull();
    }
    
    /**
     * Test order workflow with subscription-based ordering
     */
    @Test
    @Order(13)
    @DisplayName("Subscription-based Recurring Order")
    void testSubscriptionBasedRecurringOrder() {
        authenticateUser("test@example.com", "password123");
        
        Map<String, Object> subscriptionOrderRequest = Map.of(
            "userId", testUserId,
            "restaurantId", testRestaurantId,
            "deliveryAddress", "123 Test Street",
            "items", List.of(
                Map.of("menuItemId", testMenuItemId1, "quantity", 1, "price", new BigDecimal("15.99"))
            ),
            "subscriptionDetails", Map.of(
                "frequency", "WEEKLY",
                "dayOfWeek", "MONDAY",
                "deliveryTime", "12:00",
                "subscriptionId", "sub_123",
                "autoRenew", true
            )
        );
        
        Map<String, Object> order = createOrder(subscriptionOrderRequest);
        assertThat(order.get("subscriptionDetails")).isNotNull();
        assertThat(order.get("isRecurring")).isEqualTo(true);
        
        Long orderId = ((Number) order.get("id")).longValue();
        processPayment(orderId, "CREDIT_CARD");
        confirmOrder(orderId);
        
        // Verify subscription is active
        assertThat(order.get("subscriptionStatus")).isEqualTo("ACTIVE");
    }
    
    // ==================== HELPER METHODS ====================
    
    private Map<String, Object> authenticateUser(String email, String password) {
        Map<String, Object> loginRequest = Map.of(
            "email", email,
            "password", password
        );
        
        ResponseEntity<Map> response = restTemplate.exchange(
            USER_SERVICE_URL + "/api/auth/login",
            HttpMethod.POST,
            new HttpEntity<>(loginRequest, createHeaders()),
            Map.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }
    
    private Map<String, Object> createOrder(Map<String, Object> orderRequest) {
        ResponseEntity<Map> response = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders",
            HttpMethod.POST,
            new HttpEntity<>(orderRequest, createHeaders()),
            Map.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getBody();
    }
    
    private Map<String, Object> processPayment(Long orderId, String paymentMethod) {
        Map<String, Object> paymentRequest = Map.of(
            "orderId", orderId,
            "amount", new BigDecimal("28.49"),
            "paymentMethod", paymentMethod,
            "cardNumber", "4111111111111111",
            "expiryDate", "12/25",
            "cvv", "123"
        );
        
        ResponseEntity<Map> response = restTemplate.exchange(
            PAYMENT_SERVICE_URL + "/api/payments/process",
            HttpMethod.POST,
            new HttpEntity<>(paymentRequest, createHeaders()),
            Map.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }
    
    private Map<String, Object> confirmOrder(Long orderId) {
        ResponseEntity<Map> response = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders/" + orderId + "/confirm",
            HttpMethod.POST,
            new HttpEntity<>(createHeaders()),
            Map.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }
    
    private void updateOrderStatus(Long orderId, String status) {
        Map<String, Object> statusUpdate = Map.of("status", status);
        ResponseEntity<Map> response = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders/" + orderId + "/status",
            HttpMethod.PUT,
            new HttpEntity<>(statusUpdate, createHeaders()),
            Map.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    
    private Map<String, Object> createOrderRequest() {
        return Map.of(
            "userId", testUserId,
            "restaurantId", testRestaurantId,
            "deliveryAddress", "123 Test Street, Test City",
            "items", List.of(
                Map.of("menuItemId", testMenuItemId1, "quantity", 1, "price", new BigDecimal("15.99")),
                Map.of("menuItemId", testMenuItemId2, "quantity", 1, "price", new BigDecimal("12.50"))
            )
        );
    }
    
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (authToken != null) {
            headers.set("Authorization", "Bearer " + authToken);
        }
        return headers;
    }
    
    // ==================== WIREMOCK RESPONSE HELPERS ====================
    
    private String createLoginResponse() {
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Login successful",
            "userId", testUserId,
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
    
    private String createRestaurantListResponse() {
        List<Map<String, Object>> restaurants = List.of(
            Map.of(
                "id", testRestaurantId,
                "name", "Test Restaurant",
                "description", "A test restaurant",
                "address", "456 Restaurant Ave",
                "phone", "+1987654321",
                "cuisineType", "Italian",
                "isActive", true,
                "rating", 4.5
            )
        );
        try {
            return objectMapper.writeValueAsString(restaurants);
        } catch (Exception e) {
            return "[{\"id\": 1, \"name\": \"Test Restaurant\"}]";
        }
    }
    
    private String createMenuItemsResponse() {
        List<Map<String, Object>> menuItems = List.of(
            Map.of(
                "id", testMenuItemId1,
                "name", "Pizza Margherita",
                "description", "Classic pizza with tomato and mozzarella",
                "price", new BigDecimal("15.99"),
                "available", true,
                "category", "Main Course"
            ),
            Map.of(
                "id", testMenuItemId2,
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
            return "[{\"id\": 1, \"name\": \"Pizza\", \"price\": 15.99}]";
        }
    }
    
    private String createPaymentSuccessResponse() {
        Map<String, Object> response = Map.of(
            "id", 1L,
            "orderId", 1L,
            "status", "SUCCESS",
            "amount", new BigDecimal("28.49"),
            "paymentMethod", "CREDIT_CARD",
            "transactionId", "txn_12345",
            "timestamp", LocalDateTime.now().toString()
        );
        
        try {
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            return "{\"status\": \"SUCCESS\", \"transactionId\": \"txn_12345\"}";
        }
    }
    
    private String createPaymentFailureResponse(String reason) {
        Map<String, Object> response = Map.of(
            "status", "FAILED",
            "error", reason,
            "timestamp", LocalDateTime.now().toString()
        );
        
        try {
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            return "{\"status\": \"FAILED\", \"error\": \"" + reason + "\"}";
        }
    }
    
    private String createNotificationResponse() {
        Map<String, Object> response = Map.of(
            "id", 1L,
            "message", "Order notification sent",
            "status", "SENT",
            "timestamp", LocalDateTime.now().toString()
        );
        
        try {
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            return "{\"status\": \"SENT\", \"message\": \"Notification sent\"}";
        }
    }
    
    private String createClosedRestaurantResponse() {
        Map<String, Object> restaurant = Map.ofEntries(
                Map.entry("id", testRestaurantId),
                Map.entry("name", "Test Restaurant"),
                Map.entry("description", "A test restaurant"),
                Map.entry("address", "456 Restaurant Ave"),
                Map.entry("phone", "+1987654321"),
                Map.entry("cuisineType", "Italian"),
                Map.entry("isActive", false), // Restaurant is closed
                Map.entry("rating", 4.5),
                Map.entry("openingHours", Map.ofEntries(
                        Map.entry("monday", "09:00-22:00"),
                        Map.entry("tuesday", "09:00-22:00"),
                        Map.entry("wednesday", "09:00-22:00"),
                        Map.entry("thursday", "09:00-22:00"),
                        Map.entry("friday", "09:00-23:00"),
                        Map.entry("saturday", "10:00-23:00"),
                        Map.entry("sunday", "10:00-21:00")
                )),
                Map.entry("currentTime", "23:30"), // Past closing time
                Map.entry("isClosed", true)
        );
        
        try {
            return objectMapper.writeValueAsString(restaurant);
        } catch (Exception e) {
            return "{\"id\": 1, \"name\": \"Test Restaurant\", \"isActive\": false, \"isClosed\": true}";
        }
    }
}