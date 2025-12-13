package evote.stimmvergabe.application;

import evote.stimmvergabe.application.dto.VoteCreateRequest;
import evote.stimmvergabe.domain.model.Vote;
import evote.stimmvergabe.domain.repository.VoteRepository;
import evote.stimmvergabe.domain.validator.CompositeVoteValidator;
import evote.buergerverwaltung.domain.model.Voter;
import evote.buergerverwaltung.domain.repository.VoterRepository;
import evote.abstimmungsverwaltung.domain.model.Poll;
import evote.abstimmungsverwaltung.domain.repository.PollRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

/**
 * VoteService – Application Layer
 *
 * Geschäftsprozess: Stimme abgeben.
 * - prüft, ob der Voter bereits für diese Poll abgestimmt hat (Double-Voting-Prevention)
 * - erzeugt ein Vote-Domainobjekt
 * - speichert es im Repository
 * - markiert den Voter als abgestimmt für diese Poll
 * - publiziert das zugehörige Domain-Event
 *
 * DDD-Prinzipien:
 * - Voter und Vote sind separate Aggregates
 * - VoteService koordiniert die Transaktionen zwischen den Aggregates
 * - Business-Regeln (z.B. Double-Voting-Prevention) werden in den Domain-Objekten enforced
 */

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoterRepository voterRepository;
    private final PollRepository pollRepository;
    private final Clock clock;
    private final DomainEventPublisher publisher;
    private final CompositeVoteValidator compositeVoteValidator;

    /**
     * Konstruktor für Dependency Injection
     * VoterRepository und PollRepository sind REQUIRED
     * CompositeVoteValidator wird aus verschiedenen Bounded Contexts komponiert
     */
    @Autowired
    public VoteService(VoteRepository voteRepository,
                       VoterRepository voterRepository,
                       PollRepository pollRepository,
                       Clock clock,
                       DomainEventPublisher publisher,
                       CompositeVoteValidator compositeVoteValidator) {
        this.voteRepository = voteRepository;
        this.voterRepository = voterRepository;
        this.pollRepository = pollRepository;
        this.clock = clock;
        this.publisher = publisher;
        this.compositeVoteValidator = compositeVoteValidator;
    }

    /**
     * Führt den Geschäftsprozess "Stimme abgeben" aus.
     *
     * Ablauf (DDD-konform):
     * 1. Idempotenz-Prüfung: Wurde bereits ein Vote mit dieser correlationId abgegeben?
     * 2. Poll laden (muss existieren)
     * 3. Voter laden (muss existieren)
     * 4. Vote Domain Model erstellen aus VoteCreateRequest
     * 5. CompositeVoteValidator auf Vote, Poll, Voter ausführen:
     *    - PollValidator (Abstimmungsverwaltung): Poll muss zeitlich offen sein
     *    - VoterValidator (Bürgerverwaltung): Voter muss verifiziert sein, darf nicht doppelt abstimmen
     *    - VoteOptionValidator (Stimmvergabe): Option muss in Poll existieren
     * 6. Nur bei erfolgreicher Validierung:
     *    - Voter markieren und speichern
     *    - Vote persistieren
     *    - Domain-Event publizieren
     *
     * DDD-Prinzipien:
     * - Validiere Domain Models, nicht DTOs
     * - Validierung kann für Create, Update und andere Operationen wiederverwendet werden
     * - Jeder Bounded Context validiert seine eigenen Invarianten
     * - Anti-Corruption Layer (Adapters) respektiert Kontext-Grenzen
     *
     * @param req VoteCreateRequest mit pollId, optionId, voterId, correlationId
     * @throws IllegalArgumentException wenn Poll oder Voter nicht existiert, oder Option ungültig ist
     * @throws IllegalStateException wenn Validierungsbedingung verletzt wird
     */
    public void create(VoteCreateRequest req) {

        // 1. Idempotenz-Prüfung: Wurde bereits ein Vote mit dieser correlationId abgegeben?
        Optional<Vote> existingVote = voteRepository.findByCorrelationId(req.correlationId());
        if (existingVote.isPresent()) {
            return;
        }

        // 2. Poll laden (REQUIRED)
        Poll poll = pollRepository.findById(req.pollId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Poll not found: " + req.pollId()
                ));

        // 3. Voter laden (REQUIRED - Voter muss immer existieren)
        Voter voter = voterRepository.findById(req.voterId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Voter not found: " + req.voterId()
                ));

        // 4. Vote Domain Model erstellen
        // Die compact constructor des Vote Records validiert bereits grundlegende Invarianten
        Vote vote = Vote.of(
                UUID.randomUUID().toString(),
                req.pollId(),
                req.optionId(),
                req.correlationId(),
                clock
        );

        // 5. Cross-Context Validierung auf dem Vote Domain Model
        // CompositeVoteValidator kombiniert Validatoren aus verschiedenen Bounded Contexts
        // durch Anti-Corruption Layer Adapter
        // Diese Validierung kann für Create, Update und andere Operationen wiederverwendet werden
        compositeVoteValidator.validate(vote, poll, voter);

        // 6. Bei erfolgreicher Validierung: Persistieren

        // Voter markieren und speichern
        voter.markVoted(req.pollId());
        voterRepository.save(voter);

        // Vote persistieren
        voteRepository.save(vote);

        // Domain-Event publizieren
        publisher.publish(vote.getDomainEvent());
    }
}
