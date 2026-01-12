package evote.stimmvergabe.infrastructure.persistence;

import evote.stimmvergabe.domain.model.Vote;
import evote.stimmvergabe.domain.repository.VoteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-Memory-Repository als Infrastruktur-Adapter für die Stimmvergabe.
 */
public class InMemoryVoteRepository implements VoteRepository {

    private final Map<String, Vote> votesById = new ConcurrentHashMap<>();
    private final Map<String, Vote> votesByCorrelationId = new ConcurrentHashMap<>();

    @Override
    public Vote save(Vote vote) {
        if (vote == null) {
            throw new IllegalArgumentException("vote must not be null");
        }

        // Check for already existing vote with same correlationId first
        Vote existing = votesByCorrelationId.get(vote.getCorrelationId());
        if (existing != null) {
            return existing; // gleiche Stimme zurück geben
        }

        // Neue Stimme speichern (update by id is allowed)
        votesById.put(vote.getVoteId(), vote);
        votesByCorrelationId.put(vote.getCorrelationId(), vote);
        return vote;
    }

    /** Anzahl der aktuell gespeicherten Votes – nur für Tests verwendet. */
    public int count() {
        return votesById.size();
    }

    @Override
    public List<Object> findAll() {
        return new ArrayList<>(votesById.values());
    }

    @Override
    public Optional<Vote> findById(String voteId) {
        if (voteId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(votesById.get(voteId));
    }

    @Override
    public List<Vote> findAllByPollId(String pollId) {
        if (pollId == null) {
            return List.of(); // unveränderliche leere Liste
        }

        return votesById.values().stream()
                .filter(vote -> pollId.equals(vote.getPollId()))
                .toList();
    }

    @Override
    public Optional<Vote> findByCorrelationId(String correlationId) {
        if (correlationId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(votesByCorrelationId.get(correlationId));
    }

    /** Nur für Tests praktisch, um den Zustand zurückzusetzen. */
    public void clear() {
        votesById.clear();
        votesByCorrelationId.clear();
    }
}
