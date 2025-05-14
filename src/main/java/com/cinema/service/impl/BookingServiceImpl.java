package com.cinema.service.impl;

import com.cinema.dto.booking.BookingData;
import com.cinema.dto.booking.BookingRequest;
import com.cinema.dto.booking.BookingResponse;
import com.cinema.model.entity.*;
import com.cinema.repository.BookingRepository;
import com.cinema.repository.CinemaRepository;
import com.cinema.repository.MovieRepository;
import com.cinema.repository.UserRepository;
import com.cinema.service.BookingService;
import com.cinema.service.ShowtimeService;
import com.cinema.exception.BusinessException;
import com.cinema.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final MovieRepository movieRepository;
    private final CinemaRepository cinemaRepository;
    private final UserRepository userRepository;
    private final ShowtimeService showtimeService;
    private final ObjectMapper objectMapper;

    @Override
    public BookingResponse createBooking(Long userId, BookingRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        movieRepository.findById(request.getMovieId())
            .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", request.getMovieId()));
        cinemaRepository.findById(request.getCinemaId())
            .orElseThrow(() -> new ResourceNotFoundException("Cinema", "id", request.getCinemaId()));

        // Validate show time
        if (request.getShowTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Cannot book a show time in the past", "INVALID_SHOW_TIME");
        }

        // Validate seat availability
        if (!isSeatAvailable(request.getMovieId(), request.getCinemaId(), request.getShowTime(), request.getSeatLayout())) {
            throw new BusinessException("Selected seats are not available", "SEATS_NOT_AVAILABLE");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShowtime(showtimeService.getShowtimeByMovieAndCinemaAndTime(
            request.getMovieId(), request.getCinemaId(), request.getShowTime()));
        booking.setSeats(parseSeats(request.getSeatLayout()));
        booking.setTotalPrice(calculateTotalPrice(booking.getSeats()));
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setDeleted(false);

        booking = bookingRepository.save(booking);
        booking.setQrCode(generateQRCode(booking));
        booking = bookingRepository.save(booking);

        return convertToResponse(booking);
    }

    @Override
    public BookingResponse cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessException("Cannot cancel a booking that is not pending", "INVALID_BOOKING_STATUS");
        }

        if (booking.getShowtime().getShowTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Cannot cancel a booking for a past show time", "INVALID_SHOW_TIME");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);

        return convertToResponse(booking);
    }

    @Override
    public BookingResponse getBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));
        return convertToResponse(booking);
    }

    @Override
    public List<BookingResponse> getUserBookings(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public boolean validateBooking(String qrCode, Long cinemaId) {
        try {
            String decodedData = new String(Base64.getDecoder().decode(qrCode));
            BookingData bookingData = objectMapper.readValue(decodedData, BookingData.class);

            Booking booking = bookingRepository.findById(bookingData.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingData.getBookingId()));

            if (!booking.getShowtime().getScreen().getCinema().getId().equals(cinemaId)) {
                throw new BusinessException("Booking is not for this cinema", "INVALID_CINEMA");
            }

            if (booking.getStatus() != BookingStatus.CONFIRMED) {
                throw new BusinessException("Booking is not confirmed", "INVALID_BOOKING_STATUS");
            }

            if (booking.isDeleted()) {
                throw new BusinessException("Booking has been deleted", "BOOKING_DELETED");
            }

            return true;
        } catch (ResourceNotFoundException | BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Invalid QR code", "INVALID_QR_CODE");
        }
    }

    private boolean isSeatAvailable(Long movieId, Long cinemaId, LocalDateTime showTime, String seatLayout) {
        try {
            // Parse the seat layout JSON
            List<Map<String, Object>> selectedSeats = objectMapper.readValue(seatLayout,
                new TypeReference<List<Map<String, Object>>>() {});
            
            // Get all bookings for this show time
            List<Booking> existingBookings = bookingRepository.findByMovieAndShowTimeAndStatus(
                movieId, showTime, BookingStatus.CONFIRMED);
            
            // Create a set of already booked seats
            Set<String> bookedSeats = existingBookings.stream()
                .map(Booking::getSeats)
                .flatMap(List::stream)
                .map(Seat::getLabel)
                .collect(Collectors.toSet());
            
            // Check if any of the selected seats are already booked
            for (Map<String, Object> seat : selectedSeats) {
                String seatLabel = (String) seat.get("label");
                if (bookedSeats.contains(seatLabel)) {
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            throw new BusinessException("Invalid seat layout format", "INVALID_SEAT_LAYOUT");
        }
    }

    private String generateQRCode(Booking booking) {
        try {
            BookingData bookingData = new BookingData(
                booking.getId(),
                booking.getShowtime().getMovie().getId(),
                booking.getShowtime().getScreen().getCinema().getId(),
                booking.getShowtime().getShowTime().toString()
            );

            String jsonData = objectMapper.writeValueAsString(bookingData);
            String encodedData = Base64.getEncoder().encodeToString(jsonData.getBytes());

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(encodedData, BarcodeFormat.QR_CODE, 200, 200);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            throw new BusinessException("Error generating QR code", "QR_CODE_GENERATION_ERROR");
        }
    }

    private BookingResponse convertToResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setUserId(booking.getUser().getId());
        response.setUserName(booking.getUser().getName());
        response.setMovieId(booking.getShowtime().getMovie().getId());
        response.setMovieTitle(booking.getShowtime().getMovie().getTitle());
        response.setCinemaId(booking.getShowtime().getScreen().getCinema().getId());
        response.setCinemaName(booking.getShowtime().getScreen().getCinema().getName());
        response.setShowTime(booking.getShowtime().getShowTime());
        response.setSeatLayout(convertSeatsToJson(booking.getSeats()));
        response.setStatus(booking.getStatus());
        response.setQrCode(booking.getQrCode());
        response.setCreatedAt(booking.getCreatedAt());
        response.setDeleted(booking.isDeleted());
        return response;
    }

    private List<Seat> parseSeats(String seatLayout) {
        try {
            List<Map<String, Object>> seatData = objectMapper.readValue(seatLayout,
                new TypeReference<List<Map<String, Object>>>() {});
            return seatData.stream()
                .map(data -> {
                    Seat seat = new Seat();
                    seat.setLabel((String) data.get("label"));
                    return seat;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new BusinessException("Invalid seat layout format", "INVALID_SEAT_LAYOUT");
        }
    }

    private String convertSeatsToJson(List<Seat> seats) {
        try {
            List<Map<String, String>> seatData = seats.stream()
                .map(seat -> {
                    Map<String, String> data = new HashMap<>();
                    data.put("label", seat.getLabel());
                    return data;
                })
                .collect(Collectors.toList());
            return objectMapper.writeValueAsString(seatData);
        } catch (Exception e) {
            throw new BusinessException("Error converting seats to JSON", "SEAT_CONVERSION_ERROR");
        }
    }

    private BigDecimal calculateTotalPrice(List<Seat> seats) {
        BigDecimal basePrice = BigDecimal.valueOf(10.0); // Base price per seat
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (Seat seat : seats) {
            BigDecimal seatPrice = basePrice;
            
            // Add premium for VIP seats (assuming seats with row A are VIP)
            if (seat.getLabel().startsWith("A")) {
                seatPrice = seatPrice.multiply(BigDecimal.valueOf(1.5));
            }
            
            // Add premium for seats in the middle section (assuming rows B-D are middle)
            if (seat.getLabel().startsWith("B") || 
                seat.getLabel().startsWith("C") || 
                seat.getLabel().startsWith("D")) {
                seatPrice = seatPrice.multiply(BigDecimal.valueOf(1.2));
            }
            
            totalPrice = totalPrice.add(seatPrice);
        }

        return totalPrice;
    }
} 