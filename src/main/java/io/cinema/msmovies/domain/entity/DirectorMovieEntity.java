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
@Table("director_movie")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DirectorMovieEntity {
    private UUID movieId;
    private UUID directorId;
}
