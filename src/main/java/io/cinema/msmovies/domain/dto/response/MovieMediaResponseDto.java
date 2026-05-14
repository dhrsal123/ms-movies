package io.cinema.msmovies.domain.dto.response;

import java.util.UUID;

public record MovieMediaResponseDto(
        UUID id,
        String mediaType,
        String mediaUrl,
        String title,
        Integer displayOrder
) {
}