package io.cinema.msmovies.controller;

import io.cinema.msmovies.domain.dto.request.GenreRequestDto;
import io.cinema.msmovies.domain.dto.response.GenreResponseDto;
import io.cinema.msmovies.domain.dto.response.MovieResponseDto;
import io.cinema.msmovies.service.GenreService;
import io.cinema.msmovies.service.MovieService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/genres")
public class GenreController {
    private static final String CACHE_GENRES_LIST = "genres";
    private static final String CACHE_GENRE_SINGLE = "genre";
    private static final String CACHE_GENRE_MOVIES = "genre_movies";
    private final GenreService genreService;
    private final MovieService movieService;

    @Cacheable(value = CACHE_GENRES_LIST)
    @GetMapping
    public Flux<GenreResponseDto> getAllGenres() {
        log.debug("Fetching all genres from database");
        return genreService.getAllGenres();
    }

    @Cacheable(value = CACHE_GENRE_SINGLE, key = "#genreId")
    @GetMapping("/{genreId}")
    public Mono<GenreResponseDto> getGenre(@PathVariable("genreId") @NotNull UUID genreId) {
        log.debug("Fetching genre {} from database", genreId);
        return genreService.getGenreById(genreId);
    }

    @Cacheable(value = CACHE_GENRE_MOVIES, key = "#genreId")
    @GetMapping("/{genreId}/movies")
    public Flux<MovieResponseDto> getMoviesByGenre(@PathVariable("genreId") @NotNull UUID genreId) {
        log.debug("Fetching movies for genre {} from database", genreId);
        return movieService.getMoviesByGenreId(genreId);
    }

    @CacheEvict(value = CACHE_GENRES_LIST, allEntries = true)
    @PostMapping
    public Mono<GenreResponseDto> createGenre(@RequestBody @Valid GenreRequestDto genreRequestDto) {
        return genreService.createGenre(genreRequestDto);
    }

    @Caching(evict = {
            @CacheEvict(value = CACHE_GENRE_SINGLE, key = "#genreId"),
            @CacheEvict(value = CACHE_GENRES_LIST, allEntries = true),
            @CacheEvict(value = CACHE_GENRE_MOVIES, key = "#genreId")
    })
    @PutMapping("/{genreId}")
    public Mono<GenreResponseDto> updateGenre(
            @PathVariable("genreId") @NotNull UUID genreId,
            @RequestBody @Valid GenreRequestDto genreRequestDto
    ) {
        return genreService.updateGenre(genreId, genreRequestDto);
    }

    @Caching(evict = {
            @CacheEvict(value = CACHE_GENRE_SINGLE, key = "#genreId"),
            @CacheEvict(value = CACHE_GENRES_LIST, allEntries = true),
            @CacheEvict(value = CACHE_GENRE_MOVIES, key = "#genreId")
    })
    @DeleteMapping("/{genreId}")
    public Mono<Void> deleteGenre(@PathVariable("genreId") @NotNull UUID genreId) {
        return genreService.deleteGenre(genreId);
    }

}
