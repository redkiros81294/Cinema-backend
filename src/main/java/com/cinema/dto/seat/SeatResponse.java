package com.cinema.dto.seat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeatResponse {
    private Long id;
    private Long showtimeId;
    private String seatNumber;
    private String rowNumber;
    private Double price;
    private boolean isReserved;
    private String seatType;
    private String status; // e.g., "AVAILABLE", "RESERVED", "BOOKED", "MAINTENANCE"
} 