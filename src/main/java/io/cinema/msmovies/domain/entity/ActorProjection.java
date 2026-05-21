package io.cinema.msmovies.domain.entity;

import java.util.UUID;

public record ActorProjection(UUID movieId, ActorEntity actor) {
}