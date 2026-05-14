package io.cinema.msmovies.domain.dto.request;

import io.cinema.msmovies.domain.enumerated.MediaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MovieMediaRequestDto(
        @NotNull(message = "Media type is required (e.g. TRAILER, GALLERY)")
        MediaType mediaType,

        @NotBlank(message = "Media URL is required")
        @Size(max = 1024)
        String mediaUrl,

        @Size(max = 255)
        String title,

        Integer displayOrder
) {
}