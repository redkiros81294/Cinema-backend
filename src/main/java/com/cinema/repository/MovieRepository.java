package com.cinema.repository;

import com.cinema.model.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByCinemaId(Long cinemaId);
    
    @Query("SELECT m FROM Movie m WHERE m.title LIKE %:title% AND m.deleted = false")
    List<Movie> searchByTitle(String title);
    
    @Query("SELECT m FROM Movie m WHERE m.cinema.id = :cinemaId AND m.deleted = false")
    List<Movie> findActiveByCinemaId(Long cinemaId);
    
    boolean existsByTitleAndCinemaId(String title, Long cinemaId);

    List<Movie> findByTitleContainingIgnoreCase(String title);

    long countByActive(boolean active);

    boolean existsByTitle(String title);

    @Query("SELECT m FROM Movie m WHERE m.active = true AND m.deleted = false")
    List<Movie> findByActiveTrueAndDeletedFalse();

    @Query("SELECT m FROM Movie m WHERE m.cinema.id = :cinemaId AND m.active = true AND m.deleted = false")
    List<Movie> findByCinemaIdAndActiveTrueAndDeletedFalse(Long cinemaId);

    @Query("SELECT m FROM Movie m WHERE m.title LIKE %:query% AND m.active = true AND m.deleted = false")
    List<Movie> findByTitleContainingIgnoreCaseAndActiveTrueAndDeletedFalse(String query);
} 