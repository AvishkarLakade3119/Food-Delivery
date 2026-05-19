package com.fooddelivery.e2e.performance;

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
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

/**
 * Performance and Load Testing for Food Delivery Platform
 * 
 * This test class focuses on performance, scalability, and load testing scenarios:
 * 
 * 1. Concurrent Order Placement
 * 2. High-Volume Transaction Processing
 * 3. Database Connection Pool Testing
 * 4. Service Response Time Validation
 * 5. Memory and Resource Usage Monitoring
 * 6. Circuit Breaker and Rate Limiting
 * 7. Stress Testing Under Load
 * 8. Performance Regression Testing
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("e2e-test")
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PerformanceAndLoadE2ETest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("food_delivery_performance")
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
    
    // Performance thresholds
    private static final long MAX_RESPONSE_TIME_MS = 5000; // 5 seconds
    private static final long MAX_ORDER_CREATION_TIME_MS = 3000; // 3 seconds
    private static final long MAX_PAYMENT_PROCESSING_TIME_MS = 10000; // 10 seconds
    private static final int CONCURRENT_USERS = 50;
    private static final int LOAD_TEST_DURATION_SECONDS = 30;
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        
        // Performance tuning properties
        registry.add("spring.datasource.hikari.maximum-pool-size", () -> "20");
        registry.add("spring.datasource.hikari.minimum-idle", () -> "5");
        registry.add("spring.datasource.hikari.connection-timeout", () -> "20000");
        registry.add("spring.datasource.hikari.idle-timeout", () -> "300000");
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
        setupPerformanceStubs();
    }
    
    /**
     * Setup WireMock stubs optimized for performance testing
     */
    private void setupPerformanceStubs() {
        // Fast response stubs for performance testing
        stubFor(post(urlEqualTo("/api/auth/login"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createFastLoginResponse())
                        .withFixedDelay(100))); // 100ms delay
        
        stubFor(get(urlEqualTo("/api/restaurants"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createFastRestaurantsResponse())
                        .withFixedDelay(50))); // 50ms delay
        
        stubFor(get(urlMatching("/api/restaurants/\\d+/menu"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createFastMenuResponse())
                        .withFixedDelay(75))); // 75ms delay
        
        stubFor(post(urlEqualTo("/api/payments/process"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createFastPaymentResponse())
                        .withFixedDelay(200))); // 200ms delay
        
        stubFor(post(urlEqualTo("/api/notifications"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createFastNotificationResponse())
                        .withFixedDelay(50))); // 50ms delay
    }
    
    // ==================== CONCURRENT ORDER PLACEMENT TESTS ====================
    
    /**
     * Test concurrent order placement by multiple users
     */
    @Test
    @Order(1)
    @DisplayName("Concurrent Order Placement by Multiple Users")
    void testConcurrentOrderPlacementByMultipleUsers() throws InterruptedException {
        int numberOfConcurrentOrders = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfConcurrentOrders);
        CountDownLatch latch = new CountDownLatch(numberOfConcurrentOrders);
        
        List<Future<OrderResult>> futures = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicLong totalResponseTime = new AtomicLong(0);
        
        // Submit concurrent order creation tasks
        for (int i = 0; i < numberOfConcurrentOrders; i++) {
            final int userId = i + 1;
            Future<OrderResult> future = executorService.submit(() -> {
                try {
                    Instant start = Instant.now();
                    
                    // Authenticate user
                    authenticateUser("user" + userId + "@example.com", "password123");

                    // Create order
                    Map<String, Object> orderRequest = createOrderRequest((long) userId, 1L);
                    ResponseEntity<Map> response = restTemplate.exchange(
                        ORDER_SERVICE_URL + "/api/orders",
                        HttpMethod.POST,
                        new HttpEntity<>(orderRequest, createHeaders()),
                        Map.class
                    );
                    
                    Instant end = Instant.now();
                    long responseTime = Duration.between(start, end).toMillis();
                    
                    OrderResult result = new OrderResult();
                    result.success = response.getStatusCode() == HttpStatus.CREATED;
                    result.responseTime = responseTime;
                    result.orderId = result.success ? 
                        ((Number) response.getBody().get("id")).longValue() : null;
                    
                    if (result.success) {
                        successCount.incrementAndGet();
                    } else {
                        failureCount.incrementAndGet();
                    }
                    
                    totalResponseTime.addAndGet(responseTime);
                    
                    return result;
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    OrderResult result = new OrderResult();
                    result.success = false;
                    result.error = e.getMessage();
                    return result;
                } finally {
                    latch.countDown();
                }
            });
            futures.add(future);
        }
        
        // Wait for all tasks to complete
        boolean completed = latch.await(60, TimeUnit.SECONDS);
        assertThat(completed).isTrue();
        
        executorService.shutdown();
        
        // Analyze results
        long averageResponseTime = totalResponseTime.get() / numberOfConcurrentOrders;
        double successRate = (double) successCount.get() / numberOfConcurrentOrders * 100;
        
        System.out.println("=== Concurrent Order Placement Results ===");
        System.out.println("Total Orders: " + numberOfConcurrentOrders);
        System.out.println("Successful Orders: " + successCount.get());
        System.out.println("Failed Orders: " + failureCount.get());
        System.out.println("Success Rate: " + String.format("%.2f%%", successRate));
        System.out.println("Average Response Time: " + averageResponseTime + "ms");
        
        // Assertions
        assertThat(successRate).isGreaterThanOrEqualTo(80.0); // At least 80% success rate
        assertThat(averageResponseTime).isLessThanOrEqualTo(MAX_ORDER_CREATION_TIME_MS);
        assertThat(successCount.get()).isGreaterThan(0);
    }
    
    /**
     * Test high-volume order processing with payment
     */
    @Test
    @Order(2)
    @DisplayName("High-Volume Order Processing with Payment")
    void testHighVolumeOrderProcessingWithPayment() throws InterruptedException {
        int numberOfOrders = 15;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfOrders);
        CountDownLatch latch = new CountDownLatch(numberOfOrders);
        
        AtomicInteger completedOrders = new AtomicInteger(0);
        AtomicLong totalProcessingTime = new AtomicLong(0);
        
        for (int i = 0; i < numberOfOrders; i++) {
            final int orderIndex = i;
            executorService.submit(() -> {
                try {
                    Instant start = Instant.now();
                    
                    // Complete order workflow
                    authenticateUser("user" + orderIndex + "@example.com", "password123");

                    Map<String, Object> orderRequest = createOrderRequest((long) (orderIndex + 1), 1L);
                    ResponseEntity<Map> orderResponse = restTemplate.exchange(
                        ORDER_SERVICE_URL + "/api/orders",
                        HttpMethod.POST,
                        new HttpEntity<>(orderRequest, createHeaders()),
                        Map.class
                    );
                    
                    if (orderResponse.getStatusCode() == HttpStatus.CREATED) {
                        Long orderId = ((Number) orderResponse.getBody().get("id")).longValue();
                        
                        // Process payment
                        Map<String, Object> paymentRequest = createPaymentRequest(orderId);
                        ResponseEntity<Map> paymentResponse = restTemplate.exchange(
                            PAYMENT_SERVICE_URL + "/api/payments/process",
                            HttpMethod.POST,
                            new HttpEntity<>(paymentRequest, createHeaders()),
                            Map.class
                        );
                        
                        if (paymentResponse.getStatusCode() == HttpStatus.OK) {
                            completedOrders.incrementAndGet();
                        }
                    }
                    
                    Instant end = Instant.now();
                    totalProcessingTime.addAndGet(Duration.between(start, end).toMillis());
                    
                } catch (Exception e) {
                    System.err.println("Order processing failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        boolean completed = latch.await(120, TimeUnit.SECONDS);
        assertThat(completed).isTrue();
        
        executorService.shutdown();
        
        long averageProcessingTime = totalProcessingTime.get() / numberOfOrders;
        double completionRate = (double) completedOrders.get() / numberOfOrders * 100;
        
        System.out.println("=== High-Volume Processing Results ===");
        System.out.println("Total Orders: " + numberOfOrders);
        System.out.println("Completed Orders: " + completedOrders.get());
        System.out.println("Completion Rate: " + String.format("%.2f%%", completionRate));
        System.out.println("Average Processing Time: " + averageProcessingTime + "ms");
        
        assertThat(completionRate).isGreaterThanOrEqualTo(70.0);
        assertThat(averageProcessingTime).isLessThanOrEqualTo(MAX_PAYMENT_PROCESSING_TIME_MS);
    }
    
    // ==================== RESPONSE TIME VALIDATION TESTS ====================
    
    /**
     * Test service response time under normal load
     */
    @Test
    @Order(10)
    @DisplayName("Service Response Time Under Normal Load")
    void testServiceResponseTimeUnderNormalLoad() {
        Map<String, Long> responseTimeLimits = Map.of(
            "User Authentication", 2000L,
            "Restaurant Listing", 1500L,
            "Menu Retrieval", 1000L,
            "Order Creation", MAX_ORDER_CREATION_TIME_MS,
            "Payment Processing", MAX_PAYMENT_PROCESSING_TIME_MS
        );
        
        Map<String, Long> actualResponseTimes = new HashMap<>();
        
        // Test User Authentication
        Instant start = Instant.now();
        authenticateUser("test@example.com", "password123");
        long authTime = Duration.between(start, Instant.now()).toMillis();
        actualResponseTimes.put("User Authentication", authTime);
        
        // Test Restaurant Listing
        start = Instant.now();
        ResponseEntity<List> restaurantsResponse = restTemplate.exchange(
            RESTAURANT_SERVICE_URL + "/api/restaurants",
            HttpMethod.GET,
            new HttpEntity<>(createHeaders()),
            List.class
        );
        long restaurantTime = Duration.between(start, Instant.now()).toMillis();
        actualResponseTimes.put("Restaurant Listing", restaurantTime);
        
        // Test Menu Retrieval
        start = Instant.now();
        restTemplate.exchange(
            RESTAURANT_SERVICE_URL + "/api/restaurants/1/menu",
            HttpMethod.GET,
            new HttpEntity<>(createHeaders()),
            List.class
        );
        long menuTime = Duration.between(start, Instant.now()).toMillis();
        actualResponseTimes.put("Menu Retrieval", menuTime);
        
        // Test Order Creation
        start = Instant.now();
        Map<String, Object> orderRequest = createOrderRequest(1L, 1L);
        ResponseEntity<Map> orderResponse = restTemplate.exchange(
            ORDER_SERVICE_URL + "/api/orders",
            HttpMethod.POST,
            new HttpEntity<>(orderRequest, createHeaders()),
            Map.class
        );
        long orderTime = Duration.between(start, Instant.now()).toMillis();
        actualResponseTimes.put("Order Creation", orderTime);
        
        // Test Payment Processing (if order was created successfully)
        if (orderResponse.getStatusCode() == HttpStatus.CREATED) {
            Long orderId = ((Number) orderResponse.getBody().get("id")).longValue();
            start = Instant.now();
            Map<String, Object> paymentRequest = createPaymentRequest(orderId);
            restTemplate.exchange(
                PAYMENT_SERVICE_URL + "/api/payments/process",
                HttpMethod.POST,
                new HttpEntity<>(paymentRequest, createHeaders()),
                Map.class
            );
            long paymentTime = Duration.between(start, Instant.now()).toMillis();
            actualResponseTimes.put("Payment Processing", paymentTime);
        }
        
        // Validate response times
        System.out.println("=== Response Time Analysis ===");
        for (Map.Entry<String, Long> entry : actualResponseTimes.entrySet()) {
            String operation = entry.getKey();
            Long actualTime = entry.getValue();
            Long limit = responseTimeLimits.get(operation);
            
            System.out.println(operation + ": " + actualTime + "ms (limit: " + limit + "ms)");
            assertThat(actualTime).isLessThanOrEqualTo(limit);
        }
    }
    
    /**
     * Test response time consistency across multiple requests
     */
    @Test
    @Order(11)
    @DisplayName("Response Time Consistency Across Multiple Requests")
    void testResponseTimeConsistencyAcrossMultipleRequests() {
        int numberOfRequests = 10;
        List<Long> responseTimes = new ArrayList<>();
        
        for (int i = 0; i < numberOfRequests; i++) {
            Instant start = Instant.now();
            
            restTemplate.exchange(
                RESTAURANT_SERVICE_URL + "/api/restaurants",
                HttpMethod.GET,
                new HttpEntity<>(createHeaders()),
                List.class
            );
            
            long responseTime = Duration.between(start, Instant.now()).toMillis();
            responseTimes.add(responseTime);
            
            // Small delay between requests
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Calculate statistics
        double average = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        long min = responseTimes.stream().mapToLong(Long::longValue).min().orElse(0L);
        long max = responseTimes.stream().mapToLong(Long::longValue).max().orElse(0L);
        double variance = responseTimes.stream()
                .mapToDouble(time -> Math.pow(time - average, 2))
                .average().orElse(0.0);
        double standardDeviation = Math.sqrt(variance);
        
        System.out.println("=== Response Time Consistency Analysis ===");
        System.out.println("Number of Requests: " + numberOfRequests);
        System.out.println("Average Response Time: " + String.format("%.2f ms", average));
        System.out.println("Min Response Time: " + min + " ms");
        System.out.println("Max Response Time: " + max + " ms");
        System.out.println("Standard Deviation: " + String.format("%.2f ms", standardDeviation));
        
        // Assertions for consistency
        assertThat(average).isLessThanOrEqualTo(2000.0); // Average should be reasonable
        assertThat(max - min).isLessThanOrEqualTo(3000L); // Range should not be too wide
        assertThat(standardDeviation).isLessThanOrEqualTo(1000.0); // Low variability
    }
    
    // ==================== STRESS TESTING ====================
    
    /**
     * Test system behavior under stress conditions
     */
    @Test
    @Order(20)
    @DisplayName("System Behavior Under Stress Conditions")
    void testSystemBehaviorUnderStressConditions() throws InterruptedException {
        int stressTestDuration = 20; // seconds
        int requestsPerSecond = 5;
        
        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_USERS);
        AtomicInteger totalRequests = new AtomicInteger(0);
        AtomicInteger successfulRequests = new AtomicInteger(0);
        AtomicInteger failedRequests = new AtomicInteger(0);
        
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        
        // Schedule requests at regular intervals
        ScheduledFuture<?> stressTest = scheduler.scheduleAtFixedRate(() -> {
            for (int i = 0; i < requestsPerSecond; i++) {
                executorService.submit(() -> {
                    try {
                        totalRequests.incrementAndGet();
                        
                        // Perform a simple operation (restaurant listing)
                        ResponseEntity<List> response = restTemplate.exchange(
                            RESTAURANT_SERVICE_URL + "/api/restaurants",
                            HttpMethod.GET,
                            new HttpEntity<>(createHeaders()),
                            List.class
                        );
                        
                        if (response.getStatusCode().is2xxSuccessful()) {
                            successfulRequests.incrementAndGet();
                        } else {
                            failedRequests.incrementAndGet();
                        }
                    } catch (Exception e) {
                        failedRequests.incrementAndGet();
                    }
                });
            }
        }, 0, 1, TimeUnit.SECONDS);
        
        // Run stress test for specified duration
        Thread.sleep(stressTestDuration * 1000);
        stressTest.cancel(false);
        
        // Wait for remaining tasks to complete
        executorService.shutdown();
        boolean terminated = executorService.awaitTermination(30, TimeUnit.SECONDS);
        scheduler.shutdown();
        
        assertThat(terminated).isTrue();
        
        // Analyze stress test results
        int total = totalRequests.get();
        int successful = successfulRequests.get();
        int failed = failedRequests.get();
        double successRate = total > 0 ? (double) successful / total * 100 : 0;
        
        System.out.println("=== Stress Test Results ===");
        System.out.println("Test Duration: " + stressTestDuration + " seconds");
        System.out.println("Total Requests: " + total);
        System.out.println("Successful Requests: " + successful);
        System.out.println("Failed Requests: " + failed);
        System.out.println("Success Rate: " + String.format("%.2f%%", successRate));
        System.out.println("Requests per Second: " + (total / stressTestDuration));
        
        // Assertions - system should handle stress gracefully
        assertThat(successRate).isGreaterThanOrEqualTo(60.0); // At least 60% success under stress
        assertThat(total).isGreaterThan(0);
    }
    
    /**
     * Test memory usage and resource consumption
     */
    @Test
    @Order(21)
    @DisplayName("Memory Usage and Resource Consumption")
    void testMemoryUsageAndResourceConsumption() throws InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        
        // Measure initial memory usage
        runtime.gc(); // Suggest garbage collection
        Thread.sleep(1000); // Wait for GC
        
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // Perform memory-intensive operations
        List<Map<String, Object>> orders = new ArrayList<>();
        
        for (int i = 0; i < 100; i++) {
            try {
                authenticateUser("user" + i + "@example.com", "password123");

                Map<String, Object> orderRequest = createOrderRequest((long) (i + 1), 1L);
                ResponseEntity<Map> response = restTemplate.exchange(
                    ORDER_SERVICE_URL + "/api/orders",
                    HttpMethod.POST,
                    new HttpEntity<>(orderRequest, createHeaders()),
                    Map.class
                );
                
                if (response.getStatusCode() == HttpStatus.CREATED) {
                    orders.add(response.getBody());
                }
                
                // Small delay to prevent overwhelming the system
                Thread.sleep(50);
            } catch (Exception e) {
                // Continue with next iteration
            }
        }
        
        // Measure final memory usage
        runtime.gc();
        Thread.sleep(1000);
        
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = finalMemory - initialMemory;
        
        System.out.println("=== Memory Usage Analysis ===");
        System.out.println("Initial Memory: " + (initialMemory / 1024 / 1024) + " MB");
        System.out.println("Final Memory: " + (finalMemory / 1024 / 1024) + " MB");
        System.out.println("Memory Increase: " + (memoryIncrease / 1024 / 1024) + " MB");
        System.out.println("Orders Created: " + orders.size());
        
        // Assertions for memory usage
        assertThat(memoryIncrease).isLessThan(200 * 1024 * 1024); // Less than 200MB increase
        assertThat(orders.size()).isGreaterThan(0);
    }
    
    // ==================== DATABASE PERFORMANCE TESTS ====================
    
    /**
     * Test database connection pool under load
     */
    @Test
    @Order(30)
    @DisplayName("Database Connection Pool Under Load")
    void testDatabaseConnectionPoolUnderLoad() throws InterruptedException {
        int numberOfConcurrentDbOperations = 25;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfConcurrentDbOperations);
        CountDownLatch latch = new CountDownLatch(numberOfConcurrentDbOperations);
        
        AtomicInteger successfulOperations = new AtomicInteger(0);
        AtomicInteger failedOperations = new AtomicInteger(0);
        
        for (int i = 0; i < numberOfConcurrentDbOperations; i++) {
            final int operationId = i;
            executorService.submit(() -> {
                try {
                    // Perform database-intensive operations
                    authenticateUser("dbtest" + operationId + "@example.com", "password123");

                    // Create order (writes to database)
                    Map<String, Object> orderRequest = createOrderRequest((long) (operationId + 1), 1L);
                    ResponseEntity<Map> createResponse = restTemplate.exchange(
                        ORDER_SERVICE_URL + "/api/orders",
                        HttpMethod.POST,
                        new HttpEntity<>(orderRequest, createHeaders()),
                        Map.class
                    );
                    
                    if (createResponse.getStatusCode() == HttpStatus.CREATED) {
                        Long orderId = ((Number) createResponse.getBody().get("id")).longValue();
                        
                        // Read from database
                        ResponseEntity<Map> readResponse = restTemplate.exchange(
                            ORDER_SERVICE_URL + "/api/orders/" + orderId,
                            HttpMethod.GET,
                            new HttpEntity<>(createHeaders()),
                            Map.class
                        );
                        
                        if (readResponse.getStatusCode() == HttpStatus.OK) {
                            successfulOperations.incrementAndGet();
                        } else {
                            failedOperations.incrementAndGet();
                        }
                    } else {
                        failedOperations.incrementAndGet();
                    }
                } catch (Exception e) {
                    failedOperations.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        boolean completed = latch.await(60, TimeUnit.SECONDS);
        assertThat(completed).isTrue();
        
        executorService.shutdown();
        
        double successRate = (double) successfulOperations.get() / numberOfConcurrentDbOperations * 100;
        
        System.out.println("=== Database Connection Pool Test Results ===");
        System.out.println("Concurrent Operations: " + numberOfConcurrentDbOperations);
        System.out.println("Successful Operations: " + successfulOperations.get());
        System.out.println("Failed Operations: " + failedOperations.get());
        System.out.println("Success Rate: " + String.format("%.2f%%", successRate));
        
        // Database should handle concurrent connections well
        assertThat(successRate).isGreaterThanOrEqualTo(75.0);
    }
    
    // ==================== HELPER METHODS ====================
    
    private void authenticateUser(String email, String password) {
        Map<String, Object> loginRequest = Map.of(
            "email", email,
            "password", password
        );
        
        restTemplate.exchange(
            USER_SERVICE_URL + "/api/auth/login",
            HttpMethod.POST,
            new HttpEntity<>(loginRequest, createHeaders()),
            Map.class
        );
    }
    
    private Map<String, Object> createOrderRequest(Long userId, Long restaurantId) {
        return Map.of(
            "userId", userId,
            "restaurantId", restaurantId,
            "deliveryAddress", "123 Test Street, City",
            "items", List.of(
                Map.of("menuItemId", 1L, "quantity", 1, "price", new BigDecimal("15.99"))
            )
        );
    }
    
    private Map<String, Object> createPaymentRequest(Long orderId) {
        return Map.of(
            "orderId", orderId,
            "amount", new BigDecimal("15.99"),
            "paymentMethod", "CREDIT_CARD",
            "cardNumber", "4111111111111111",
            "expiryDate", "12/25",
            "cvv", "123"
        );
    }
    
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
    
    // ==================== RESULT CLASSES ====================
    
    private static class OrderResult {
        boolean success;
        Long orderId;
        long responseTime;
        String error;
    }
    
    // ==================== WIREMOCK RESPONSE HELPERS ====================
    
    private String createFastLoginResponse() {
        Map<String, Object> response = Map.of(
            "success", true,
            "userId", 1L,
            "token", "fast_token_123"
        );
        try {
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            return "{\"success\": true, \"token\": \"fast_token_123\"}";
        }
    }
    
    private String createFastRestaurantsResponse() {
        List<Map<String, Object>> restaurants = List.of(
            Map.of("id", 1L, "name", "Fast Restaurant", "isActive", true)
        );
        try {
            return objectMapper.writeValueAsString(restaurants);
        } catch (Exception e) {
            return "[{\"id\": 1, \"name\": \"Fast Restaurant\", \"isActive\": true}]";
        }
    }
    
    private String createFastMenuResponse() {
        List<Map<String, Object>> menuItems = List.of(
            Map.of("id", 1L, "name", "Fast Pizza", "price", 15.99, "available", true)
        );
        try {
            return objectMapper.writeValueAsString(menuItems);
        } catch (Exception e) {
            return "[{\"id\": 1, \"name\": \"Fast Pizza\", \"price\": 15.99, \"available\": true}]";
        }
    }
    
    private String createFastPaymentResponse() {
        Map<String, Object> payment = Map.of(
            "status", "SUCCESS",
            "transactionId", "fast_txn_123"
        );
        try {
            return objectMapper.writeValueAsString(payment);
        } catch (Exception e) {
            return "{\"status\": \"SUCCESS\", \"transactionId\": \"fast_txn_123\"}";
        }
    }
    
    private String createFastNotificationResponse() {
        Map<String, Object> notification = Map.of(
            "status", "SENT",
            "id", 1L
        );
        try {
            return objectMapper.writeValueAsString(notification);
        } catch (Exception e) {
            return "{\"status\": \"SENT\", \"id\": 1}";
        }
    }
}