package com.cinema.repository;

import com.cinema.model.entity.Booking;
import com.cinema.model.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.deleted = false ORDER BY b.createdAt DESC")
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.deleted = false")
    List<Booking> findActiveByUserId(Long userId);
    
    @Query("SELECT b FROM Booking b WHERE b.showtime.movie.id = :movieId AND b.showtime.showTime = :showTime AND b.status = :status AND b.deleted = false")
    List<Booking> findByMovieAndShowTimeAndStatus(Long movieId, LocalDateTime showTime, BookingStatus status);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.showtime.movie.id = :movieId AND b.showtime.showTime = :showTime AND b.status = :status AND b.deleted = false")
    long countByMovieAndShowTimeAndStatus(Long movieId, LocalDateTime showTime, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.showtime.showTime BETWEEN :start AND :end AND b.status = :status AND b.deleted = false")
    List<Booking> findByShowTimeBetweenAndStatus(LocalDateTime start, LocalDateTime end, BookingStatus status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.showtime.showTime BETWEEN :start AND :end AND b.status = :status AND b.deleted = false")
    long countByShowTimeBetweenAndStatus(LocalDateTime start, LocalDateTime end, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.showtime.movie.id = :movieId AND b.deleted = false")
    List<Booking> findByMovieId(Long movieId);

    @Query("SELECT b FROM Booking b WHERE b.showtime.screen.cinema.id = :cinemaId AND b.deleted = false")
    List<Booking> findByCinemaId(Long cinemaId);

    @Query("SELECT b FROM Booking b WHERE b.createdAt BETWEEN :start AND :end AND b.status = :status AND b.deleted = false")
    List<Booking> findByCreatedAtBetweenAndStatus(LocalDateTime start, LocalDateTime end, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.createdAt BETWEEN :start AND :end AND b.deleted = false")
    List<Booking> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT b FROM Booking b WHERE b.status = :status AND b.createdAt BETWEEN :start AND :end AND b.deleted = false")
    List<Booking> findByStatusAndCreatedAtBetween(BookingStatus status, LocalDateTime start, LocalDateTime end);
} 