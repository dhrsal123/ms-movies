package io.cinema.msmovies.service.impl;

import io.cinema.domain.exceptions.CinemaException;
import io.cinema.msmovies.domain.dto.response.MovieResponseDto;
import io.cinema.msmovies.mapper.MovieMapper;
import io.cinema.msmovies.repository.MovieRepository;
import io.cinema.msmovies.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;

import java.util.UUID;

import static io.cinema.domain.enumerated.CinemaExceptionTypes.TECHNICAL_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final TransactionalOperator transactionalOperator;
    private final MovieMapper movieMapper;

    @Override
    public Flux<MovieResponseDto> getMoviesByGenreId(UUID genreId) {
        return movieRepository.findByGenreId(genreId)
                .map(movieMapper::toDto)
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to get movies by genre, error: {} ", e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during read", TECHNICAL_ERROR)
                );
    }
}
