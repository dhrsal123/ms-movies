package io.cinema.msmovies.controller;

import io.cinema.msmovies.domain.dto.request.GenreRequestDto;
import io.cinema.msmovies.domain.dto.response.GenreResponseDto;
import io.cinema.msmovies.domain.dto.response.MovieResponseDto;
import io.cinema.msmovies.factory.GenreMockFactory;
import io.cinema.msmovies.factory.MovieMockFactory;
import io.cinema.msmovies.service.GenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GenreControllerTest {
    private GenreService genreService;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        this.genreService = mock(GenreService.class);
        this.webTestClient = WebTestClient.bindToController(new GenreController(genreService)).build();
    }

    @Test
    void shouldGetAllGenres() {
        // arrange
        var genreId = UUID.randomUUID();
        var genreResponse = GenreMockFactory.buildGenreResponseDto(genreId);

        when(genreService.getAllGenres()).thenReturn(Flux.just(genreResponse));

        // act & assert
        webTestClient.get()
                .uri("/api/v1/genres")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(GenreResponseDto.class)
                .hasSize(1)
                .contains(genreResponse);

        verify(genreService).getAllGenres();
    }

    @Test
    void shouldGetGenreById() {
        // arrange
        var genreId = UUID.randomUUID();
        var genreResponse = GenreMockFactory.buildGenreResponseDto(genreId);

        when(genreService.getGenre(genreId)).thenReturn(Mono.just(genreResponse));

        // act & assert
        webTestClient.get()
                .uri("/api/v1/genres/{genreId}", genreId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GenreResponseDto.class)
                .isEqualTo(genreResponse);

        verify(genreService).getGenre(genreId);
    }

    @Test
    void shouldGetMoviesByGenre() {
        // arrange
        var genreId = UUID.randomUUID();
        var movieId = UUID.randomUUID();
        var movie = MovieMockFactory.movieResponseDto(movieId);

        when(genreService.getMovieByGenre(genreId)).thenReturn(Flux.just(movie));

        // act & assert
        webTestClient.get()
                .uri("/api/v1/genres/{genreId}/movies", genreId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MovieResponseDto.class)
                .hasSize(1)
                .contains(movie);

        verify(genreService).getMovieByGenre(genreId);

    }

    @Test
    void shouldCreateGenre() {
        // arrange
        var genreId = UUID.randomUUID();
        var genreResponse = GenreMockFactory.buildGenreResponseDto(genreId);
        var genreRequest = GenreMockFactory.buildGenreRequestDto();

        when(genreService.createGenre(genreRequest)).thenReturn(Mono.just(genreResponse));

        // act & assert
        webTestClient.post()
                .uri("/api/v1/genres")
                .body(Mono.just(genreRequest), GenreRequestDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GenreResponseDto.class)
                .isEqualTo(genreResponse);

        verify(genreService).createGenre(genreRequest);

    }

    @Test
    void shouldUpdateGenre() {
        // arrange
        var genreId = UUID.randomUUID();
        var genreResponse = GenreMockFactory.buildGenreResponseDto(genreId);
        var genreRequest = GenreMockFactory.buildGenreRequestDto();

        when(genreService.updateGenre(genreId, genreRequest)).thenReturn(Mono.just(genreResponse));

        // act & assert
        webTestClient.put()
                .uri("/api/v1/genres/{genreId}", genreId)
                .body(Mono.just(genreRequest), GenreRequestDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GenreResponseDto.class)
                .isEqualTo(genreResponse);

        verify(genreService).updateGenre(genreId, genreRequest);
    }

    @Test
    void shouldDeleteGenre() {
        // arrange
        var genreId = UUID.randomUUID();

        when(genreService.deleteGenre(genreId)).thenReturn(Mono.empty());

        // act & assert
        webTestClient.delete()
                .uri("/api/v1/genres/{genreId}", genreId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);


        verify(genreService).deleteGenre(genreId);
    }


}