package com.cinema.dto.showtime;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ShowtimeResponse {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private Long cinemaId;
    private String cinemaName;
    private LocalDateTime showTime;
    private String hallNumber;
    private Double price;
    private Integer totalSeats;
    private Integer availableSeats;
    private boolean isActive;
} 