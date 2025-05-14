package com.cinema.repository;

import com.cinema.model.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    @Query("SELECT s FROM Showtime s WHERE s.movie.id = :movieId AND s.active = true AND s.deleted = false")
    List<Showtime> findByMovieId(Long movieId);

    @Query("SELECT s FROM Showtime s WHERE s.screen.cinema.id = :cinemaId AND s.active = true AND s.deleted = false")
    List<Showtime> findByCinemaId(Long cinemaId);

    @Query("SELECT s FROM Showtime s WHERE s.movie.id = :movieId AND s.screen.cinema.id = :cinemaId AND s.showTime >= :startTime AND s.active = true AND s.deleted = false")
    List<Showtime> findByMovieAndCinemaAndDateRange(Long movieId, Long cinemaId, LocalDateTime startTime);

    @Query("SELECT s FROM Showtime s WHERE s.showTime >= :startTime AND s.active = true AND s.deleted = false")
    List<Showtime> findUpcomingShowtimes(LocalDateTime startTime);
} 