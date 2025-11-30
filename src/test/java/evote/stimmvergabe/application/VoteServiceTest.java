package evote.stimmvergabe.application;

import evote.stimmvergabe.application.dto.VoteCreateRequest;
import evote.stimmvergabe.domain.model.Vote;
import evote.stimmvergabe.domain.repository.VoteRepository;
import evote.stimmvergabe.events.VoteCastEvent;
import evote.stimmvergabe.infrastructure.persistence.InMemoryVoteRepository;
import evote.buergerverwaltung.domain.model.Voter;
import evote.buergerverwaltung.domain.repository.VoterRepository;
import evote.buergerverwaltung.infrastructure.persistence.InMemoryVoterRepository;
import evote.buergerverwaltung.domain.valueobjects.Name;
import evote.buergerverwaltung.domain.valueobjects.Adresse;
import evote.buergerverwaltung.domain.valueobjects.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class VoteServiceTest {

    // Fake Event Publisher zum Testen
    static class FakeEventPublisher extends DomainEventPublisher {
        Object published;
        @Override
        public void publish(Object event) {
            this.published = event;
        }
    }

    private final Clock fixedClock = Clock.fixed(
            Instant.parse("2030-01-01T12:00:00Z"),
            ZoneOffset.UTC
    );

    /**
     * Helper-Methode: Erstellt einen verifzierten Voter für Tests
     */
    private Voter createVerifiedVoter(VoterRepository voterRepo, String firstName, String lastName) {
        Voter voter = Voter.register(
                new Name(firstName, lastName),
                new Adresse("Musterstraße", "1", "", "12345", "Berlin"),
                new Email("max@example.com"),
                LocalDate.of(1990, 1, 1),
                "Mitte"
        );
        voter.verify();
        voterRepo.save(voter);
        return voter;
    }

    @Test
    @DisplayName("create(): Vote wird erstellt, gespeichert und Event wird publiziert (ohne VoterRepository)")
    void create_shouldCreateStoreAndPublishEvent() {

        // ---------- Arrange ----------
        VoteRepository voteRepo = new InMemoryVoteRepository();
        FakeEventPublisher publisher = new FakeEventPublisher();

        VoteService service = new VoteService(voteRepo, fixedClock, publisher);

        VoteCreateRequest req = new VoteCreateRequest(
                "poll-1",
                "option-5",
                "voter-123",
                "correlation-uuid-001"  // correlationId vom Frontend
        );

        // ---------- Act ----------
        service.create(req);

        // ---------- Assert ----------

        // 1) Repository enthält einen Vote
        assertEquals(1, voteRepo.count(), "Es sollte genau 1 Vote gespeichert werden");

        Vote stored = (Vote) voteRepo.findAll().get(0);

        assertEquals("poll-1", stored.getPollId());
        assertEquals("option-5", stored.getOptionId());
        assertEquals("correlation-uuid-001", stored.getCorrelationId());
        assertEquals(Instant.parse("2030-01-01T12:00:00Z"), stored.getCastAt());

        // 2) Event wurde publiziert
        assertNotNull(publisher.published, "Es sollte ein Event publiziert werden");
        assertTrue(publisher.published instanceof VoteCastEvent);

        VoteCastEvent evt = (VoteCastEvent) publisher.published;

        // 3) Event enthält korrekte Werte
        assertEquals(stored.getVoteId(), evt.voteId());
        assertEquals(stored.getPollId(), evt.pollId());
        assertEquals(stored.getOptionId(), evt.optionId());
        assertEquals(stored.getCastAt(), evt.castAt());
    }

    @Test
    @DisplayName("create() mit VoterRepository: Voter wird mit markVoted() aktualisiert nach erfolgreichem Vote")
    void create_shouldMarkVoterAsVoted() {
        // ---------- Arrange ----------
        VoteRepository voteRepo = new InMemoryVoteRepository();
        VoterRepository voterRepo = new InMemoryVoterRepository();
        FakeEventPublisher publisher = new FakeEventPublisher();

        Voter voter = createVerifiedVoter(voterRepo, "Max", "Mustermann");

        VoteService service = new VoteService(voteRepo, voterRepo, fixedClock, publisher);

        VoteCreateRequest req = new VoteCreateRequest(
                "poll-1",
                "option-5",
                voter.getVoterId(),
                "correlation-uuid-002"  // correlationId vom Frontend
        );

        // ---------- Act ----------
        service.create(req);

        // ---------- Assert ----------
        // Vote sollte gespeichert sein
        assertEquals(1, voteRepo.count());

        // Voter sollte aktualisiert sein und hasVoted(poll-1) sollte true sein
        Voter updatedVoter = voterRepo.findById(voter.getVoterId()).orElse(null);
        assertNotNull(updatedVoter, "Voter sollte existieren");
        assertTrue(updatedVoter.hasVoted("poll-1"), "Voter sollte als abgestimmt markiert sein");
    }

    @Test
    @DisplayName("create() wirft Exception wenn Voter bereits für diese Poll abgestimmt hat (Double-Voting Prevention)")
    void create_throwsException_whenVoterAlreadyVoted() {
        // ---------- Arrange ----------
        VoteRepository voteRepo = new InMemoryVoteRepository();
        VoterRepository voterRepo = new InMemoryVoterRepository();
        FakeEventPublisher publisher = new FakeEventPublisher();

        // Voter erstellen, verifizieren und als "bereits abgestimmt" markieren
        Voter voter = Voter.register(
                new Name("Max", "Mustermann"),
                new Adresse("Musterstraße", "1", "", "12345", "Berlin"),
                new Email("max@example.com"),
                LocalDate.of(1990, 1, 1),
                "Mitte"
        );
        voter.verify();
        voter.markVoted("poll-1");  // Voter hat bereits abgestimmt
        voterRepo.save(voter);

        VoteService service = new VoteService(voteRepo, voterRepo, fixedClock, publisher);

        VoteCreateRequest req = new VoteCreateRequest(
                "poll-1",
                "option-5",
                voter.getVoterId(),
                "correlation-uuid-003"  // correlationId vom Frontend
        );

        // ---------- Act & Assert ----------
        assertThrows(
                IllegalStateException.class,
                () -> service.create(req),
                "Sollte IllegalStateException werfen wenn Voter bereits abgestimmt hat"
        );

        // Vote sollte NICHT gespeichert sein
        assertEquals(0, voteRepo.count(), "Kein Vote sollte gespeichert sein bei Double-Voting");
    }

    @Test
    @DisplayName("create() erlaubt mehrere Votes für verschiedene Polls vom gleichen Voter")
    void create_allowsMultipleVotesForDifferentPolls() {
        // ---------- Arrange ----------
        VoteRepository voteRepo = new InMemoryVoteRepository();
        VoterRepository voterRepo = new InMemoryVoterRepository();
        FakeEventPublisher publisher = new FakeEventPublisher();

        Voter voter = createVerifiedVoter(voterRepo, "Max", "Mustermann");

        VoteService service = new VoteService(voteRepo, voterRepo, fixedClock, publisher);

        // ---------- Act ----------
        // Voter stimmt für poll-1 ab
        VoteCreateRequest req1 = new VoteCreateRequest(
                "poll-1",
                "option-A",
                voter.getVoterId(),
                "correlation-uuid-004"  // unterschiedliche correlationId
        );
        service.create(req1);

        // Voter stimmt für poll-2 ab (verschiedene Poll)
        VoteCreateRequest req2 = new VoteCreateRequest(
                "poll-2",
                "option-B",
                voter.getVoterId(),
                "correlation-uuid-005"  // unterschiedliche correlationId
        );
        service.create(req2);

        // ---------- Assert ----------
        // Beide Votes sollten gespeichert sein
        assertEquals(2, voteRepo.count(), "Es sollten 2 Votes gespeichert sein");

        // Voter sollte beide Polls als abgestimmt haben
        Voter updatedVoter = voterRepo.findById(voter.getVoterId()).orElse(null);
        assertNotNull(updatedVoter);
        assertTrue(updatedVoter.hasVoted("poll-1"), "Voter sollte für poll-1 abgestimmt haben");
        assertTrue(updatedVoter.hasVoted("poll-2"), "Voter sollte für poll-2 abgestimmt haben");
    }

    @Test
    @DisplayName("create() wirft Exception wenn Voter nicht verifiziert ist")
    void create_throwsException_whenVoterNotVerified() {
        // ---------- Arrange ----------
        VoteRepository voteRepo = new InMemoryVoteRepository();
        VoterRepository voterRepo = new InMemoryVoterRepository();
        FakeEventPublisher publisher = new FakeEventPublisher();

        // Voter erstellen OHNE zu verifizieren
        Voter voter = Voter.register(
                new Name("Max", "Mustermann"),
                new Adresse("Musterstraße", "1", "", "12345", "Berlin"),
                new Email("max@example.com"),
                LocalDate.of(1990, 1, 1),
                "Mitte"
        );
        // voter.verify() wird NICHT aufgerufen
        voterRepo.save(voter);

        VoteService service = new VoteService(voteRepo, voterRepo, fixedClock, publisher);

        VoteCreateRequest req = new VoteCreateRequest(
                "poll-1",
                "option-5",
                voter.getVoterId(),
                "correlation-uuid-006"  // correlationId vom Frontend
        );

        // ---------- Act & Assert ----------
        assertThrows(
                IllegalStateException.class,
                () -> service.create(req),
                "Sollte IllegalStateException werfen wenn Voter nicht verifiziert ist"
        );

        // Vote sollte NICHT gespeichert sein
        assertEquals(0, voteRepo.count(), "Kein Vote sollte gespeichert sein wenn Voter unverified ist");
    }

    @Test
    @DisplayName("create() wirft Exception wenn Voter nicht existiert")
    void create_throwsException_whenVoterNotFound() {
        // ---------- Arrange ----------
        VoteRepository voteRepo = new InMemoryVoteRepository();
        VoterRepository voterRepo = new InMemoryVoterRepository();
        FakeEventPublisher publisher = new FakeEventPublisher();

        VoteService service = new VoteService(voteRepo, voterRepo, fixedClock, publisher);

        VoteCreateRequest req = new VoteCreateRequest(
                "poll-1",
                "option-5",
                "unknown-voter-id",  // Voter existiert nicht
                "correlation-uuid-007"  // correlationId vom Frontend
        );

        // ---------- Act & Assert ----------
        assertThrows(
                IllegalArgumentException.class,
                () -> service.create(req),
                "Sollte IllegalArgumentException werfen wenn Voter nicht existiert"
        );

        // Vote sollte NICHT gespeichert sein
        assertEquals(0, voteRepo.count(), "Kein Vote sollte gespeichert sein wenn Voter nicht existiert");
    }

    @Test
    @DisplayName("create() mit gleicher correlationId mehrfach aufgerufen: Idempotenz - nur 1 Vote wird gespeichert")
    void create_idempotent_whenCalledMultipleTimesWithSameCorrelationId() {
        // ---------- Arrange ----------
        VoteRepository voteRepo = new InMemoryVoteRepository();
        VoterRepository voterRepo = new InMemoryVoterRepository();
        FakeEventPublisher publisher = new FakeEventPublisher();

        Voter voter = createVerifiedVoter(voterRepo, "Max", "Mustermann");

        VoteService service = new VoteService(voteRepo, voterRepo, fixedClock, publisher);

        String sameCorrelationId = "correlation-uuid-idempotent";

        // ---------- Act ----------
        // Erster Request
        VoteCreateRequest req1 = new VoteCreateRequest(
                "poll-1",
                "option-A",
                voter.getVoterId(),
                sameCorrelationId  // gleiche correlationId
        );
        service.create(req1);

        int countAfterFirstCall = voteRepo.count();

        // Zweiter Request mit GLEICHER correlationId (z.B. User klickt "Senden" Knopf doppelt)
        VoteCreateRequest req2 = new VoteCreateRequest(
                "poll-1",
                "option-A",
                voter.getVoterId(),
                sameCorrelationId  // GLEICHE correlationId!
        );
        service.create(req2);

        int countAfterSecondCall = voteRepo.count();

        // Dritter Request mit GLEICHER correlationId
        VoteCreateRequest req3 = new VoteCreateRequest(
                "poll-1",
                "option-A",
                voter.getVoterId(),
                sameCorrelationId  // GLEICHE correlationId!
        );
        service.create(req3);

        int countAfterThirdCall = voteRepo.count();

        // ---------- Assert ----------
        // Alle Aufrufe sollten nur 1 Vote generiert haben
        assertEquals(1, countAfterFirstCall, "Nach erstem Request: 1 Vote");
        assertEquals(1, countAfterSecondCall, "Nach zweitem Request mit gleicher correlationId: immer noch 1 Vote");
        assertEquals(1, countAfterThirdCall, "Nach drittem Request mit gleicher correlationId: immer noch 1 Vote");

        // Voter sollte nur einmal als "abgestimmt" markiert sein für diese Poll
        Voter updatedVoter = voterRepo.findById(voter.getVoterId()).orElse(null);
        assertNotNull(updatedVoter);
        assertTrue(updatedVoter.hasVoted("poll-1"), "Voter sollte für poll-1 abgestimmt haben");
    }
}
