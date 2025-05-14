package com.cinema.dto.movie;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.Duration;

@Data
@Builder
public class MovieResponse {
    private Long id;
    private String title;
    private String description;
    private Duration duration;
    private String genre;
    private String language;
    private LocalDate releaseDate;
    private Double price;
    private String posterUrl;
    private String trailerUrl;
} 