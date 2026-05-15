package io.cinema.msmovies.mapper;

import io.cinema.msmovies.domain.dto.request.GenreRequestDto;
import io.cinema.msmovies.domain.dto.response.GenreResponseDto;
import io.cinema.msmovies.domain.entity.GenreEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GenreMapper {

    GenreResponseDto toDto(GenreEntity actor);

    @Mapping(target = "id", ignore = true)
    GenreEntity toEntity(GenreRequestDto actor);


    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(GenreRequestDto dto, @MappingTarget GenreEntity entity);
}
