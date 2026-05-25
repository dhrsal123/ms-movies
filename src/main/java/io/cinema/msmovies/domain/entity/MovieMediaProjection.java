package io.cinema.msmovies.domain.entity;

import java.util.UUID;

public record MovieMediaProjection(UUID movieId, MovieMediaEntity movieMedia) {
}
