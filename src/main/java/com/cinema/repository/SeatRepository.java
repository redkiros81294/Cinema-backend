package com.cinema.repository;

import com.cinema.model.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Query("SELECT s FROM Seat s WHERE s.showtime.id = :showtimeId AND s.deleted = false")
    List<Seat> findByShowtimeId(Long showtimeId);

    @Query("SELECT s FROM Seat s WHERE s.showtime.id = :showtimeId AND s.available = true AND s.deleted = false")
    List<Seat> findAvailableSeatsByShowtimeId(Long showtimeId);

    @Query("SELECT COUNT(s) FROM Seat s WHERE s.showtime.id = :showtimeId AND s.available = true AND s.deleted = false")
    long countAvailableSeatsByShowtimeId(Long showtimeId);
} 