package io.cinema.msmovies.service;

import io.cinema.msmovies.domain.dto.request.GenreRequestDto;
import io.cinema.msmovies.domain.dto.response.GenreResponseDto;
import io.cinema.msmovies.domain.dto.response.MovieResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface GenreService {
    Flux<GenreResponseDto> getAllGenres();

    Mono<GenreResponseDto> getGenre(UUID genreId);

    Flux<MovieResponseDto> getMovieByGenre(UUID genreId);

    Mono<GenreResponseDto> createGenre(GenreRequestDto genreRequestDto);

    Mono<GenreResponseDto> updateGenre(UUID genreId, GenreRequestDto genreRequestDto);

    Mono<Void> deleteGenre(UUID genreId);
}
