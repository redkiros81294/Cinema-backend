package com.cinema.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingData {
    private Long bookingId;
    private Long movieId;
    private Long cinemaId;
    private String showTime;
} 