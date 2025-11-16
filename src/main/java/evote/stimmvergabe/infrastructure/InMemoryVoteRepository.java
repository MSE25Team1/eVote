package evote.stimmvergabe.infrastructure;

import evote.stimmvergabe.domain.model.Vote;
import evote.stimmvergabe.domain.repository.VoteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryVoteRepository implements VoteRepository {

    private final Map<String, Vote> votesById = new ConcurrentHashMap<>();
    private final Map<String, Vote> votesByCorrelationId = new ConcurrentHashMap<>();

    @Override
    public Vote save(Vote vote) {
        if (vote == null) {
            throw new IllegalArgumentException("vote must not be null");
        }

        // Check for already existing vote with samevote id or CorrelationId first

        Vote existing = votesByCorrelationId.get(vote.getCorrelationId());
        if (existing != null) {
            return existing; // gleiche Stimme zurück geben
        }

        // Neue Stimme speichern
        votesById.put(vote.getVoteId(), vote);

        // Kollisionen bei correlationId würden hier die letzte Überschreiben
        // In einer echten Datenbank-Implementierung (nicht im In-Memory-Mock)
        // würde man prüfen, ob eine Stimme mit derselben correlationId bereits existiert,
        // um doppelte Stimmabgaben zu verhindern, selbst wenn z. B. der Request zweimal gesendet wurde.
        votesByCorrelationId.put(vote.getCorrelationId(), vote);

        return vote;
    }





    /** Anzahl der aktuell gespeicherten Votes – nur für Tests verwendet. */
    public int count() {
        return votesById.size();
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
        List<Vote> result = new ArrayList<>();
        if (pollId == null) {
            return result;
        }

        for (Vote vote : votesById.values()) {
            if (pollId.equals(vote.getPollId())) {
                result.add(vote);
            }
        }
        return result;
    }

    @Override
    public Optional<Vote> findByCorrelationId(String correlationId) {
        if (correlationId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(votesByCorrelationId.get(correlationId));
    }

    /**
     * Nur für Tests praktisch, um den Zustand zurückzusetzen.
     */
    public void clear() {
        votesById.clear();
        votesByCorrelationId.clear();
    }
}
