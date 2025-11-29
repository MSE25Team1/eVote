package evote.abstimmungsverwaltung.infrastructure.persistence;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import evote.abstimmungsverwaltung.domain.model.Poll;
import evote.abstimmungsverwaltung.domain.repository.PollRepository;

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
    public List<Poll> findAllOpenAt(Instant instant) {
        List<Poll> result = new ArrayList<>();
        for (Poll poll : store.values()) {
            if (poll.isOpenAt(instant)) {
                result.add(poll);
            }
        }
        return result;
    }

    // optional für Tests
    public void clear() {
        store.clear();
    }
}

