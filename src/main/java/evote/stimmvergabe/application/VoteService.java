package evote.stimmvergabe.application;

import evote.stimmvergabe.application.dto.VoteCreateRequest;
import evote.stimmvergabe.domain.model.Vote;
import evote.stimmvergabe.domain.repository.VoteRepository;
import evote.buergerverwaltung.domain.model.Voter;
import evote.buergerverwaltung.domain.repository.VoterRepository;
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
    private final Optional<VoterRepository> voterRepository;
    private final Clock clock;
    private final DomainEventPublisher publisher;

    /**
     * Konstruktor für Dependency Injection
     * VoterRepository ist optional - wenn nicht vorhanden, funktioniert der Service auch ohne Double-Voting-Prevention
     */
    @Autowired
    public VoteService(VoteRepository voteRepository,
                       Optional<VoterRepository> voterRepository,
                       Clock clock,
                       DomainEventPublisher publisher) {
        this.voteRepository = voteRepository;
        this.voterRepository = voterRepository;
        this.clock = clock;
        this.publisher = publisher;
    }

    /**
     * Konstruktor für Tests ohne VoterRepository
     */
    public VoteService(VoteRepository voteRepository,
                       Clock clock,
                       DomainEventPublisher publisher) {
        this(voteRepository, Optional.empty(), clock, publisher);
    }

    /**
     * Konstruktor für Tests mit VoterRepository
     */
    public VoteService(VoteRepository voteRepository,
                       VoterRepository voterRepository,
                       Clock clock,
                       DomainEventPublisher publisher) {
        this(voteRepository, Optional.ofNullable(voterRepository), clock, publisher);
    }

    /**
     * Führt den Geschäftsprozess "Stimme abgeben" aus.
     *
     * Idempotenz & Double-Voting-Prevention:
     * 1. Prüfe auf Basis von correlationId, ob Vote bereits vorhanden ist
     *    (Mehrfache HTTP-Requests werden erkannt und ignoriert)
     * 2. Voter-Aggregate laden und validieren (falls VoterRepository verfügbar)
     * 3. Prüfen: Ist Voter verifiziert?
     * 4. Prüfen: Hat Voter bereits für diese Poll abgestimmt?
     * 5. Vote erstellen und speichern (mit der vom Frontend gesendeten correlationId)
     * 6. Voter.markVoted(pollId) aufrufen
     * 7. Voter speichern (aktualisiert)
     * 8. Domain-Event publizieren
     *
     * @param req VoteCreateRequest mit pollId, optionId, voterId, correlationId
     * @throws IllegalArgumentException wenn Voter nicht existiert
     * @throws IllegalStateException wenn Voter nicht verifiziert oder bereits abgestimmt
     */
    public void create(VoteCreateRequest req) {

        // Idempotenz-Prüfung: Wurde bereits ein Vote mit dieser correlationId abgegeben?
        // Falls ja, ist dies ein Duplicate-Request und wird ignoriert
        Optional<Vote> existingVote = voteRepository.findByCorrelationId(req.correlationId());
        if (existingVote.isPresent()) {
            // Vote bereits vorhanden - idempotent erfolgreich abschließen
            return;
        }

        // Voter-Lookup und Validierung, falls VoterRepository verfügbar
        voterRepository
                // flatMap: Optional<VoterRepository> -> Optional<Voter>
                // Wenn ein VoterRepository vorhanden ist, versuche den Voter anhand der voterId zu laden.
                .flatMap(repo -> repo.findById(req.voterId()))

                // map: Optional<Voter> -> Optional<Voter>
                // Wenn der Voter existiert, markiere ihn als abgestimmt für die gegebene Poll.
                // Speichere den aktualisierten Voter nur, wenn ein Repository vorhanden ist.
                .map(voter -> {
                    voter.markVoted(req.pollId());                           // Business-Regel: Voter als abgestimmt markieren
                    voterRepository.ifPresent(repo -> repo.save(voter));     // Persistiere Änderungen optional
                    return voter;                                           // Rückgabe für Optional-Pipeline
                })

                // orElseThrow: Wenn kein Voter gefunden wurde (Optional.empty), werfe eine Exception.
                .orElseThrow(() -> new IllegalArgumentException("Voter not found: " + req.voterId()));

        // Domain-Objekt erzeugen mit eindeutiger voteId und correlationId vom Frontend
        Vote vote = Vote.of(
                UUID.randomUUID().toString(),  // voteId wird generiert
                req.pollId(),
                req.optionId(),
                req.correlationId(),           // correlationId vom Frontend (eindeutig pro Abstimmungsvorgang)
                clock
        );

        // Persistieren
        voteRepository.save(vote);

        // Domain-Event publizieren
        publisher.publish(vote.getDomainEvent());
    }
}
