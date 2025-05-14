package com.cinema.dto.booking;

import com.cinema.model.entity.BookingStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long movieId;
    private String movieTitle;
    private Long cinemaId;
    private String cinemaName;
    private LocalDateTime showTime;
    private String seatLayout;
    private BookingStatus status;
    private String qrCode;
    private LocalDateTime createdAt;
    private boolean deleted;
} 