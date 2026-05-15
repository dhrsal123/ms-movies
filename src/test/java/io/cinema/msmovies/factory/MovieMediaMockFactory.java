package io.cinema.msmovies.factory;

import io.cinema.msmovies.domain.dto.response.MovieMediaResponseDto;
import io.cinema.msmovies.domain.enumerated.MediaType;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class MovieMediaMockFactory {
    public static MovieMediaResponseDto buildMovieMediaResponseDto(UUID movieMediaId) {
        return new MovieMediaResponseDto(
                movieMediaId,
                MediaType.TRAILER,
                "https://localhost/movie-trailer.mp4",
                "The awakening of Jordan",
                0
        );
    }
}
