package com.cinema.service;

import com.cinema.dto.booking.BookingRequest;
import com.cinema.dto.booking.BookingResponse;
import java.util.List;

public interface BookingService {
    BookingResponse createBooking(Long userId, BookingRequest request);
    BookingResponse cancelBooking(Long bookingId);
    BookingResponse getBooking(Long bookingId);
    List<BookingResponse> getUserBookings(Long userId);
    boolean validateBooking(String qrCode, Long cinemaId);
} 