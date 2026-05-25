package io.cinema.msmovies.service.impl;

import io.cinema.domain.exceptions.CinemaException;
import io.cinema.msmovies.domain.dto.request.MovieRequestDto;
import io.cinema.msmovies.domain.dto.response.MovieResponseDto;
import io.cinema.msmovies.domain.entity.ActorProjection;
import io.cinema.msmovies.domain.entity.DirectorProjection;
import io.cinema.msmovies.domain.entity.GenreProjection;
import io.cinema.msmovies.domain.entity.MovieEntity;
import io.cinema.msmovies.domain.entity.MovieMediaProjection;
import io.cinema.msmovies.mapper.MovieMapper;
import io.cinema.msmovies.repository.ActorRepository;
import io.cinema.msmovies.repository.DirectorRepository;
import io.cinema.msmovies.repository.GenreRepository;
import io.cinema.msmovies.repository.MovieMediaRepository;
import io.cinema.msmovies.repository.MovieRepository;
import io.cinema.msmovies.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.cinema.domain.enumerated.CinemaExceptionTypes.NOT_FOUND;
import static io.cinema.domain.enumerated.CinemaExceptionTypes.TECHNICAL_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private static final String MOVIE_NOT_FOUND = "Movie not found: ";
    private static final String DB_ERROR_DURING_READ = "DB error during read";

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final ActorRepository actorRepository;
    private final DirectorRepository directorRepository;
    private final MovieMediaRepository movieMediaRepository;
    private final TransactionalOperator transactionalOperator;
    private final MovieMapper movieMapper;

    @Override
    public Flux<MovieResponseDto> getAllMovies(int page, int size) {
        return movieRepository.findAllBy(PageRequest.of(page, size))
                .collectList()
                .flatMapMany(this::batchEnrich)
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to get movies: {}", e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException(DB_ERROR_DURING_READ, TECHNICAL_ERROR)
                );
    }

    @Override
    public Flux<MovieResponseDto> getMoviesByGenreId(UUID genreId) {
        return movieRepository.findByGenreId(genreId)
                .collectList()
                .flatMapMany(this::batchEnrich)
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to get movies by genre {}: {}", genreId, e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException(DB_ERROR_DURING_READ, TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<MovieResponseDto> getMovieById(UUID movieId) {
        return movieRepository.findById(movieId)
                .switchIfEmpty(Mono.error(new CinemaException(MOVIE_NOT_FOUND + movieId, NOT_FOUND)))
                .flatMap(movie -> Mono.zip(
                        genreRepository.findByMovieId(movieId).collect(Collectors.toSet()),
                        actorRepository.findByMovieId(movieId).collect(Collectors.toSet()),
                        directorRepository.findByMovieId(movieId).collect(Collectors.toSet()),
                        movieMediaRepository.findAllByMovieId(movieId).collectList()
                ).map(tuple -> movieMapper.toDto(
                        movie,
                        tuple.getT1(),
                        tuple.getT2(),
                        tuple.getT3(),
                        tuple.getT4()
                )))
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to get movie {}: {}", movieId, e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException(DB_ERROR_DURING_READ, TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<MovieResponseDto> createMovie(MovieRequestDto dto) {
        MovieEntity entity = movieMapper.toEntity(dto);

        return movieRepository.save(entity)
                .flatMap(saved -> saveRelationships(saved.getId(), dto)
                        .then(getMovieById(saved.getId())))
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to create movie: {}", e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during create", TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<MovieResponseDto> updateMovie(UUID movieId, MovieRequestDto dto) {
        return movieRepository.findById(movieId)
                .switchIfEmpty(Mono.error(new CinemaException(MOVIE_NOT_FOUND + movieId, NOT_FOUND)))
                .flatMap(existing -> {
                    movieMapper.updateEntityFromDto(dto, existing);
                    return movieRepository.save(existing);
                })
                .flatMap(saved -> deleteRelationships(movieId)
                        .then(saveRelationships(movieId, dto))
                        .then(getMovieById(movieId)))
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to update movie {}: {}", movieId, e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during update", TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<Void> deleteMovie(UUID movieId) {
        return movieRepository.findById(movieId)
                .switchIfEmpty(Mono.error(new CinemaException(MOVIE_NOT_FOUND + movieId, NOT_FOUND)))
                .flatMap(movie -> deleteRelationships(movieId)
                        .then(movieRepository.deleteById(movieId)))
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to delete movie {}: {}", movieId, e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during delete", TECHNICAL_ERROR)
                );
    }

    // private methods
    private Flux<MovieResponseDto> batchEnrich(List<MovieEntity> movies) {
        if (movies.isEmpty()) return Flux.empty();

        Set<UUID> ids = movies.stream()
                .map(MovieEntity::getId)
                .collect(Collectors.toSet());

        var genresByMovie = genreRepository
                .findByMovieIdIn(ids)
                .collectMultimap(GenreProjection::movieId, GenreProjection::genre, HashMap::new)
                .map(m -> m.entrySet().stream().collect(
                        Collectors.toMap(Map.Entry::getKey, e -> new HashSet<>(e.getValue()))));

        var actorsByMovie = actorRepository
                .findByMovieIdIn(ids)
                .collectMultimap(ActorProjection::movieId, ActorProjection::actor, HashMap::new)
                .map(m -> m.entrySet().stream().collect(
                        Collectors.toMap(Map.Entry::getKey, e -> new HashSet<>(e.getValue()))));

        var directorsByMovie = directorRepository
                .findByMovieIdIn(ids)
                .collectMultimap(DirectorProjection::movieId, DirectorProjection::director, HashMap::new)
                .map(m -> m.entrySet().stream().collect(
                        Collectors.toMap(Map.Entry::getKey, e -> new HashSet<>(e.getValue()))));

        var mediaByMovie = movieMediaRepository
                .findAllByMovieIdIn(ids)
                .collectMultimap(MovieMediaProjection::movieId, MovieMediaProjection::movieMedia, HashMap::new)
                .map(m -> m.entrySet().stream().collect(
                        Collectors.toMap(Map.Entry::getKey, e -> new ArrayList<>(e.getValue()))));

        return Mono.zip(genresByMovie, actorsByMovie, directorsByMovie, mediaByMovie)
                .flatMapMany(tuple ->
                        Flux.fromIterable(movies).map(movie -> movieMapper.toDto(
                                movie,
                                tuple.getT1().getOrDefault(movie.getId(), new HashSet<>()),
                                tuple.getT2().getOrDefault(movie.getId(), new HashSet<>()),
                                tuple.getT3().getOrDefault(movie.getId(), new HashSet<>()),
                                tuple.getT4().getOrDefault(movie.getId(), new ArrayList<>())
                        )));
    }

    private Mono<Void> saveRelationships(UUID movieId, MovieRequestDto dto) {
        Mono<Void> saveGenres = genreRepository.saveMovieGenres(movieId, nullSafe(dto.genreIds())).then();
        Mono<Void> saveActors = actorRepository.saveMovieActors(movieId, nullSafe(dto.actorIds())).then();
        Mono<Void> saveDirectors = directorRepository.saveMovieDirectors(movieId, nullSafe(dto.directorIds())).then();
        Mono<Void> saveMedia = dto.media() != null && !dto.media().isEmpty()
                ? movieMediaRepository.saveMovieMedia(movieId, dto.media()).then()
                : Mono.empty();

        return Mono.when(saveGenres, saveActors, saveDirectors, saveMedia);
    }

    private Mono<Void> deleteRelationships(UUID movieId) {
        return Mono.when(
                genreRepository.deleteByMovieId(movieId),
                actorRepository.deleteByMovieId(movieId),
                directorRepository.deleteByMovieId(movieId),
                movieMediaRepository.deleteByMovieId(movieId)
        );
    }

    private <T> Set<T> nullSafe(Set<T> set) {
        return set != null ? set : Collections.emptySet();
    }
}