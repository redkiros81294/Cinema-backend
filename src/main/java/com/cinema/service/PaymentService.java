package com.cinema.service;

import com.cinema.dto.payment.PaymentRequest;
import com.cinema.dto.payment.PaymentResponse;


import java.util.List;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest request);
    
    PaymentResponse getPayment(Long paymentId);
    
    PaymentResponse getPaymentByStripeId(String stripePaymentId);
    
    List<PaymentResponse> getPaymentsByBooking(Long bookingId);
    
    List<PaymentResponse> getPaymentsByUser(Long userId);
    
    PaymentResponse confirmPayment(String stripePaymentId);
    
    PaymentResponse cancelPayment(Long paymentId);
    
    PaymentResponse refundPayment(Long paymentId, String reason);
    
    void handleWebhook(String payload, String signature);
    
    void processPendingPayments();
} 