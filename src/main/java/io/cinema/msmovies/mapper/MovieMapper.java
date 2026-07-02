package io.cinema.msmovies.mapper;

import io.cinema.msmovies.domain.dto.request.MovieRequestDto;
import io.cinema.msmovies.domain.dto.response.MovieInfoResponseDto;
import io.cinema.msmovies.domain.dto.response.MovieResponseDto;
import io.cinema.msmovies.domain.entity.ActorEntity;
import io.cinema.msmovies.domain.entity.DirectorEntity;
import io.cinema.msmovies.domain.entity.GenreEntity;
import io.cinema.msmovies.domain.entity.MovieEntity;
import io.cinema.msmovies.domain.entity.MovieMediaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {
                ActorMapper.class,
                DirectorMapper.class,
                GenreMapper.class,
                MovieMediaMapper.class
        }
)
public interface MovieMapper {

    MovieResponseDto toDto(MovieEntity movie,
                           Set<GenreEntity> genres,
                           Set<ActorEntity> actors,
                           Set<DirectorEntity> directors,
                           List<MovieMediaEntity> media
    );

    MovieInfoResponseDto toDto(MovieEntity movie);

    @Mapping(target = "id", ignore = true)
    MovieEntity toEntity(MovieRequestDto movie);


    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(MovieRequestDto dto, @MappingTarget MovieEntity entity);
}
