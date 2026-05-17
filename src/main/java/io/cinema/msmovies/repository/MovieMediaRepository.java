package io.cinema.msmovies.repository;

import io.cinema.msmovies.domain.entity.MovieMediaEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface MovieMediaRepository extends R2dbcRepository<MovieMediaEntity, UUID> {
    Flux<MovieMediaEntity> findAllByMovieId(UUID movieId);
}
