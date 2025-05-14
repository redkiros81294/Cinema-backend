package com.cinema.dto.booking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;


@Data
public class BookingRequest {
    @NotNull(message = "Movie ID is required")
    private Long movieId;

    @NotNull(message = "Cinema ID is required")
    private Long cinemaId;

    @NotNull(message = "Show time is required")
    private LocalDateTime showTime;

    @NotBlank(message = "Seat layout is required")
    private String seatLayout; // JSON string of selected seats
} 