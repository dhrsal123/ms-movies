package io.cinema.msmovies.service;

import io.cinema.domain.exceptions.CinemaException;
import io.cinema.msmovies.domain.entity.GenreEntity;
import io.cinema.msmovies.factory.GenreMockFactory;
import io.cinema.msmovies.factory.MovieMockFactory;
import io.cinema.msmovies.mapper.GenreMapper;
import io.cinema.msmovies.mapper.GenreMapperImpl;
import io.cinema.msmovies.mapper.MovieMapper;
import io.cinema.msmovies.mapper.MovieMapperImpl;
import io.cinema.msmovies.repository.GenreRepository;
import io.cinema.msmovies.repository.MovieRepository;
import io.cinema.msmovies.service.impl.GenreServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static io.cinema.domain.enumerated.CinemaExceptionTypes.NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import({GenreMapperImpl.class, MovieMapperImpl.class})
class GenreServiceImplTest {

    @Autowired
    private GenreMapper genreMapper;

    @Autowired
    private MovieMapper movieMapper;

    private GenreRepository genreRepository;
    private MovieRepository movieRepository;
    private TransactionalOperator transactionalOperator;
    private GenreService genreService;

    @BeforeEach
    void setUp() {
        this.genreRepository = Mockito.mock(GenreRepository.class);
        this.movieRepository = Mockito.mock(MovieRepository.class);
        this.transactionalOperator = Mockito.mock(TransactionalOperator.class);

        when(transactionalOperator.transactional(any(Flux.class)))
                .thenAnswer(transactionalOperator -> transactionalOperator.getArgument(0));

        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(transactionalOperator -> transactionalOperator.getArgument(0));

        this.genreService = new GenreServiceImpl(
                genreRepository,
                movieRepository,
                transactionalOperator,
                genreMapper,
                movieMapper
        );
    }

    @Test
    void shouldGetAllGenres() {
        // arrange
        var genreId = UUID.randomUUID();
        var genre = GenreMockFactory.buildGenreEntity(genreId);
        var genreResponse = GenreMockFactory.buildGenreResponseDto(genreId);

        when(genreRepository.findAll()).thenReturn(Flux.just(genre));

        // act
        var response = genreService.getAllGenres();

        // assert
        StepVerifier.create(response)
                .expectNext(genreResponse)
                .verifyComplete();

        verify(genreRepository).findAll();
    }

    @Test
    void shouldGetGenreById() {
        // arrange
        var genreId = UUID.randomUUID();
        var genre = GenreMockFactory.buildGenreEntity(genreId);
        var genreResponse = GenreMockFactory.buildGenreResponseDto(genreId);

        when(genreRepository.findById(genreId)).thenReturn(Mono.just(genre));

        // act
        var response = genreService.getGenreById(genreId);

        // assert
        StepVerifier.create(response)
                .expectNext(genreResponse)
                .verifyComplete();

        verify(genreRepository).findById(genreId);
    }

    @Test
    void shouldGetMovieByGenre() {
        // arrange
        var movieId = UUID.randomUUID();
        var movie = MovieMockFactory.buildMovieEntity(movieId);
        var movieResponse = MovieMockFactory.buildMovieResponseDto(movieId);
        var genreId = UUID.randomUUID();

        when(movieRepository.findByGenreId(genreId)).thenReturn(Flux.just(movie));

        // act
        var response = genreService.getMovieByGenre(genreId);

        // assert
        StepVerifier.create(response)
                .expectNext(movieResponse)
                .verifyComplete();

        verify(movieRepository).findByGenreId(genreId);
    }

