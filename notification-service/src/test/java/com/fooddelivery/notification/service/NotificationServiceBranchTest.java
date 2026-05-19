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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Branch coverage test for NotificationService to cover missed branches.
 * This file targets the InterruptedException catch block in sendNotification().
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceBranchTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private NotificationRequest notificationRequest;

    @BeforeEach
    void setUp() {
        notificationRequest = new NotificationRequest();
        notificationRequest.setUserId(1L);
        notificationRequest.setMessage("Test notification message");
        notificationRequest.setType("ORDER_CREATED");
    }

    /**
     * Test to cover the InterruptedException catch block in sendNotification().
     * This test interrupts the current thread before calling sendNotification(),
     * which causes the Thread.sleep(100) to throw InterruptedException.
     * 
     * Covers: NotificationService.java lines 117-119 (catch block)
     */
    @Test
    void sendNotification_InterruptedException_ShouldHandleInterrupt() {
        // Interrupt the current thread BEFORE calling sendNotification
        // This will cause Thread.sleep(100) to throw InterruptedException
        Thread.currentThread().interrupt();
        
        // Call sendNotification - the catch block should execute
        notificationService.sendNotification(notificationRequest);
        
        // Verify that the thread's interrupted status is set
        // The catch block calls Thread.currentThread().interrupt() to restore the flag
        assertThat(Thread.interrupted()).isTrue(); // This clears the flag
    }

    /**
     * Additional test to verify sendNotification works normally when not interrupted.
     * This ensures the TRUE path (no exception) is also covered.
     */
    @Test
    void sendNotification_NoInterruption_ShouldCompleteNormally() {
        // Ensure thread is not interrupted
        Thread.interrupted(); // Clear any existing interrupt flag
        
        // Call sendNotification normally
        notificationService.sendNotification(notificationRequest);
        
        // Verify no interrupt flag is set
        assertThat(Thread.interrupted()).isFalse();
    }
}