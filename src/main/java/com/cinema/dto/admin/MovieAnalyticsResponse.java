package com.cinema.dto.admin;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class MovieAnalyticsResponse {
    private Long movieId;
    private String movieTitle;
    private long totalBookings;
    private BigDecimal totalRevenue;
    private BigDecimal averageRevenuePerBooking;
    private double occupancyRate; // Percentage of seats booked
} 