package io.cinema.msmovies.domain.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record MovieResponseDto(
        UUID id,
        String title,
        String description,
        LocalDate releaseDate,
        String posterUrl,
        String backdropUrl,

        Set<GenreResponseDto> genres,
        Set<ActorResponseDto> actors,
        Set<DirectorResponseDto> directors,
        List<MovieMediaResponseDto> media
) {
}