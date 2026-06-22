package com.fooddelivery.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/orders")
    public Mono<ResponseEntity<Map<String, Object>>> ordersFallback() {
        return Mono.just(buildResponse("Order Service is temporarily unavailable. Please retry shortly."));
    }

    @RequestMapping("/payments")
    public Mono<ResponseEntity<Map<String, Object>>> paymentsFallback() {
        return Mono.just(buildResponse("Payment Service is temporarily unavailable. Please retry shortly."));
    }

    @RequestMapping("/users")
    public Mono<ResponseEntity<Map<String, Object>>> usersFallback() {
        return Mono.just(buildResponse("User Service is temporarily unavailable."));
    }

    @RequestMapping("/restaurants")
    public Mono<ResponseEntity<Map<String, Object>>> restaurantsFallback() {
        return Mono.just(buildResponse("Restaurant Service is temporarily unavailable."));
    }

    @RequestMapping("/notifications")
    public Mono<ResponseEntity<Map<String, Object>>> notificationsFallback() {
        return Mono.just(buildResponse("Notification Service is temporarily unavailable."));
    }

    private ResponseEntity<Map<String, Object>> buildResponse(String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        body.put("error", "Service Unavailable");
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("fallback", true);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }
}