package io.cinema.msmovies.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ActorRequestDto(
        @NotBlank(message = "Actor name is required")
        @Size(max = 100, message = "Name cannot exceed 100 characters")
        String name,

        @NotNull(message = "The actor's birth date must not be null")
        LocalDate birthDate,

        @Size(max = 1024, message = "Profile picture URL cannot exceed 1024 characters")
        String profilePictureUrl
) {
}
