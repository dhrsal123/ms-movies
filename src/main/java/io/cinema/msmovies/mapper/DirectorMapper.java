package io.cinema.msmovies.mapper;

import io.cinema.msmovies.domain.dto.request.DirectorRequestDto;
import io.cinema.msmovies.domain.dto.response.DirectorResponseDto;
import io.cinema.msmovies.domain.entity.DirectorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DirectorMapper {

    @Mapping(target = "birthDate", source = "birthday")
    DirectorResponseDto toDto(DirectorEntity actor);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "birthday", source = "birthDate")
    DirectorEntity toEntity(DirectorRequestDto actor);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "birthday", source = "birthDate")
    void updateEntityFromDto(DirectorRequestDto dto, @MappingTarget DirectorEntity entity);
}
