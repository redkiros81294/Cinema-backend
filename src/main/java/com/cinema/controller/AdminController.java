package com.cinema.controller;

import com.cinema.dto.booking.BookingResponse;
import com.cinema.dto.seat.SeatRequest;
import com.cinema.dto.seat.SeatResponse;
import com.cinema.dto.showtime.ShowtimeRequest;
import com.cinema.dto.showtime.ShowtimeResponse;
import com.cinema.dto.user.UserResponse;
import com.cinema.model.entity.UserRole;
import com.cinema.service.BookingService;
import com.cinema.service.AdminService;
import com.cinema.service.UserService;
import com.cinema.service.ShowtimeService;
import com.cinema.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin management APIs")
public class AdminController {
    private final BookingService bookingService;
    private final AdminService adminService;
    private final UserService userService;
    private final ShowtimeService showtimeService;
    private final SeatService seatService;

    // Booking Verification
    @GetMapping("/bookings/verify/{bookingId}")
    @Operation(summary = "Verify a booking")
    public ResponseEntity<BookingResponse> verifyBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getBooking(bookingId));
    }

    @PostMapping("/bookings/validate")
    @Operation(summary = "Validate a booking")
    public ResponseEntity<Boolean> validateBooking(
            @RequestParam String qrCode,
            @RequestParam Long cinemaId) {
        return ResponseEntity.ok(bookingService.validateBooking(qrCode, cinemaId));
    }

    // User Management
    @GetMapping("/users")
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/users/{id}/role")
    @Operation(summary = "Update user role")
    public ResponseEntity<UserResponse> updateUserRole(@PathVariable Long id, @RequestParam String role) {
        return ResponseEntity.ok(userService.updateUserRole(id, UserRole.valueOf(role.toUpperCase())));
    }

    @PutMapping("/users/{userId}/status")
    @Operation(summary = "Update user status")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam boolean active) {
        return ResponseEntity.ok(userService.updateUserStatus(userId, active));
    }

    // Showtime Management
    @PostMapping("/showtimes")
    @Operation(summary = "Create a new showtime")
    public ResponseEntity<ShowtimeResponse> createShowtime(@Valid @RequestBody ShowtimeRequest request) {
        return ResponseEntity.ok(showtimeService.createShowtime(request));
    }

    @PutMapping("/showtimes/{id}")
    @Operation(summary = "Update a showtime")
    public ResponseEntity<ShowtimeResponse> updateShowtime(
            @PathVariable Long id,
            @Valid @RequestBody ShowtimeRequest request) {
        return ResponseEntity.ok(showtimeService.updateShowtime(id, request));
    }

    @DeleteMapping("/showtimes/{id}")
    @Operation(summary = "Delete a showtime")
    public ResponseEntity<Void> deleteShowtime(@PathVariable Long id) {
        showtimeService.deleteShowtime(id);
        return ResponseEntity.ok().build();
    }

    // Seat Management
    @PostMapping("/seats")
    @Operation(summary = "Create seats for a showtime")
    public ResponseEntity<List<SeatResponse>> createSeats(@Valid @RequestBody SeatRequest request) {
        return ResponseEntity.ok(seatService.createSeats(request));
    }

    @PutMapping("/seats/{id}")
    @Operation(summary = "Update a seat")
    public ResponseEntity<SeatResponse> updateSeat(
            @PathVariable Long id,
            @Valid @RequestBody SeatRequest request) {
        return ResponseEntity.ok(seatService.updateSeat(id, request));
    }

    // Analytics and Reports
    @GetMapping("/analytics/revenue")
    @Operation(summary = "Get revenue analytics")
    public ResponseEntity<Map<String, Double>> getRevenueAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(adminService.getRevenueAnalytics(startDate, endDate));
    }

    @GetMapping("/analytics/bookings")
    @Operation(summary = "Get booking analytics")
    public ResponseEntity<Map<String, Long>> getBookingAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(adminService.getBookingAnalytics(startDate, endDate));
    }

    @GetMapping("/analytics/movies")
    @Operation(summary = "Get movie analytics")
    public ResponseEntity<List<Map<String, Object>>> getMovieAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(adminService.getMovieAnalytics(startDate, endDate));
    }

    @GetMapping("/analytics/cinemas")
    @Operation(summary = "Get cinema analytics")
    public ResponseEntity<List<Map<String, Object>>> getCinemaAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(adminService.getCinemaAnalytics(startDate, endDate));
    }

    // Enhanced Analytics
    @GetMapping("/analytics/dashboard")
    @Operation(summary = "Get dashboard statistics")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/reports/revenue")
    @Operation(summary = "Export revenue report")
    public ResponseEntity<byte[]> exportRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "PDF") String format) {
        return adminService.exportRevenueReport(startDate, endDate, format);
    }

    @GetMapping("/reports/bookings")
    @Operation(summary = "Export booking report")
    public ResponseEntity<byte[]> exportBookingReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "PDF") String format) {
        return adminService.exportBookingReport(startDate, endDate, format);
    }

    // System Settings
    @GetMapping("/settings")
    @Operation(summary = "Get system settings")
    public ResponseEntity<Map<String, Object>> getSystemSettings() {
        return ResponseEntity.ok(adminService.getSystemSettings());
    }

    @PutMapping("/settings")
    @Operation(summary = "Update system settings")
    public ResponseEntity<Map<String, Object>> updateSystemSettings(
            @RequestBody Map<String, Object> settings) {
        return ResponseEntity.ok(adminService.updateSystemSettings(settings));
    }
} 