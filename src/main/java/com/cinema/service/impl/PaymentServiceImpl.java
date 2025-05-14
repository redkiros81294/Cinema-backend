package com.cinema.service.impl;

import com.cinema.dto.payment.PaymentRequest;
import com.cinema.dto.payment.PaymentResponse;
import com.cinema.dto.notification.NotificationRequest;
import com.cinema.model.entity.Booking;
import com.cinema.model.entity.Payment;
import com.cinema.model.entity.PaymentStatus;
import com.cinema.model.entity.NotificationType;
import com.cinema.repository.BookingRepository;
import com.cinema.repository.PaymentRepository;
import com.cinema.service.NotificationService;
import com.cinema.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cinema.exception.PaymentException;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.exception.BusinessException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {
        try {
            Stripe.apiKey = stripeSecretKey;

            Booking booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", request.getBookingId()));

            if (booking.getUser().getStripeCustomerId() == null) {
                throw new BusinessException("User does not have a Stripe customer ID", "MISSING_STRIPE_CUSTOMER");
            }

            // Create PaymentIntent
            Map<String, String> metadata = new HashMap<>();
            metadata.put("bookingId", booking.getId().toString());
            metadata.put("userId", booking.getUser().getId().toString());

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(request.getAmount().multiply(new java.math.BigDecimal("100")).longValue())
                    .setCurrency(request.getCurrency().toLowerCase())
                    .setPaymentMethod(request.getPaymentMethod())
                    .setConfirm(true)
                    .setDescription(request.getDescription())
                    .setCustomer(booking.getUser().getStripeCustomerId())
                    .putAllMetadata(metadata)
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Create payment record
            Payment payment = Payment.builder()
                    .booking(booking)
                    .stripePaymentId(paymentIntent.getId())
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .status(PaymentStatus.PROCESSING)
                    .paymentMethod(request.getPaymentMethod())
                    .build();

            payment = paymentRepository.save(payment);

            // Send notification
            NotificationRequest notificationRequest = new NotificationRequest();
            notificationRequest.setUserId(booking.getUser().getId());
            notificationRequest.setTitle("Payment Processing");
            notificationRequest.setMessage("Your payment for booking #" + booking.getId() + " is being processed.");
            notificationRequest.setType(NotificationType.PAYMENT_PROCESSING);
            notificationService.createNotification(notificationRequest);

            return convertToResponse(payment, paymentIntent.getClientSecret());
        } catch (StripeException e) {
            throw new PaymentException("Failed to create payment: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));
        return convertToResponse(payment, null);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByStripeId(String stripePaymentId) {
        Payment payment = paymentRepository.findByStripePaymentId(stripePaymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "stripePaymentId", stripePaymentId));
        return convertToResponse(payment, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByBooking(Long bookingId) {
        return paymentRepository.findByBookingIdOrderByCreatedAtDesc(bookingId)
                .stream()
                .map(payment -> convertToResponse(payment, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByUser(Long userId) {
        return paymentRepository.findByUserId(userId)
                .stream()
                .map(payment -> convertToResponse(payment, null))
                .collect(Collectors.toList());
    }

    @Override
    public PaymentResponse confirmPayment(String stripePaymentId) {
        try {
            Stripe.apiKey = stripeSecretKey;

            Payment payment = paymentRepository.findByStripePaymentId(stripePaymentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment", "stripePaymentId", stripePaymentId));

            PaymentIntent paymentIntent = PaymentIntent.retrieve(stripePaymentId);

            if ("succeeded".equals(paymentIntent.getStatus())) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setCompletedAt(LocalDateTime.now());
                payment = paymentRepository.save(payment);

                // Send notification
                NotificationRequest notificationRequest = new NotificationRequest();
                notificationRequest.setUserId(payment.getBooking().getUser().getId());
                notificationRequest.setTitle("Payment Confirmed");
                notificationRequest.setMessage("Your payment for booking #" + payment.getBooking().getId() + " has been confirmed.");
                notificationRequest.setType(NotificationType.PAYMENT_CONFIRMED);
                notificationService.createNotification(notificationRequest);

                return convertToResponse(payment, null);
            } else {
                throw new PaymentException("Payment not succeeded: " + paymentIntent.getStatus());
            }
        } catch (StripeException e) {
            throw new PaymentException("Failed to confirm payment: " + e.getMessage(), e);
        }
    }

    @Override
    public PaymentResponse cancelPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

        if (payment.getStatus() != PaymentStatus.PENDING && payment.getStatus() != PaymentStatus.PROCESSING) {
            throw new BusinessException("Cannot cancel payment in status: " + payment.getStatus(), "INVALID_PAYMENT_STATUS");
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        payment = paymentRepository.save(payment);

        // Send notification
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setUserId(payment.getBooking().getUser().getId());
        notificationRequest.setTitle("Payment Cancelled");
        notificationRequest.setMessage("Your payment for booking #" + payment.getBooking().getId() + " has been cancelled.");
        notificationRequest.setType(NotificationType.PAYMENT_FAILED);
        notificationService.createNotification(notificationRequest);

        return convertToResponse(payment, null);
    }

    @Override
    public PaymentResponse refundPayment(Long paymentId, String reason) {
        try {
            Stripe.apiKey = stripeSecretKey;

            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

            if (payment.getStatus() != PaymentStatus.COMPLETED) {
                throw new BusinessException("Cannot refund payment in status: " + payment.getStatus(), "INVALID_PAYMENT_STATUS");
            }

            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(payment.getStripePaymentId())
                    .setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER)
                    .build();

            Refund.create(params);

            payment.setStatus(PaymentStatus.REFUNDED);
            payment = paymentRepository.save(payment);

            // Send notification
            NotificationRequest notificationRequest = new NotificationRequest();
            notificationRequest.setUserId(payment.getBooking().getUser().getId());
            notificationRequest.setTitle("Payment Refunded");
            notificationRequest.setMessage("Your payment for booking #" + payment.getBooking().getId() + " has been refunded.");
            notificationRequest.setType(NotificationType.PAYMENT_REFUNDED);
            notificationService.createNotification(notificationRequest);

            return convertToResponse(payment, null);
        } catch (StripeException e) {
            throw new PaymentException("Failed to refund payment: " + e.getMessage(), e);
        }
    }

    @Override
    public void handleWebhook(String payload, String signature) {
        try {
            Stripe.apiKey = stripeSecretKey;

            Event event = Webhook.constructEvent(payload, signature, stripeWebhookSecret);
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = dataObjectDeserializer.getObject()
                .orElseThrow(() -> new PaymentException("Failed to deserialize Stripe event object", null));

            switch (event.getType()) {
                case "payment_intent.succeeded":
                    handlePaymentIntentSucceeded((PaymentIntent) stripeObject);
                    break;
                case "payment_intent.payment_failed":
                    handlePaymentIntentFailed((PaymentIntent) stripeObject);
                    break;
                case "charge.refunded":
                    handleChargeRefunded((Charge) stripeObject);
                    break;
                default:
                    // Ignore other event types
                    break;
            }
        } catch (Exception e) {
            throw new PaymentException("Failed to handle webhook: " + e.getMessage(), e);
        }
    }

    @Override
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    public void processPendingPayments() {
        LocalDateTime before = LocalDateTime.now().minusMinutes(30);
        paymentRepository.findByStatusAndCreatedAtBefore(PaymentStatus.PROCESSING, before)
                .forEach(payment -> {
                    try {
                        PaymentIntent paymentIntent = PaymentIntent.retrieve(payment.getStripePaymentId());
                        if ("succeeded".equals(paymentIntent.getStatus())) {
                            confirmPayment(payment.getStripePaymentId());
                        } else if ("canceled".equals(paymentIntent.getStatus())) {
                            cancelPayment(payment.getId());
                        }
                    } catch (StripeException e) {
                        // Log the error but don't throw it
                        e.printStackTrace();
                    }
                });
    }

    private void handlePaymentIntentSucceeded(PaymentIntent paymentIntent) {
        Payment payment = paymentRepository.findByStripePaymentId(paymentIntent.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "stripePaymentId", paymentIntent.getId()));

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setCompletedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        // Send notification
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setUserId(payment.getBooking().getUser().getId());
        notificationRequest.setTitle("Payment Confirmed");
        notificationRequest.setMessage("Your payment for booking #" + payment.getBooking().getId() + " has been confirmed.");
        notificationRequest.setType(NotificationType.PAYMENT_CONFIRMED);
        notificationService.createNotification(notificationRequest);
    }

    private void handlePaymentIntentFailed(PaymentIntent paymentIntent) {
        Payment payment = paymentRepository.findByStripePaymentId(paymentIntent.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "stripePaymentId", paymentIntent.getId()));

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(paymentIntent.getLastPaymentError().getMessage());
        paymentRepository.save(payment);

        // Send notification
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setUserId(payment.getBooking().getUser().getId());
        notificationRequest.setTitle("Payment Failed");
        notificationRequest.setMessage("Your payment for booking #" + payment.getBooking().getId() + " has failed: " + 
                paymentIntent.getLastPaymentError().getMessage());
        notificationRequest.setType(NotificationType.PAYMENT_FAILED);
        notificationService.createNotification(notificationRequest);
    }

    private void handleChargeRefunded(Charge charge) {
        Payment payment = paymentRepository.findByStripePaymentId(charge.getPaymentIntent())
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "stripePaymentId", charge.getPaymentIntent()));

        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);

        // Send notification
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setUserId(payment.getBooking().getUser().getId());
        notificationRequest.setTitle("Payment Refunded");
        notificationRequest.setMessage("Your payment for booking #" + payment.getBooking().getId() + " has been refunded.");
        notificationRequest.setType(NotificationType.PAYMENT_REFUNDED);
        notificationService.createNotification(notificationRequest);
    }

    private PaymentResponse convertToResponse(Payment payment, String clientSecret) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setBookingId(payment.getBooking().getId());
        response.setStripePaymentId(payment.getStripePaymentId());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setStatus(payment.getStatus());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setFailureReason(payment.getFailureReason());
        response.setCreatedAt(payment.getCreatedAt());
        response.setUpdatedAt(payment.getUpdatedAt());
        response.setCompletedAt(payment.getCompletedAt());
        response.setClientSecret(clientSecret);
        return response;
    }
} 