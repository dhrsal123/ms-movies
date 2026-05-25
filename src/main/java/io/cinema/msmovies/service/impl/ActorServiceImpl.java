package io.cinema.msmovies.service.impl;

import io.cinema.domain.enumerated.CinemaExceptionTypes;
import io.cinema.domain.exceptions.CinemaException;
import io.cinema.msmovies.domain.dto.request.ActorRequestDto;
import io.cinema.msmovies.domain.dto.response.ActorResponseDto;
import io.cinema.msmovies.mapper.ActorMapper;
import io.cinema.msmovies.repository.ActorRepository;
import io.cinema.msmovies.service.ActorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.cinema.domain.enumerated.CinemaExceptionTypes.BAD_REQUEST;
import static io.cinema.domain.enumerated.CinemaExceptionTypes.NOT_FOUND;
import static io.cinema.domain.enumerated.CinemaExceptionTypes.TECHNICAL_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActorServiceImpl implements ActorService {
    private static final String ACTOR_NOT_FOUND = "Actor not found.";
    private final ActorRepository actorRepository;
    private final ActorMapper actorMapper;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Flux<ActorResponseDto> getAllActors(int page, int size) {
        return actorRepository.findAllBy(PageRequest.of(page, size))
                .map(actorMapper::toDto)
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to find all actors, error: {} ", e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during read", TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<ActorResponseDto> getActorById(UUID actorId) {
        return actorRepository.findById(actorId)
                .switchIfEmpty(Mono.error(new CinemaException(ACTOR_NOT_FOUND, NOT_FOUND)))
                .map(actorMapper::toDto)
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to find actor by id, error: {} ", e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during read", TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<ActorResponseDto> createActor(ActorRequestDto actorRequestDto) {
        var actorEntity = actorMapper.toEntity(actorRequestDto);
        return actorRepository.save(actorEntity)
                .map(actorMapper::toDto)
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to save actor, error: {} ", e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during save", TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<ActorResponseDto> updateActor(UUID actorId, ActorRequestDto actorRequestDto) {
        return actorRepository
                .findById(actorId)
                .switchIfEmpty(Mono.error(new CinemaException(ACTOR_NOT_FOUND, NOT_FOUND)))
                .flatMap(actor -> {
                    actorMapper.updateEntityFromDto(actorRequestDto, actor);
                    return actorRepository.save(actor);
                })
                .map(actorMapper::toDto)
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to update actor, error: {} ", e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during update", TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<Void> deleteActor(UUID actorId) {
        return  actorRepository
                .findById(actorId)
                .switchIfEmpty(Mono.error(new CinemaException(ACTOR_NOT_FOUND, NOT_FOUND)))
                .flatMap(actor -> actorRepository.deleteById(actorId))
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to delete actor, error: {} ", e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during delete", TECHNICAL_ERROR)
                );
    }
}
