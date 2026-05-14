package io.cinema.msmovies.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GenreRequestDto(
        @NotBlank(message = "The genre name is required")
        @Size(max = 100, message = "Name cannot exceed 100 characters")
        String name,

        @NotBlank(message = "The genre description is required")
        @Size(max = 512, message = "Description cannot exceed 512 characters")
        String description,

        @Size(max = 1024, message = "Image URL cannot exceed 1024 characters")
        String imageUrl
) {
}