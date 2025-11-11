package com.mse.eVote.stimmvergabe.infrastructure;

import com.mse.eVote.stimmvergabe.domain.model.Vote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryVoteRepositoryTest {

    private InMemoryVoteRepository repo;
    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        repo = new InMemoryVoteRepository();

        // Fixe Zeit, damit Tests stabil sind
        LocalDateTime now = LocalDateTime.of(2030, 1, 1, 12, 0);
        fixedClock = Clock.fixed(
                now.atZone(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault()
        );
    }

    // Hilfsmethode: passt genau zu deinem Vote-Konstruktor
    private Vote createVote(String voteId, String pollId, String optionId, String correlationId) {
        return Vote.of(
                voteId,
                pollId,
                optionId,
                correlationId,
                fixedClock
        );
    }

    @Test
    @DisplayName("save + findById: gespeicherte Stimme wird korrekt gefunden")
    void saveAndFindById_shouldReturnSameVote() {
        Vote vote = createVote("vote-1", "poll-1", "Option-A", "corr-1");

        repo.save(vote);

        Optional<Vote> loaded = repo.findById("vote-1");
        assertTrue(loaded.isPresent());
        assertEquals("vote-1", loaded.get().getVoteId());
        assertEquals("poll-1", loaded.get().getPollId());
        assertEquals("Option-A", loaded.get().getOptionId());
    }

    @Test
    @DisplayName("findById: unbekannte ID → Optional.empty()")
    void findById_unknownId_shouldReturnEmpty() {
        Optional<Vote> loaded = repo.findById("does-not-exist");
        assertTrue(loaded.isEmpty());
    }

    @Test
    @DisplayName("findAllByPollId: nur Stimmen der jeweiligen Poll werden zurückgegeben")
    void findAllByPollId_shouldReturnOnlyVotesOfThatPoll() {
        Vote vote1 = createVote("vote-1", "poll-1", "Option-A", "corr-1");
        Vote vote2 = createVote("vote-2", "poll-1", "Option-B", "corr-2");
        Vote vote3 = createVote("vote-3", "poll-2", "Option-A", "corr-3");

        repo.save(vote1);
        repo.save(vote2);
        repo.save(vote3);

        List<Vote> poll1Votes = repo.findAllByPollId("poll-1");
        assertEquals(2, poll1Votes.size());
        assertTrue(poll1Votes.stream().anyMatch(v -> v.getVoteId().equals("vote-1")));
        assertTrue(poll1Votes.stream().anyMatch(v -> v.getVoteId().equals("vote-2")));

        List<Vote> poll2Votes = repo.findAllByPollId("poll-2");
        assertEquals(1, poll2Votes.size());
        assertEquals("vote-3", poll2Votes.get(0).getVoteId());
    }

    @Test
    @DisplayName("findByCorrelationId: vorhandene correlationId → Vote wird gefunden")
    void findByCorrelationId_shouldReturnVote_whenExisting() {
        Vote vote = createVote("vote-1", "poll-1", "Option-A", "corr-xyz");

        repo.save(vote);

        Optional<Vote> loaded = repo.findByCorrelationId("corr-xyz");
        assertTrue(loaded.isPresent());
        assertEquals("vote-1", loaded.get().getVoteId());
    }

    @Test
    @DisplayName("findByCorrelationId: unbekannte correlationId → Optional.empty()")
    void findByCorrelationId_shouldReturnEmpty_whenNotExisting() {
        Optional<Vote> loaded = repo.findByCorrelationId("does-not-exist");
        assertTrue(loaded.isEmpty());
    }

    @Test
    @DisplayName("save: gleiche correlationId → Idempotenz, nur eine Vote im Store")
    void save_shouldBeIdempotentForSameCorrelationId() {
        Vote first = createVote("vote-1", "poll-1", "Option-A", "corr-123");
        Vote second = createVote("vote-2", "poll-1", "Option-B", "corr-123"); // gleiche correlationId!

        Vote saved1 = repo.save(first);
        Vote saved2 = repo.save(second);

        // Idempotenz: zweiter Save mit gleicher correlationId liefert denselben Vote zurück
        assertEquals(saved1.getVoteId(), saved2.getVoteId());
        assertEquals(1, repo.count()); // nur eine Vote im Store

        // Repository liefert bei Suche nach Id und CorrelationId dieselbe Stimme
        Optional<Vote> byId = repo.findById("vote-1");
        Optional<Vote> byCorr = repo.findByCorrelationId("corr-123");

        assertTrue(byId.isPresent());
        assertTrue(byCorr.isPresent());
        assertEquals(byId.get().getVoteId(), byCorr.get().getVoteId());
    }

    @Test
    @DisplayName("clear() entfernt alle Stimmen aus dem Repository")
    void clear_shouldRemoveAllVotes() {
        repo.save(createVote("v1", "poll-1", "Option-A", "corr-1"));
        repo.save(createVote("v2", "poll-1", "Option-B", "corr-2"));

        assertEquals(2, repo.count());

        repo.clear();

        assertEquals(0, repo.count());
        assertTrue(repo.findAllByPollId("poll-1").isEmpty());
    }

    @Test
    @DisplayName("findById: null → Optional.empty() (oder Exception, je nach Implementierung)")
    void findById_null_shouldHandleGracefully() {
        // Variante A: falls du Optional.empty() zurückgibst
        Optional<Vote> result = repo.findById(null);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findByCorrelationId: null → Optional.empty() (oder Exception)")
    void findByCorrelationId_null_shouldHandleGracefully() {
        // Variante A: Optional.empty()
        Optional<Vote> result = repo.findByCorrelationId(null);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("save: gleiche voteId → bestehende Stimme wird aktualisiert, kein neuer Eintrag")
    void save_sameVoteId_shouldUpdateExistingVote() {
        // ursprüngliche Stimme
        Vote original = createVote("vote-1", "poll-1", "Option-A", "corr-1");
        repo.save(original);

        // gleiche ID, andere Option (Update)
        Vote updated = createVote("vote-1", "poll-1", "Option-B", "corr-2");

        Vote saved = repo.save(updated);

        // Es existiert genau eine Stimme mit dieser ID
        Optional<Vote> loaded = repo.findById("vote-1");
        assertTrue(loaded.isPresent());
        assertEquals("Option-B", loaded.get().getOptionId());

        // count darf NICHT steigen
        assertEquals(1, repo.count());
    }

}
