package io.cinema.msmovies.mapper;

import io.cinema.msmovies.domain.dto.request.MovieRequestDto;
import io.cinema.msmovies.domain.dto.response.MovieResponseDto;
import io.cinema.msmovies.domain.entity.MovieEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MovieMapper {

    MovieResponseDto toDto(MovieEntity movie);

    @Mapping(target = "id", ignore = true)
    MovieEntity toEntity(MovieRequestDto movie);


    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(MovieRequestDto dto, @MappingTarget MovieEntity entity);
}
