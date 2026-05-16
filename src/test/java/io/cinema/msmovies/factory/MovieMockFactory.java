package io.cinema.msmovies.factory;

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
            UUID movieId
    ) {
        UUID genreId = UUID.randomUUID();

        var genre = GenreMockFactory.buildGenreResponseDto(genreId);
        var genres = Set.of(genre);

        var actorId = UUID.randomUUID();
        var actor = ActorMockFactory.buildActorResponseDto(actorId);
        var actors = Set.of(actor);

        var directorId = UUID.randomUUID();
        var director = DirectorMockFactory.buildDirectorResponseDto(directorId);
        var directors = Set.of(director);

        var mediaId = UUID.randomUUID();
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
