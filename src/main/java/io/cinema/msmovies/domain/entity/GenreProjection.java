package io.cinema.msmovies.domain.entity;

import java.util.UUID;

public record GenreProjection(UUID movieId, GenreEntity genre) {
}