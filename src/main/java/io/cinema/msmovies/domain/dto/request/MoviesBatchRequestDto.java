package io.cinema.msmovies.domain.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;
import java.util.UUID;

public record MoviesBatchRequestDto(
        @NotEmpty
        Set<UUID> moviesIds
) {
}
