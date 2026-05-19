package com.fooddelivery.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.payment.dto.PaymentRequest;
import com.fooddelivery.payment.dto.PaymentResponse;
import com.fooddelivery.payment.entity.Payment;
import com.fooddelivery.payment.entity.PaymentStatus;
import com.fooddelivery.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    private PaymentRequest paymentRequest;
    private PaymentResponse paymentResponse;
    private Payment payment;

    @BeforeEach
    void setUp() {
        paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(1L);
        paymentRequest.setAmount(new BigDecimal("100.00"));
        paymentRequest.setPaymentMethod("CREDIT_CARD");

        paymentResponse = new PaymentResponse();
        paymentResponse.setStatus("SUCCESS");
        paymentResponse.setTransactionId("txn123");
        paymentResponse.setMessage("Payment processed successfully");

        payment = new Payment();
        payment.setId(1L);
        payment.setOrderId(1L);
        payment.setAmount(new BigDecimal("100.00"));
        payment.setPaymentMethod("CREDIT_CARD");
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId("txn123");
    }

    @Test
    void processPayment_ValidRequest_ShouldReturnOk() throws Exception {
        when(paymentService.processPayment(any(PaymentRequest.class))).thenReturn(paymentResponse);

        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.transactionId").value("txn123"));

        verify(paymentService).processPayment(any(PaymentRequest.class));
    }

    @Test
    void getPaymentById_ExistingId_ShouldReturnPayment() throws Exception {
        when(paymentService.findById(1L)).thenReturn(Optional.of(payment));

        mockMvc.perform(get("/api/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.amount").value(100.00));

        verify(paymentService).findById(1L);
    }

    @Test
    void getPaymentById_NonExistingId_ShouldReturnNotFound() throws Exception {
        when(paymentService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/payments/999"))
                .andExpect(status().isNotFound());

        verify(paymentService).findById(999L);
    }

    @Test
    void getPaymentByOrderId_ExistingOrderId_ShouldReturnPaymentList() throws Exception {
        List<Payment> payments = Arrays.asList(payment);
        when(paymentService.findByOrderId(1L)).thenReturn(payments);

        mockMvc.perform(get("/api/payments/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].orderId").value(1L));

        verify(paymentService).findByOrderId(1L);
    }

    @Test
    void getAllPayments_ShouldReturnAllPayments() throws Exception {
        Payment payment2 = new Payment();
        payment2.setId(2L);
        payment2.setOrderId(2L);
        payment2.setAmount(new BigDecimal("200.00"));
        payment2.setPaymentMethod("PAYPAL");
        payment2.setStatus(PaymentStatus.SUCCESS);

        List<Payment> payments = Arrays.asList(payment, payment2);
        when(paymentService.getAllPayments()).thenReturn(payments);

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(paymentService).getAllPayments();
    }

    @Test
    void getPaymentsByStatus_ShouldReturnFilteredPayments() throws Exception {
        List<Payment> payments = Arrays.asList(payment);
        when(paymentService.getPaymentsByStatus(PaymentStatus.SUCCESS)).thenReturn(payments);

        mockMvc.perform(get("/api/payments/status/SUCCESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(paymentService).getPaymentsByStatus(PaymentStatus.SUCCESS);
    }

    @Test
    void refundPayment_ValidId_ShouldReturnRefundedPayment() throws Exception {
        Payment refundedPayment = new Payment();
        refundedPayment.setId(1L);
        refundedPayment.setOrderId(1L);
        refundedPayment.setAmount(new BigDecimal("100.00"));
        refundedPayment.setPaymentMethod("CREDIT_CARD");
        refundedPayment.setStatus(PaymentStatus.REFUNDED);
        refundedPayment.setTransactionId("txn123");

        // Mock that payment exists and is successful
        Payment successfulPayment = new Payment();
        successfulPayment.setId(1L);
        successfulPayment.setOrderId(1L);
        successfulPayment.setAmount(new BigDecimal("100.00"));
        successfulPayment.setPaymentMethod("CREDIT_CARD");
        successfulPayment.setStatus(PaymentStatus.SUCCESS);
        successfulPayment.setTransactionId("txn123");

        when(paymentService.findById(1L)).thenReturn(Optional.of(successfulPayment));
        when(paymentService.save(any(Payment.class))).thenReturn(refundedPayment);

        mockMvc.perform(post("/api/payments/1/refund"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("REFUNDED"));

        verify(paymentService).findById(1L);
        verify(paymentService).save(any(Payment.class));
    }

    @Test
    void refundPayment_InvalidPayment_ShouldReturnBadRequest() throws Exception {
        // Mock that payment exists but is not successful (PENDING status)
        Payment pendingPayment = new Payment();
        pendingPayment.setId(1L);
        pendingPayment.setOrderId(1L);
        pendingPayment.setAmount(new BigDecimal("100.00"));
        pendingPayment.setPaymentMethod("CREDIT_CARD");
        pendingPayment.setStatus(PaymentStatus.PENDING);
        pendingPayment.setTransactionId("txn123");

        when(paymentService.findById(1L)).thenReturn(Optional.of(pendingPayment));

        mockMvc.perform(post("/api/payments/1/refund"))
                .andExpect(status().isBadRequest());

        verify(paymentService).findById(1L);
    }

    @Test
    void processPayment_InvalidJson_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void processPayment_MissingAmount_ShouldReturnBadRequest() throws Exception {
        paymentRequest.setAmount(null);

        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void processPayment_MissingOrderId_ShouldReturnBadRequest() throws Exception {
        paymentRequest.setOrderId(null);

        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void processPayment_MissingPaymentMethod_ShouldReturnBadRequest() throws Exception {
        paymentRequest.setPaymentMethod(null);

        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refundPayment_AlreadyRefunded_ShouldReturnBadRequest() throws Exception {
        Payment refundedPayment = new Payment();
        refundedPayment.setId(1L);
        refundedPayment.setStatus(PaymentStatus.REFUNDED);

        when(paymentService.findById(1L)).thenReturn(Optional.of(refundedPayment));

        mockMvc.perform(post("/api/payments/1/refund")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Payment already refunded"));
    }

    @Test
    void createPayment_ValidPayment_ShouldReturnCreated() throws Exception {
        Payment newPayment = new Payment();
        newPayment.setOrderId(1L);
        newPayment.setAmount(new BigDecimal("100.00"));
        newPayment.setPaymentMethod("CREDIT_CARD");

        Payment savedPayment = new Payment();
        savedPayment.setId(1L);
        savedPayment.setOrderId(1L);
        savedPayment.setAmount(new BigDecimal("100.00"));
        savedPayment.setPaymentMethod("CREDIT_CARD");
        savedPayment.setStatus(PaymentStatus.PENDING);

        when(paymentService.save(any(Payment.class))).thenReturn(savedPayment);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPayment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void processPaymentById_ValidId_ShouldReturnOk() throws Exception {
        Payment pendingPayment = new Payment();
        pendingPayment.setId(1L);
        pendingPayment.setStatus(PaymentStatus.PENDING);

        Payment updatedPayment = new Payment();
        updatedPayment.setId(1L);
        updatedPayment.setStatus(PaymentStatus.SUCCESS);

        when(paymentService.findById(1L)).thenReturn(Optional.of(pendingPayment));
        when(paymentService.save(any(Payment.class))).thenReturn(updatedPayment);

        mockMvc.perform(post("/api/payments/1/process")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void processPaymentById_AlreadyProcessed_ShouldReturnBadRequest() throws Exception {
        Payment successfulPayment = new Payment();
        successfulPayment.setId(1L);
        successfulPayment.setStatus(PaymentStatus.SUCCESS);

        when(paymentService.findById(1L)).thenReturn(Optional.of(successfulPayment));

        mockMvc.perform(post("/api/payments/1/process")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Payment already processed"));
    }

    @Test
    void processPaymentById_NotFound_ShouldReturnNotFound() throws Exception {
        when(paymentService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/payments/999/process")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePayment_ValidId_ShouldReturnOk() throws Exception {
        Payment existingPayment = new Payment();
        existingPayment.setId(1L);
        existingPayment.setStatus(PaymentStatus.PENDING);

        Payment updateRequest = new Payment();
        updateRequest.setOrderId(1L);
        updateRequest.setAmount(new BigDecimal("150.00"));
        updateRequest.setPaymentMethod("DEBIT_CARD");

        Payment updatedPayment = new Payment();
        updatedPayment.setId(1L);
        updatedPayment.setOrderId(1L);
        updatedPayment.setAmount(new BigDecimal("150.00"));
        updatedPayment.setPaymentMethod("DEBIT_CARD");

        when(paymentService.findById(1L)).thenReturn(Optional.of(existingPayment));
        when(paymentService.save(any(Payment.class))).thenReturn(updatedPayment);

        mockMvc.perform(put("/api/payments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(150.00));
    }

    @Test
    void updatePayment_NotFound_ShouldReturnNotFound() throws Exception {
        Payment updateRequest = new Payment();
        updateRequest.setOrderId(1L);
        updateRequest.setAmount(new BigDecimal("150.00"));
        updateRequest.setPaymentMethod("DEBIT_CARD");

        when(paymentService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/payments/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePayment_ValidId_ShouldReturnNoContent() throws Exception {
        Payment existingPayment = new Payment();
        existingPayment.setId(1L);

        when(paymentService.findById(1L)).thenReturn(Optional.of(existingPayment));
        doNothing().when(paymentService).deleteById(1L);

        mockMvc.perform(delete("/api/payments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePayment_NotFound_ShouldReturnNotFound() throws Exception {
        when(paymentService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/payments/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void processPaymentById_FailedStatus_ShouldReturnOk() throws Exception {
        // Test the non-SUCCESS/non-COMPLETED branch in line 45 - covers missed branch
        // Since COMPLETED doesn't exist in PaymentStatus enum, test with FAILED status
        Payment failedPayment = new Payment();
        failedPayment.setId(1L);
        failedPayment.setStatus(PaymentStatus.FAILED);

        Payment updatedPayment = new Payment();
        updatedPayment.setId(1L);
        updatedPayment.setStatus(PaymentStatus.SUCCESS);

        when(paymentService.findById(1L)).thenReturn(Optional.of(failedPayment));
        when(paymentService.save(any(Payment.class))).thenReturn(updatedPayment);

        mockMvc.perform(post("/api/payments/1/process")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void refundPayment_FailedStatus_ShouldReturnBadRequest() throws Exception {
        // Test the non-SUCCESS/non-COMPLETED branch in line 116 - covers missed branch
        // Since COMPLETED doesn't exist in PaymentStatus enum, test with FAILED status
        Payment failedPayment = new Payment();
        failedPayment.setId(1L);
        failedPayment.setOrderId(1L);
        failedPayment.setAmount(new BigDecimal("100.00"));
        failedPayment.setPaymentMethod("CREDIT_CARD");
        failedPayment.setStatus(PaymentStatus.FAILED);
        failedPayment.setTransactionId("txn123");

        when(paymentService.findById(1L)).thenReturn(Optional.of(failedPayment));

        mockMvc.perform(post("/api/payments/1/refund"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Can only refund successful payments"));

        verify(paymentService).findById(1L);
    }

    @Test
    void processPaymentById_CompletedStatus_ShouldReturnBadRequest() throws Exception {
        // Test the COMPLETED branch in line 45 - covers the second part of OR condition
        // Create a mock Payment that returns "COMPLETED" from getStatus().toString()
        Payment completedPayment = mock(Payment.class);
        when(completedPayment.getStatus()).thenReturn(PaymentStatus.SUCCESS);
        // Override toString to return "COMPLETED"
        PaymentStatus mockStatus = mock(PaymentStatus.class);
        when(mockStatus.toString()).thenReturn("COMPLETED");
        when(completedPayment.getStatus()).thenReturn(mockStatus);

        when(paymentService.findById(1L)).thenReturn(Optional.of(completedPayment));

        mockMvc.perform(post("/api/payments/1/process"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Payment already processed"));
    }

    @Test
    void refundPayment_CompletedStatus_ShouldReturnOk() throws Exception {
        // Test the COMPLETED branch in line 116 - covers the second part of AND condition
        // Create a payment with COMPLETED status (mocked)
        Payment completedPayment = mock(Payment.class);
        PaymentStatus mockStatus = mock(PaymentStatus.class);
        when(mockStatus.toString()).thenReturn("COMPLETED");
        when(completedPayment.getStatus()).thenReturn(mockStatus);
        when(completedPayment.getId()).thenReturn(1L);

        Payment refundedPayment = new Payment();
        refundedPayment.setId(1L);
        refundedPayment.setStatus(PaymentStatus.REFUNDED);

        when(paymentService.findById(1L)).thenReturn(Optional.of(completedPayment));
        when(paymentService.save(any(Payment.class))).thenReturn(refundedPayment);

        mockMvc.perform(post("/api/payments/1/refund"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REFUNDED"));
    }
}