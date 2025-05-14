package com.cinema.service;

import com.cinema.dto.showtime.ShowtimeRequest;
import com.cinema.dto.showtime.ShowtimeResponse;
import com.cinema.model.entity.Showtime;
import java.time.LocalDateTime;
import java.util.List;

public interface ShowtimeService {
    List<ShowtimeResponse> getAllShowtimes();
    ShowtimeResponse getShowtimeById(Long id);
    List<ShowtimeResponse> getShowtimesByMovieId(Long movieId);
    ShowtimeResponse createShowtime(ShowtimeRequest request);
    ShowtimeResponse updateShowtime(Long id, ShowtimeRequest request);
    void deleteShowtime(Long id);
    List<ShowtimeResponse> getShowtimesByCinemaId(Long cinemaId);
    List<ShowtimeResponse> getShowtimesByDateRange(LocalDateTime start, LocalDateTime end);
    boolean isShowtimeAvailable(Long showtimeId);
    Showtime getShowtimeByMovieAndCinemaAndTime(Long movieId, Long cinemaId, LocalDateTime showTime);
} 