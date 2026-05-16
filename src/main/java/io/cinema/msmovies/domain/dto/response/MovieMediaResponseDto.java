package io.cinema.msmovies.domain.dto.response;

import io.cinema.msmovies.domain.enumerated.MediaType;

import java.util.UUID;

public record MovieMediaResponseDto(
        UUID id,
        MediaType mediaType,
        String mediaUrl,
        String title,
        Integer displayOrder
) {
}