    @Test
    void shouldCreateGenre() {
        // arrange
        var genreRequest = GenreMockFactory.buildGenreRequestDto();
        var genreId = UUID.randomUUID();
        var genre = GenreMockFactory.buildGenreEntity(genreId);
        var genreResponse = GenreMockFactory.buildGenreResponseDto(genreId);

        when(genreRepository.save(any(GenreEntity.class))).thenReturn(Mono.just(genre));

        // act
        var response = genreService.createGenre(genreRequest);

        // assert
        StepVerifier.create(response)
                .expectNext(genreResponse)
                .verifyComplete();

        verify(genreRepository).save(any(GenreEntity.class));
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldUpdateGenre() {
        // arrange
        var genreRequest = GenreMockFactory.buildGenreRequestDto();
        var genreId = UUID.randomUUID();
        var genre = GenreMockFactory.buildGenreEntity(genreId);
        var genreResponse = GenreMockFactory.buildGenreResponseDto(genreId);

        when(genreRepository.findById(genreId)).thenReturn(Mono.just(genre));
        when(genreRepository.save(any(GenreEntity.class))).thenReturn(Mono.just(genre));

        // act
        var response = genreService.updateGenre(genreId, genreRequest);

        // assert
        StepVerifier.create(response)
                .expectNext(genreResponse)
                .verifyComplete();

        verify(genreRepository).findById(genreId);
        verify(genreRepository).save(any(GenreEntity.class));
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldDeleteGenre() {
        // arrange
        var genreId = UUID.randomUUID();
        var genre = GenreMockFactory.buildGenreEntity(genreId);

        when(genreRepository.findById(genreId)).thenReturn(Mono.just(genre));
        when(genreRepository.deleteById(genreId)).thenReturn(Mono.empty());

        // act
        var response = genreService.deleteGenre(genreId);

        // assert
        StepVerifier.create(response)
                .verifyComplete();

        verify(genreRepository).findById(genreId);
        verify(genreRepository).deleteById(genreId);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldFailToUpdateGenreWhenIdDoesNotExist() {
        // arrange
        var genreRequest = GenreMockFactory.buildGenreRequestDto();
        var genreId = UUID.randomUUID();

        when(genreRepository.findById(genreId)).thenReturn(Mono.empty());

        // act
        var response = genreService.updateGenre(genreId, genreRequest);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException &&
                                e.getMessage().equals("Genre not found.") &&
                                ((CinemaException) e).getExceptionType() == NOT_FOUND
                )
                .verify();

        verify(genreRepository).findById(genreId);
        verify(genreRepository, times(0)).save(any(GenreEntity.class));
    }

    @Test
    void shouldFailToDeleteGenreWhenIdDoesNotExist() {
        // arrange
        var genreId = UUID.randomUUID();

        when(genreRepository.findById(genreId)).thenReturn(Mono.empty());

        // act
        var response = genreService.deleteGenre(genreId);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException &&
                                e.getMessage().equals("Genre not found.") &&
                                ((CinemaException) e).getExceptionType() == NOT_FOUND
                )
                .verify();

        verify(genreRepository).findById(genreId);
        verify(genreRepository, times(0)).deleteById(any(UUID.class));
    }

    @Test
    void shouldFailToCreateGenreWhenTheresAnException() {
        // arrange
        var genreRequest = GenreMockFactory.buildGenreRequestDto();
        var dbException = new CannotCreateTransactionException("Generic Exception");

        when(genreRepository.save(any(GenreEntity.class))).thenReturn(Mono.error(dbException));

        // act
        var response = genreService.createGenre(genreRequest);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException && e.getMessage().equals("DB error during save")
                )
                .verify();

        verify(genreRepository).save(any(GenreEntity.class));
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    // --- NEW TESTS APPENDED FOR 100% COVERAGE ---

    @Test
    void shouldFailToGetAllGenresWhenTheresAnException() {
        // arrange
        var dbException = new CannotCreateTransactionException("Generic Exception");
        when(genreRepository.findAll()).thenReturn(Flux.error(dbException));

        // act
        var response = genreService.getAllGenres();

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException && e.getMessage().equals("DB error during read")
                )
                .verify();

        verify(genreRepository).findAll();
    }

    @Test
    void shouldFailToGetGenreByIdWhenIdDoesNotExist() {
        // arrange
        var genreId = UUID.randomUUID();
        when(genreRepository.findById(genreId)).thenReturn(Mono.empty());

        // act
        var response = genreService.getGenreById(genreId);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException &&
                                e.getMessage().equals("Genre not found.") &&
                                ((CinemaException) e).getExceptionType() == NOT_FOUND
                )
                .verify();

        verify(genreRepository).findById(genreId);
    }

    @Test
    void shouldFailToGetGenreByIdWhenTheresAnException() {
        // arrange
        var genreId = UUID.randomUUID();
        var dbException = new CannotCreateTransactionException("Generic Exception");
        when(genreRepository.findById(genreId)).thenReturn(Mono.error(dbException));

        // act
        var response = genreService.getGenreById(genreId);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException && e.getMessage().equals("DB error during read")
                )
                .verify();

        verify(genreRepository).findById(genreId);
    }

    @Test
    void shouldFailToGetMovieByGenreWhenTheresAnException() {
        // arrange
        var genreId = UUID.randomUUID();
        var dbException = new CannotCreateTransactionException("Generic Exception");
        when(movieRepository.findByGenreId(genreId)).thenReturn(Flux.error(dbException));

        // act
        var response = genreService.getMovieByGenre(genreId);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException && e.getMessage().equals("DB error during read")
                )
                .verify();

        verify(movieRepository).findByGenreId(genreId);
    }

    @Test
    void shouldFailToUpdateGenreWhenTheresAnException() {
        // arrange
        var genreRequest = GenreMockFactory.buildGenreRequestDto();
        var genreId = UUID.randomUUID();
        var genre = GenreMockFactory.buildGenreEntity(genreId);
        var dbException = new CannotCreateTransactionException("Generic Exception");

        when(genreRepository.findById(genreId)).thenReturn(Mono.just(genre));
        when(genreRepository.save(any(GenreEntity.class))).thenReturn(Mono.error(dbException));

        // act
        var response = genreService.updateGenre(genreId, genreRequest);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException && e.getMessage().equals("DB error during update")
                )
                .verify();

        verify(genreRepository).findById(genreId);
        verify(genreRepository).save(any(GenreEntity.class));
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldFailToDeleteGenreWhenTheresAnException() {
        // arrange
        var genreId = UUID.randomUUID();
        var genre = GenreMockFactory.buildGenreEntity(genreId);
        var dbException = new CannotCreateTransactionException("Generic Exception");

        when(genreRepository.findById(genreId)).thenReturn(Mono.just(genre));
        when(genreRepository.deleteById(genreId)).thenReturn(Mono.error(dbException));

        // act
        var response = genreService.deleteGenre(genreId);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException && e.getMessage().equals("DB error during delete")
                )
                .verify();

        verify(genreRepository).findById(genreId);
        verify(genreRepository).deleteById(genreId);
        verify(transactionalOperator).transactional(any(Mono.class));
    }
}