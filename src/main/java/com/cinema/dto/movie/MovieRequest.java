package com.cinema.dto.movie;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.Duration;

import java.util.List;

@Data
public class MovieRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private Duration duration;

    private String genre;

    private String language;

    private LocalDate releaseDate;

    private Double price;

    private String posterUrl;

    private String trailerUrl;

    @NotNull(message = "Cinema ID is required")
    private Long cinemaId;

    @NotNull(message = "Show times are required")
    private List<String> showTimes; // List of ISO-8601 formatted dates
} 