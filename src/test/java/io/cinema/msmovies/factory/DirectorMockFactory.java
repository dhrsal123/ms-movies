package io.cinema.msmovies.factory;

import io.cinema.msmovies.domain.dto.request.DirectorRequestDto;
import io.cinema.msmovies.domain.dto.response.DirectorResponseDto;
import io.cinema.msmovies.domain.entity.DirectorEntity;
import io.cinema.msmovies.domain.entity.DirectorProjection;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.UUID;

@UtilityClass
public class DirectorMockFactory {
    public static DirectorEntity buildDirectorEntity(UUID directorId) {
        return new DirectorEntity(
                directorId,
                "Christopher Nolan",
                LocalDate.of(2025, 10, 15),
                "https://localhost/bucket/chris-nolan.png"
        );
    }

    public static DirectorResponseDto buildDirectorResponseDto(UUID directorId) {
        return new DirectorResponseDto(
                directorId,
                "Christopher Nolan",
                LocalDate.of(2025, 10, 15),
                "https://localhost/bucket/chris-nolan.png"
        );
    }

    public static DirectorRequestDto buildDirectorRequestDto() {
        return new DirectorRequestDto(
                "Christopher Nolan",
                LocalDate.of(2025, 10, 15),
                "https://localhost/bucket/chris-nolan.png"

        );
    }

    public static DirectorProjection buildDirectorProjection(UUID movieId, UUID directorId){
        var directorEntity = buildDirectorEntity(directorId);

        return new DirectorProjection(
                movieId,
                directorEntity
        );
    }
}
