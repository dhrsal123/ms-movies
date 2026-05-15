package io.cinema.msmovies.controller;

import io.cinema.msmovies.domain.dto.response.DirectorResponseDto;
import io.cinema.msmovies.factory.DirectorMockFactory;
import io.cinema.msmovies.service.DirectorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DirectorControllerTest {
    private DirectorService directorService;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        this.directorService = Mockito.mock(DirectorService.class);
        this.webTestClient = WebTestClient.bindToController(new DirectorController(directorService)).build();
    }

    @Test
    void shouldGetAllDirectors() {
        // arrange
        var directorId = UUID.randomUUID();
        var director = DirectorMockFactory.buildDirectorResponseDto(directorId);
        when(directorService.getAllDirectors(0, 10)).thenReturn(Flux.just(director));

        // act & assert
        webTestClient.get().uri("/api/v1/directors?page=0&size=10")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DirectorResponseDto.class)
                .hasSize(1)
                .contains(director);


        verify(directorService).getAllDirectors(0, 10);
    }

    @Test
    void shouldGetDirectorById() {
        // arrange
        var directorId = UUID.randomUUID();
        var director = DirectorMockFactory.buildDirectorResponseDto(directorId);
        when(directorService.getDirectorById(directorId)).thenReturn(Mono.just(director));

        // act & assert
        webTestClient.get().uri("/api/v1/directors/{directorId}", directorId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DirectorResponseDto.class)
                .isEqualTo(director);

        verify(directorService).getDirectorById(directorId);
    }

    @Test
    void shouldCreateDirector() {
        // arrange
        var directorId = UUID.randomUUID();
        var directorResponse = DirectorMockFactory.buildDirectorResponseDto(directorId);
        var directorRequest = DirectorMockFactory.buildDirectorRequestDto();
        when(directorService.createDirector(directorRequest)).thenReturn(Mono.just(directorResponse));

        // act & assert
        webTestClient.post().uri("/api/v1/directors")
                .body(Mono.just(directorRequest), DirectorResponseDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DirectorResponseDto.class)
                .isEqualTo(directorResponse);

        verify(directorService).createDirector(directorRequest);
    }

    @Test
    void shouldUpdateDirector() {
        // arrange
        var directorId = UUID.randomUUID();
        var directorResponse = DirectorMockFactory.buildDirectorResponseDto(directorId);
        var directorRequest = DirectorMockFactory.buildDirectorRequestDto();
        when(directorService.updateDirector(directorId, directorRequest)).thenReturn(Mono.just(directorResponse));

        // act & assert
        webTestClient.put().uri("/api/v1/directors/{directorId}", directorId)
                .body(Mono.just(directorRequest), DirectorResponseDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DirectorResponseDto.class)
                .isEqualTo(directorResponse);

        verify(directorService).updateDirector(directorId, directorRequest);
    }

    @Test
    void shouldDeleteDirector() {
        // arrange
        var directorId = UUID.randomUUID();
        when(directorService.deleteDirector(directorId)).thenReturn(Mono.empty());

        // act & assert
        webTestClient.delete().uri("/api/v1/directors/{directorId}", directorId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);

        verify(directorService).deleteDirector(directorId);
    }


}