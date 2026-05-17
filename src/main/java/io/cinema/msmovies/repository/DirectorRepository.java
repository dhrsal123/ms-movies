package io.cinema.msmovies.repository;

import io.cinema.msmovies.domain.entity.DirectorEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface DirectorRepository extends R2dbcRepository<DirectorEntity, UUID> {
    Flux<DirectorEntity> findAllBy(Pageable page);

    @Query("""
            SELECT d.* 
            FROM director d 
            JOIN director_movie dm 
            on d.id = dm.director_id
            WHERE dm.movie_id = :movieId
            """)
    Flux<DirectorEntity> findByMovieId(UUID movieId);
}
