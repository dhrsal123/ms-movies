package io.cinema.msmovies.controller;

import io.cinema.msmovies.domain.dto.request.MovieRequestDto;
import io.cinema.msmovies.domain.dto.response.MovieResponseDto;
import io.cinema.msmovies.factory.MovieMockFactory;
import io.cinema.msmovies.service.MovieService;
import io.cinema.msmovies.service.impl.MovieServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MovieControllerTest {
    private MovieService movieService;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        this.movieService = mock(MovieServiceImpl.class);
        this.webTestClient = WebTestClient.bindToController(new MovieController(movieService)).build();
    }

    @Test
    void shouldGetAllMovies() {
        // arrange
        var movieId = UUID.randomUUID();
        var genreId = UUID.randomUUID();
        var actorId = UUID.randomUUID();
        var directorId = UUID.randomUUID();
        var mediaId = UUID.randomUUID();

        var movie = MovieMockFactory.buildMovieResponseDto(
                movieId,
                genreId,
                actorId,
                directorId,
                mediaId
        );
        when(movieService.getAllMovies(0, 10)).thenReturn(Flux.just(movie));
        // act & arrange
        webTestClient.get()
                .uri("/api/v1/movies?page=0&size=10")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MovieResponseDto.class)
                .contains(movie)
                .hasSize(1);
    }

    @Test
    void shouldGetMovieById() {
        // arrange
        var movieId = UUID.randomUUID();
        var genreId = UUID.randomUUID();
        var actorId = UUID.randomUUID();
        var directorId = UUID.randomUUID();
        var mediaId = UUID.randomUUID();

        var movie = MovieMockFactory.buildMovieResponseDto(
                movieId,
                genreId,
                actorId,
                directorId,
                mediaId
        );

        when(movieService.getMovieById(movieId)).thenReturn(Mono.just(movie));
        // act & arrange
        webTestClient.get()
                .uri("/api/v1/movies/{movieId}", movieId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovieResponseDto.class)
                .isEqualTo(movie);

        verify(movieService).getMovieById(movieId);
    }

    @Test
    void shouldCreateMovie() {
        // arrange
        var movieId = UUID.randomUUID();
        var genreId = UUID.randomUUID();
        var actorId = UUID.randomUUID();
        var directorId = UUID.randomUUID();
        var mediaId = UUID.randomUUID();

        var movie = MovieMockFactory.buildMovieResponseDto(
                movieId,
                genreId,
                actorId,
                directorId,
                mediaId
        );
        var movieRequest = MovieMockFactory.buildMovieRequestDto(
                genreId,
                actorId,
                directorId,
                mediaId
        );

        when(movieService.createMovie(movieRequest)).thenReturn(Mono.just(movie));
        // act & arrange
        webTestClient.post()
                .uri("/api/v1/movies")
                .body(Mono.just(movieRequest), MovieRequestDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovieResponseDto.class)
                .isEqualTo(movie);

        verify(movieService).createMovie(movieRequest);
    }


    @Test
    void shouldUpdateMovie() {
        // arrange
        var movieId = UUID.randomUUID();
        var genreId = UUID.randomUUID();
        var actorId = UUID.randomUUID();
        var directorId = UUID.randomUUID();
        var mediaId = UUID.randomUUID();

        var movie = MovieMockFactory.buildMovieResponseDto(
                movieId,
                genreId,
                actorId,
                directorId,
                mediaId
        );
        var movieRequest = MovieMockFactory.buildMovieRequestDto(
                genreId,
                actorId,
                directorId,
                mediaId
        );

        when(movieService.updateMovie(movieId, movieRequest)).thenReturn(Mono.just(movie));
        // act & arrange
        webTestClient.put()
                .uri("/api/v1/movies/{movieId}", movieId)
                .body(Mono.just(movieRequest), MovieRequestDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovieResponseDto.class)
                .isEqualTo(movie);

        verify(movieService).updateMovie(movieId, movieRequest);
    }


    @Test
    void shouldDeleteMovieById() {
        // arrange
        var movieId = UUID.randomUUID();

        when(movieService.deleteMovie(movieId)).thenReturn(Mono.empty());
        // act & arrange
        webTestClient.delete()
                .uri("/api/v1/movies/{movieId}", movieId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);

        verify(movieService).deleteMovie(movieId);
    }

}