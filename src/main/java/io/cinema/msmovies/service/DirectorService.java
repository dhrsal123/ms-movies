package io.cinema.msmovies.service;

import io.cinema.msmovies.domain.dto.request.DirectorRequestDto;
import io.cinema.msmovies.domain.dto.response.DirectorResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface DirectorService {
    Flux<DirectorResponseDto> getAllDirectors(int page, int size);

    Mono<DirectorResponseDto> getDirectorById(UUID directorId);

    Mono<DirectorResponseDto> createDirector(DirectorRequestDto directorRequestDto);

    Mono<DirectorResponseDto> updateDirector(UUID directorId, DirectorRequestDto directorRequestDto);

    Mono<Void> deleteDirector(UUID directorId);
}
