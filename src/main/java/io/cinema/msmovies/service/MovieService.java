package io.cinema.msmovies.service;

import io.cinema.msmovies.domain.dto.request.MovieRequestDto;
import io.cinema.msmovies.domain.dto.request.MoviesBatchRequestDto;
import io.cinema.msmovies.domain.dto.response.MovieInfoResponseDto;
import io.cinema.msmovies.domain.dto.response.MovieResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MovieService {
    Flux<MovieResponseDto> getAllMovies(int page, int size);

    Flux<MovieResponseDto> getMoviesByGenreId(UUID genreId);

    Mono<MovieResponseDto> getMovieById(UUID movieId);

    Flux<MovieInfoResponseDto> getMoviesInfoByIds(MoviesBatchRequestDto moviesBatchRequestDto);

    Mono<MovieResponseDto> createMovie(MovieRequestDto movieRequestDto);

    Mono<MovieResponseDto> updateMovie(UUID movieId, MovieRequestDto movieRequestDto);

    Mono<Void> deleteMovie(UUID movieId);
}
