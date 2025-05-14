package com.cinema.service;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;

/**
 * Service interface for admin operations including analytics and reporting.
 */
public interface AdminService {
    /**
     * Get revenue analytics for a specific date range.
     * @param startDate Start date for analytics
     * @param endDate End date for analytics
     * @return Map of revenue analytics
     */
    Map<String, Double> getRevenueAnalytics(LocalDate startDate, LocalDate endDate);

    /**
     * Get booking analytics for a specific date range.
     * @param startDate Start date for analytics
     * @param endDate End date for analytics
     * @return Map of booking analytics
     */
    Map<String, Long> getBookingAnalytics(LocalDate startDate, LocalDate endDate);

    /**
     * Get movie analytics for a specific date range.
     * @param startDate Start date for analytics
     * @param endDate End date for analytics
     * @return List of movie analytics
     */
    List<Map<String, Object>> getMovieAnalytics(LocalDate startDate, LocalDate endDate);

    /**
     * Get cinema analytics for a specific date range.
     * @param startDate Start date for analytics
     * @param endDate End date for analytics
     * @return List of cinema analytics
     */
    List<Map<String, Object>> getCinemaAnalytics(LocalDate startDate, LocalDate endDate);

    /**
     * Get dashboard statistics.
     * @return Dashboard statistics
     */
    Map<String, Object> getDashboardStats();

    /**
     * Export booking report for a specific date range.
     * @param startDate Start date for report
     * @param endDate End date for report
     * @param format Report format (PDF, CSV, etc.)
     * @return Report data as byte array
     */
    ResponseEntity<byte[]> exportBookingReport(LocalDate startDate, LocalDate endDate, String format);

    /**
     * Export revenue report for a specific date range.
     * @param startDate Start date for report
     * @param endDate End date for report
     * @param format Report format (PDF, CSV, etc.)
     * @return Report data as byte array
     */
    ResponseEntity<byte[]> exportRevenueReport(LocalDate startDate, LocalDate endDate, String format);

    Map<String, Object> getSystemSettings();
    Map<String, Object> updateSystemSettings(Map<String, Object> settings);
} 