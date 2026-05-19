package com.fooddelivery.payment.controller;

import com.fooddelivery.payment.dto.PaymentRequest;
import com.fooddelivery.payment.dto.PaymentResponse;
import com.fooddelivery.payment.entity.Payment;
import com.fooddelivery.payment.entity.PaymentStatus;
import com.fooddelivery.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody Payment payment) {
        payment.setId(null); // Force new ID generation
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(java.time.LocalDateTime.now());
        payment.setUpdatedAt(java.time.LocalDateTime.now());
        Payment saved = paymentService.save(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        PaymentResponse response = paymentService.processPayment(paymentRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/process")
    public ResponseEntity<?> processPayment(@PathVariable("id") Long id) {
        return paymentService.findById(id)
                .map(payment -> {
                    if ("SUCCESS".equals(payment.getStatus().toString()) || "COMPLETED".equals(payment.getStatus().toString())) {
                        Map<String, Object> error = new LinkedHashMap<>();
                        error.put("status", 400);
                        error.put("message", "Payment already processed");
                        return ResponseEntity.badRequest().body(error);
                    }
                    payment.setStatus(PaymentStatus.SUCCESS);
                    payment.setUpdatedAt(java.time.LocalDateTime.now());
                    Payment updated = paymentService.save(payment);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable("id") Long id) {
        return paymentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(
            @PathVariable("id") Long id,
            @Valid @RequestBody Payment payment) {
        return paymentService.findById(id)
                .map(existing -> {
                    payment.setId(id);
                    payment.setUpdatedAt(java.time.LocalDateTime.now());
                    return ResponseEntity.ok(paymentService.save(payment));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable("id") Long id) {
        if (paymentService.findById(id).isPresent()) {
            paymentService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Payment>> getPaymentsByOrderId(@PathVariable("orderId") Long orderId) {
        List<Payment> payments = paymentService.findByOrderId(orderId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable("status") PaymentStatus status) {
        List<Payment> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<?> refundPayment(@PathVariable("id") Long id) {
        return paymentService.findById(id)
                .map(payment -> {
                    if ("REFUNDED".equals(payment.getStatus().toString())) {
                        Map<String, Object> error = new LinkedHashMap<>();
                        error.put("status", 400);
                        error.put("message", "Payment already refunded");
                        return ResponseEntity.badRequest().body(error);
                    }
                    if (!"SUCCESS".equals(payment.getStatus().toString()) && !"COMPLETED".equals(payment.getStatus().toString())) {
                        Map<String, Object> error = new LinkedHashMap<>();
                        error.put("status", 400);
                        error.put("message", "Can only refund successful payments");
                        return ResponseEntity.badRequest().body(error);
                    }
                    payment.setStatus(PaymentStatus.REFUNDED);
                    payment.setUpdatedAt(java.time.LocalDateTime.now());
                    Payment updated = paymentService.save(payment);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}