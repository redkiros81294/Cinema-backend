package com.cinema.service.impl;

import com.cinema.model.entity.*;
import com.cinema.repository.*;
import com.cinema.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {
    private final BookingRepository bookingRepository;
    private final MovieRepository movieRepository;
    private final CinemaRepository cinemaRepository;
    private final UserRepository userRepository;
    private final ScreenRepository screenRepository;

    @Override
    public Map<String, Double> getRevenueAnalytics(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        List<Booking> bookings = bookingRepository.findByCreatedAtBetweenAndStatus(start, end, BookingStatus.CONFIRMED);
        
        Map<String, Double> analytics = new TreeMap<>();
        bookings.forEach(booking -> {
            String date = booking.getCreatedAt().toLocalDate().toString();
            analytics.merge(date, booking.getTotalPrice().doubleValue(), Double::sum);
        });
        
        return analytics;
    }

    @Override
    public Map<String, Long> getBookingAnalytics(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        List<Booking> bookings = bookingRepository.findByCreatedAtBetweenAndStatus(start, end, BookingStatus.CONFIRMED);
        
        Map<String, Long> analytics = new TreeMap<>();
        bookings.forEach(booking -> {
            String date = booking.getCreatedAt().toLocalDate().toString();
            analytics.merge(date, 1L, Long::sum);
        });
        
        return analytics;
    }

    @Override
    public List<Map<String, Object>> getMovieAnalytics(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        List<Booking> bookings = bookingRepository.findByCreatedAtBetweenAndStatus(start, end, BookingStatus.CONFIRMED);
        
        Map<Movie, List<Booking>> movieBookings = bookings.stream()
            .collect(Collectors.groupingBy(booking -> booking.getShowtime().getMovie()));
        
        return movieBookings.entrySet().stream()
            .map(entry -> {
                Map<String, Object> analytics = new HashMap<>();
                Movie movie = entry.getKey();
                List<Booking> movieBookingList = entry.getValue();
                
                analytics.put("movieId", movie.getId());
                analytics.put("title", movie.getTitle());
                analytics.put("totalBookings", movieBookingList.size());
                analytics.put("totalRevenue", calculateTotalRevenue(movieBookingList));
                analytics.put("averageOccupancy", calculateAverageOccupancyRate(movieBookingList));
                
                return analytics;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getCinemaAnalytics(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        List<Booking> bookings = bookingRepository.findByCreatedAtBetweenAndStatus(start, end, BookingStatus.CONFIRMED);
        
        Map<Cinema, List<Booking>> cinemaBookings = bookings.stream()
            .collect(Collectors.groupingBy(booking -> booking.getShowtime().getScreen().getCinema()));
        
        return cinemaBookings.entrySet().stream()
            .map(entry -> {
                Map<String, Object> analytics = new HashMap<>();
                Cinema cinema = entry.getKey();
                List<Booking> cinemaBookingList = entry.getValue();
                
                analytics.put("cinemaId", cinema.getId());
                analytics.put("name", cinema.getName());
                analytics.put("totalBookings", cinemaBookingList.size());
                analytics.put("totalRevenue", calculateTotalRevenue(cinemaBookingList));
                analytics.put("averageOccupancy", calculateAverageOccupancyRate(cinemaBookingList));
                
                return analytics;
            })
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getDashboardStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime startOfWeek = now.minusDays(7).toLocalDate().atStartOfDay();
        LocalDateTime startOfMonth = now.minusDays(30).toLocalDate().atStartOfDay();

        // Get bookings for different time periods
        List<Booking> todayBookings = bookingRepository.findByCreatedAtBetweenAndStatus(
            startOfDay, now, BookingStatus.CONFIRMED);
        List<Booking> weeklyBookings = bookingRepository.findByCreatedAtBetweenAndStatus(
            startOfWeek, now, BookingStatus.CONFIRMED);
        List<Booking> monthlyBookings = bookingRepository.findByCreatedAtBetweenAndStatus(
            startOfMonth, now, BookingStatus.CONFIRMED);

        Map<String, Object> stats = new HashMap<>();
        
        // Revenue stats
        stats.put("todayRevenue", calculateTotalRevenue(todayBookings));
        stats.put("weeklyRevenue", calculateTotalRevenue(weeklyBookings));
        stats.put("monthlyRevenue", calculateTotalRevenue(monthlyBookings));
        
        // Booking stats
        stats.put("todayBookings", todayBookings.size());
        stats.put("weeklyBookings", weeklyBookings.size());
        stats.put("monthlyBookings", monthlyBookings.size());
        
        // Active counts
        stats.put("activeUsers", userRepository.countByDeletedFalse());
        stats.put("activeMovies", movieRepository.countByActive(true));
        stats.put("activeCinemas", cinemaRepository.countByActiveTrue());
        
        // Screen stats
        List<Screen> allScreens = screenRepository.findAll();
        stats.put("totalScreens", allScreens.size());
        stats.put("activeScreens", allScreens.stream().filter(Screen::isActive).count());
        
        // Occupancy stats
        stats.put("averageOccupancyRate", calculateAverageOccupancyRate(monthlyBookings));
        
        return stats;
    }

    @Override
    public ResponseEntity<byte[]> exportRevenueReport(LocalDate startDate, LocalDate endDate, String format) {
        Map<String, Double> analytics = getRevenueAnalytics(startDate, endDate);
        byte[] report = generateReport(analytics, "Revenue Report", format);
        
        String filename = "revenue-report-" + startDate + "-to-" + endDate + "." + format.toLowerCase();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(report);
    }

    @Override
    public ResponseEntity<byte[]> exportBookingReport(LocalDate startDate, LocalDate endDate, String format) {
        Map<String, Long> analytics = getBookingAnalytics(startDate, endDate);
        byte[] report = generateReport(analytics, "Booking Report", format);
        
        String filename = "booking-report-" + startDate + "-to-" + endDate + "." + format.toLowerCase();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(report);
    }

    @Override
    public Map<String, Object> getSystemSettings() {
        Map<String, Object> settings = new HashMap<>();
        
        // Get system statistics
        settings.put("totalUsers", userRepository.count());
        settings.put("activeUsers", userRepository.countByDeletedFalse());
        settings.put("totalMovies", movieRepository.count());
        settings.put("activeMovies", movieRepository.countByActive(true));
        settings.put("totalCinemas", cinemaRepository.count());
        settings.put("activeCinemas", cinemaRepository.countByActiveTrue());
        settings.put("totalScreens", screenRepository.count());
        
        // Get booking statistics
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.minusDays(30).toLocalDate().atStartOfDay();
        List<Booking> monthlyBookings = bookingRepository.findByCreatedAtBetweenAndStatus(
            startOfMonth, now, BookingStatus.CONFIRMED);
        
        settings.put("monthlyBookings", monthlyBookings.size());
        settings.put("monthlyRevenue", calculateTotalRevenue(monthlyBookings));
        settings.put("averageOccupancyRate", calculateAverageOccupancyRate(monthlyBookings));
        
        return settings;
    }

    @Override
    public Map<String, Object> updateSystemSettings(Map<String, Object> settings) {
        // Validate settings
        if (settings == null || settings.isEmpty()) {
            throw new IllegalArgumentException("Settings cannot be null or empty");
        }

        Map<String, Object> updatedSettings = new HashMap<>();
        
        // Process each setting
        for (Map.Entry<String, Object> entry : settings.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            switch (key) {
                case "maintenanceMode":
                    if (value instanceof Boolean) {
                        // In a real application, this would update a system-wide flag
                        updatedSettings.put(key, value);
                    } else {
                        throw new IllegalArgumentException("maintenanceMode must be a boolean");
                    }
                    break;
                    
                case "maxBookingsPerUser":
                    if (value instanceof Integer && (Integer) value > 0) {
                        // In a real application, this would update a system-wide limit
                        updatedSettings.put(key, value);
                    } else {
                        throw new IllegalArgumentException("maxBookingsPerUser must be a positive integer");
                    }
                    break;
                    
                case "bookingCancellationWindow":
                    if (value instanceof Integer && (Integer) value >= 0) {
                        // In a real application, this would update the cancellation window in minutes
                        updatedSettings.put(key, value);
                    } else {
                        throw new IllegalArgumentException("bookingCancellationWindow must be a non-negative integer");
                    }
                    break;
                    
                default:
                    throw new IllegalArgumentException("Unknown setting: " + key);
            }
        }
        
        // In a real application, you would persist these settings to a database
        // and possibly trigger system-wide updates
        
        return updatedSettings;
    }

    private BigDecimal calculateTotalRevenue(List<Booking> bookings) {
        return bookings.stream()
            .map(Booking::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private double calculateAverageOccupancyRate(List<Booking> bookings) {
        if (bookings.isEmpty()) {
            return 0.0;
        }

        double totalOccupancy = bookings.stream()
            .mapToDouble(booking -> {
                int totalSeats = booking.getShowtime().getScreen().getTotalSeats();
                int bookedSeats = booking.getSeats().size();
                return (double) bookedSeats / totalSeats;
            })
            .sum();

        return (totalOccupancy / bookings.size()) * 100;
    }

    private byte[] generateReport(Object data, String title, String format) {
        StringBuilder report = new StringBuilder();
        report.append(title).append("\n\n");
        
        if (data instanceof Map) {
            ((Map<?, ?>) data).forEach((key, value) -> 
                report.append(key).append(": ").append(value).append("\n"));
        }
        
        return report.toString().getBytes();
    }
} 