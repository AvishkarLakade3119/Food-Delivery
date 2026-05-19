package com.fooddelivery.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.notification.dto.NotificationRequest;
import com.fooddelivery.notification.entity.Notification;
import com.fooddelivery.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void getAllNotifications_ShouldReturnList() throws Exception {
        List<Notification> notifications = Arrays.asList(notification);
        when(notificationService.getAllNotifications()).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].message").value("Test notification message"))
                .andExpect(jsonPath("$[0].type").value("ORDER_CREATED"));

        verify(notificationService).getAllNotifications();
    }

    @Test
    void getNotificationById_ShouldReturnNotification() throws Exception {
        when(notificationService.getNotificationById(1L)).thenReturn(Optional.of(notification));

        mockMvc.perform(get("/api/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.message").value("Test notification message"));

        verify(notificationService).getNotificationById(1L);
    }

    @Test
    void getNotificationById_NotFound_ShouldReturn404() throws Exception {
        when(notificationService.getNotificationById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/notifications/1"))
                .andExpect(status().isNotFound());

        verify(notificationService).getNotificationById(1L);
    }

    @Test
    void getNotificationsByUserId_ShouldReturnList() throws Exception {
        List<Notification> notifications = Arrays.asList(notification);
        when(notificationService.getNotificationsByUserId(1L)).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].userId").value(1L));

        verify(notificationService).getNotificationsByUserId(1L);
    }

    @Test
    void createNotification_ShouldReturnCreated() throws Exception {
        when(notificationService.createNotification(any(NotificationRequest.class))).thenReturn(notification);

        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.message").value("Test notification message"));

        verify(notificationService).createNotification(any(NotificationRequest.class));
    }

    @Test
    void sendNotification_ShouldReturnNotification() throws Exception {
        when(notificationService.save(any(Notification.class))).thenReturn(notification);

        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("SENT"));

        verify(notificationService).save(any(Notification.class));
    }

    @Test
    void sendEmailNotification_ShouldReturnSuccess() throws Exception {
        doNothing().when(notificationService).sendEmailNotification(any(NotificationRequest.class));

        mockMvc.perform(post("/api/notifications/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Email notification sent successfully"));

        verify(notificationService).sendEmailNotification(any(NotificationRequest.class));
    }

    @Test
    void sendSmsNotification_ShouldReturnSuccess() throws Exception {
        doNothing().when(notificationService).sendSmsNotification(any(NotificationRequest.class));

        mockMvc.perform(post("/api/notifications/sms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("SMS notification sent successfully"));

        verify(notificationService).sendSmsNotification(any(NotificationRequest.class));
    }

    @Test
    void updateNotification_ShouldReturnUpdated() throws Exception {
        Notification updatedNotification = new Notification();
        updatedNotification.setId(1L);
        updatedNotification.setMessage("Updated message");
        updatedNotification.setType("ORDER_UPDATED");

        when(notificationService.updateNotification(eq(1L), any(NotificationRequest.class)))
                .thenReturn(Optional.of(updatedNotification));

        notificationRequest.setMessage("Updated message");
        notificationRequest.setType("ORDER_UPDATED");

        mockMvc.perform(put("/api/notifications/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Updated message"))
                .andExpect(jsonPath("$.type").value("ORDER_UPDATED"));

        verify(notificationService).updateNotification(eq(1L), any(NotificationRequest.class));
    }

    @Test
    void markAsRead_ShouldReturnUpdatedNotification() throws Exception {
        Notification readNotification = new Notification();
        readNotification.setId(1L);
        readNotification.setIsRead(true);

        when(notificationService.getNotificationById(1L)).thenReturn(Optional.of(notification));
        when(notificationService.save(any(Notification.class))).thenReturn(readNotification);

        mockMvc.perform(put("/api/notifications/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isRead").value(true));

        verify(notificationService).getNotificationById(1L);
        verify(notificationService).save(any(Notification.class));
    }
    @Test
    void deleteNotification_ShouldReturnNoContent() throws Exception {
        when(notificationService.deleteNotification(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/notifications/1"))
                .andExpect(status().isNoContent());

        verify(notificationService).deleteNotification(1L);
    }

    @Test
    void deleteNotification_NotFound_ShouldReturn404() throws Exception {
        when(notificationService.deleteNotification(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/notifications/1"))
                .andExpect(status().isNotFound());

        verify(notificationService).deleteNotification(1L);
    }

    @Test
    void healthCheck_ShouldReturnSuccess() throws Exception {
        mockMvc.perform(get("/api/notifications/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification Service is running"));
    }
}