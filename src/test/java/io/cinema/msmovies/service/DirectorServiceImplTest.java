package io.cinema.msmovies.service;

import io.cinema.domain.exceptions.CinemaException;
import io.cinema.msmovies.domain.entity.DirectorEntity;
import io.cinema.msmovies.factory.DirectorMockFactory;
import io.cinema.msmovies.mapper.DirectorMapper;
import io.cinema.msmovies.mapper.DirectorMapperImpl;
import io.cinema.msmovies.repository.DirectorRepository;
import io.cinema.msmovies.service.impl.DirectorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
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
@Import({DirectorMapperImpl.class})
class DirectorServiceImplTest {

    @Autowired
    private DirectorMapper directorMapper;

    private DirectorRepository directorRepository;
    private TransactionalOperator transactionalOperator;
    private DirectorService directorService;

    @BeforeEach
    void setUp() {
        this.directorRepository = Mockito.mock(DirectorRepository.class);
        this.transactionalOperator = Mockito.mock(TransactionalOperator.class);
        when(transactionalOperator.transactional(any(Flux.class)))
                .thenAnswer(transactionalOperator -> transactionalOperator.getArgument(0));

        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(transactionalOperator -> transactionalOperator.getArgument(0));


        this.directorService = new DirectorServiceImpl(directorMapper, transactionalOperator, directorRepository);
    }

    @Test
    void shouldGetAllDirectors() {
        // arrange
        var directorId = UUID.randomUUID();
        var director = DirectorMockFactory.buildDirectorEntity(directorId);
        var directorResponse = DirectorMockFactory.buildDirectorResponseDto(directorId);
        int page = 0, size = 10;

        when(directorRepository.findAllBy(PageRequest.of(page, size))).thenReturn(Flux.just(director));
        // act
        var response = directorService.getAllDirectors(page, size);

        // assert
        StepVerifier.create(response)
                .expectNext(directorResponse)
                .verifyComplete();

        verify(directorRepository).findAllBy(PageRequest.of(page, size));
        verify(transactionalOperator).transactional(any(Flux.class));
    }

    @Test
    void shouldGetDirectorById() {
        // arrange
        var directorId = UUID.randomUUID();
        var director = DirectorMockFactory.buildDirectorEntity(directorId);
        var directorResponse = DirectorMockFactory.buildDirectorResponseDto(directorId);

        when(directorRepository.findById(directorId)).thenReturn(Mono.just(director));

        // act
        var response = directorService.getDirectorById(directorId);

        // assert
        StepVerifier.create(response)
                .expectNext(directorResponse)
                .verifyComplete();

        verify(directorRepository).findById(directorId);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldCreateDirector() {
        // arrange
        var directorRequest = DirectorMockFactory.buildDirectorRequestDto();

        var directorId = UUID.randomUUID();
        var director = DirectorMockFactory.buildDirectorEntity(directorId);

        var directorResponse = DirectorMockFactory.buildDirectorResponseDto(directorId);

        when(directorRepository.save(any(DirectorEntity.class))).thenReturn(Mono.just(director));

        // act
        var response = directorService.createDirector(directorRequest);

        // assert
        StepVerifier.create(response)
                .expectNext(directorResponse)
                .verifyComplete();

        verify(directorRepository).save(any(DirectorEntity.class));
        verify(transactionalOperator).transactional(any(Mono.class));
    }


    @Test
    void shouldUpdateDirector() {
        // arrange
        var directorRequest = DirectorMockFactory.buildDirectorRequestDto();

        var directorId = UUID.randomUUID();
        var director = DirectorMockFactory.buildDirectorEntity(directorId);

        var directorResponse = DirectorMockFactory.buildDirectorResponseDto(directorId);

        when(directorRepository.findById(directorId)).thenReturn(Mono.just(director));
        when(directorRepository.save(any(DirectorEntity.class))).thenReturn(Mono.just(director));

        // act
        var response = directorService.updateDirector(directorId, directorRequest);

        // assert
        StepVerifier.create(response)
                .expectNext(directorResponse)
                .verifyComplete();

        verify(directorRepository).findById(directorId);
        verify(directorRepository).save(any(DirectorEntity.class));
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldDeleteDirector() {
        // arrange
        var directorId = UUID.randomUUID();
        var director = DirectorMockFactory.buildDirectorEntity(directorId);

        when(directorRepository.findById(directorId)).thenReturn(Mono.just(director));
        when(directorRepository.deleteById(directorId)).thenReturn(Mono.empty());

        // act
        var response = directorService.deleteDirector(directorId);

        // assert
        StepVerifier.create(response)
                .verifyComplete();

        verify(directorRepository).findById(directorId);
        verify(directorRepository).deleteById(directorId);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldFailToUpdateDirectorWhenIdIsInvalid() {
        // arrange
        var directorRequest = DirectorMockFactory.buildDirectorRequestDto();

        var directorId = UUID.randomUUID();

        when(directorRepository.findById(directorId)).thenReturn(Mono.empty());

        // act
        var response = directorService.updateDirector(directorId, directorRequest);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException && e.getMessage().equals("Director not found.")
                )
                .verify();

        verify(directorRepository).findById(directorId);
        verify(transactionalOperator).transactional(any(Mono.class));
        verify(directorRepository, times(0)).save(any(DirectorEntity.class));
    }

    @Test
    void shouldFailToUpdateDirectorWhenTheresAnException() {
        // arrange
        var dbException = new CannotCreateTransactionException("Generic Exception");
        var directorRequest = DirectorMockFactory.buildDirectorRequestDto();

        var directorId = UUID.randomUUID();

        when(directorRepository.findById(directorId)).thenReturn(Mono.error(dbException));

        // act
        var response = directorService.updateDirector(directorId, directorRequest);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException && e.getMessage().equals("DB error during update")
                )
                .verify();

        verify(directorRepository).findById(directorId);
        verify(transactionalOperator).transactional(any(Mono.class));
        verify(directorRepository, times(0)).save(any(DirectorEntity.class));
    }

    @Test
    void shouldFailToGetAllDirectorsWhenTheresAnException() {
        // arrange
        int page = 0, size = 10;
        var dbException = new CannotCreateTransactionException("Generic Exception");

        when(directorRepository.findAllBy(PageRequest.of(page, size))).thenReturn(Flux.error(dbException));

        // act
        var response = directorService.getAllDirectors(page, size);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException && e.getMessage().equals("DB error during read")
                )
                .verify();

        verify(directorRepository).findAllBy(PageRequest.of(page, size));
        verify(transactionalOperator).transactional(any(Flux.class));
    }

