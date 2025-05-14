package com.cinema.controller;

import com.cinema.dto.booking.BookingRequest;
import com.cinema.dto.booking.BookingResponse;
import com.cinema.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestAttribute("userId") Long userId,
            @RequestBody BookingRequest request) {
        return ResponseEntity.ok(bookingService.createBooking(userId, request));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.cancelBooking(bookingId));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getBooking(bookingId));
    }

    @GetMapping("/user")
    public ResponseEntity<List<BookingResponse>> getUserBookings(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(bookingService.getUserBookings(userId));
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateBooking(
            @RequestParam String qrCode,
            @RequestParam Long cinemaId) {
        return ResponseEntity.ok(bookingService.validateBooking(qrCode, cinemaId));
    }
} 