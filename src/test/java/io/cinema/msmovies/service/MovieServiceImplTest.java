package io.cinema.msmovies.service;

import io.cinema.domain.exceptions.CinemaException;
import io.cinema.msmovies.domain.dto.request.MovieRequestDto;
import io.cinema.msmovies.domain.entity.MovieEntity;
import io.cinema.msmovies.factory.ActorMockFactory;
import io.cinema.msmovies.factory.DirectorMockFactory;
import io.cinema.msmovies.factory.GenreMockFactory;
import io.cinema.msmovies.factory.MovieMediaMockFactory;
import io.cinema.msmovies.factory.MovieMockFactory;
import io.cinema.msmovies.mapper.ActorMapperImpl;
import io.cinema.msmovies.mapper.DirectorMapperImpl;
import io.cinema.msmovies.mapper.GenreMapperImpl;
import io.cinema.msmovies.mapper.MovieMapper;
import io.cinema.msmovies.mapper.MovieMapperImpl;
import io.cinema.msmovies.mapper.MovieMediaMapperImpl;
import io.cinema.msmovies.repository.ActorRepository;
import io.cinema.msmovies.repository.DirectorRepository;
import io.cinema.msmovies.repository.GenreRepository;
import io.cinema.msmovies.repository.MovieMediaRepository;
import io.cinema.msmovies.repository.MovieRepository;
import io.cinema.msmovies.service.impl.MovieServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@Import({
        MovieMapperImpl.class,
        ActorMapperImpl.class,
        DirectorMapperImpl.class,
        GenreMapperImpl.class,
        MovieMediaMapperImpl.class
})
class MovieServiceImplTest {
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private ActorRepository actorRepository;
    @Mock
    private DirectorRepository directorRepository;
    @Mock
    private MovieMediaRepository movieMediaRepository;
    @Mock
    private TransactionalOperator transactionalOperator;

    @Autowired
    private MovieMapper movieMapper;

    private MovieService movieService;

    @BeforeEach
    void setUp() {
        lenient().when(transactionalOperator.transactional(any(Flux.class)))
                .thenAnswer(transactionalOperator -> transactionalOperator.getArgument(0));

        lenient().when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(transactionalOperator -> transactionalOperator.getArgument(0));

        this.movieService = new MovieServiceImpl(
                movieRepository,
                genreRepository,
                actorRepository,
                directorRepository,
                movieMediaRepository,
                transactionalOperator,
                movieMapper
        );
    }

    @Test
    void shouldGetAllMovies() {
        // arrange
        var page = 0;
        var size = 10;
        var pageable = PageRequest.of(page, size);

        var movieId = UUID.randomUUID();
        Collection<UUID> movieIds = Set.of(movieId);
        var movie = MovieMockFactory.buildMovieEntity(movieId);
        when(movieRepository.findAllBy(pageable)).thenReturn(Flux.just(movie));

        var genreId = UUID.randomUUID();
        var genres = GenreMockFactory.buildGenreProjection(movieId, genreId);
        when(genreRepository.findByMovieIdIn(movieIds))
                .thenReturn(Flux.just(genres));

        var actorId = UUID.randomUUID();
        var actors = ActorMockFactory.buildActorProjection(movieId, actorId);
        when(actorRepository.findByMovieIdIn(movieIds)).thenReturn(Flux.just(actors));

        var directorId = UUID.randomUUID();
        var directors = DirectorMockFactory.buildDirectorProjection(movieId, directorId);
        when(directorRepository.findByMovieIdIn(movieIds)).thenReturn(Flux.just(directors));

        var movieMediaId = UUID.randomUUID();
        var movieMedia = MovieMediaMockFactory.buildMovieMediaProjection(movieId, movieMediaId);
        when(movieMediaRepository.findAllByMovieIdIn(movieIds)).thenReturn(Flux.just(movieMedia));

        var movieResponse = MovieMockFactory.buildMovieResponseDto(
                movieId,
                genreId,
                actorId,
                directorId,
                movieMediaId
        );

        // act
        var response = movieService.getAllMovies(page, size);

        // assert
        StepVerifier.create(response)
                .expectNext(movieResponse)
                .verifyComplete();

        verify(movieRepository).findAllBy(pageable);
        verify(genreRepository).findByMovieIdIn(movieIds);
        verify(actorRepository).findByMovieIdIn(movieIds);
        verify(directorRepository).findByMovieIdIn(movieIds);
        verify(movieMediaRepository).findAllByMovieIdIn(movieIds);
        verify(transactionalOperator).transactional(any(Flux.class));
    }

