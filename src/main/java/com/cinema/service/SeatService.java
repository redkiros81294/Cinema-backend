package com.cinema.service;

import com.cinema.dto.seat.SeatRequest;
import com.cinema.dto.seat.SeatResponse;
import java.util.List;

public interface SeatService {
    List<SeatResponse> getAllSeats();
    SeatResponse getSeatById(Long id);
    List<SeatResponse> getSeatsByShowtimeId(Long showtimeId);
    SeatResponse createSeat(SeatRequest request);
    List<SeatResponse> createSeats(SeatRequest request);
    SeatResponse updateSeat(Long id, SeatRequest request);
    void deleteSeat(Long id);
    boolean isSeatAvailable(Long seatId);
    List<SeatResponse> getAvailableSeats(Long showtimeId);
} 