package io.cinema.msmovies.repository;

import io.cinema.msmovies.domain.dto.request.MovieMediaRequestDto;
import io.cinema.msmovies.domain.entity.MovieMediaEntity;
import io.cinema.msmovies.domain.entity.MovieMediaProjection;
import io.cinema.msmovies.domain.enumerated.MediaType;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface MovieMediaRepository extends R2dbcRepository<MovieMediaEntity, UUID> {

    Flux<MovieMediaEntity> findAllByMovieId(UUID movieId);

    @Query("""
            SELECT * FROM movie_media 
            WHERE movie_id IN (:movieIds)
            """)
    Flux<MovieMediaProjection> findAllByMovieIdIn(Collection<UUID> movieIds);

    @Modifying
    @Query("DELETE FROM movie_media WHERE movie_id = :movieId")
    Mono<Void> deleteByMovieId(UUID movieId);

    @Modifying
    @Query("""
            INSERT INTO movie_media (movie_id, media_type, media_url, title, display_order) VALUES (:movieId, :mediaType, :url, :title, :displayOrder)
            """)
    Mono<Void> saveSingleMedia(UUID movieId, MediaType mediaType, String url, String title, int displayOrder);

    default Mono<Void> saveMovieMedia(UUID movieId, List<MovieMediaRequestDto> mediaList) {
        if (mediaList == null || mediaList.isEmpty()) return Mono.empty();
        return Flux.fromIterable(mediaList)
                .flatMap(media -> saveSingleMedia(
                        movieId,
                        media.mediaType(),
                        media.mediaUrl(),
                        media.title(),
                        media.displayOrder()
                ))
                .then();
    }
}