    @Test
    void shouldFailToGetDirectorByIdWhenTheresAnException() {
        // arrange
        var directorId = UUID.randomUUID();
        var dbException = new CannotCreateTransactionException("Generic Exception");

        when(directorRepository.findById(directorId)).thenReturn(Mono.error(dbException));

        // act
        var response = directorService.getDirectorById(directorId);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException && e.getMessage().equals("DB error during read")
                )
                .verify();

        verify(directorRepository).findById(directorId);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldFailToCreateDirectorWhenTheresAnException() {
        // arrange
        var directorRequest = DirectorMockFactory.buildDirectorRequestDto();
        var dbException = new CannotCreateTransactionException("Generic Exception");

        when(directorRepository.save(any(DirectorEntity.class))).thenReturn(Mono.error(dbException));

        // act
        var response = directorService.createDirector(directorRequest);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException && e.getMessage().equals("DB error during save")
                )
                .verify();

        verify(directorRepository).save(any(DirectorEntity.class));
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldFailToDeleteDirectorWhenIdIsInvalid() {
        // arrange
        var directorId = UUID.randomUUID();

        when(directorRepository.findById(directorId)).thenReturn(Mono.empty());

        // act
        var response = directorService.deleteDirector(directorId);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException && e.getMessage().equals("Director not found.")
                )
                .verify();

        verify(directorRepository).findById(directorId);
        verify(transactionalOperator).transactional(any(Mono.class));
        verify(directorRepository, times(0)).deleteById(any(UUID.class));
    }

    @Test
    void shouldFailToDeleteDirectorWhenTheresAnException() {
        // arrange
        var directorId = UUID.randomUUID();
        var dbException = new CannotCreateTransactionException("Generic Exception");

        when(directorRepository.findById(directorId)).thenReturn(Mono.error(dbException));

        // act
        var response = directorService.deleteDirector(directorId);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException && e.getMessage().equals("DB error during delete")
                )
                .verify();

        verify(directorRepository).findById(directorId);
        verify(transactionalOperator).transactional(any(Mono.class));
        verify(directorRepository, times(0)).deleteById(any(UUID.class));
    }


    @Test
    void shouldFailToGetDirectorByIdWhenIdDoesNotExist() {
        // arrange
        var directorId = UUID.randomUUID();

        when(directorRepository.findById(directorId)).thenReturn(Mono.empty());

        // act
        var response = directorService.getDirectorById(directorId);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException &&
                                e.getMessage().equals("Director not found.") &&
                                ((CinemaException) e).getExceptionType() == NOT_FOUND
                )
                .verify();

        verify(directorRepository).findById(directorId);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldFailToUpdateDirectorWhenIdDoesNotExist() {
        // arrange
        var directorRequest = DirectorMockFactory.buildDirectorRequestDto();
        var directorId = UUID.randomUUID();

        when(directorRepository.findById(directorId)).thenReturn(Mono.empty());

        // act
        var response = directorService.updateDirector(directorId, directorRequest);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException &&
                                e.getMessage().equals("Director not found.") &&
                                ((CinemaException) e).getExceptionType() == NOT_FOUND
                )
                .verify();

        verify(directorRepository).findById(directorId);
        verify(directorRepository, times(0)).save(any(DirectorEntity.class));
    }

    @Test
    void shouldFailToDeleteDirectorWhenIdDoesNotExist() {
        // arrange
        var directorId = UUID.randomUUID();

        when(directorRepository.findById(directorId)).thenReturn(Mono.empty());

        // act
        var response = directorService.deleteDirector(directorId);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException &&
                                e.getMessage().equals("Director not found.") &&
                                ((CinemaException) e).getExceptionType() == NOT_FOUND
                )
                .verify();

        verify(directorRepository).findById(directorId);
        verify(directorRepository, times(0)).deleteById(any(UUID.class));
    }
}