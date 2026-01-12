package evote.abstimmungsverwaltung.infrastructure.persistence;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import evote.abstimmungsverwaltung.domain.model.Poll;
import evote.abstimmungsverwaltung.domain.repository.PollRepository;

/**
 * In-Memory-Repository als Infrastruktur-Adapter für Abstimmungen.
 */
public class InMemoryPollRepository implements PollRepository {

    private final Map<String, Poll> store = new ConcurrentHashMap<>();

    @Override
    public Poll save(Poll poll) {
        store.put(poll.getPollId(), poll);
        return poll;
    }

    @Override
    public Optional<Poll> findById(String pollId) {
        return Optional.ofNullable(store.get(pollId));
    }

    @Override
    public List<Poll> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Poll> findAllOpenAt(Instant instant) {
        return store.values().stream()
                // Reine Funktion: filtert die Polls basierend auf dem Instant
                .filter(poll -> poll.isOpenAt(instant))
                // Sammelt das Ergebnis unveränderlich
                .toList();
    }

    // optional für Tests
    public void clear() {
        store.clear();
    }
}
