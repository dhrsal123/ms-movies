package io.cinema.msmovies.domain.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record DirectorResponseDto(
        UUID id,
        String name,
        LocalDate birthDate,
        String profilePictureUrl
) {
}