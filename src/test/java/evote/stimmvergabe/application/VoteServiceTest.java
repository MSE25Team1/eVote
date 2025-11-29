package evote.stimmvergabe.application;

import evote.stimmvergabe.application.dto.VoteCreateRequest;
import evote.stimmvergabe.domain.model.Vote;
import evote.stimmvergabe.domain.repository.VoteRepository;
import evote.stimmvergabe.events.VoteCastEvent;
import evote.stimmvergabe.infrastructure.InMemoryVoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class VoteServiceTest {

    // Fake Event Publisher zum Testen
    static class FakeEventPublisher implements DomainEventPublisher {
        Object published;
        @Override
        public void publish(Object event) {
            this.published = event;
        }
    }

    @Test
    @DisplayName("create(): Vote wird erstellt, gespeichert und Event wird publiziert")
    void create_shouldCreateStoreAndPublishEvent() {

        // ---------- Arrange ----------
        VoteRepository repo = new InMemoryVoteRepository();
        FakeEventPublisher publisher = new FakeEventPublisher();

        Clock fixedClock = Clock.fixed(
                Instant.parse("2030-01-01T12:00:00Z"),
                ZoneOffset.UTC
        );

        VoteService service = new VoteService(repo, fixedClock, publisher);

        VoteCreateRequest req = new VoteCreateRequest(
                "poll-1",
                "option-5",
                "voter-123"
        );

        // ---------- Act ----------
        service.create(req);

        // ---------- Assert ----------

        // 1) Repository enthält einen Vote
        assertEquals(1, repo.count(), "Es sollte genau 1 Vote gespeichert werden");

        Vote stored = repo.findAll().get(0);

        assertEquals("poll-1", stored.getPollId());
        assertEquals("option-5", stored.getOptionId());
        assertEquals("voter-123", stored.getCorrelationId()); // ihr nutzt voterId als correlationId
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
}
