package com.cinema.dto.showtime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShowtimeRequest {
    @NotNull(message = "Movie ID is required")
    private Long movieId;

    @NotNull(message = "Cinema ID is required")
    private Long cinemaId;

    @NotNull(message = "Hall number is required")
    private String hallNumber;

    @NotNull(message = "Show time is required")
    private LocalDateTime showTime;

    private boolean active = true;
} 