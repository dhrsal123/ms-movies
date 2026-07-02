package io.cinema.msmovies.controller;

import io.cinema.domain.annotations.HasEmployeeRole;
import io.cinema.msmovies.domain.dto.request.MovieRequestDto;
import io.cinema.msmovies.domain.dto.request.MoviesBatchRequestDto;
import io.cinema.msmovies.domain.dto.response.MovieInfoResponseDto;
import io.cinema.msmovies.domain.dto.response.MovieResponseDto;
import io.cinema.msmovies.service.MovieService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies")
public class MovieController {
    private static final String CACHE_MOVIE = "movie";
    private static final String CACHE_MOVIE_LIST = "movies";
    private final MovieService movieService;

    @Cacheable(value = CACHE_MOVIE_LIST, key = "{#page, #size}")
    @GetMapping
    public Flux<MovieResponseDto> getAllMovies(
            @RequestParam("page") @PositiveOrZero int page,
            @RequestParam("size") @Positive int size
    ) {
        return movieService.getAllMovies(page, size);
    }

    @Cacheable(value = CACHE_MOVIE, key = "#movieId")
    @GetMapping("/{movieId}")
    public Mono<MovieResponseDto> getMovieById(@PathVariable("movieId") UUID movieId) {
        return movieService.getMovieById(movieId);
    }

    @PostMapping("/batch")
    public Flux<MovieInfoResponseDto> getMoviesByIds(@RequestBody @Valid MoviesBatchRequestDto moviesBatchRequestDto) {
        return movieService.getMoviesInfoByIds(moviesBatchRequestDto);
    }

    @CacheEvict(value = CACHE_MOVIE_LIST, allEntries = true)
    @HasEmployeeRole
    @PostMapping
    public Mono<MovieResponseDto> createMovie(@RequestBody @Valid MovieRequestDto movieRequestDto) {
        return movieService.createMovie(movieRequestDto);
    }

    @Caching(evict = {
            @CacheEvict(value = CACHE_MOVIE, key = "#movieId"),
            @CacheEvict(value = CACHE_MOVIE_LIST, allEntries = true)
    })
    @HasEmployeeRole
    @PutMapping("/{movieId}")
    public Mono<MovieResponseDto> updateMovie(
            @PathVariable("movieId") UUID movieId,
            @RequestBody @Valid MovieRequestDto movieRequestDto
    ) {
        return movieService.updateMovie(movieId, movieRequestDto);
    }

    @Caching(evict = {
            @CacheEvict(value = CACHE_MOVIE, key = "#movieId"),
            @CacheEvict(value = CACHE_MOVIE_LIST, allEntries = true)
    })
    @HasEmployeeRole
    @DeleteMapping("/{movieId}")
    public Mono<Void> deleteMovie(@PathVariable("movieId") UUID movieId) {
        return movieService.deleteMovie(movieId);
    }

}