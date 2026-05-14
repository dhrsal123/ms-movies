package io.cinema.msmovies.mapper;

import io.cinema.msmovies.domain.dto.request.ActorRequestDto;
import io.cinema.msmovies.domain.dto.response.ActorResponseDto;
import io.cinema.msmovies.domain.entity.ActorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ActorMapper {

    @Mapping(target = "birthDate", source = "birthday")
    ActorResponseDto toDto(ActorEntity actor);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "birthday", source = "birthDate")
    ActorEntity toEntity(ActorRequestDto actor);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "birthday", source = "birthDate")
    void updateEntityFromDto(ActorRequestDto dto, @MappingTarget ActorEntity entity);
}
