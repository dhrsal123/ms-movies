package io.cinema.msmovies.repository;

import io.cinema.msmovies.domain.entity.MovieEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface MovieRepository extends R2dbcRepository<MovieEntity, UUID> {
    @Query("""
            SELECT m.* FROM movie m 
            JOIN movie_genre mg ON m.id = mg.movie_id
            WHERE mg.genre_id = :genreId
            """)
    Flux<MovieEntity> findByGenreId(UUID genreId);

    Flux<MovieEntity> findAllBy(Pageable page);
}
