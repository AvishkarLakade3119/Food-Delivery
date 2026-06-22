package com.fooddelivery.notification.service;

import com.fooddelivery.notification.dto.NotificationRequest;
import com.fooddelivery.notification.entity.Notification;
import com.fooddelivery.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    // Get all notifications
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
    
    // Get notification by ID
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }
    
    // Get notifications by user ID
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Create a new notification
    public Notification createNotification(NotificationRequest request) {
        Notification notification = new Notification(request.getUserId(), request.getTitle(), request.getMessage(), request.getType());
        Notification savedNotification = notificationRepository.save(notification);

        // Also send the notification (log it)
        sendNotification(request);

        return savedNotification;
    }

    // Update notification
    public Optional<Notification> updateNotification(Long id, NotificationRequest request) {
        Optional<Notification> existingNotification = notificationRepository.findById(id);

        if (existingNotification.isPresent()) {
            Notification notification = existingNotification.get();
            notification.setUserId(request.getUserId());
            notification.setTitle(request.getTitle());
            notification.setMessage(request.getMessage());
            notification.setType(request.getType());
            notification.preUpdate();

            return Optional.of(notificationRepository.save(notification));
        }

        return Optional.empty();
    }

    // Delete notification
    public boolean deleteNotification(Long id) {
        if (notificationRepository.existsById(id)) {
            notificationRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Save notification
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    // Find all notifications
    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    // Find notification by ID
    public Optional<Notification> findById(Long id) {
        return notificationRepository.findById(id);
    }

    // Delete notification by ID
    public void deleteById(Long id) {
        notificationRepository.deleteById(id);
    }

    // Find notifications by user ID
    public List<Notification> findByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
    
    public void sendNotification(NotificationRequest request) {
        String timestamp = LocalDateTime.now().format(formatter);
        
        // Log the notification to console (simulating email/SMS)
        logger.info("\n" +
                "=================================================\n" +
                "           NOTIFICATION SENT\n" +
                "=================================================\n" +
                "Timestamp: {}\n" +
                "User ID: {}\n" +
                "Type: {}\n" +
                "Message: {}\n" +
                "=================================================\n",
                timestamp, request.getUserId(), request.getType(), request.getMessage());
        
        // Simulate processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public void sendEmailNotification(NotificationRequest request) {
        String timestamp = LocalDateTime.now().format(formatter);
        
        logger.info("\n" +
                "=================================================\n" +
                "           EMAIL NOTIFICATION\n" +
                "=================================================\n" +
                "Timestamp: {}\n" +
                "To User ID: {}\n" +
                "Subject: Food Delivery Update\n" +
                "Message: {}\n" +
                "Type: {}\n" +
                "Status: SENT\n" +
                "=================================================\n",
                timestamp, request.getUserId(), request.getMessage(), request.getType());
    }
    
    public void sendSmsNotification(NotificationRequest request) {
        String timestamp = LocalDateTime.now().format(formatter);
        
        logger.info("\n" +
                "=================================================\n" +
                "           SMS NOTIFICATION\n" +
                "=================================================\n" +
                "Timestamp: {}\n" +
                "To User ID: {}\n" +
                "SMS Message: {}\n" +
                "Type: {}\n" +
                "Status: DELIVERED\n" +
                "=================================================\n",
                timestamp, request.getUserId(), request.getMessage(), request.getType());
    }

    public void sendOrderStatusNotification(Long userId, String orderStatus, Long orderId) {
        String title = "Order Status Update";
        String message = String.format("Your order #%d status has been updated to: %s", orderId, orderStatus);
        NotificationRequest request = new NotificationRequest(userId, title, message, "ORDER_STATUS_UPDATE");
        sendNotification(request);
    }

    public void sendWelcomeNotification(Long userId, String userName) {
        String title = "Welcome to Food Delivery";
        String message = String.format("Welcome to Food Delivery App, %s! Your account has been created successfully.", userName);
        NotificationRequest request = new NotificationRequest(userId, title, message, "WELCOME");
        sendNotification(request);
    }

    public void sendPaymentNotification(Long userId, String paymentStatus, String transactionId) {
        String title = "Payment Notification";
        String message = String.format("Payment %s. Transaction ID: %s", paymentStatus, transactionId);
        NotificationRequest request = new NotificationRequest(userId, title, message, "PAYMENT_UPDATE");
        sendNotification(request);
    }
}