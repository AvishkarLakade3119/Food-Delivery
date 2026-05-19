package com.fooddelivery.payment.service;

import com.fooddelivery.payment.dto.PaymentRequest;
import com.fooddelivery.payment.dto.PaymentResponse;
import com.fooddelivery.payment.entity.Payment;
import com.fooddelivery.payment.entity.PaymentStatus;
import com.fooddelivery.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    private final Random random = new Random();
    
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        // Create payment record
        Payment payment = new Payment(
                paymentRequest.getOrderId(),
                paymentRequest.getAmount(),
                paymentRequest.getPaymentMethod()
        );
        
        // Simulate payment processing
        boolean paymentSuccess = simulatePaymentProcessing();
        
        if (paymentSuccess) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(generateTransactionId());
            paymentRepository.save(payment);
            
            return new PaymentResponse(
                    "SUCCESS",
                    payment.getTransactionId(),
                    "Payment processed successfully"
            );
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            
            return new PaymentResponse(
                    "FAILED",
                    null,
                    "Payment processing failed"
            );
        }
    }
    
    private boolean simulatePaymentProcessing() {
        // Simulate 80% success rate
        return random.nextInt(100) < 80;
    }
    
    private String generateTransactionId() {
        return "TXN_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
    
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
    }

    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order id: " + orderId));
    }

    public List<Payment> findByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(payment -> List.of(payment))
                .orElse(List.of());
    }
    
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    public Payment refundPayment(Long id) {
        Payment payment = getPaymentById(id);

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new RuntimeException("Only successful payments can be refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        return paymentRepository.save(payment);
    }

    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    public java.util.Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    public void deleteById(Long id) {
        paymentRepository.deleteById(id);
    }
}