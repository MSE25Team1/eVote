package evote.abstimmungsverwaltung.infrastructure;

import evote.abstimmungsverwaltung.infrastructure.persistence.InMemoryPollRepository;
import evote.abstimmungsverwaltung.domain.model.Poll;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryPollRepositoryTest {

    private final InMemoryPollRepository repo = new InMemoryPollRepository();
    private Clock fixedClock;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        // deterministische Zeitbasis für isOpen()
        now = LocalDateTime.of(2030, 1, 1, 12, 0);
        fixedClock = Clock.fixed(
                now.atZone(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault()
        );
    }





    @Test
    void saveAndFindById_shouldReturnSamePoll() {
        /* Poll mit fixer Clock wie oben erzeugen */
        Poll poll = new Poll (
                "poll-1",
                "Testwahl",
                List.of("Option-A", "Option-B"),
                now.minusHours(1),
                now.plusHours(1),
                100,
                fixedClock
        );
        repo.save(poll);

        Optional<Poll> loaded = repo.findById(poll.getPollId());
        assertTrue(loaded.isPresent());
        assertEquals(poll.getPollId(), loaded.get().getPollId());
    }

    @Test
    void findAllOpenAt_shouldReturnOnlyPollsOpenAtGivenTime() {

        LocalDateTime t0time = LocalDateTime.of(2030, 1, 1, 12, 0);
        Instant t0 = t0time
                .atZone(ZoneId.systemDefault())
                .toInstant();

        /* Start vor t0, Ende nach t0 */;
        Poll openPoll = new Poll (
                "poll-1",
                "Testwahl",
                List.of("Ja", "Nein"),
                t0time.minusHours(1),
                t0time.plusHours(1),
                100,
                fixedClock
        );
                /* Start und Ende vor t0 */;
        Poll closedPoll = new Poll (
                "poll-50",
                "Testwahl",
                List.of("Ja", "Nein"),
                t0time.minusHours(2),
                t0time.minusHours(1),
                100,
                fixedClock
        );

        repo.save(openPoll);
        repo.save(closedPoll);

        List<Poll> result = repo.findAllOpenAt(t0);

        assertEquals(1, result.size());
        assertEquals(openPoll.getPollId(), result.get(0).getPollId());
    }
}