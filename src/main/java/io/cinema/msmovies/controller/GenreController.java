package io.cinema.msmovies.controller;

import io.cinema.msmovies.domain.dto.request.GenreRequestDto;
import io.cinema.msmovies.domain.dto.response.GenreResponseDto;
import io.cinema.msmovies.domain.dto.response.MovieResponseDto;
import io.cinema.msmovies.service.GenreService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    private final GenreService genreService;

    @Cacheable(value = "genres")
    @GetMapping
    public Flux<GenreResponseDto> getAllGenres() {
        return genreService.getAllGenres();
    }

    @Cacheable(value = "genre", key = "#genreId")
    @GetMapping("/{genreId}")
    public Mono<GenreResponseDto> getGenre(@PathVariable("genreId") @NotNull UUID genreId) {
        return genreService.getGenre(genreId);
    }

    @Cacheable(value = "genre_movies", key = "#genreId")
    @GetMapping("/{genreId}/movies")
    public Flux<MovieResponseDto> getMoviesByGenre(@PathVariable("genreId") @NotNull UUID genreId) {
        return genreService.getMovieByGenre(genreId);
    }

    @PostMapping
    public Mono<GenreResponseDto> createGenre(@Valid GenreRequestDto genreRequestDto) {
        return genreService.createGenre(genreRequestDto);
    }

    @PutMapping("/{genreId}")
    public Mono<GenreResponseDto> updateGenre(
            @PathVariable("genreId") @NotNull UUID genreId,
            @Valid GenreRequestDto genreRequestDto
    ) {
        return genreService.updateGenre(genreId, genreRequestDto);
    }

    @DeleteMapping("/{genreId}")
    public Mono<Void> deleteGenre(@PathVariable("genreId") @NotNull UUID genreId) {
        return genreService.deleteGenre(genreId);
    }

}
