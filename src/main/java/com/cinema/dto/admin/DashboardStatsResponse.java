package com.cinema.dto.admin;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class DashboardStatsResponse {
    // Today's stats
    private long todayBookings;
    private BigDecimal todayRevenue;
    
    // Weekly stats
    private long weeklyBookings;
    private BigDecimal weeklyRevenue;
    
    // Monthly stats
    private long monthlyBookings;
    private BigDecimal monthlyRevenue;
    
    // Active counts
    private long activeUsers;
    private long activeMovies;
    private long activeCinemas;
    
    // System health
    private int totalScreens;
    private int activeScreens;
    private double averageOccupancyRate;
    private double averageRevenuePerBooking;
} 