package com.cinema.service.impl;

import com.cinema.dto.showtime.ShowtimeRequest;
import com.cinema.dto.showtime.ShowtimeResponse;
import com.cinema.exception.BusinessException;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.entity.Movie;
import com.cinema.model.entity.Screen;
import com.cinema.model.entity.Showtime;
import com.cinema.repository.MovieRepository;
import com.cinema.repository.ScreenRepository;
import com.cinema.repository.ShowtimeRepository;
import com.cinema.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShowtimeServiceImpl implements ShowtimeService {
    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;

    @Override
    public List<ShowtimeResponse> getAllShowtimes() {
        return showtimeRepository.findAll().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public ShowtimeResponse getShowtimeById(Long id) {
        Showtime showtime = showtimeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Showtime", "id", id));
        return convertToResponse(showtime);
    }

    @Override
    public List<ShowtimeResponse> getShowtimesByMovieId(Long movieId) {
        return showtimeRepository.findByMovieId(movieId).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ShowtimeResponse createShowtime(ShowtimeRequest request) {
        // Validate movie exists
        Movie movie = movieRepository.findById(request.getMovieId())
            .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", request.getMovieId()));

        // Validate screen exists and belongs to the specified cinema
        Screen screen = screenRepository.findByCinemaIdAndName(request.getCinemaId(), request.getHallNumber())
            .orElseThrow(() -> new ResourceNotFoundException("Screen", "cinemaId and name", 
                String.format("%d, %s", request.getCinemaId(), request.getHallNumber())));

        // Validate show time is in the future
        if (request.getShowTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Show time must be in the future", "INVALID_SHOW_TIME");
        }

        // Check for overlapping showtimes in the same screen
        List<Showtime> existingShowtimes = showtimeRepository.findByMovieAndCinemaAndDateRange(
            request.getMovieId(), request.getCinemaId(), request.getShowTime());
        
        for (Showtime existingShowtime : existingShowtimes) {
            if (existingShowtime.getScreen().getId().equals(screen.getId()) &&
                existingShowtime.getShowTime().equals(request.getShowTime())) {
                throw new BusinessException("A showtime already exists for this screen at the specified time", 
                    "DUPLICATE_SHOWTIME");
            }
        }

        // Create new showtime
        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setScreen(screen);
        showtime.setShowTime(request.getShowTime());
        showtime.setActive(request.isActive());

        return convertToResponse(showtimeRepository.save(showtime));
    }

    @Override
    @Transactional
    public ShowtimeResponse updateShowtime(Long id, ShowtimeRequest request) {
        Showtime showtime = showtimeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Showtime", "id", id));

        // Validate movie exists if changed
        if (!showtime.getMovie().getId().equals(request.getMovieId())) {
            Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", request.getMovieId()));
            showtime.setMovie(movie);
        }

        // Validate screen exists and belongs to the specified cinema if changed
        if (!showtime.getScreen().getName().equals(request.getHallNumber()) ||
            !showtime.getScreen().getCinema().getId().equals(request.getCinemaId())) {
            Screen screen = screenRepository.findByCinemaIdAndName(request.getCinemaId(), request.getHallNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Screen", "cinemaId and name", 
                    String.format("%d, %s", request.getCinemaId(), request.getHallNumber())));
            showtime.setScreen(screen);
        }

        // Validate show time is in the future if changed
        if (!showtime.getShowTime().equals(request.getShowTime())) {
            if (request.getShowTime().isBefore(LocalDateTime.now())) {
                throw new BusinessException("Show time must be in the future", "INVALID_SHOW_TIME");
            }

            // Check for overlapping showtimes in the same screen
            List<Showtime> existingShowtimes = showtimeRepository.findByMovieAndCinemaAndDateRange(
                request.getMovieId(), request.getCinemaId(), request.getShowTime());
            
            for (Showtime existingShowtime : existingShowtimes) {
                if (!existingShowtime.getId().equals(id) &&
                    existingShowtime.getScreen().getId().equals(showtime.getScreen().getId()) &&
                    existingShowtime.getShowTime().equals(request.getShowTime())) {
                    throw new BusinessException("A showtime already exists for this screen at the specified time", 
                        "DUPLICATE_SHOWTIME");
                }
            }
            showtime.setShowTime(request.getShowTime());
        }

        showtime.setActive(request.isActive());

        return convertToResponse(showtimeRepository.save(showtime));
    }

    @Override
    public void deleteShowtime(Long id) {
        Showtime showtime = showtimeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Showtime", "id", id));
        showtimeRepository.delete(showtime);
    }

    @Override
    public List<ShowtimeResponse> getShowtimesByCinemaId(Long cinemaId) {
        return showtimeRepository.findByCinemaId(cinemaId).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<ShowtimeResponse> getShowtimesByDateRange(LocalDateTime start, LocalDateTime end) {
        return showtimeRepository.findUpcomingShowtimes(start).stream()
            .filter(showtime -> !showtime.getShowTime().isAfter(end))
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public boolean isShowtimeAvailable(Long showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
            .orElseThrow(() -> new ResourceNotFoundException("Showtime", "id", showtimeId));
        return showtime.isActive() && !showtime.isDeleted();
    }

    @Override
    public Showtime getShowtimeByMovieAndCinemaAndTime(Long movieId, Long cinemaId, LocalDateTime showTime) {
        List<Showtime> showtimes = showtimeRepository.findByMovieAndCinemaAndDateRange(movieId, cinemaId, showTime);
        return showtimes.stream()
            .filter(showtime -> showtime.getShowTime().equals(showTime))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Showtime", "movieId, cinemaId, showTime", 
                String.format("%d, %d, %s", movieId, cinemaId, showTime)));
    }

    private ShowtimeResponse convertToResponse(Showtime showtime) {
        return ShowtimeResponse.builder()
            .id(showtime.getId())
            .movieId(showtime.getMovie().getId())
            .movieTitle(showtime.getMovie().getTitle())
            .cinemaId(showtime.getScreen().getCinema().getId())
            .cinemaName(showtime.getScreen().getCinema().getName())
            .showTime(showtime.getShowTime())
            .hallNumber(showtime.getScreen().getName())
            .totalSeats(showtime.getScreen().getTotalSeats())
            .availableSeats((int) showtime.getSeats().stream().filter(seat -> seat.isAvailable() && !seat.isDeleted()).count())
            .isActive(showtime.isActive())
            .build();
    }
} 