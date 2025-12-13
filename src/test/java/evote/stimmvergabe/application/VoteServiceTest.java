package evote.stimmvergabe.application;

import evote.stimmvergabe.application.dto.VoteCreateRequest;
import evote.stimmvergabe.domain.model.Vote;
import evote.stimmvergabe.domain.repository.VoteRepository;
import evote.stimmvergabe.domain.validator.*;
import evote.stimmvergabe.domain.validator.adapter.PollValidatorAdapter;
import evote.stimmvergabe.domain.validator.adapter.VoterValidatorAdapter;
import evote.stimmvergabe.events.VoteCastEvent;
import evote.stimmvergabe.infrastructure.persistence.InMemoryVoteRepository;
import evote.buergerverwaltung.domain.model.Voter;
import evote.buergerverwaltung.domain.repository.VoterRepository;
import evote.buergerverwaltung.domain.validator.VoterValidator;
import evote.buergerverwaltung.infrastructure.persistence.InMemoryVoterRepository;
import evote.buergerverwaltung.domain.valueobjects.Name;
import evote.buergerverwaltung.domain.valueobjects.Adresse;
import evote.buergerverwaltung.domain.valueobjects.Email;
import evote.abstimmungsverwaltung.domain.model.Poll;
import evote.abstimmungsverwaltung.domain.repository.PollRepository;
import evote.abstimmungsverwaltung.domain.validator.PollValidator;
import evote.abstimmungsverwaltung.infrastructure.persistence.InMemoryPollRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

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

    /**
     * Helper-Methode: Erstellt eine offene Poll für Tests
     */
    private Poll createOpenPoll(PollRepository pollRepo, String pollId, String... options) {
        Poll poll = new Poll(
                pollId,
                "Test Poll",
                Arrays.asList(options),
                LocalDateTime.parse("2029-12-31T00:00:00"),
                LocalDateTime.parse("2030-12-31T00:00:00"),
                10,
                fixedClock
        );
        pollRepo.save(poll);
        return poll;
    }

    /**
     * Helper-Methode: Erstellt einen CompositeVoteValidator für Tests
     * Demonstriert die Anti-Corruption Layer Adapters zwischen Bounded Contexts
     */
    private CompositeVoteValidator createCompositeValidator() {
        PollValidator pollValidator = new PollValidator();
        VoterValidator voterValidator = new VoterValidator();

        return new PollValidatorAdapter(pollValidator, fixedClock)
                .and(new VoterValidatorAdapter(voterValidator))
                .and(new VoteOptionValidator());
    }

    @Test
    @DisplayName("create(): Vote wird erstellt, gespeichert und Event wird publiziert (Cross-Context Validation)")
    void create_shouldCreateStoreAndPublishEvent() {

        // ---------- Arrange ----------
        VoteRepository voteRepo = new InMemoryVoteRepository();
        VoterRepository voterRepo = new InMemoryVoterRepository();
        PollRepository pollRepo = new InMemoryPollRepository();
        FakeEventPublisher publisher = new FakeEventPublisher();

        // Create composite validator with cross-context adapters
        PollValidator pollValidator = new PollValidator();
        VoterValidator voterValidator = new VoterValidator();
        CompositeVoteValidator validator = new PollValidatorAdapter(pollValidator, fixedClock)
                .and(new VoterValidatorAdapter(voterValidator))
                .and(new VoteOptionValidator());

        Voter voter = createVerifiedVoter(voterRepo, "Max", "Mustermann");
        createOpenPoll(pollRepo, "poll-1", "option-5");
        VoteService service = new VoteService(voteRepo, voterRepo, pollRepo, fixedClock, publisher, validator);

        VoteCreateRequest req = new VoteCreateRequest(
                "poll-1",
                "option-5",
                voter.getVoterId(),
                "correlation-uuid-001"
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
        assertInstanceOf(VoteCastEvent.class, publisher.published);

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
        PollRepository pollRepo = new InMemoryPollRepository();
        FakeEventPublisher publisher = new FakeEventPublisher();
        CompositeVoteValidator validator = createCompositeValidator();

        Voter voter = createVerifiedVoter(voterRepo, "Max", "Mustermann");
        createOpenPoll(pollRepo, "poll-1", "option-5");

        VoteService service = new VoteService(voteRepo, voterRepo, pollRepo, fixedClock, publisher, validator);

        VoteCreateRequest req = new VoteCreateRequest(
                "poll-1",
                "option-5",
                voter.getVoterId(),
                "correlation-uuid-002"
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
        PollRepository pollRepo = new InMemoryPollRepository();
        FakeEventPublisher publisher = new FakeEventPublisher();
        CompositeVoteValidator validator = createCompositeValidator();

        // ...existing test setup...
        Voter voter = Voter.register(
                new Name("Max", "Mustermann"),
                new Adresse("Musterstraße", "1", "", "12345", "Berlin"),
                new Email("max@example.com"),
                LocalDate.of(1990, 1, 1),
                "Mitte"
        );
        voter.verify();
        voter.markVoted("poll-1");
        voterRepo.save(voter);

        createOpenPoll(pollRepo, "poll-1", "option-5");
        VoteService service = new VoteService(voteRepo, voterRepo, pollRepo, fixedClock, publisher, validator);

        VoteCreateRequest req = new VoteCreateRequest(
                "poll-1",
                "option-5",
                voter.getVoterId(),
                "correlation-uuid-003"
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
        PollRepository pollRepo = new InMemoryPollRepository();
        FakeEventPublisher publisher = new FakeEventPublisher();
        CompositeVoteValidator validator = createCompositeValidator();

        Voter voter = createVerifiedVoter(voterRepo, "Max", "Mustermann");
        createOpenPoll(pollRepo, "poll-1", "option-A", "option-B");
        createOpenPoll(pollRepo, "poll-2", "option-A", "option-B");

        VoteService service = new VoteService(voteRepo, voterRepo, pollRepo, fixedClock, publisher, validator);

        // ---------- Act ----------
        // Voter stimmt für poll-1 ab
        VoteCreateRequest req1 = new VoteCreateRequest(
                "poll-1",
                "option-A",
                voter.getVoterId(),
                "correlation-uuid-004"
        );
        service.create(req1);

        // Voter stimmt für poll-2 ab (verschiedene Poll)
        VoteCreateRequest req2 = new VoteCreateRequest(
                "poll-2",
                "option-B",
                voter.getVoterId(),
                "correlation-uuid-005"
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
        PollRepository pollRepo = new InMemoryPollRepository();
        FakeEventPublisher publisher = new FakeEventPublisher();
        CompositeVoteValidator validator = createCompositeValidator();

        // Voter erstellen OHNE zu verifizieren
        Voter voter = Voter.register(
                new Name("Max", "Mustermann"),
                new Adresse("Musterstraße", "1", "", "12345", "Berlin"),
                new Email("max@example.com"),
                LocalDate.of(1990, 1, 1),
                "Mitte"
        );
        voterRepo.save(voter);

        createOpenPoll(pollRepo, "poll-1", "option-5");
        VoteService service = new VoteService(voteRepo, voterRepo, pollRepo, fixedClock, publisher, validator);

        VoteCreateRequest req = new VoteCreateRequest(
                "poll-1",
                "option-5",
                voter.getVoterId(),
                "correlation-uuid-006"
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
        PollRepository pollRepo = new InMemoryPollRepository();
        FakeEventPublisher publisher = new FakeEventPublisher();
        CompositeVoteValidator validator = createCompositeValidator();

        createOpenPoll(pollRepo, "poll-1", "option-5");
        VoteService service = new VoteService(voteRepo, voterRepo, pollRepo, fixedClock, publisher, validator);

        VoteCreateRequest req = new VoteCreateRequest(
                "poll-1",
                "option-5",
                "unknown-voter-id",
                "correlation-uuid-007"
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
        PollRepository pollRepo = new InMemoryPollRepository();
        FakeEventPublisher publisher = new FakeEventPublisher();
        CompositeVoteValidator validator = createCompositeValidator();

        Voter voter = createVerifiedVoter(voterRepo, "Max", "Mustermann");
        createOpenPoll(pollRepo, "poll-1", "option-A");

        VoteService service = new VoteService(voteRepo, voterRepo, pollRepo, fixedClock, publisher, validator);

        String sameCorrelationId = "correlation-uuid-idempotent";

        // ---------- Act ----------
        // Erster Request
        VoteCreateRequest req1 = new VoteCreateRequest(
                "poll-1",
                "option-A",
                voter.getVoterId(),
                sameCorrelationId
        );
        service.create(req1);

        int countAfterFirstCall = voteRepo.count();

        // Zweiter Request mit GLEICHER correlationId
        VoteCreateRequest req2 = new VoteCreateRequest(
                "poll-1",
                "option-A",
                voter.getVoterId(),
                sameCorrelationId
        );
        service.create(req2);

        int countAfterSecondCall = voteRepo.count();

        // Dritter Request mit GLEICHER correlationId
        VoteCreateRequest req3 = new VoteCreateRequest(
                "poll-1",
                "option-A",
                voter.getVoterId(),
                sameCorrelationId
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
