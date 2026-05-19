package com.fooddelivery.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.notification.dto.NotificationRequest;
import com.fooddelivery.notification.entity.Notification;
import com.fooddelivery.notification.repository.NotificationRepository;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class NotificationControllerIT {

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
    private NotificationRepository notificationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private NotificationRequest notificationRequest;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/notifications";
        notificationRepository.deleteAll();

        notificationRequest = new NotificationRequest();
        notificationRequest.setUserId(1L);
        notificationRequest.setMessage("Test notification message");
        notificationRequest.setType("ORDER_CREATED");
    }

    @Test
    void createNotification_ShouldReturnCreated() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<NotificationRequest> request = new HttpEntity<>(notificationRequest, headers);

        ResponseEntity<Notification> response = restTemplate.postForEntity(baseUrl, request, Notification.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUserId()).isEqualTo(1L);
        assertThat(response.getBody().getMessage()).isEqualTo("Test notification message");
        assertThat(response.getBody().getType()).isEqualTo("ORDER_CREATED");
    }

    @Test
    void sendNotification_ShouldReturnNotification() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<NotificationRequest> request = new HttpEntity<>(notificationRequest, headers);

        ResponseEntity<Notification> response = restTemplate.postForEntity(
                baseUrl + "/send", request, Notification.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("SENT");
        assertThat(response.getBody().getIsRead()).isFalse();
    }

    @Test
    void getAllNotifications_ShouldReturnList() {
        // Create a notification first
        Notification notification = createTestNotification();
        notificationRepository.save(notification);

        ResponseEntity<Notification[]> response = restTemplate.getForEntity(baseUrl, Notification[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);
    }

    @Test
    void getNotificationById_ShouldReturnNotification() {
        // Create a notification first
        Notification notification = createTestNotification();
        Notification saved = notificationRepository.save(notification);

        ResponseEntity<Notification> response = restTemplate.getForEntity(
                baseUrl + "/" + saved.getId(), Notification.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(saved.getId());
    }

    @Test
    void getNotificationsByUserId_ShouldReturnList() {
        // Create a notification first
        Notification notification = createTestNotification();
        notificationRepository.save(notification);

        ResponseEntity<Notification[]> response = restTemplate.getForEntity(
                baseUrl + "/user/1", Notification[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void updateNotification_ShouldReturnUpdated() {
        // Create a notification first
        Notification notification = createTestNotification();
        Notification saved = notificationRepository.save(notification);

        NotificationRequest updateRequest = new NotificationRequest();
        updateRequest.setUserId(1L);
        updateRequest.setMessage("Updated message");
        updateRequest.setType("ORDER_UPDATED");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<NotificationRequest> request = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<Notification> response = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.PUT,
                request,
                Notification.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Updated message");
    }

    @Test
    void markAsRead_ShouldReturnUpdatedNotification() {
        // Create a notification first
        Notification notification = createTestNotification();
        Notification saved = notificationRepository.save(notification);

        ResponseEntity<Notification> response = restTemplate.exchange(
                baseUrl + "/" + saved.getId() + "/read",
                HttpMethod.PUT,
                null,
                Notification.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getIsRead()).isTrue();
    }

    @Test
    void deleteNotification_ShouldReturnNoContent() {
        // Create a notification first
        Notification notification = createTestNotification();
        Notification saved = notificationRepository.save(notification);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void sendEmailNotification_ShouldReturnSuccess() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<NotificationRequest> request = new HttpEntity<>(notificationRequest, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/email", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Email notification sent successfully");
    }

    @Test
    void sendSmsNotification_ShouldReturnSuccess() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<NotificationRequest> request = new HttpEntity<>(notificationRequest, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/sms", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("SMS notification sent successfully");
    }

    @Test
    void healthCheck_ShouldReturnSuccess() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/health", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Notification Service is running");
    }

    private Notification createTestNotification() {
        Notification notification = new Notification();
        notification.setUserId(1L);
        notification.setMessage("Test notification message");
        notification.setType("ORDER_CREATED");
        notification.setStatus("SENT");
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        return notification;
    }
}