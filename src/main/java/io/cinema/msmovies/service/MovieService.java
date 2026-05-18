package io.cinema.msmovies.service;

import io.cinema.msmovies.domain.dto.response.MovieResponseDto;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface MovieService {
    Flux<MovieResponseDto> getMoviesByGenreId(UUID genreId);
}
