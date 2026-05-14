package io.cinema.msmovies.domain.entity;

import io.cinema.domain.entity.AuditableEntity;
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
@Table("genre")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class GenreEntity extends AuditableEntity {
    @Id
    private UUID id;

    private String name;
    private String description;
    private String imageUrl;
}
