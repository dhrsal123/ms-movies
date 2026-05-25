package io.cinema.msmovies.repository;

import io.cinema.msmovies.domain.entity.DirectorEntity;
import io.cinema.msmovies.domain.entity.DirectorProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface DirectorRepository extends R2dbcRepository<DirectorEntity, UUID> {

    Flux<DirectorEntity> findAllBy(Pageable page);

    @Query("""
            SELECT d.* FROM director d 
            JOIN director_movie dm ON d.id = dm.director_id
            WHERE dm.movie_id = :movieId
            """)
    Flux<DirectorEntity> findByMovieId(UUID movieId);

    @Query("""
            SELECT d.*, dm.movie_id as movie_id 
            FROM director d 
            JOIN director_movie dm ON d.id = dm.director_id
            WHERE dm.movie_id IN (:movieIds)
            """)
    Flux<DirectorProjection> findByMovieIdIn(Collection<UUID> movieIds);

    @Modifying
    @Query("DELETE FROM director_movie WHERE movie_id = :movieId")
    Mono<Void> deleteByMovieId(UUID movieId);

    @Modifying
    @Query("INSERT INTO director_movie (movie_id, director_id) VALUES (:movieId, :directorId)")
    Mono<Void> saveMovieDirector(UUID movieId, UUID directorId);

    default Mono<Void> saveMovieDirectors(UUID movieId, Set<UUID> directorIds) {
        if (directorIds == null || directorIds.isEmpty()) return Mono.empty();
        return Flux.fromIterable(directorIds)
                .flatMap(directorId -> saveMovieDirector(movieId, directorId))
                .then();
    }
}