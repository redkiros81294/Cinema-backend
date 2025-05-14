package com.cinema.dto.admin;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class CinemaAnalyticsResponse {
    private Long cinemaId;
    private String cinemaName;
    private long totalBookings;
    private BigDecimal totalRevenue;
    private BigDecimal averageRevenuePerBooking;
    private double occupancyRate; // Percentage of seats booked
    private int totalScreens;
    private int activeScreens;
} 