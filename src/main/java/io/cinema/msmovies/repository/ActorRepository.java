package io.cinema.msmovies.repository;

import io.cinema.msmovies.domain.entity.ActorEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ActorRepository extends R2dbcRepository<ActorEntity, UUID> {
    Flux<ActorEntity> findAllBy(Pageable page);

    @Query("""
            SELECT a.* 
            FROM actor a 
            JOIN movie_actor ma
            on a.id = ma.actor_id
            WHERE ma.movie_id = :movieId
            """)
    Flux<ActorEntity> findByMovieId(UUID movieId);
}
