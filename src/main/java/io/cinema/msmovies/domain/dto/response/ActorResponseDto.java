package io.cinema.msmovies.domain.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record ActorResponseDto(
        UUID id,
        String name,
        LocalDate birthDate,
        String profilePictureUrl
) {
}