package evote.abstimmungsverwaltung.domain.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import evote.abstimmungsverwaltung.domain.model.Poll;

public interface PollRepository {

    Poll save(Poll poll);

    Optional<Poll> findById(String pollId);

    /**
     * Liefert alle Abstimmungen, die zum gegebenen Zeitpunkt geöffnet sind.
     */
    List<Poll> findAllOpenAt(Instant instant);
}