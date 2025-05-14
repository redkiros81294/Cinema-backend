package com.cinema.service.impl;

import com.cinema.dto.movie.MovieRequest;
import com.cinema.dto.movie.MovieResponse;
import com.cinema.model.entity.Movie;
import com.cinema.repository.MovieRepository;
import com.cinema.service.MovieService;
import com.cinema.exception.BusinessException;
import com.cinema.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;

    @Override
    public MovieResponse createMovie(MovieRequest request) {
        validateMovieRequest(request);

        if (movieRepository.existsByTitle(request.getTitle())) {
            throw new BusinessException("Movie with this title already exists", "MOVIE_TITLE_EXISTS");
        }

        Movie movie = new Movie();
        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setDuration(request.getDuration());
        movie.setGenre(request.getGenre());
        movie.setLanguage(request.getLanguage());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setPrice(request.getPrice());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setTrailerUrl(request.getTrailerUrl());
        movie.setActive(true);
        movie.setCreatedAt(LocalDateTime.now());
        
        return mapToResponse(movieRepository.save(movie));
    }

    @Override
    public MovieResponse updateMovie(Long id, MovieRequest request) {
        validateMovieRequest(request);

        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", id));

        // Check if title is being changed and if new title already exists
        if (!movie.getTitle().equals(request.getTitle()) && 
            movieRepository.existsByTitle(request.getTitle())) {
            throw new BusinessException("Movie with this title already exists", "MOVIE_TITLE_EXISTS");
        }
        
        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setDuration(request.getDuration());
        movie.setGenre(request.getGenre());
        movie.setLanguage(request.getLanguage());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setPrice(request.getPrice());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setTrailerUrl(request.getTrailerUrl());
        movie.setUpdatedAt(LocalDateTime.now());
        
        return mapToResponse(movieRepository.save(movie));
    }

    @Override
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", id));
        
        // Soft delete
        movie.setActive(false);
        movie.setDeleted(true);
        movie.setDeletedAt(LocalDateTime.now());
        movieRepository.save(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public MovieResponse getMovie(Long id) {
        return movieRepository.findById(id)
            .map(this::mapToResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponse> getAllMovies() {
        return movieRepository.findByActiveTrueAndDeletedFalse().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponse> getMoviesByCinema(Long cinemaId) {
        return movieRepository.findByCinemaIdAndActiveTrueAndDeletedFalse(cinemaId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponse> searchMovies(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new BusinessException("Search query cannot be empty", "INVALID_SEARCH_QUERY");
        }
        return movieRepository.findByTitleContainingIgnoreCaseAndActiveTrueAndDeletedFalse(query.trim()).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    private void validateMovieRequest(MovieRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new BusinessException("Movie title is required", "INVALID_TITLE");
        }
        if (request.getDuration() == null || request.getDuration().isZero() || request.getDuration().isNegative()) {
            throw new BusinessException("Movie duration must be greater than 0", "INVALID_DURATION");
        }
        if (request.getPrice() == null || request.getPrice().doubleValue() < 0) {
            throw new BusinessException("Movie price must be non-negative", "INVALID_PRICE");
        }
        if (request.getReleaseDate() == null) {
            throw new BusinessException("Release date is required", "INVALID_RELEASE_DATE");
        }
    }

    private MovieResponse mapToResponse(Movie movie) {
        return MovieResponse.builder()
            .id(movie.getId())
            .title(movie.getTitle())
            .description(movie.getDescription())
            .duration(movie.getDuration())
            .genre(movie.getGenre())
            .language(movie.getLanguage())
            .releaseDate(movie.getReleaseDate())
            .price(movie.getPrice())
            .posterUrl(movie.getPosterUrl())
            .trailerUrl(movie.getTrailerUrl())
            .build();
    }
} 