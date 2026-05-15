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

import static io.cinema.domain.enumerated.CinemaExceptionTypes.BAD_REQUEST;
import static io.cinema.domain.enumerated.CinemaExceptionTypes.TECHNICAL_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {
    private final DirectorMapper directorMapper;
    private final TransactionalOperator transactionalOperator;
    private final DirectorRepository directorRepository;

    @Override
    public Flux<DirectorResponseDto> getAllDirectors(int page, int size) {
        return null;
    }

    @Override
    public Mono<DirectorResponseDto> getDirectorById(UUID directorId) {
        return null;
    }

    @Override
    public Mono<DirectorResponseDto> createDirector(DirectorRequestDto directorRequestDto) {
        return null;
    }

    @Override
    public Mono<DirectorResponseDto> updateDirector(UUID directorId, DirectorRequestDto directorRequestDto) {
        return null;
    }

    @Override
    public Mono<Void> deleteDirector(UUID directorId) {
        return null;
    }
}
