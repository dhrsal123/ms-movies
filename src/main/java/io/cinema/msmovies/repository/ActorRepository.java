package io.cinema.msmovies.repository;

import io.cinema.msmovies.domain.entity.ActorEntity;
import io.cinema.msmovies.domain.entity.ActorProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface ActorRepository extends R2dbcRepository<ActorEntity, UUID> {
    Flux<ActorEntity> findAllBy(Pageable page);

    @Query("""
            SELECT a.* FROM actor a 
            JOIN movie_actor ma ON a.id = ma.actor_id
            WHERE ma.movie_id = :movieId
            """)
    Flux<ActorEntity> findByMovieId(UUID movieId);

    @Query("""
            SELECT a.*, ma.movie_id as movie_id 
            FROM actor a 
            JOIN movie_actor ma ON a.id = ma.actor_id
            WHERE ma.movie_id IN (:movieIds)
            """)
    Flux<ActorProjection> findByMovieIdIn(Collection<UUID> movieIds);

    @Modifying
    @Query("DELETE FROM movie_actor WHERE movie_id = :movieId")
    Mono<Void> deleteByMovieId(UUID movieId);

    @Modifying
    @Query("INSERT INTO movie_actor (movie_id, actor_id) VALUES (:movieId, :actorId)")
    Mono<Void> saveMovieActor(UUID movieId, UUID actorId);

    default Mono<Void> saveMovieActors(UUID movieId, Set<UUID> actorIds) {
        if (actorIds == null || actorIds.isEmpty()) return Mono.empty();
        return Flux.fromIterable(actorIds)
                .flatMap(actorId -> saveMovieActor(movieId, actorId))
                .then();
    }
}