    @Test
    void shouldGetAllMoviesEmpty() {
        // arrange
        var page = 0;
        var size = 10;
        var pageable = PageRequest.of(page, size);
        when(movieRepository.findAllBy(pageable)).thenReturn(Flux.empty());

        // act
        var response = movieService.getAllMovies(page, size);

        // assert
        StepVerifier.create(response)
                .verifyComplete();

        verify(movieRepository).findAllBy(pageable);
    }

    @Test
    void shouldReturnErrorWhenGetAllMoviesFails() {
        // arrange
        var page = 0;
        var size = 10;
        var pageable = PageRequest.of(page, size);
        when(movieRepository.findAllBy(pageable)).thenReturn(Flux.error(new RuntimeException("DB Error")));

        // act
        var response = movieService.getAllMovies(page, size);

        // assert
        StepVerifier.create(response)
                .expectError(CinemaException.class)
                .verify();
    }

    @Test
    void shouldGetMoviesByGenreId() {
        // arrange
        var movieId = UUID.randomUUID();
        Collection<UUID> movieIds = Set.of(movieId);

        var genreId = UUID.randomUUID();
        var movie = MovieMockFactory.buildMovieEntity(movieId);
        when(movieRepository.findByGenreId(genreId)).thenReturn(Flux.just(movie));

        var genres = GenreMockFactory.buildGenreProjection(movieId, genreId);
        when(genreRepository.findByMovieIdIn(movieIds))
                .thenReturn(Flux.just(genres));

        var actorId = UUID.randomUUID();
        var actors = ActorMockFactory.buildActorProjection(movieId, actorId);
        when(actorRepository.findByMovieIdIn(movieIds)).thenReturn(Flux.just(actors));

        var directorId = UUID.randomUUID();
        var directors = DirectorMockFactory.buildDirectorProjection(movieId, directorId);
        when(directorRepository.findByMovieIdIn(movieIds)).thenReturn(Flux.just(directors));

        var movieMediaId = UUID.randomUUID();
        var movieMedia = MovieMediaMockFactory.buildMovieMediaProjection(movieId, movieMediaId);
        when(movieMediaRepository.findAllByMovieIdIn(movieIds)).thenReturn(Flux.just(movieMedia));

        var movieResponse = MovieMockFactory.buildMovieResponseDto(
                movieId,
                genreId,
                actorId,
                directorId,
                movieMediaId
        );

        // act
        var response = movieService.getMoviesByGenreId(genreId);

        // assert
        StepVerifier.create(response)
                .expectNext(movieResponse)
                .verifyComplete();

        verify(movieRepository).findByGenreId(genreId);
        verify(genreRepository).findByMovieIdIn(movieIds);
        verify(actorRepository).findByMovieIdIn(movieIds);
        verify(directorRepository).findByMovieIdIn(movieIds);
        verify(movieMediaRepository).findAllByMovieIdIn(movieIds);
        verify(transactionalOperator).transactional(any(Flux.class));
    }

    @Test
    void shouldReturnErrorWhenGetMoviesByGenreIdFails() {
        // arrange
        var genreId = UUID.randomUUID();
        when(movieRepository.findByGenreId(genreId)).thenReturn(Flux.error(new RuntimeException("DB Error")));

        // act
        var response = movieService.getMoviesByGenreId(genreId);

        // assert
        StepVerifier.create(response)
                .expectError(CinemaException.class)
                .verify();
    }

