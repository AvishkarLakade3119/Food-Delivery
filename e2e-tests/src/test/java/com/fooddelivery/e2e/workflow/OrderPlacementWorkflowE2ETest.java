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
 * Comprehensive End-to-End Test Suite for Order Placement Workflow
 * 
 * This test class covers the complete order placement workflow:
 * 1. User Authentication
 * 2. Restaurant Menu Browsing
 * 3. Cart Management
 * 4. Order Placement with Validation
 * 5. Payment Processing
 * 6. Order Confirmation and Notification
 * 
 * Tests include positive scenarios, negative scenarios, edge cases,
 * and integration points between all microservices.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("e2e-test")
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderPlacementWorkflowE2ETest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("food_delivery_e2e")
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
    private Long testUserId;
    private Long testRestaurantId;
    private Long testMenuItemId1;
    private Long testMenuItemId2;
    private String authToken;
    private Map<String, Object> testUser;
    private Map<String, Object> testRestaurant;
    
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
        // Reset WireMock
        wireMockServer.resetAll();
        
        // Setup test data
        setupTestData();
        
        // Configure default stubs
        setupDefaultWireMockStubs();
    }
    
    /**
     * Setup test data for E2E scenarios
     */
    private void setupTestData() {
        testUserId = 1L;
        testRestaurantId = 1L;
        testMenuItemId1 = 1L;
        testMenuItemId2 = 2L;
        
        testUser = Map.of(
            "id", testUserId,
            "username", "testuser",
            "email", "test@example.com",
            "password", "password123",
            "firstName", "John",
            "lastName", "Doe",
            "phone", "+1234567890",
            "address", "123 Test Street, Test City",
            "role", "CUSTOMER"
        );
        
        testRestaurant = Map.of(
            "id", testRestaurantId,
            "name", "Test Restaurant",
            "description", "A test restaurant",
            "address", "456 Restaurant Ave",
            "phone", "+1987654321",
            "cuisineType", "Italian",
            "isActive", true,
            "rating", 4.5
        );
    }
    
    /**
     * Setup default WireMock stubs for external services
     */
    private void setupDefaultWireMockStubs() {
        // User service stubs
        stubFor(post(urlEqualTo("/api/auth/register"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createSuccessResponse(testUser))));
        
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
        
        stubFor(get(urlMatching("/api/restaurants/\\d+"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createRestaurantResponse())));
        
        // Menu items stub
        stubFor(get(urlMatching("/api/restaurants/\\d+/menu"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createMenuItemsResponse())));
        
        // Payment service stubs
        stubFor(post(urlEqualTo("/api/payments/process"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createPaymentSuccessResponse())));
        
        // Notification service stubs
        stubFor(post(urlEqualTo("/api/notifications"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createNotificationResponse())));
    }
    
    // ==================== POSITIVE SCENARIOS ====================
    
    /**
     * Test complete successful order placement flow from authentication to notification
     * Covers the happy path scenario where everything works as expected
     */
    @Test
    @Order(1)
    @DisplayName("Complete Successful Order Placement Flow")
    void testCompleteSuccessfulOrderPlacementFlow() {
        // Step 1: User Authentication
        Map<String, Object> loginResponse = authenticateUser("test@example.com", "password123");
        assertThat(loginResponse).containsKey("success");
        assertThat(loginResponse.get("success")).isEqualTo(true);
        
        // Step 2: Browse Restaurants
        List<Map<String, Object>> restaurants = browseRestaurants();
        assertThat(restaurants).isNotEmpty();
        assertThat(restaurants.get(0)).containsKey("id");
        
        // Step 3: Browse Menu Items
        List<Map<String, Object>> menuItems = browseMenuItems(testRestaurantId);
        assertThat(menuItems).hasSize(2);
        
        // Step 4: Create Order with Multiple Items
        Map<String, Object> orderRequest = createOrderRequest();
        Map<String, Object> order = createOrder(orderRequest);
        assertThat(order).containsKey("id");
        assertThat(order.get("status")).isEqualTo("CREATED");
        
        Long orderId = ((Number) order.get("id")).longValue();
        
        // Step 5: Process Payment
        Map<String, Object> paymentResponse = processPayment(orderId, "CREDIT_CARD");
        assertThat(paymentResponse.get("status")).isEqualTo("SUCCESS");
        
        // Step 6: Verify Order Confirmation
        Map<String, Object> confirmedOrder = confirmOrder(orderId);
        assertThat(confirmedOrder.get("status")).isEqualTo("CONFIRMED");
        
        // Step 7: Verify Notification Sent
        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    verify(postRequestedFor(urlEqualTo("/api/notifications"))
                            .withRequestBody(containing("Order confirmed")));
                });
    }
    
    /**
     * Test order placement with multiple items from the same restaurant
     */
    @Test
    @Order(2)
    @DisplayName("Order with Multiple Items from Same Restaurant")
    void testOrderWithMultipleItemsSameRestaurant() {
        authenticateUser("test@example.com", "password123");
        
        Map<String, Object> orderRequest = Map.of(
            "userId", testUserId,
            "restaurantId", testRestaurantId,
            "deliveryAddress", "123 Test Street",
            "items", List.of(
                Map.of("menuItemId", testMenuItemId1, "quantity", 2, "price", new BigDecimal("15.99")),
                Map.of("menuItemId", testMenuItemId2, "quantity", 1, "price", new BigDecimal("12.50"))
            )
        );
        
        Map<String, Object> order = createOrder(orderRequest);
        assertThat(order.get("totalAmount")).isEqualTo(44.48); // (15.99 * 2) + 12.50
        assertThat(((List<?>) order.get("orderItems"))).hasSize(2);
    }
    
    /**
     * Test order placement with different payment methods
     */
    @Test
    @Order(3)
    @DisplayName("Order with Different Payment Methods")
    void testOrderWithDifferentPaymentMethods() {
        authenticateUser("test@example.com", "password123");
        
        String[] paymentMethods = {"CREDIT_CARD", "DEBIT_CARD", "DIGITAL_WALLET"};
        
        for (String paymentMethod : paymentMethods) {
            Map<String, Object> orderRequest = createOrderRequest();
            Map<String, Object> order = createOrder(orderRequest);
            Long orderId = ((Number) order.get("id")).longValue();
            
            Map<String, Object> paymentResponse = processPayment(orderId, paymentMethod);
            assertThat(paymentResponse.get("status")).isEqualTo("SUCCESS");
            assertThat(paymentResponse.get("paymentMethod")).isEqualTo(paymentMethod);
        }
    }
    
    /**
     * Test order modifications before payment
     */
    @Test
    @Order(4)
    @DisplayName("Order Modifications Before Payment")
    void testOrderModificationsBeforePayment() {
        authenticateUser("test@example.com", "password123");
        
        // Create initial order
        Map<String, Object> orderRequest = createOrderRequest();
        Map<String, Object> order = createOrder(orderRequest);
        Long orderId = ((Number) order.get("id")).longValue();
        
        // Modify order (add more items)
        Map<String, Object> updatedOrder = Map.of(
            "userId", testUserId,
            "restaurantId", testRestaurantId,
            "deliveryAddress", "123 Updated Street",
            "items", List.of(
                Map.of("menuItemId", testMenuItemId1, "quantity", 3, "price", new BigDecimal("15.99")),
                Map.of("menuItemId", testMenuItemId2, "quantity", 2, "price", new BigDecimal("12.50"))
            )
        );
        
        ResponseEntity<Map> response = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders/" + orderId,
            HttpMethod.PUT,
            new HttpEntity<>(updatedOrder, createHeaders()),
            Map.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> modified = response.getBody();
        assertThat(modified.get("deliveryAddress")).isEqualTo("123 Updated Street");
    }
    
    /**
     * Test repeat orders from order history
     */
    @Test
    @Order(5)
    @DisplayName("Repeat Orders from Order History")
    void testRepeatOrdersFromHistory() {
        authenticateUser("test@example.com", "password123");
        
        // Create and complete first order
        Map<String, Object> orderRequest = createOrderRequest();
        Map<String, Object> firstOrder = createOrder(orderRequest);
        Long firstOrderId = ((Number) firstOrder.get("id")).longValue();
        processPayment(firstOrderId, "CREDIT_CARD");
        confirmOrder(firstOrderId);
        
        // Get user's order history
        ResponseEntity<List> historyResponse = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders/user/" + testUserId,
            HttpMethod.GET,
            new HttpEntity<>(createHeaders()),
            List.class
        );
        
        assertThat(historyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Map<String, Object>> orderHistory = historyResponse.getBody();
        assertThat(orderHistory).isNotEmpty();
        
        // Repeat the first order
        Map<String, Object> repeatOrder = createOrder(orderRequest);
        assertThat(repeatOrder.get("restaurantId")).isEqualTo(firstOrder.get("restaurantId"));
        assertThat(repeatOrder.get("totalAmount")).isEqualTo(firstOrder.get("totalAmount"));
    }
    
    // ==================== NEGATIVE SCENARIOS ====================
    
    /**
     * Test authentication failures with invalid credentials
     */
    @Test
    @Order(10)
    @DisplayName("Authentication Failures - Invalid Credentials")
    void testAuthenticationFailuresInvalidCredentials() {
        // Test invalid email
        Map<String, Object> invalidEmailResponse = authenticateUserExpectingFailure(
            "invalid@example.com", "password123", HttpStatus.UNAUTHORIZED);
        assertThat(invalidEmailResponse.get("success")).isEqualTo(false);
        assertThat(invalidEmailResponse.get("message")).isEqualTo("Invalid email or password");
        
        // Test invalid password
        Map<String, Object> invalidPasswordResponse = authenticateUserExpectingFailure(
            "test@example.com", "wrongpassword", HttpStatus.UNAUTHORIZED);
        assertThat(invalidPasswordResponse.get("success")).isEqualTo(false);
        
        // Test missing credentials
        Map<String, Object> missingCredsResponse = authenticateUserExpectingFailure(
            null, null, HttpStatus.BAD_REQUEST);
        assertThat(missingCredsResponse.get("success")).isEqualTo(false);
    }
    
    /**
     * Test expired token scenarios
     */
    @Test
    @Order(11)
    @DisplayName("Authentication Failures - Expired Tokens")
    void testExpiredTokenScenarios() {
        // Simulate expired token by using invalid authorization header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer expired_token_12345");
        
        Map<String, Object> orderRequest = createOrderRequest();
        
        ResponseEntity<Map> response = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders",
            HttpMethod.POST,
            new HttpEntity<>(orderRequest, headers),
            Map.class
        );
        
        // Should fail due to invalid/expired token
        assertThat(response.getStatusCode()).isIn(HttpStatus.UNAUTHORIZED, HttpStatus.FORBIDDEN);
    }
    
    /**
     * Test restaurant unavailable/closed scenarios
     */
    @Test
    @Order(12)
    @DisplayName("Restaurant Unavailable During Order Placement")
    void testRestaurantUnavailableDuringOrderPlacement() {
        authenticateUser("test@example.com", "password123");

        // Mock restaurant as closed/unavailable
        stubFor(get(urlMatching("/api/restaurants/\\d+"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Restaurant not found or closed\"}"))); // Fixed missing closing parenthesis
        
        Map<String, Object> orderRequest = createOrderRequest();
        
        ResponseEntity<Map> response = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders",
            HttpMethod.POST,
            new HttpEntity<>(orderRequest, createHeaders()),
            Map.class
        );
        
        assertThat(response.getStatusCode()).isIn(HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Test menu items out of stock scenarios
     */
    @Test
    @Order(13)
    @DisplayName("Menu Items Out of Stock")
    void testMenuItemsOutOfStock() {
        authenticateUser("test@example.com", "password123");
        
        // Mock menu items as out of stock
        stubFor(get(urlMatching("/api/restaurants/\\d+/menu"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createOutOfStockMenuResponse())));
        
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
     * Test invalid payment information scenarios
     */
    @Test
    @Order(14)
    @DisplayName("Invalid Payment Information")
    void testInvalidPaymentInformation() {
        authenticateUser("test@example.com", "password123");
        
        Map<String, Object> orderRequest = createOrderRequest();
        Map<String, Object> order = createOrder(orderRequest);
        Long orderId = ((Number) order.get("id")).longValue();
        
        // Mock payment failure due to invalid payment info
        stubFor(post(urlEqualTo("/api/payments/process"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createPaymentFailureResponse("Invalid card number"))));
        
        Map<String, Object> paymentRequest = Map.of(
            "orderId", orderId,
            "amount", new BigDecimal("28.49"),
            "paymentMethod", "CREDIT_CARD",
            "cardNumber", "1234567890123456", // Invalid card
            "expiryDate", "12/25",
            "cvv", "123"
        );
        
        ResponseEntity<Map> response = restTemplate.exchange(
            PAYMENT_SERVICE_URL + "/api/payments/process",
            HttpMethod.POST,
            new HttpEntity<>(paymentRequest, createHeaders()),
            Map.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, Object> paymentResponse = response.getBody();
        assertThat(paymentResponse.get("status")).isEqualTo("FAILED");
    }
    
    /**
     * Test payment processing failures
     */
    @Test
    @Order(15)
    @DisplayName("Payment Processing Failures")
    void testPaymentProcessingFailures() {
        authenticateUser("test@example.com", "password123");
        
        Map<String, Object> orderRequest = createOrderRequest();
        Map<String, Object> order = createOrder(orderRequest);
        Long orderId = ((Number) order.get("id")).longValue();
        
        // Mock payment service timeout
        stubFor(post(urlEqualTo("/api/payments/process"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withFixedDelay(30000) // 30 second delay to simulate timeout
                        .withHeader("Content-Type", "application/json")
                        .withBody(createPaymentFailureResponse("Payment service timeout"))));
        
        Map<String, Object> paymentRequest = Map.of(
            "orderId", orderId,
            "amount", new BigDecimal("28.49"),
            "paymentMethod", "CREDIT_CARD"
        );
        
        ResponseEntity<Map> response = restTemplate.exchange(
            PAYMENT_SERVICE_URL + "/api/payments/process",
            HttpMethod.POST,
            new HttpEntity<>(paymentRequest, createHeaders()),
            Map.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Test network timeout scenarios between services
     */
    @Test
    @Order(16)
    @DisplayName("Network Timeouts Between Services")
    void testNetworkTimeoutsBetweenServices() {
        authenticateUser("test@example.com", "password123");
        
        // Mock restaurant service with delay
        stubFor(get(urlMatching("/api/restaurants/\\d+"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay(35000) // 35 second delay
                        .withHeader("Content-Type", "application/json")
                        .withBody(createRestaurantResponse())));
        
        Map<String, Object> orderRequest = createOrderRequest();
        
        // This should timeout or handle the delay gracefully
        assertThatThrownBy(() -> {
            restTemplate.exchange(
                ORDER_SERVICE_URL + "/api/orders",
                HttpMethod.POST,
                new HttpEntity<>(orderRequest, createHeaders()),
                Map.class
            );
        }).isInstanceOf(Exception.class);
    }
    
    /**
     * Test service unavailability scenarios
     */
    @Test
    @Order(17)
    @DisplayName("Service Unavailability Scenarios")
    void testServiceUnavailabilityScenarios() {
        authenticateUser("test@example.com", "password123");
        
        // Mock payment service as completely unavailable
        stubFor(post(urlEqualTo("/api/payments/process"))
                .willReturn(aResponse()
                        .withStatus(503)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Payment service unavailable\"}")));
        
        Map<String, Object> orderRequest = createOrderRequest();
        Map<String, Object> order = createOrder(orderRequest);
        Long orderId = ((Number) order.get("id")).longValue();
        
        Map<String, Object> paymentRequest = Map.of(
            "orderId", orderId,
            "amount", new BigDecimal("28.49"),
            "paymentMethod", "CREDIT_CARD"
        );
        
        ResponseEntity<Map> response = restTemplate.exchange(
            PAYMENT_SERVICE_URL + "/api/payments/process",
            HttpMethod.POST,
            new HttpEntity<>(paymentRequest, createHeaders()),
            Map.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    // ==================== EDGE CASES ====================
    
    /**
     * Test empty cart validation
     */
    @Test
    @Order(20)
    @DisplayName("Empty Cart Validation")
    void testEmptyCartValidation() {
        authenticateUser("test@example.com", "password123");
        
        Map<String, Object> emptyOrderRequest = Map.of(
            "userId", testUserId,
            "restaurantId", testRestaurantId,
            "deliveryAddress", "123 Test Street",
            "items", Collections.emptyList()
        );
        
        ResponseEntity<Map> response = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders",
            HttpMethod.POST,
            new HttpEntity<>(emptyOrderRequest, createHeaders()),
            Map.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, Object> errorResponse = response.getBody();
        assertThat(errorResponse.get("message")).asString().contains("empty");
    }
    
    /**
     * Test maximum order quantity limits
     */
    @Test
    @Order(21)
    @DisplayName("Maximum Order Quantity Limits")
    void testMaximumOrderQuantityLimits() {
        authenticateUser("test@example.com", "password123");
        
        Map<String, Object> largeOrderRequest = Map.of(
            "userId", testUserId,
            "restaurantId", testRestaurantId,
            "deliveryAddress", "123 Test Street",
            "items", List.of(
                Map.of("menuItemId", testMenuItemId1, "quantity", 999, "price", new BigDecimal("15.99"))
            )
        );
        
        ResponseEntity<Map> response = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders",
            HttpMethod.POST,
            new HttpEntity<>(largeOrderRequest, createHeaders()),
            Map.class
        );
        
        assertThat(response.getStatusCode()).isIn(HttpStatus.BAD_REQUEST, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    
    /**
     * Test minimum order amount requirements
     */
    @Test
    @Order(22)
    @DisplayName("Minimum Order Amount Requirements")
    void testMinimumOrderAmountRequirements() {
        authenticateUser("test@example.com", "password123");
        
        Map<String, Object> smallOrderRequest = Map.of(
            "userId", testUserId,
            "restaurantId", testRestaurantId,
            "deliveryAddress", "123 Test Street",
            "items", List.of(
                Map.of("menuItemId", testMenuItemId1, "quantity", 1, "price", new BigDecimal("0.50"))
            )
        );
        
        ResponseEntity<Map> response = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders",
            HttpMethod.POST,
            new HttpEntity<>(smallOrderRequest, createHeaders()),
            Map.class
        );
        
        // Should either succeed or fail with minimum amount validation
        if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
            Map<String, Object> errorResponse = response.getBody();
            assertThat(errorResponse.get("message")).asString().contains("minimum");
        }
    }
    
    /**
     * Test delivery address validation
     */
    @Test
    @Order(23)
    @DisplayName("Delivery Address Validation")
    void testDeliveryAddressValidation() {
        authenticateUser("test@example.com", "password123");
        
        // Test with invalid/empty address
        Map<String, Object> invalidAddressRequest = Map.of(
            "userId", testUserId,
            "restaurantId", testRestaurantId,
            "deliveryAddress", "",
            "items", List.of(
                Map.of("menuItemId", testMenuItemId1, "quantity", 1, "price", new BigDecimal("15.99"))
            )
        );
        
        ResponseEntity<Map> response = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders",
            HttpMethod.POST,
            new HttpEntity<>(invalidAddressRequest, createHeaders()),
            Map.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Test concurrent order placement by same user
     */
    @Test
    @Order(24)
    @DisplayName("Concurrent Order Placement by Same User")
    void testConcurrentOrderPlacementBySameUser() throws InterruptedException {
        authenticateUser("test@example.com", "password123");
        
        List<Thread> threads = new ArrayList<>();
        List<ResponseEntity<Map>> responses = Collections.synchronizedList(new ArrayList<>());
        
        // Create multiple threads to place orders concurrently
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread(() -> {
                try {
                    Map<String, Object> orderRequest = createOrderRequest();
                    ResponseEntity<Map> response = restTemplate.exchange(
                        ORDER_SERVICE_URL + "/api/orders",
                        HttpMethod.POST,
                        new HttpEntity<>(orderRequest, createHeaders()),
                        Map.class
                    );
                    responses.add(response);
                } catch (Exception e) {
                    // Handle exceptions in concurrent execution
                }
            });
            threads.add(thread);
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join(10000); // 10 second timeout
        }
        
        // Verify that all orders were processed (or properly rejected)
        assertThat(responses).isNotEmpty();
        for (ResponseEntity<Map> response : responses) {
            assertThat(response.getStatusCode()).isIn(
                HttpStatus.CREATED, HttpStatus.BAD_REQUEST, HttpStatus.CONFLICT
            );
        }
    }
    
    /**
     * Test order cancellation during processing
     */
    @Test
    @Order(25)
    @DisplayName("Order Cancellation During Processing")
    void testOrderCancellationDuringProcessing() {
        authenticateUser("test@example.com", "password123");
        
        Map<String, Object> orderRequest = createOrderRequest();
        Map<String, Object> order = createOrder(orderRequest);
        Long orderId = ((Number) order.get("id")).longValue();
        
        // Try to cancel the order immediately
        ResponseEntity<Map> cancelResponse = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders/" + orderId + "/cancel",
            HttpMethod.POST,
            new HttpEntity<>(createHeaders()),
            Map.class
        );
        
        assertThat(cancelResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> cancelledOrder = cancelResponse.getBody();
        assertThat(cancelledOrder.get("status")).isEqualTo("CANCELLED");
    }

    /**
     * Test partial payment failures
     */
    @Test
    @Order(26)
    @DisplayName("Partial Payment Failures")
    void testPartialPaymentFailures() {
        authenticateUser("test@example.com", "password123");

        Map<String, Object> orderRequest = createOrderRequest();
        Map<String, Object> order = createOrder(orderRequest);
        Long orderId = ((Number) order.get("id")).longValue();

        // Mock partial payment failure
        stubFor(post(urlEqualTo("/api/payments/process"))
                .willReturn(aResponse()
                        .withStatus(402)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createPartialPaymentFailureResponse())));

        Map<String, Object> paymentRequest = Map.of(
                "orderId", orderId,
                "amount", new BigDecimal("28.49"),
                "paymentMethod", "CREDIT_CARD"
        );

        ResponseEntity<Map> response = restTemplate.exchange(
                PAYMENT_SERVICE_URL + "/api/payments/process",
                HttpMethod.POST,
                new HttpEntity<>(paymentRequest, createHeaders()),
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PAYMENT_REQUIRED);
        Map<String, Object> paymentResponse = response.getBody();
        assertThat(paymentResponse.get("status")).isEqualTo("PARTIAL_FAILURE");
    }

    /**
     * Test order workflow with loyalty points redemption
     */
    @Test
    @Order(27)
    @DisplayName("Order with Loyalty Points Redemption")
    void testOrderWithLoyaltyPointsRedemption() {
        authenticateUser("test@example.com", "password123");

        Map<String, Object> orderRequest = Map.of(
                "userId", testUserId,
                "restaurantId", testRestaurantId,
                "deliveryAddress", "123 Test Street",
                "items", List.of(
                        Map.of("menuItemId", testMenuItemId1, "quantity", 1, "price", new BigDecimal("25.99"))
                ),
                "loyaltyPointsToRedeem", 500,
                "discountAmount", new BigDecimal("5.00")
        );

        Map<String, Object> order = createOrder(orderRequest);
        assertThat(order.get("totalAmount")).isEqualTo(20.99); // 25.99 - 5.00

        Long orderId = ((Number) order.get("id")).longValue();
        Map<String, Object> paymentResponse = processPayment(orderId, "CREDIT_CARD");
        assertThat(paymentResponse.get("status")).isEqualTo("SUCCESS");
    }

    /**
     * Test order workflow with promo codes
     */
    @Test
    @Order(28)
    @DisplayName("Order with Promo Code Application")
    void testOrderWithPromoCodeApplication() {
        authenticateUser("test@example.com", "password123");

        Map<String, Object> orderRequest = Map.of(
                "userId", testUserId,
                "restaurantId", testRestaurantId,
                "deliveryAddress", "123 Test Street",
                "items", List.of(
                        Map.of("menuItemId", testMenuItemId1, "quantity", 1, "price", new BigDecimal("30.00"))
                ),
                "promoCode", "SAVE20",
                "discountPercentage", 20
        );

        Map<String, Object> order = createOrder(orderRequest);
        assertThat(order.get("totalAmount")).isEqualTo(24.00); // 30.00 - 20%

        Long orderId = ((Number) order.get("id")).longValue();
        Map<String, Object> paymentResponse = processPayment(orderId, "DIGITAL_WALLET");
        assertThat(paymentResponse.get("status")).isEqualTo("SUCCESS");
    }

    /**
     * Test order workflow with scheduled delivery
     */
    @Test
    @Order(29)
    @DisplayName("Order with Scheduled Delivery")
    void testOrderWithScheduledDelivery() {
        authenticateUser("test@example.com", "password123");

        LocalDateTime scheduledTime = LocalDateTime.now().plusHours(2);
        Map<String, Object> orderRequest = Map.of(
                "userId", testUserId,
                "restaurantId", testRestaurantId,
                "deliveryAddress", "123 Test Street",
                "items", List.of(
                        Map.of("menuItemId", testMenuItemId1, "quantity", 1, "price", new BigDecimal("18.50"))
                ),
                "scheduledDeliveryTime", scheduledTime.toString(),
                "deliveryType", "SCHEDULED"
        );

        Map<String, Object> order = createOrder(orderRequest);
        assertThat(order.get("deliveryType")).isEqualTo("SCHEDULED");
        assertThat(order.get("scheduledDeliveryTime")).isNotNull();

        Long orderId = ((Number) order.get("id")).longValue();
        Map<String, Object> paymentResponse = processPayment(orderId, "CREDIT_CARD");
        assertThat(paymentResponse.get("status")).isEqualTo("SUCCESS");
    }

    /**
     * Test order workflow with delivery instructions
     */
    @Test
    @Order(30)
    @DisplayName("Order with Special Delivery Instructions")
    void testOrderWithSpecialDeliveryInstructions() {
        authenticateUser("test@example.com", "password123");

        Map<String, Object> orderRequest = Map.of(
                "userId", testUserId,
                "restaurantId", testRestaurantId,
                "deliveryAddress", "123 Test Street, Apt 4B",
                "items", List.of(
                        Map.of("menuItemId", testMenuItemId1, "quantity", 1, "price", new BigDecimal("22.00"))
                ),
                "deliveryInstructions", "Ring doorbell twice, leave at door if no answer",
                "contactlessDelivery", true
        );

        Map<String, Object> order = createOrder(orderRequest);
        assertThat(order.get("deliveryInstructions")).isEqualTo("Ring doorbell twice, leave at door if no answer");
        assertThat(order.get("contactlessDelivery")).isEqualTo(true);
    }
    
    // ==================== INTEGRATION POINTS ====================
    
    /**
     * Test User-service ↔ Order-service authentication validation
     */
    @Test
    @Order(30)
    @DisplayName("User-Service to Order-Service Authentication Validation")
    void testUserServiceToOrderServiceAuthValidation() {
        // Test with valid authentication
        Map<String, Object> loginResponse = authenticateUser("test@example.com", "password123");
        assertThat(loginResponse.get("success")).isEqualTo(true);
        
        Map<String, Object> orderRequest = createOrderRequest();
        ResponseEntity<Map> validResponse = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders",
            HttpMethod.POST,
            new HttpEntity<>(orderRequest, createHeaders()),
            Map.class
        );
        assertThat(validResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        // Test with invalid authentication
        HttpHeaders invalidHeaders = new HttpHeaders();
        invalidHeaders.setContentType(MediaType.APPLICATION_JSON);
        invalidHeaders.set("Authorization", "Bearer invalid_token");
        
        ResponseEntity<Map> invalidResponse = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders",
            HttpMethod.POST,
            new HttpEntity<>(orderRequest, invalidHeaders),
            Map.class
        );
        assertThat(invalidResponse.getStatusCode()).isIn(HttpStatus.UNAUTHORIZED, HttpStatus.FORBIDDEN);
    }
    
    /**
     * Test Restaurant-service ↔ Order-service menu item availability
     */
    @Test
    @Order(31)
    @DisplayName("Restaurant-Service to Order-Service Menu Availability")
    void testRestaurantServiceToOrderServiceMenuAvailability() {
        authenticateUser("test@example.com", "password123");
        
        // First verify menu items are available
        ResponseEntity<List> menuResponse = restTemplate.exchange(
            RESTAURANT_SERVICE_URL + "/api/restaurants/" + testRestaurantId + "/menu",
            HttpMethod.GET,
            new HttpEntity<>(createHeaders()),
            List.class
        );
        assertThat(menuResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // Then place order with those menu items
        Map<String, Object> orderRequest = createOrderRequest();
        ResponseEntity<Map> orderResponse = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders",
            HttpMethod.POST,
            new HttpEntity<>(orderRequest, createHeaders()),
            Map.class
        );
        assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        // Verify order contains the correct menu items
        Map<String, Object> order = orderResponse.getBody();
        List<Map<String, Object>> orderItems = (List<Map<String, Object>>) order.get("orderItems");
        assertThat(orderItems).isNotEmpty();
        assertThat(orderItems.get(0).get("menuItemId")).isEqualTo(testMenuItemId1);
    }
    
    /**
     * Test Order-service ↔ Payment-service transaction processing
     */
    @Test
    @Order(32)
    @DisplayName("Order-Service to Payment-Service Transaction Processing")
    void testOrderServiceToPaymentServiceTransactionProcessing() {
        authenticateUser("test@example.com", "password123");
        
        // Create order
        Map<String, Object> orderRequest = createOrderRequest();
        Map<String, Object> order = createOrder(orderRequest);
        Long orderId = ((Number) order.get("id")).longValue();
        
        // Process payment
        Map<String, Object> paymentResponse = processPayment(orderId, "CREDIT_CARD");
        assertThat(paymentResponse.get("status")).isEqualTo("SUCCESS");
        assertThat(paymentResponse.get("orderId")).isEqualTo(orderId.intValue());
        
        // Verify payment was recorded
        ResponseEntity<List> paymentsResponse = restTemplate.exchange(
            PAYMENT_SERVICE_URL + "/api/payments/order/" + orderId,
            HttpMethod.GET,
            new HttpEntity<>(createHeaders()),
            List.class
        );
        assertThat(paymentsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Map<String, Object>> payments = paymentsResponse.getBody();
        assertThat(payments).isNotEmpty();
    }
    
    /**
     * Test Order-service ↔ Notification-service order updates
     */
    @Test
    @Order(33)
    @DisplayName("Order-Service to Notification-Service Order Updates")
    void testOrderServiceToNotificationServiceOrderUpdates() {
        authenticateUser("test@example.com", "password123");
        
        // Create and process order
        Map<String, Object> orderRequest = createOrderRequest();
        Map<String, Object> order = createOrder(orderRequest);
        Long orderId = ((Number) order.get("id")).longValue();
        
        processPayment(orderId, "CREDIT_CARD");
        confirmOrder(orderId);
        
        // Verify notifications were sent
        await().atMost(15, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    verify(postRequestedFor(urlEqualTo("/api/notifications"))
                            .withRequestBody(containing("Order")));
                });
        
        // Update order status and verify notification
        Map<String, Object> statusUpdate = Map.of("status", "PREPARING");
        ResponseEntity<Map> updateResponse = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders/" + orderId + "/status",
            HttpMethod.PUT,
            new HttpEntity<>(statusUpdate, createHeaders()),
            Map.class
        );
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // Verify status update notification
        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    verify(moreThan(1), postRequestedFor(urlEqualTo("/api/notifications")));
                });
    }
    
    /**
     * Test API Gateway routing and load balancing
     */
    @Test
    @Order(34)
    @DisplayName("API Gateway Routing and Load Balancing")
    void testApiGatewayRoutingAndLoadBalancing() {
        // Test routing through API Gateway
        String gatewayUrl = "http://localhost:8080";
        
        // Test user service routing
        Map<String, Object> loginRequest = Map.of(
            "email", "test@example.com",
            "password", "password123"
        );
        
        ResponseEntity<Map> authResponse = restTemplate.exchange(
            gatewayUrl + "/user-service/api/auth/login",
            HttpMethod.POST,
            new HttpEntity<>(loginRequest, createHeaders()),
            Map.class
        );
        
        // Should route correctly to user service
        assertThat(authResponse.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.SERVICE_UNAVAILABLE);
        
        // Test order service routing
        if (authResponse.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> orderRequest = createOrderRequest();
            ResponseEntity<Map> orderResponse = restTemplate.exchange(
                gatewayUrl + "/order-service/api/orders",
                HttpMethod.POST,
                new HttpEntity<>(orderRequest, createHeaders()),
                Map.class
            );
            
            assertThat(orderResponse.getStatusCode()).isIn(
                HttpStatus.CREATED, HttpStatus.SERVICE_UNAVAILABLE, HttpStatus.BAD_GATEWAY
            );
        }
    }
    
    /**
     * Test database consistency across services
     */
    @Test
    @Order(35)
    @DisplayName("Database Consistency Across Services")
    void testDatabaseConsistencyAcrossServices() {
        authenticateUser("test@example.com", "password123");
        
        // Create order and verify it's consistent across services
        Map<String, Object> orderRequest = createOrderRequest();
        Map<String, Object> order = createOrder(orderRequest);
        Long orderId = ((Number) order.get("id")).longValue();
        
        // Process payment
        Map<String, Object> paymentResponse = processPayment(orderId, "CREDIT_CARD");
        assertThat(paymentResponse.get("status")).isEqualTo("SUCCESS");
        
        // Verify order status in order service
        ResponseEntity<Map> orderStatusResponse = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders/" + orderId,
            HttpMethod.GET,
            new HttpEntity<>(createHeaders()),
            Map.class
        );
        assertThat(orderStatusResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // Verify payment record in payment service
        ResponseEntity<List> paymentStatusResponse = restTemplate.exchange(
            PAYMENT_SERVICE_URL + "/api/payments/order/" + orderId,
            HttpMethod.GET,
            new HttpEntity<>(createHeaders()),
            List.class
        );
        assertThat(paymentStatusResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // Verify data consistency
        Map<String, Object> orderData = orderStatusResponse.getBody();
        List<Map<String, Object>> paymentData = paymentStatusResponse.getBody();
        
        assertThat(orderData.get("id")).isEqualTo(orderId.intValue());
        assertThat(paymentData).isNotEmpty();
        assertThat(paymentData.get(0).get("orderId")).isEqualTo(orderId.intValue());
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
    
    private Map<String, Object> authenticateUserExpectingFailure(String email, String password, HttpStatus expectedStatus) {
        Map<String, Object> loginRequest = new HashMap<>();
        if (email != null) loginRequest.put("email", email);
        if (password != null) loginRequest.put("password", password);
        
        ResponseEntity<Map> response = restTemplate.exchange(
            USER_SERVICE_URL + "/api/auth/login",
            HttpMethod.POST,
            new HttpEntity<>(loginRequest, createHeaders()),
            Map.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
        return response.getBody();
    }
    
    private List<Map<String, Object>> browseRestaurants() {
        ResponseEntity<List> response = restTemplate.exchange(
            RESTAURANT_SERVICE_URL + "/api/restaurants",
            HttpMethod.GET,
            new HttpEntity<>(createHeaders()),
            List.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }
    
    private List<Map<String, Object>> browseMenuItems(Long restaurantId) {
        ResponseEntity<List> response = restTemplate.exchange(
            RESTAURANT_SERVICE_URL + "/api/restaurants/" + restaurantId + "/menu",
            HttpMethod.GET,
            new HttpEntity<>(createHeaders()),
            List.class
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
    
    private String createSuccessResponse(Map<String, Object> user) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.putAll(user);
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            return "{\"success\": true, \"message\": \"User registered successfully\"}";
        }
    }
    
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
        List<Map<String, Object>> restaurants = List.of(testRestaurant);
        try {
            return objectMapper.writeValueAsString(restaurants);
        } catch (Exception e) {
            return "[{\"id\": 1, \"name\": \"Test Restaurant\"}]";
        }
    }
    
    private String createRestaurantResponse() {
        try {
            return objectMapper.writeValueAsString(testRestaurant);
        } catch (Exception e) {
            return "{\"id\": 1, \"name\": \"Test Restaurant\"}";
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
    
    private String createOutOfStockMenuResponse() {
        List<Map<String, Object>> menuItems = List.of(
            Map.of(
                "id", testMenuItemId1,
                "name", "Pizza Margherita",
                "price", new BigDecimal("15.99"),
                "available", false
            ),
            Map.of(
                "id", testMenuItemId2,
                "name", "Caesar Salad",
                "price", new BigDecimal("12.50"),
                "available", false
            )
        );
        
        try {
            return objectMapper.writeValueAsString(menuItems);
        } catch (Exception e) {
            return "[{\"id\": 1, \"available\": false}]";
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
    
    private String createPartialPaymentFailureResponse() {
        Map<String, Object> response = Map.of(
            "status", "PARTIAL_FAILURE",
            "error", "Insufficient funds",
            "partialAmount", new BigDecimal("10.00"),
            "remainingAmount", new BigDecimal("18.49"),
            "timestamp", LocalDateTime.now().toString()
        );
        
        try {
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            return "{\"status\": \"PARTIAL_FAILURE\", \"error\": \"Insufficient funds\"}";
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

    private String createLimitedInventoryMenuResponse() {
        List<Map<String, Object>> menuItems = List.of(
                Map.of(
                        "id", testMenuItemId1,
                        "name", "Pizza Margherita",
                        "description", "Classic pizza with tomato and mozzarella",
                        "price", new BigDecimal("15.99"),
                        "available", true,
                        "category", "Main Course",
                        "inventoryCount", 3 // Limited inventory
                ),
                Map.of(
                        "id", testMenuItemId2,
                        "name", "Caesar Salad",
                        "description", "Fresh romaine lettuce with Caesar dressing",
                        "price", new BigDecimal("12.50"),
                        "available", true,
                        "category", "Salad",
                        "inventoryCount", 10
                )
        );

        try {
            return objectMapper.writeValueAsString(menuItems);
        } catch (Exception e) {
            return "[{\"id\": 1, \"name\": \"Pizza\", \"price\": 15.99, \"inventoryCount\": 3}]";
        }
    }
}