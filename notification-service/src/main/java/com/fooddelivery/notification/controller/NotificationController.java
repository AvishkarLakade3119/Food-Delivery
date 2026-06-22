package com.fooddelivery.notification.controller;

import com.fooddelivery.notification.dto.NotificationRequest;
import com.fooddelivery.notification.entity.Notification;
import com.fooddelivery.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200", "http://localhost:8080", "http://localhost:8081"}, allowCredentials = "true")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // GET /api/notifications - Get all notifications
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    // GET /api/notifications/{id} - Get notification by ID
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable("id") Long id) {
        Optional<Notification> notification = notificationService.getNotificationById(id);
        return notification.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/notifications/user/{userId} - Get notifications by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable("userId") Long userId) {
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    // POST /api/notifications - Create a new notification
    @PostMapping
    public ResponseEntity<Notification> createNotification(@Valid @RequestBody NotificationRequest notificationRequest) {
        Notification notification = notificationService.createNotification(notificationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(notification);
    }

    // POST /api/notifications/send - Send notification (create and send)
    @PostMapping("/send")
    public ResponseEntity<Notification> sendNotification(@Valid @RequestBody NotificationRequest notificationRequest) {
        Notification notification = new Notification();
        notification.setUserId(notificationRequest.getUserId());
        notification.setTitle(notificationRequest.getTitle());
        notification.setMessage(notificationRequest.getMessage());
        notification.setType(notificationRequest.getType());
        notification.setStatus("SENT");
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        Notification saved = notificationService.save(notification);
        return ResponseEntity.ok(saved);
    }

    // POST /api/notifications/email - Send email notification
    @PostMapping("/email")
    public ResponseEntity<String> sendEmailNotification(@Valid @RequestBody NotificationRequest notificationRequest) {
        notificationService.sendEmailNotification(notificationRequest);
        return ResponseEntity.ok("Email notification sent successfully");
    }

    // POST /api/notifications/sms - Send SMS notification
    @PostMapping("/sms")
    public ResponseEntity<String> sendSmsNotification(@Valid @RequestBody NotificationRequest notificationRequest) {
        notificationService.sendSmsNotification(notificationRequest);
        return ResponseEntity.ok("SMS notification sent successfully");
    }

    // PUT /api/notifications/{id} - Update notification
    @PutMapping("/{id}")
    public ResponseEntity<Notification> updateNotification(@PathVariable("id") Long id, @Valid @RequestBody NotificationRequest notificationRequest) {
        Optional<Notification> updatedNotification = notificationService.updateNotification(id, notificationRequest);
        return updatedNotification.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/notifications/{id}/read - Mark notification as read
    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable("id") Long id) {
        return notificationService.getNotificationById(id)
                .map(notification -> {
                    notification.setIsRead(true);
                    notification.setUpdatedAt(LocalDateTime.now());
                    Notification updated = notificationService.save(notification);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/notifications/{id} - Delete notification
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable("id") Long id) {
        boolean deleted = notificationService.deleteNotification(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // GET /api/notifications/health - Health check
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Notification Service is running");
    }
}