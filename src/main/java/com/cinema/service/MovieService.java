package com.cinema.service;

import com.cinema.dto.movie.MovieRequest;
import com.cinema.dto.movie.MovieResponse;

import java.util.List;

public interface MovieService {
    MovieResponse createMovie(MovieRequest request);
    MovieResponse updateMovie(Long id, MovieRequest request);
    void deleteMovie(Long id);
    MovieResponse getMovie(Long id);
    List<MovieResponse> getAllMovies();
    List<MovieResponse> getMoviesByCinema(Long cinemaId);
    List<MovieResponse> searchMovies(String query);
} 