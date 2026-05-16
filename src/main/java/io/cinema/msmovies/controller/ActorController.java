package io.cinema.msmovies.controller;

import io.cinema.domain.annotations.HasEmployeeRole;
import io.cinema.msmovies.domain.dto.request.ActorRequestDto;
import io.cinema.msmovies.domain.dto.response.ActorResponseDto;
import io.cinema.msmovies.service.ActorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/actors")
public class ActorController {
    public static final String CACHE_ACTOR_SINGLE = "actor";
    private final ActorService actorService;

    private static final String CACHE_ACTORS_LIST = "actors";

    @Cacheable(value = CACHE_ACTORS_LIST, key = "{#page, #size}")
    @GetMapping
    public Flux<ActorResponseDto> getAllActors(
            @RequestParam("page") @PositiveOrZero int page,
            @RequestParam("size") @Positive int size
    ) {
        return actorService.getAllActors(page, size);
    }

    @Cacheable(value = CACHE_ACTOR_SINGLE, key = "#actorId")
    @GetMapping("/{actorId}")
    public Mono<ActorResponseDto> getActorById(
            @PathVariable("actorId") @NotNull UUID actorId
    ) {
        return actorService.getActorById(actorId);
    }

    @CacheEvict(value = CACHE_ACTORS_LIST, allEntries = true)
    @HasEmployeeRole
    @PostMapping
    public Mono<ActorResponseDto> createActor(@RequestBody @Valid ActorRequestDto actorRequestDto) {
        return actorService.createActor(actorRequestDto);
    }

    @Caching(evict = {
            @CacheEvict(value = CACHE_ACTOR_SINGLE, key = "#actorId"),
            @CacheEvict(value = CACHE_ACTORS_LIST, allEntries = true)
    })
    @HasEmployeeRole
    @PutMapping("/{actorId}")
    public Mono<ActorResponseDto> updateActor(
            @PathVariable("actorId") @NotNull UUID actorId,
            @RequestBody @Valid ActorRequestDto actorRequestDto
    ) {
        return actorService.updateActor(actorId, actorRequestDto);
    }

    @Caching(evict = {
            @CacheEvict(value = CACHE_ACTOR_SINGLE, key = "#actorId"),
            @CacheEvict(value = CACHE_ACTORS_LIST, allEntries = true)
    })
    @HasEmployeeRole
    @DeleteMapping("/{actorId}")
    public Mono<Void> deleteActor(@PathVariable("actorId") @NotNull UUID actorId) {
        return actorService.deleteActor(actorId);
    }

}
