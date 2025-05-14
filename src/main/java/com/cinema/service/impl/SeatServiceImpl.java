package com.cinema.service.impl;

import com.cinema.dto.seat.SeatRequest;
import com.cinema.dto.seat.SeatResponse;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.entity.Seat;
import com.cinema.model.entity.Showtime;
import com.cinema.repository.SeatRepository;
import com.cinema.repository.ShowtimeRepository;
import com.cinema.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatServiceImpl implements SeatService {
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;

    @Override
    public List<SeatResponse> getAllSeats() {
        return seatRepository.findAll().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public SeatResponse getSeatById(Long id) {
        Seat seat = seatRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Seat", "id", id));
        return convertToResponse(seat);
    }

    @Override
    public List<SeatResponse> getSeatsByShowtimeId(Long showtimeId) {
        return seatRepository.findByShowtimeId(showtimeId).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SeatResponse createSeat(SeatRequest request) {
        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
            .orElseThrow(() -> new ResourceNotFoundException("Showtime", "id", request.getShowtimeId()));
        Seat seat = new Seat();
        seat.setShowtime(showtime);
        seat.setLabel(request.getLabel());
        seat.setAvailable(request.isAvailable());
        seat.setDeleted(false);
        Seat saved = seatRepository.save(seat);
        return convertToResponse(saved);
    }

    @Override
    @Transactional
    public List<SeatResponse> createSeats(SeatRequest request) {
        // Default behavior: create a single seat and return
        return List.of(createSeat(request));
    }

    @Override
    @Transactional
    public SeatResponse updateSeat(Long id, SeatRequest request) {
        Seat seat = seatRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Seat", "id", id));
        if (request.getLabel() != null) {
            seat.setLabel(request.getLabel());
        }
        seat.setAvailable(request.isAvailable());
        seat.setDeleted(request.isDeleted());
        Seat updated = seatRepository.save(seat);
        return convertToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteSeat(Long id) {
        Seat seat = seatRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Seat", "id", id));
        seat.setDeleted(true);
        seatRepository.save(seat);
    }

    @Override
    public boolean isSeatAvailable(Long seatId) {
        Seat seat = seatRepository.findById(seatId)
            .orElseThrow(() -> new ResourceNotFoundException("Seat", "id", seatId));
        return seat.isAvailable() && !seat.isDeleted();
    }

    @Override
    public List<SeatResponse> getAvailableSeats(Long showtimeId) {
        return seatRepository.findAvailableSeatsByShowtimeId(showtimeId).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    private SeatResponse convertToResponse(Seat seat) {
        return SeatResponse.builder()
            .id(seat.getId())
            .showtimeId(seat.getShowtime().getId())
            .seatNumber(seat.getLabel())
            .rowNumber(null)
            .price(null)
            .isReserved(!seat.isAvailable())
            .seatType(null)
            .status(seat.isAvailable() ? "AVAILABLE" : "RESERVED")
            .build();
    }
} 