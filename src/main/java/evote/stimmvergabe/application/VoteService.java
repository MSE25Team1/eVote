package evote.stimmvergabe.application;

import evote.stimmvergabe.application.dto.VoteCreateRequest;
import evote.stimmvergabe.domain.model.Vote;
import evote.stimmvergabe.domain.repository.VoteRepository;
import org.springframework.stereotype.Service;


import java.time.Clock;
import java.util.UUID;

/**
 * VoteService – Application Layer
 *
 * Geschäftsprozess: Stimme abgeben.
 * - erzeugt ein Vote-Domainobjekt
 * - speichert es im Repository
 * - publiziert das zugehörige Domain-Event
 */

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final Clock clock;
    private final DomainEventPublisher publisher;

    public VoteService(VoteRepository voteRepository,
                       Clock clock,
                       DomainEventPublisher publisher) {
        this.voteRepository = voteRepository;
        this.clock = clock;
        this.publisher = publisher;
    }

    // public VoteService(VoteRepository repo, Clock fixedClock, evote.stimmvergabe.application.VoteServiceTest.FakeEventPublisher publisher) {
    //}

    /**
     * Führt den Geschäftsprozess "Stimme abgeben" aus.
     */
    public void create(VoteCreateRequest req) {

        // Domain-Objekt erzeugen
        Vote vote = Vote.of(
                UUID.randomUUID().toString(), // voteId wird generiert
                req.pollId(),
                req.optionId(),
                req.voterId(),      // bei euch = correlationId
                clock
        );

        // Persistieren
        voteRepository.save(vote);

        // Domain-Event publizieren
        publisher.publish(vote.getDomainEvent());
    }
}
