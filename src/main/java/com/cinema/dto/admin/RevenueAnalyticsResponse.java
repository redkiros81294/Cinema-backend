package com.cinema.dto.admin;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class RevenueAnalyticsResponse {
    private LocalDate date;
    private BigDecimal revenue;
    private long bookingCount;
    private BigDecimal averageRevenuePerBooking;
} 