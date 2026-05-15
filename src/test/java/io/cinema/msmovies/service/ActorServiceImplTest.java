package io.cinema.msmovies.service;

import io.cinema.domain.exceptions.CinemaException;
import io.cinema.msmovies.domain.entity.ActorEntity;
import io.cinema.msmovies.factory.ActorMockFactory;
import io.cinema.msmovies.mapper.ActorMapper;
import io.cinema.msmovies.mapper.ActorMapperImpl;
import io.cinema.msmovies.repository.ActorRepository;
import io.cinema.msmovies.service.impl.ActorServiceImpl;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import({ActorMapperImpl.class})
class ActorServiceImplTest {

    @Autowired
    private ActorMapper actorMapper;

    private ActorRepository actorRepository;
    private TransactionalOperator transactionalOperator;
    private ActorService actorService;

    @BeforeEach
    void setUp() {
        this.actorRepository = Mockito.mock(ActorRepository.class);
        this.transactionalOperator = Mockito.mock(TransactionalOperator.class);
        when(transactionalOperator.transactional(any(Flux.class)))
                .thenAnswer(transactionalOperator -> transactionalOperator.getArgument(0));

        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(transactionalOperator -> transactionalOperator.getArgument(0));


        this.actorService = new ActorServiceImpl(actorRepository, actorMapper, transactionalOperator);
    }

    @Test
    void shouldGetAllActors() {
        // arrange
        var actorId = UUID.randomUUID();
        var actor = ActorMockFactory.buildActorEntity(actorId);
        var actorResponse = ActorMockFactory.buildActorResponseDto(actorId);
        int page = 0, size = 10;

        when(actorRepository.findAllBy(PageRequest.of(page, size))).thenReturn(Flux.just(actor));
        // act
        var response = actorService.getAllActors(page, size);

        // assert
        StepVerifier.create(response)
                .expectNext(actorResponse)
                .verifyComplete();

        verify(actorRepository).findAllBy(PageRequest.of(page, size));
        verify(transactionalOperator).transactional(any(Flux.class));
    }

    @Test
    void shouldGetActorById() {
        // arrange
        var actorId = UUID.randomUUID();
        var actor = ActorMockFactory.buildActorEntity(actorId);
        var actorResponse = ActorMockFactory.buildActorResponseDto(actorId);

        when(actorRepository.findById(actorId)).thenReturn(Mono.just(actor));

        // act
        var response = actorService.getActorById(actorId);

        // assert
        StepVerifier.create(response)
                .expectNext(actorResponse)
                .verifyComplete();

        verify(actorRepository).findById(actorId);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldCreateActor() {
        // arrange
        var actorRequest = ActorMockFactory.buildActorRequestDto();

        var actorId = UUID.randomUUID();
        var actor = ActorMockFactory.buildActorEntity(actorId);

        var actorResponse = ActorMockFactory.buildActorResponseDto(actorId);

        when(actorRepository.save(any(ActorEntity.class))).thenReturn(Mono.just(actor));

        // act
        var response = actorService.createActor(actorRequest);

        // assert
        StepVerifier.create(response)
                .expectNext(actorResponse)
                .verifyComplete();

        verify(actorRepository).save(any(ActorEntity.class));
        verify(transactionalOperator).transactional(any(Mono.class));
    }


    @Test
    void shouldUpdateActor() {
        // arrange
        var actorRequest = ActorMockFactory.buildActorRequestDto();

        var actorId = UUID.randomUUID();
        var actor = ActorMockFactory.buildActorEntity(actorId);

        var actorResponse = ActorMockFactory.buildActorResponseDto(actorId);

        when(actorRepository.findById(actorId)).thenReturn(Mono.just(actor));
        when(actorRepository.save(any(ActorEntity.class))).thenReturn(Mono.just(actor));

        // act
        var response = actorService.updateActor(actorId, actorRequest);

        // assert
        StepVerifier.create(response)
                .expectNext(actorResponse)
                .verifyComplete();

        verify(actorRepository).findById(actorId);
        verify(actorRepository).save(any(ActorEntity.class));
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldDeleteActor() {
        // arrange
        var actorId = UUID.randomUUID();
        var actor = ActorMockFactory.buildActorEntity(actorId);

        when(actorRepository.findById(actorId)).thenReturn(Mono.just(actor));
        when(actorRepository.deleteById(actorId)).thenReturn(Mono.empty());

        // act
        var response = actorService.deleteActor(actorId);

        // assert
        StepVerifier.create(response)
                .verifyComplete();

        verify(actorRepository).findById(actorId);
        verify(actorRepository).deleteById(actorId);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldFailToUpdateActorWhenIdIsInvalid() {
        // arrange
        var actorRequest = ActorMockFactory.buildActorRequestDto();

        var actorId = UUID.randomUUID();

        when(actorRepository.findById(actorId)).thenReturn(Mono.empty());

        // act
        var response = actorService.updateActor(actorId, actorRequest);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException && e.getMessage().equals("Actor not found.")
                )
                .verify();

        verify(actorRepository).findById(actorId);
        verify(transactionalOperator).transactional(any(Mono.class));
        verify(actorRepository, times(0)).save(any(ActorEntity.class));
    }

    @Test
    void shouldFailToUpdateActorWhenTheresAnException() {
        // arrange
        var dbException = new CannotCreateTransactionException("Generic Exception");
        var actorRequest = ActorMockFactory.buildActorRequestDto();

        var actorId = UUID.randomUUID();

        when(actorRepository.findById(actorId)).thenReturn(Mono.error(dbException));

        // act
        var response = actorService.updateActor(actorId, actorRequest);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException && e.getMessage().equals("DB error during update")
                )
                .verify();

        verify(actorRepository).findById(actorId);
        verify(transactionalOperator).transactional(any(Mono.class));
        verify(actorRepository, times(0)).save(any(ActorEntity.class));
    }

    @Test
    void shouldFailToGetAllActorsWhenTheresAnException() {
        // arrange
        int page = 0, size = 10;
        var dbException = new CannotCreateTransactionException("Generic Exception");

        when(actorRepository.findAllBy(PageRequest.of(page, size))).thenReturn(Flux.error(dbException));

        // act
        var response = actorService.getAllActors(page, size);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException && e.getMessage().equals("DB error during read")
                )
                .verify();

        verify(actorRepository).findAllBy(PageRequest.of(page, size));
        verify(transactionalOperator).transactional(any(Flux.class));
    }

