package io.cinema.msmovies.controller;

import io.cinema.msmovies.domain.dto.response.ActorResponseDto;
import io.cinema.msmovies.factory.ActorMockFactory;
import io.cinema.msmovies.service.ActorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ActorControllerTest {
    private ActorService actorService;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        this.actorService = Mockito.mock(ActorService.class);
        this.webTestClient = WebTestClient.bindToController(new ActorController(actorService)).build();
    }

    @Test
    void shouldGetAllActors() {
        // arrange
        var actorId = UUID.randomUUID();
        var actor = ActorMockFactory.buildActorResponseDto(actorId);
        when(actorService.getAllActors(0, 10)).thenReturn(Flux.just(actor));

        // act & assert
        webTestClient.get().uri("/api/v1/actors?page=0&size=10")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ActorResponseDto.class)
                .hasSize(1)
                .contains(actor);


        verify(actorService).getAllActors(0, 10);
    }

    @Test
    void shouldGetActorById() {
        // arrange
        var actorId = UUID.randomUUID();
        var actor = ActorMockFactory.buildActorResponseDto(actorId);
        when(actorService.getActorById(actorId)).thenReturn(Mono.just(actor));

        // act & assert
        webTestClient.get().uri("/api/v1/actors/{actorId}", actorId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ActorResponseDto.class)
                .isEqualTo(actor);

        verify(actorService).getActorById(actorId);
    }

    @Test
    void shouldCreateActor() {
        // arrange
        var actorId = UUID.randomUUID();
        var actorResponse = ActorMockFactory.buildActorResponseDto(actorId);
        var actorRequest = ActorMockFactory.buildActorRequestDto();
        when(actorService.createActor(actorRequest)).thenReturn(Mono.just(actorResponse));

        // act & assert
        webTestClient.post().uri("/api/v1/actors")
                .body(Mono.just(actorRequest), ActorResponseDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ActorResponseDto.class)
                .isEqualTo(actorResponse);

        verify(actorService).createActor(actorRequest);
    }

    @Test
    void shouldUpdateActor() {
        // arrange
        var actorId = UUID.randomUUID();
        var actorResponse = ActorMockFactory.buildActorResponseDto(actorId);
        var actorRequest = ActorMockFactory.buildActorRequestDto();
        when(actorService.updateActor(actorId, actorRequest)).thenReturn(Mono.just(actorResponse));

        // act & assert
        webTestClient.put().uri("/api/v1/actors/{actorId}", actorId)
                .body(Mono.just(actorRequest), ActorResponseDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ActorResponseDto.class)
                .isEqualTo(actorResponse);

        verify(actorService).updateActor(actorId, actorRequest);
    }

    @Test
    void shouldDeleteActor() {
        // arrange
        var actorId = UUID.randomUUID();
        when(actorService.deleteActor(actorId)).thenReturn(Mono.empty());

        // act & assert
        webTestClient.delete().uri("/api/v1/actors/{actorId}", actorId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);

        verify(actorService).deleteActor(actorId);
    }


}