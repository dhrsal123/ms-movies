package io.cinema.msmovies.service.impl;

import io.cinema.domain.exceptions.CinemaException;
import io.cinema.msmovies.domain.dto.request.GenreRequestDto;
import io.cinema.msmovies.domain.dto.response.GenreResponseDto;
import io.cinema.msmovies.mapper.GenreMapper;
import io.cinema.msmovies.repository.GenreRepository;
import io.cinema.msmovies.service.GenreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.cinema.domain.enumerated.CinemaExceptionTypes.NOT_FOUND;
import static io.cinema.domain.enumerated.CinemaExceptionTypes.TECHNICAL_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private static final String GENRE_NOT_FOUND = "Genre not found.";
    private final GenreRepository genreRepository;
    private final TransactionalOperator transactionalOperator;
    private final GenreMapper genreMapper;

    @Override
    public Flux<GenreResponseDto> getAllGenres() {
        return genreRepository.findAll()
                .map(genreMapper::toDto)
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to get all genres, error: {} ", e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during read", TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<GenreResponseDto> getGenreById(UUID genreId) {
        return genreRepository.findById(genreId)
                .switchIfEmpty(Mono.error(new CinemaException(GENRE_NOT_FOUND, NOT_FOUND)))
                .map(genreMapper::toDto)
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to get genre by id, error: {} ", e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during read", TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<GenreResponseDto> createGenre(GenreRequestDto genreRequestDto) {
        var genre = genreMapper.toEntity(genreRequestDto);
        return genreRepository.save(genre)
                .map(genreMapper::toDto)
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to create genre, error: {} ", e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during save", TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<GenreResponseDto> updateGenre(UUID genreId, GenreRequestDto genreRequestDto) {
        return genreRepository.findById(genreId)
                .switchIfEmpty(Mono.error(new CinemaException(GENRE_NOT_FOUND, NOT_FOUND)))
                .flatMap(genreEntity -> {
                    genreMapper.updateEntityFromDto(genreRequestDto, genreEntity);
                    return genreRepository.save(genreEntity);
                })
                .map(genreMapper::toDto)
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to update genre, error: {} ", e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during update", TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<Void> deleteGenre(UUID genreId) {
        return genreRepository.findById(genreId)
                .switchIfEmpty(Mono.error(new CinemaException(GENRE_NOT_FOUND, NOT_FOUND)))
                .flatMap(genreEntity -> genreRepository.deleteById(genreId))
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to delete genre, error: {} ", e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during delete", TECHNICAL_ERROR)
                );
    }
}
