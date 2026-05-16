package io.cinema.msmovies.factory;

import io.cinema.msmovies.domain.dto.request.GenreRequestDto;
import io.cinema.msmovies.domain.dto.response.GenreResponseDto;
import io.cinema.msmovies.domain.entity.GenreEntity;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class GenreMockFactory {

    public static GenreResponseDto buildGenreResponseDto(UUID genreId) {
        return new GenreResponseDto(
                genreId,
                "Horror",
                "Horror movies, very scary.",
                "https://localhost/bucket/scary.png"
        );
    }

    public static GenreEntity buildGenreEntity(UUID genreId) {
        return new GenreEntity(
                genreId,
                "Horror",
                "Horror movies, very scary.",
                "https://localhost/bucket/scary.png"
        );
    }

    public static GenreRequestDto buildGenreRequestDto() {
        return new GenreRequestDto(
                "Horror",
                "Horror movies, very scary.",
                "https://localhost/bucket/scary.png"
        );
    }
}
