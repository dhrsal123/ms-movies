package io.cinema.msmovies.service;

import io.cinema.msmovies.domain.dto.request.ActorRequestDto;
import io.cinema.msmovies.domain.dto.response.ActorResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ActorService {
    Flux<ActorResponseDto> getAllActors(int page, int size);

    Mono<ActorResponseDto> getActorById(UUID actorId);

    Mono<ActorResponseDto> createActor(ActorRequestDto actorRequestDto);

    Mono<ActorResponseDto> updateActor(UUID actorId, ActorRequestDto actorRequestDto);

    Mono<Void> deleteActor(UUID actorId);
}
