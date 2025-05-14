package com.cinema.repository;

import com.cinema.model.entity.Screen;
import com.cinema.model.entity.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {
    List<Screen> findByCinema(Cinema cinema);
    List<Screen> findByCinemaAndActiveTrue(Cinema cinema);
    long countByCinemaAndActiveTrue(Cinema cinema);
    boolean existsByNameAndCinema(String name, Cinema cinema);

    @Query("SELECT s FROM Screen s WHERE s.cinema.id = :cinemaId AND s.name = :name AND s.deleted = false")
    Optional<Screen> findByCinemaIdAndName(Long cinemaId, String name);

    @Query("SELECT s FROM Screen s WHERE s.cinema.id = :cinemaId AND s.deleted = false")
    List<Screen> findByCinemaId(Long cinemaId);

    @Query("SELECT COUNT(s) FROM Screen s WHERE s.cinema.id = :cinemaId AND s.deleted = false")
    long countByCinemaId(Long cinemaId);
} 