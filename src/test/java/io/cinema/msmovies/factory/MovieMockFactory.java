package io.cinema.msmovies.factory;

import io.cinema.msmovies.domain.dto.request.MovieRequestDto;
import io.cinema.msmovies.domain.dto.request.MoviesBatchRequestDto;
import io.cinema.msmovies.domain.dto.response.MovieInfoResponseDto;
import io.cinema.msmovies.domain.dto.response.MovieResponseDto;
import io.cinema.msmovies.domain.entity.MovieEntity;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@UtilityClass
public class MovieMockFactory {

    public static MovieResponseDto buildMovieResponseDto(
            UUID movieId,
            UUID genreId,
            UUID actorId,
            UUID directorId,
            UUID mediaId
    ) {
        var genre = GenreMockFactory.buildGenreResponseDto(genreId);
        var genres = Set.of(genre);

        var actor = ActorMockFactory.buildActorResponseDto(actorId);
        var actors = Set.of(actor);

        var director = DirectorMockFactory.buildDirectorResponseDto(directorId);
        var directors = Set.of(director);

        var media = MovieMediaMockFactory.buildMovieMediaResponseDto(mediaId);

        return new MovieResponseDto(
                movieId,
                "Some movie - test",
                "Very very good movie, some say it's the best movie in the world",
                LocalDate.of(2025, 10, 15),
                "https://localhost/bucket/123-poster.png",
                "https://localhost/bucket/123-backdrop.png",
                genres,
                actors,
                directors,
                List.of(media)
        );
    }


    public static MovieRequestDto buildMovieRequestDto(
            UUID genreId,
            UUID actorId,
            UUID directorId,
            UUID mediaId
    ) {
        var media = MovieMediaMockFactory.buildMovieMediaRequestDto(mediaId);

        return new MovieRequestDto(
                "Some movie - test",
                "Very very good movie, some say it's the best movie in the world",
                LocalDate.of(2025, 10, 15),
                "https://localhost/bucket/123-poster.png",
                "https://localhost/bucket/123-backdrop.png",
                Set.of(genreId),
                Set.of(actorId),
                Set.of(directorId),
                List.of(media)
        );
    }

    public static MovieInfoResponseDto buildMovieInfoResponseDto(UUID movieId) {
        return new MovieInfoResponseDto(
                movieId,
                "Some movie - test",
                "Very very good movie, some say it's the best movie in the world",
                LocalDate.of(2025, 10, 15),
                "https://localhost/bucket/123-poster.png",
                "https://localhost/bucket/123-backdrop.png"
        );
    }

    public static MoviesBatchRequestDto buildMoviesBatchRequestDto(Set<UUID> moviesIds) {
        return new MoviesBatchRequestDto(moviesIds);
    }

    public static MovieEntity buildMovieEntity(
            UUID movieId
    ) {
        return new MovieEntity(
                movieId,
                "Some movie - test",
                "Very very good movie, some say it's the best movie in the world",
                LocalDate.of(2025, 10, 15),
                "https://localhost/bucket/123-poster.png",
                "https://localhost/bucket/123-backdrop.png"
        );
    }
}
