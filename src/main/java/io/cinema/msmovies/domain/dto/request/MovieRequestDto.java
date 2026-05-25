package io.cinema.msmovies.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record MovieRequestDto(
        @NotBlank(message = "Movie title is required")
        @Size(max = 100, message = "Title cannot exceed 100 characters")
        String title,

        @NotBlank(message = "Movie description is required")
        @Size(max = 512, message = "Description cannot exceed 512 characters")
        String description,

        @NotNull(message = "Release date is required")
        LocalDate releaseDate,

        @Size(max = 1024, message = "Poster URL cannot exceed 1024 characters")
        String posterUrl,

        @Size(max = 1024, message = "Backdrop URL cannot exceed 1024 characters")
        String backdropUrl,

        @NotEmpty(message = "A movie must have at least one genre")
        Set<UUID> genreIds,

        @NotEmpty(message = "A movie must have at least one actor")
        Set<UUID> actorIds,

        @NotEmpty(message = "A movie must have at least one director")
        Set<UUID> directorIds,

        List<MovieMediaRequestDto> media
) {
}