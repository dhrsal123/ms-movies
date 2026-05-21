package io.cinema.msmovies.repository;

import io.cinema.msmovies.domain.entity.GenreEntity;
import io.cinema.msmovies.domain.entity.GenreProjection;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface GenreRepository extends R2dbcRepository<GenreEntity, UUID> {

    @Query("""
            SELECT g.* FROM genre g 
            JOIN movie_genre mg ON g.id = mg.genre_id
            WHERE mg.movie_id = :movieId
            """)
    Flux<GenreEntity> findByMovieId(UUID movieId);

    @Query("""
            SELECT g.*, mg.movie_id as movie_id 
            FROM genre g 
            JOIN movie_genre mg ON g.id = mg.genre_id
            WHERE mg.movie_id IN (:movieIds)
            """)
    Flux<GenreProjection> findByMovieIdIn(Collection<UUID> movieIds);

    @Modifying
    @Query("DELETE FROM movie_genre WHERE movie_id = :movieId")
    Mono<Void> deleteByMovieId(UUID movieId);

    @Modifying
    @Query("INSERT INTO movie_genre (movie_id, genre_id) VALUES (:movieId, :genreId)")
    Mono<Void> saveMovieGenre(UUID movieId, UUID genreId);

    default Mono<Void> saveMovieGenres(UUID movieId, Set<UUID> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) return Mono.empty();
        return Flux.fromIterable(genreIds)
                .flatMap(genreId -> saveMovieGenre(movieId, genreId))
                .then();
    }
}