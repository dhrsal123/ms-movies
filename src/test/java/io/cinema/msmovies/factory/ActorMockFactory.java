package io.cinema.msmovies.factory;

import io.cinema.msmovies.domain.dto.request.ActorRequestDto;
import io.cinema.msmovies.domain.dto.response.ActorResponseDto;
import io.cinema.msmovies.domain.entity.ActorEntity;
import io.cinema.msmovies.domain.entity.ActorProjection;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.UUID;

@UtilityClass
public class ActorMockFactory {
    public static ActorEntity buildActorEntity(UUID actorId) {
        return new ActorEntity(
                actorId,
                "Jacob Lopez",
                LocalDate.of(2025, 10, 15),
                "https://localhost/bucket/jacob_lopez.png"
        );
    }

    public static ActorResponseDto buildActorResponseDto(UUID actorId) {
        return new ActorResponseDto(
                actorId,
                "Jacob Lopez",
                LocalDate.of(2025, 10, 15),
                "https://localhost/bucket/jacob_lopez.png"
        );
    }

    public static ActorRequestDto buildActorRequestDto() {
        return new ActorRequestDto(
                "Jacob Lopez",
                LocalDate.of(2025, 10, 15),
                "https://localhost/bucket/jacob_lopez.png"
        );
    }

    public static ActorProjection buildActorProjection(UUID movieId, UUID actorId) {
        var actorEntity = buildActorEntity(actorId);

        return new ActorProjection(
                movieId,
                actorEntity
        );
    }
}
