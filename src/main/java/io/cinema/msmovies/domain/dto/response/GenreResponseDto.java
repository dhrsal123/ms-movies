package io.cinema.msmovies.domain.dto.response;

import java.util.UUID;

public record GenreResponseDto(
        UUID id,
        String name,
        String description,
        String imageUrl
) {
}