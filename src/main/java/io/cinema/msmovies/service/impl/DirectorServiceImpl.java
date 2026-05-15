package io.cinema.msmovies.service.impl;

import io.cinema.domain.exceptions.CinemaException;
import io.cinema.msmovies.domain.dto.request.DirectorRequestDto;
import io.cinema.msmovies.domain.dto.response.DirectorResponseDto;
import io.cinema.msmovies.mapper.DirectorMapper;
import io.cinema.msmovies.repository.DirectorRepository;
import io.cinema.msmovies.service.DirectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
public class DirectorServiceImpl implements DirectorService {
    private static final String DIRECTOR_NOT_FOUND = "Director not found.";
    private final DirectorMapper directorMapper;
    private final TransactionalOperator transactionalOperator;
    private final DirectorRepository directorRepository;

    @Override
    public Flux<DirectorResponseDto> getAllDirectors(int page, int size) {
        return directorRepository.findAllBy(PageRequest.of(page, size))
                .map(directorMapper::toDto)
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to get all directors, error: {} ", e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during read", TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<DirectorResponseDto> getDirectorById(UUID directorId) {
        return directorRepository.findById(directorId)
                .switchIfEmpty(Mono.error(new CinemaException(DIRECTOR_NOT_FOUND, NOT_FOUND)))
                .map(directorMapper::toDto)
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to get director by id, error: {} ", e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during read", TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<DirectorResponseDto> createDirector(DirectorRequestDto directorRequestDto) {
        var directorEntity = directorMapper.toEntity(directorRequestDto);
        return directorRepository.save(directorEntity)
                .map(directorMapper::toDto)
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to save director, error: {} ", e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during save", TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<DirectorResponseDto> updateDirector(UUID directorId, DirectorRequestDto directorRequestDto) {
        return directorRepository.findById(directorId)
                .switchIfEmpty(Mono.error(new CinemaException(DIRECTOR_NOT_FOUND, NOT_FOUND)))
                .flatMap(entity -> {
                    directorMapper.updateEntityFromDto(directorRequestDto, entity);
                    return directorRepository.save(entity);
                })
                .map(directorMapper::toDto)
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to update director, error: {} ", e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during update", TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<Void> deleteDirector(UUID directorId) {
        return directorRepository.findById(directorId)
                .switchIfEmpty(Mono.error(new CinemaException(DIRECTOR_NOT_FOUND, NOT_FOUND)))
                .flatMap(entity -> directorRepository.deleteById(directorId))
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to delete director, error: {} ", e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during delete", TECHNICAL_ERROR)
                );
    }
}
