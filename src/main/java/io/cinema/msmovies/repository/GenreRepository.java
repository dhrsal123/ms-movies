package io.cinema.msmovies.repository;

import io.cinema.msmovies.domain.entity.GenreEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface GenreRepository extends R2dbcRepository<GenreEntity, UUID> {
    @Query("""
            SELECT g.* FROM genre g 
            JOIN movie_genre mg ON g.id = mg.genre_id
            WHERE mg.movie_id = :movieId
            """)
    Flux<GenreEntity> findByMovieId(UUID movieId);
}
