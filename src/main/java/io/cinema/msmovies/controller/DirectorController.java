package io.cinema.msmovies.controller;

import io.cinema.domain.annotations.HasEmployeeRole;
import io.cinema.msmovies.domain.dto.request.DirectorRequestDto;
import io.cinema.msmovies.domain.dto.response.DirectorResponseDto;
import io.cinema.msmovies.service.DirectorService;
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
@RequestMapping("/api/v1/directors")
public class DirectorController {
    private final DirectorService directorService;

    private static final String CACHE_DIRECTORS_LIST = "directors";
    private static final String CACHE_DIRECTOR_SINGLE = "director";

    @Cacheable(value = CACHE_DIRECTORS_LIST, key = "{#page, #size}")
    @GetMapping
    public Flux<DirectorResponseDto> getAllDirectors(
            @RequestParam("page") @PositiveOrZero int page,
            @RequestParam("size") @Positive int size
    ) {
        return directorService.getAllDirectors(page, size);
    }

    @Cacheable(value = CACHE_DIRECTOR_SINGLE, key = "#directorId")
    @GetMapping("/{directorId}")
    public Mono<DirectorResponseDto> getDirectorById(
            @PathVariable("directorId") @NotNull UUID directorId
    ) {
        return directorService.getDirectorById(directorId);
    }

    @CacheEvict(value = CACHE_DIRECTORS_LIST, allEntries = true)
    @HasEmployeeRole
    @PostMapping
    public Mono<DirectorResponseDto> createDirector(@RequestBody @Valid DirectorRequestDto directorRequestDto) {
        return directorService.createDirector(directorRequestDto);
    }

    @Caching(evict = {
            @CacheEvict(value = CACHE_DIRECTOR_SINGLE, key = "#directorId"),
            @CacheEvict(value = CACHE_DIRECTORS_LIST, allEntries = true)
    })
    @HasEmployeeRole
    @PutMapping("/{directorId}")
    public Mono<DirectorResponseDto> updateDirector(
            @PathVariable("directorId") @NotNull UUID directorId,
            @RequestBody @Valid DirectorRequestDto directorRequestDto
    ) {
        return directorService.updateDirector(directorId, directorRequestDto);
    }

    @Caching(evict = {
            @CacheEvict(value = CACHE_DIRECTOR_SINGLE, key = "#directorId"),
            @CacheEvict(value = CACHE_DIRECTORS_LIST, allEntries = true)
    })
    @HasEmployeeRole
    @DeleteMapping("/{directorId}")
    public Mono<Void> deleteDirector(@PathVariable("directorId") @NotNull UUID directorId) {
        return directorService.deleteDirector(directorId);
    }

}