    @Test
    void shouldGetMovieById() {
        // arrange
        var movieId = UUID.randomUUID();

        var movie = MovieMockFactory.buildMovieEntity(movieId);
        when(movieRepository.findById(movieId)).thenReturn(Mono.just(movie));

        var genreId = UUID.randomUUID();
        var genres = GenreMockFactory.buildGenreEntity(genreId);
        when(genreRepository.findByMovieId(movieId))
                .thenReturn(Flux.just(genres));

        var actorId = UUID.randomUUID();
        var actors = ActorMockFactory.buildActorEntity(actorId);
        when(actorRepository.findByMovieId(movieId)).thenReturn(Flux.just(actors));

        var directorId = UUID.randomUUID();
        var directors = DirectorMockFactory.buildDirectorEntity(directorId);
        when(directorRepository.findByMovieId(movieId)).thenReturn(Flux.just(directors));

        var movieMediaId = UUID.randomUUID();
        var movieMedia = MovieMediaMockFactory.buildMovieMediaEntity(movieMediaId, movieId);
        when(movieMediaRepository.findAllByMovieId(movieId)).thenReturn(Flux.just(movieMedia));

        var movieResponse = MovieMockFactory.buildMovieResponseDto(
                movieId,
                genreId,
                actorId,
                directorId,
                movieMediaId
        );

        // act
        var response = movieService.getMovieById(movieId);

        // assert
        StepVerifier.create(response)
                .expectNext(movieResponse)
                .verifyComplete();

        verify(movieRepository).findById(movieId);
        verify(genreRepository).findByMovieId(movieId);
        verify(actorRepository).findByMovieId(movieId);
        verify(directorRepository).findByMovieId(movieId);
        verify(movieMediaRepository).findAllByMovieId(movieId);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldReturnNotFoundWhenGetMovieByIdEmpty() {
        // arrange
        var movieId = UUID.randomUUID();
        when(movieRepository.findById(movieId)).thenReturn(Mono.empty());

        // act
        var response = movieService.getMovieById(movieId);

        // assert
        StepVerifier.create(response)
                .expectError(CinemaException.class)
                .verify();
    }

    @Test
    void shouldReturnErrorWhenGetMovieByIdFails() {
        // arrange
        var movieId = UUID.randomUUID();
        when(movieRepository.findById(movieId)).thenReturn(Mono.error(new RuntimeException("DB Error")));

        // act
        var response = movieService.getMovieById(movieId);

        // assert
        StepVerifier.create(response)
                .expectError(CinemaException.class)
                .verify();
    }

    @Test
    void shouldCreateMovie() {
        // arrange
        var movieId = UUID.randomUUID();

        var movie = MovieMockFactory.buildMovieEntity(movieId);
        when(movieRepository.save(any(MovieEntity.class))).thenReturn(Mono.just(movie));
        when(movieRepository.findById(movieId)).thenReturn(Mono.just(movie));

        var genreId = UUID.randomUUID();
        var genres = GenreMockFactory.buildGenreEntity(genreId);
        when(genreRepository.findByMovieId(movieId))
                .thenReturn(Flux.just(genres));

        when(genreRepository.saveMovieGenres(movieId, Set.of(genreId)))
                .thenReturn(Mono.empty());

        var actorId = UUID.randomUUID();
        var actors = ActorMockFactory.buildActorEntity(actorId);
        when(actorRepository.findByMovieId(movieId)).thenReturn(Flux.just(actors));
        when(actorRepository.saveMovieActors(movieId, Set.of(actorId))).thenReturn(Mono.empty());

        var directorId = UUID.randomUUID();
        var directors = DirectorMockFactory.buildDirectorEntity(directorId);
        when(directorRepository.findByMovieId(movieId)).thenReturn(Flux.just(directors));
        when(directorRepository.saveMovieDirectors(movieId, Set.of(directorId))).thenReturn(Mono.empty());

        var movieMediaId = UUID.randomUUID();
        var movieMedia = MovieMediaMockFactory.buildMovieMediaEntity(movieMediaId, movieId);
        when(movieMediaRepository.findAllByMovieId(movieId)).thenReturn(Flux.just(movieMedia));

        var movieResponse = MovieMockFactory.buildMovieResponseDto(
                movieId,
                genreId,
                actorId,
                directorId,
                movieMediaId
        );

        var movieRequest = MovieMockFactory.buildMovieRequestDto(
                genreId,
                actorId,
                directorId,
                movieMediaId
        );

        when(movieMediaRepository.saveMovieMedia(movieId, movieRequest.media())).thenReturn(Mono.empty());

        // act
        var response = movieService.createMovie(movieRequest);

        // assert
        StepVerifier.create(response)
                .expectNext(movieResponse)
                .verifyComplete();

        verify(movieRepository).findById(movieId);
        verify(genreRepository).findByMovieId(movieId);
        verify(actorRepository).findByMovieId(movieId);
        verify(directorRepository).findByMovieId(movieId);
        verify(movieMediaRepository).findAllByMovieId(movieId);
        verify(transactionalOperator, times(2)).transactional(any(Mono.class));
    }

    @Test
    void shouldCreateMovieWithNullAndEmptyRelationships() {
        // arrange
        var movieId = UUID.randomUUID();
        var movieRequest = mock(MovieRequestDto.class);
        when(movieRequest.genreIds()).thenReturn(null);
        when(movieRequest.actorIds()).thenReturn(null);
        when(movieRequest.directorIds()).thenReturn(null);
        when(movieRequest.media()).thenReturn(Collections.emptyList());

        var movie = MovieMockFactory.buildMovieEntity(movieId);
        when(movieRepository.save(any(MovieEntity.class))).thenReturn(Mono.just(movie));
        when(movieRepository.findById(movieId)).thenReturn(Mono.just(movie));

        when(genreRepository.saveMovieGenres(movieId, Collections.emptySet())).thenReturn(Mono.empty());
        when(genreRepository.findByMovieId(movieId)).thenReturn(Flux.empty());

        when(actorRepository.saveMovieActors(movieId, Collections.emptySet())).thenReturn(Mono.empty());
        when(actorRepository.findByMovieId(movieId)).thenReturn(Flux.empty());

        when(directorRepository.saveMovieDirectors(movieId, Collections.emptySet())).thenReturn(Mono.empty());
        when(directorRepository.findByMovieId(movieId)).thenReturn(Flux.empty());

        when(movieMediaRepository.findAllByMovieId(movieId)).thenReturn(Flux.empty());

        // act
        var response = movieService.createMovie(movieRequest);

        // assert
        StepVerifier.create(response)
                .expectNextCount(1)
                .verifyComplete();

        verify(genreRepository).saveMovieGenres(movieId, Collections.emptySet());
        verify(actorRepository).saveMovieActors(movieId, Collections.emptySet());
        verify(directorRepository).saveMovieDirectors(movieId, Collections.emptySet());
        verify(movieMediaRepository, times(0)).saveMovieMedia(any(), any());
    }

    @Test
    void shouldReturnErrorWhenCreateMovieFails() {
        // arrange
        var movieRequest = mock(MovieRequestDto.class);
        when(movieRepository.save(any(MovieEntity.class))).thenReturn(Mono.error(new RuntimeException("DB Error")));

        // act
        var response = movieService.createMovie(movieRequest);

        // assert
        StepVerifier.create(response)
                .expectError(CinemaException.class)
                .verify();
    }

    @Test
    void shouldUpdateMovie() {
        // arrange
        var movieId = UUID.randomUUID();

        var movie = MovieMockFactory.buildMovieEntity(movieId);
        when(movieRepository.save(any(MovieEntity.class))).thenReturn(Mono.just(movie));
        when(movieRepository.findById(movieId)).thenReturn(Mono.just(movie));

        var genreId = UUID.randomUUID();
        var genres = GenreMockFactory.buildGenreEntity(genreId);
        when(genreRepository.findByMovieId(movieId))
                .thenReturn(Flux.just(genres));

        when(genreRepository.saveMovieGenres(movieId, Set.of(genreId)))
                .thenReturn(Mono.empty());
        when(genreRepository.deleteByMovieId(movieId)).thenReturn(Mono.empty());

        var actorId = UUID.randomUUID();
        var actors = ActorMockFactory.buildActorEntity(actorId);
        when(actorRepository.findByMovieId(movieId)).thenReturn(Flux.just(actors));
        when(actorRepository.saveMovieActors(movieId, Set.of(actorId))).thenReturn(Mono.empty());
        when(actorRepository.deleteByMovieId(movieId)).thenReturn(Mono.empty());

        var directorId = UUID.randomUUID();
        var directors = DirectorMockFactory.buildDirectorEntity(directorId);
        when(directorRepository.findByMovieId(movieId)).thenReturn(Flux.just(directors));
        when(directorRepository.saveMovieDirectors(movieId, Set.of(directorId))).thenReturn(Mono.empty());
        when(directorRepository.deleteByMovieId(movieId)).thenReturn(Mono.empty());

        var movieMediaId = UUID.randomUUID();
        var movieMedia = MovieMediaMockFactory.buildMovieMediaEntity(movieMediaId, movieId);
        when(movieMediaRepository.findAllByMovieId(movieId)).thenReturn(Flux.just(movieMedia));
        when(movieMediaRepository.deleteByMovieId(movieId)).thenReturn(Mono.empty());

        var movieResponse = MovieMockFactory.buildMovieResponseDto(
                movieId,
                genreId,
                actorId,
                directorId,
                movieMediaId
        );

        var movieRequest = MovieMockFactory.buildMovieRequestDto(
                genreId,
                actorId,
                directorId,
                movieMediaId
        );

        when(movieMediaRepository.saveMovieMedia(movieId, movieRequest.media())).thenReturn(Mono.empty());

        // act
        var response = movieService.updateMovie(movieId, movieRequest);

        // assert
        StepVerifier.create(response)
                .expectNext(movieResponse)
                .verifyComplete();

        verify(movieRepository, times(2)).findById(movieId);
        verify(genreRepository).findByMovieId(movieId);
        verify(genreRepository).saveMovieGenres(movieId, Set.of(genreId));
        verify(genreRepository).deleteByMovieId(movieId);

        verify(actorRepository).findByMovieId(movieId);
        verify(actorRepository).deleteByMovieId(movieId);

        verify(directorRepository).findByMovieId(movieId);
        verify(directorRepository).deleteByMovieId(movieId);

        verify(movieMediaRepository).findAllByMovieId(movieId);
        verify(movieMediaRepository).deleteByMovieId(movieId);


        verify(transactionalOperator, times(2)).transactional(any(Mono.class));
    }

    @Test
    void shouldReturnNotFoundWhenUpdateMovieEmpty() {
        // arrange
        var movieId = UUID.randomUUID();
        var movieRequest = mock(MovieRequestDto.class);
        when(movieRepository.findById(movieId)).thenReturn(Mono.empty());

        // act
        var response = movieService.updateMovie(movieId, movieRequest);

        // assert
        StepVerifier.create(response)
                .expectError(CinemaException.class)
                .verify();
    }

    @Test
    void shouldReturnErrorWhenUpdateMovieFails() {
        // arrange
        var movieId = UUID.randomUUID();
        var movieRequest = mock(MovieRequestDto.class);
        when(movieRepository.findById(movieId)).thenReturn(Mono.error(new RuntimeException("DB Error")));

        // act
        var response = movieService.updateMovie(movieId, movieRequest);

        // assert
        StepVerifier.create(response)
                .expectError(CinemaException.class)
                .verify();
    }

    @Test
    void shouldDeleteMovie() {
        // arrange
        var movieId = UUID.randomUUID();

        var movie = MovieMockFactory.buildMovieEntity(movieId);
        when(movieRepository.deleteById(movieId)).thenReturn(Mono.empty());
        when(movieRepository.findById(movieId)).thenReturn(Mono.just(movie));

        when(genreRepository.deleteByMovieId(movieId)).thenReturn(Mono.empty());
        when(actorRepository.deleteByMovieId(movieId)).thenReturn(Mono.empty());
        when(directorRepository.deleteByMovieId(movieId)).thenReturn(Mono.empty());
        when(movieMediaRepository.deleteByMovieId(movieId)).thenReturn(Mono.empty());

        // act
        var response = movieService.deleteMovie(movieId);

        // assert
        StepVerifier.create(response)
                .verifyComplete();

        verify(movieRepository).findById(movieId);
        verify(genreRepository).deleteByMovieId(movieId);
        verify(actorRepository).deleteByMovieId(movieId);
        verify(directorRepository).deleteByMovieId(movieId);
        verify(movieMediaRepository).deleteByMovieId(movieId);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldReturnNotFoundWhenDeleteMovieEmpty() {
        // arrange
        var movieId = UUID.randomUUID();
        when(movieRepository.findById(movieId)).thenReturn(Mono.empty());

        // act
        var response = movieService.deleteMovie(movieId);

        // assert
        StepVerifier.create(response)
                .expectError(CinemaException.class)
                .verify();
    }

    @Test
    void shouldReturnErrorWhenDeleteMovieFails() {
        // arrange
        var movieId = UUID.randomUUID();
        when(movieRepository.findById(movieId)).thenReturn(Mono.error(new RuntimeException("DB Error")));

        // act
        var response = movieService.deleteMovie(movieId);

        // assert
        StepVerifier.create(response)
                .expectError(CinemaException.class)
                .verify();
    }

    @Test
    void shouldGetMoviesInfoByIdsBatch() {
        // arrange
        var movieId = UUID.randomUUID();
        var request = MovieMockFactory.buildMoviesBatchRequestDto(Set.of(movieId));
        var entity = MovieMockFactory.buildMovieEntity(movieId);
        var responseDto = MovieMockFactory.buildMovieInfoResponseDto(movieId);

        when(movieRepository.findAllById(request.moviesIds())).thenReturn(Flux.just(entity));

        // act
        var response = movieService.getMoviesInfoByIds(request);

        // assert
        StepVerifier.create(response)
                .expectNext(responseDto)
                .verifyComplete();

        verify(movieRepository).findAllById(request.moviesIds());
    }

    @Test
    void shouldReturnErrorWhenGetMoviesInfoByIdsFails() {
        // arrange
        var movieId = UUID.randomUUID();
        var request = MovieMockFactory.buildMoviesBatchRequestDto(Set.of(movieId));

        when(movieRepository.findAllById(request.moviesIds()))
                .thenReturn(Flux.error(new RuntimeException("DB Error")));

        // act
        var response = movieService.getMoviesInfoByIds(request);

        // assert
        StepVerifier.create(response)
                .expectError(CinemaException.class)
                .verify();

        verify(movieRepository).findAllById(request.moviesIds());
    }
}