package com.fooddelivery.order.client;

import com.fooddelivery.order.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service")
public interface NotificationClient {
    
    @PostMapping("/notifications/send")
    void sendNotification(@RequestBody NotificationRequest notificationRequest);
}