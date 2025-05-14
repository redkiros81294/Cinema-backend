package com.cinema.repository;

import com.cinema.model.entity.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, Long> {
    List<Cinema> findByActive(boolean active);
    boolean existsByNameAndLocations(String name, String locations);
    long countByActive(boolean active);
    List<Cinema> findByActiveTrue();
    long countByActiveTrue();
} 