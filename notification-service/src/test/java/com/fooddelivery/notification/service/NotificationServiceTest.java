package com.fooddelivery.notification.service;

import com.fooddelivery.notification.dto.NotificationRequest;
import com.fooddelivery.notification.entity.Notification;
import com.fooddelivery.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Notification notification;
    private NotificationRequest notificationRequest;

    @BeforeEach
    void setUp() {
        notification = new Notification();
        notification.setId(1L);
        notification.setUserId(1L);
        notification.setMessage("Test notification message");
        notification.setType("ORDER_CREATED");
        notification.setStatus("SENT");
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());

        notificationRequest = new NotificationRequest();
        notificationRequest.setUserId(1L);
        notificationRequest.setMessage("Test notification message");
        notificationRequest.setType("ORDER_CREATED");
    }

    @Test
    void getAllNotifications_ShouldReturnAllNotifications() {
        List<Notification> notifications = Arrays.asList(notification);
        when(notificationRepository.findAll()).thenReturn(notifications);

        List<Notification> result = notificationService.getAllNotifications();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(notificationRepository).findAll();
    }

    @Test
    void getNotificationById_ShouldReturnNotification() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        Optional<Notification> result = notificationService.getNotificationById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(notificationRepository).findById(1L);
    }

    @Test
    void getNotificationById_NotFound_ShouldReturnEmpty() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Notification> result = notificationService.getNotificationById(1L);

        assertThat(result).isEmpty();
        verify(notificationRepository).findById(1L);
    }

    @Test
    void getNotificationsByUserId_ShouldReturnUserNotifications() {
        List<Notification> notifications = Arrays.asList(notification);
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(notifications);

        List<Notification> result = notificationService.getNotificationsByUserId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(1L);
        verify(notificationRepository).findByUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void createNotification_ShouldCreateAndReturnNotification() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification result = notificationService.createNotification(notificationRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getMessage()).isEqualTo("Test notification message");
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void updateNotification_ShouldUpdateAndReturnNotification() {
        Notification updatedNotification = new Notification();
        updatedNotification.setId(1L);
        updatedNotification.setMessage("Updated message");
        updatedNotification.setType("ORDER_UPDATED");

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(updatedNotification);

        NotificationRequest updateRequest = new NotificationRequest();
        updateRequest.setUserId(1L);
        updateRequest.setMessage("Updated message");
        updateRequest.setType("ORDER_UPDATED");

        Optional<Notification> result = notificationService.updateNotification(1L, updateRequest);

        assertThat(result).isPresent();
        assertThat(result.get().getMessage()).isEqualTo("Updated message");
        verify(notificationRepository).findById(1L);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void updateNotification_NotFound_ShouldReturnEmpty() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Notification> result = notificationService.updateNotification(1L, notificationRequest);

        assertThat(result).isEmpty();
        verify(notificationRepository).findById(1L);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void deleteNotification_ShouldReturnTrue() {
        when(notificationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(notificationRepository).deleteById(1L);

        boolean result = notificationService.deleteNotification(1L);

        assertThat(result).isTrue();
        verify(notificationRepository).existsById(1L);
        verify(notificationRepository).deleteById(1L);
    }

    @Test
    void deleteNotification_NotFound_ShouldReturnFalse() {
        when(notificationRepository.existsById(1L)).thenReturn(false);

        boolean result = notificationService.deleteNotification(1L);

        assertThat(result).isFalse();
        verify(notificationRepository).existsById(1L);
        verify(notificationRepository, never()).deleteById(1L);
    }

    @Test
    void save_ShouldSaveAndReturnNotification() {
        when(notificationRepository.save(notification)).thenReturn(notification);

        Notification result = notificationService.save(notification);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(notificationRepository).save(notification);
    }

    @Test
    void findAll_ShouldReturnAllNotifications() {
        List<Notification> notifications = Arrays.asList(notification);
        when(notificationRepository.findAll()).thenReturn(notifications);

        List<Notification> result = notificationService.findAll();

        assertThat(result).hasSize(1);
        verify(notificationRepository).findAll();
    }

    @Test
    void findById_ShouldReturnOptionalNotification() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        Optional<Notification> result = notificationService.findById(1L);

        assertThat(result).isPresent();
        verify(notificationRepository).findById(1L);
    }

    @Test
    void deleteById_ShouldDeleteNotification() {
        doNothing().when(notificationRepository).deleteById(1L);

        notificationService.deleteById(1L);

        verify(notificationRepository).deleteById(1L);
    }

    @Test
    void findByUserId_ShouldReturnUserNotifications() {
        List<Notification> notifications = Arrays.asList(notification);
        when(notificationRepository.findByUserId(1L)).thenReturn(notifications);

        List<Notification> result = notificationService.findByUserId(1L);

        assertThat(result).hasSize(1);
        verify(notificationRepository).findByUserId(1L);
    }

    @Test
    void sendNotification_ShouldProcessNotification() {
        // This method logs output, so we just verify it doesn't throw exceptions
        notificationService.sendNotification(notificationRequest);
        
        // No verification needed as this is a logging method
        // The test passes if no exception is thrown
    }

    @Test
    void sendEmailNotification_ShouldProcessEmailNotification() {
        // This method logs output, so we just verify it doesn't throw exceptions
        notificationService.sendEmailNotification(notificationRequest);
        
        // No verification needed as this is a logging method
        // The test passes if no exception is thrown
    }

    @Test
    void sendSmsNotification_ShouldProcessSmsNotification() {
        // This method logs output, so we just verify it doesn't throw exceptions
        notificationService.sendSmsNotification(notificationRequest);
        
        // No verification needed as this is a logging method
        // The test passes if no exception is thrown
    }

    @Test
    void sendOrderStatusNotification_ShouldProcessOrderStatusNotification() {
        // This method calls sendNotification internally
        notificationService.sendOrderStatusNotification(1L, "CONFIRMED", 123L);
        
        // No verification needed as this is a logging method
        // The test passes if no exception is thrown
    }

    @Test
    void sendWelcomeNotification_ShouldProcessWelcomeNotification() {
        // This method calls sendNotification internally
        notificationService.sendWelcomeNotification(1L, "John Doe");
        
        // No verification needed as this is a logging method
        // The test passes if no exception is thrown
    }

    @Test
    void sendPaymentNotification_ShouldProcessPaymentNotification() {
        // This method calls sendNotification internally
        notificationService.sendPaymentNotification(1L, "SUCCESS", "TXN123456");
        
        // No verification needed as this is a logging method
        // The test passes if no exception is thrown
    }
}