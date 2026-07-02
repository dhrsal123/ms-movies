package io.cinema.msmovies.domain.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record MovieInfoResponseDto(
        UUID id,
        String title,
        String description,
        LocalDate releaseDate,
        String posterUrl,
        String backdropUrl
) {
}
