package io.cinema.msmovies.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@Table("movie_actor")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieActorEntity {
    private UUID movieId;
    private UUID actorId;
}