    @Test
    void shouldFailToGetActorByIdWhenTheresAnException() {
        // arrange
        var actorId = UUID.randomUUID();
        var dbException = new CannotCreateTransactionException("Generic Exception");

        when(actorRepository.findById(actorId)).thenReturn(Mono.error(dbException));

        // act
        var response = actorService.getActorById(actorId);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException && e.getMessage().equals("DB error during read")
                )
                .verify();

        verify(actorRepository).findById(actorId);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldFailToCreateActorWhenTheresAnException() {
        // arrange
        var actorRequest = ActorMockFactory.buildActorRequestDto();
        var dbException = new CannotCreateTransactionException("Generic Exception");

        when(actorRepository.save(any(ActorEntity.class))).thenReturn(Mono.error(dbException));

        // act
        var response = actorService.createActor(actorRequest);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException && e.getMessage().equals("DB error during save")
                )
                .verify();

        verify(actorRepository).save(any(ActorEntity.class));
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldFailToDeleteActorWhenIdIsInvalid() {
        // arrange
        var actorId = UUID.randomUUID();

        when(actorRepository.findById(actorId)).thenReturn(Mono.empty());

        // act
        var response = actorService.deleteActor(actorId);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException && e.getMessage().equals("Actor not found.")
                )
                .verify();

        verify(actorRepository).findById(actorId);
        verify(transactionalOperator).transactional(any(Mono.class));
        verify(actorRepository, times(0)).deleteById(any(UUID.class));
    }

    @Test
    void shouldFailToDeleteActorWhenTheresAnException() {
        // arrange
        var actorId = UUID.randomUUID();
        var dbException = new CannotCreateTransactionException("Generic Exception");

        when(actorRepository.findById(actorId)).thenReturn(Mono.error(dbException));

        // act
        var response = actorService.deleteActor(actorId);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException && e.getMessage().equals("DB error during delete")
                )
                .verify();

        verify(actorRepository).findById(actorId);
        verify(transactionalOperator).transactional(any(Mono.class));
        verify(actorRepository, times(0)).deleteById(any(UUID.class));
    }

    @Test
    void shouldFailToGetActorByIdWhenIdDoesNotExist() {
        // arrange
        var actorId = UUID.randomUUID();

        when(actorRepository.findById(actorId)).thenReturn(Mono.empty());

        // act
        var response = actorService.getActorById(actorId);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException &&
                                e.getMessage().equals("Actor not found.")
                )
                .verify();

        verify(actorRepository).findById(actorId);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldFailToUpdateActorWhenIdDoesNotExist() {
        // arrange
        var actorRequest = ActorMockFactory.buildActorRequestDto();
        var actorId = UUID.randomUUID();

        when(actorRepository.findById(actorId)).thenReturn(Mono.empty());

        // act
        var response = actorService.updateActor(actorId, actorRequest);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException &&
                                e.getMessage().equals("Actor not found.")
                )
                .verify();

        verify(actorRepository).findById(actorId);
        verify(actorRepository, times(0)).save(any(ActorEntity.class));
    }

    @Test
    void shouldFailToDeleteActorWhenIdDoesNotExist() {
        // arrange
        var actorId = UUID.randomUUID();

        when(actorRepository.findById(actorId)).thenReturn(Mono.empty());

        // act
        var response = actorService.deleteActor(actorId);

        // assert
        StepVerifier.create(response)
                .expectErrorMatches(e ->
                        e instanceof CinemaException &&
                                e.getMessage().equals("Actor not found.")
                )
                .verify();

        verify(actorRepository).findById(actorId);
        verify(actorRepository, times(0)).deleteById(any(UUID.class));
    }
}