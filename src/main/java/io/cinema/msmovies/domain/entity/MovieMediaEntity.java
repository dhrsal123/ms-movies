package io.cinema.msmovies.domain.entity;

import io.cinema.domain.entity.AuditableEntity;
import io.cinema.msmovies.domain.enumerated.MediaType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@Table("movie_media")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class MovieMediaEntity extends AuditableEntity {
    @Id
    private UUID id;

    private UUID movieId;
    private MediaType mediaType;
    private String mediaUrl;
    private String title;
    private Integer displayOrder;
}