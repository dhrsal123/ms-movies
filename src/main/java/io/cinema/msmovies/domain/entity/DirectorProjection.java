package io.cinema.msmovies.domain.entity;

import java.util.UUID;

public record DirectorProjection(UUID movieId, DirectorEntity director) {
}
