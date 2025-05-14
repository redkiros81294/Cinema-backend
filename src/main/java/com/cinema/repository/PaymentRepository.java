package com.cinema.repository;

import com.cinema.model.entity.Payment;
import com.cinema.model.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBookingIdOrderByCreatedAtDesc(Long bookingId);
    
    Optional<Payment> findByStripePaymentId(String stripePaymentId);
    
    List<Payment> findByStatusAndCreatedAtBefore(PaymentStatus status, LocalDateTime before);
    
    @Query("SELECT p FROM Payment p WHERE p.booking.user.id = ?1 ORDER BY p.createdAt DESC")
    List<Payment> findByUserId(Long userId);
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = ?1 AND p.createdAt BETWEEN ?2 AND ?3")
    BigDecimal sumAmountByStatusAndDateBetween(PaymentStatus status, LocalDateTime start, LocalDateTime end);
} 