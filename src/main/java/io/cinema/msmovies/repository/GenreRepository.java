package io.cinema.msmovies.repository;

import io.cinema.msmovies.domain.entity.GenreEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface GenreRepository extends R2dbcRepository<GenreEntity, UUID> {
}
