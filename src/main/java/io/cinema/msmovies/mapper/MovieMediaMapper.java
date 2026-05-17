package io.cinema.msmovies.mapper;

import io.cinema.msmovies.domain.dto.request.MovieMediaRequestDto;
import io.cinema.msmovies.domain.dto.response.MovieMediaResponseDto;
import io.cinema.msmovies.domain.entity.MovieMediaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MovieMediaMapper {

    MovieMediaResponseDto toDto(MovieMediaEntity media);

    @Mapping(target = "id", ignore = true)
    MovieMediaEntity toEntity(MovieMediaRequestDto media);


    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(MovieMediaRequestDto dto, @MappingTarget MovieMediaEntity entity);
}
