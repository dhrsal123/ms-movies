package io.cinema.msmovies.service.impl;

import io.cinema.msmovies.domain.dto.request.GenreRequestDto;
import io.cinema.msmovies.domain.dto.response.GenreResponseDto;
import io.cinema.msmovies.domain.dto.response.MovieResponseDto;
import io.cinema.msmovies.mapper.GenreMapper;
import io.cinema.msmovies.repository.GenreRepository;
import io.cinema.msmovies.repository.MovieRepository;
import io.cinema.msmovies.service.GenreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;
    private final TransactionalOperator transactionalOperator;
    private final GenreMapper genreMapper;

    @Override
    public Flux<GenreResponseDto> getAllGenres() {
        return null;
    }

    @Override
    public Mono<GenreResponseDto> getGenre(UUID genreId) {
        return null;
    }

    @Override

    public Flux<MovieResponseDto> getMovieByGenre(UUID genreId) {
        return null;
    }

    @Override
    public Mono<GenreResponseDto> createGenre(GenreRequestDto genreRequestDto) {
        return null;
    }

    @Override
    public Mono<GenreResponseDto> updateGenre(UUID genreId, GenreRequestDto genreRequestDto) {
        return null;
    }

    @Override
    public Mono<Void> deleteGenre(UUID genreId) {
        return null;
    }
}
