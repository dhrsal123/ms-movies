package io.cinema.msmovies.domain.entity;

import io.cinema.domain.entity.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Table("movie")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class MovieEntity extends AuditableEntity {

    @Id
    private UUID id;

    private String title;
    private String description;
    private LocalDate releaseDate;

    private String posterUrl;
    private String backdropUrl;

}
