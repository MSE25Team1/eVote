package evote.abstimmungsverwaltung.application;

import evote.abstimmungsverwaltung.application.dto.PollDTO;
import evote.abstimmungsverwaltung.domain.model.Poll;
import evote.abstimmungsverwaltung.infrastructure.persistence.InMemoryPollRepository;
import evote.buergerverwaltung.domain.model.Voter;
import evote.buergerverwaltung.domain.repository.VoterRepository;
import evote.buergerverwaltung.domain.valueobjects.Adresse;
import evote.buergerverwaltung.domain.valueobjects.Email;
import evote.buergerverwaltung.domain.valueobjects.Name;
import evote.buergerverwaltung.infrastructure.persistence.InMemoryVoterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PollServiceTest {

    private InMemoryPollRepository pollRepository;
    private VoterRepository voterRepository;
    private Clock fixedClock;
    private LocalDateTime now;
    private PollService pollService;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.of(2035, 5, 10, 12, 0);
        fixedClock = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        pollRepository = new InMemoryPollRepository();
        voterRepository = new InMemoryVoterRepository();
        pollService = new PollService(pollRepository, voterRepository, fixedClock, new PollAssembler());
    }

    @Test
    void findActivePollsForVoter_filtersParticipationAndTimeAndSortsByEndDate() {
        Voter voter = Voter.register(
                new Name("Sandra", "Schmidt"),
                new Adresse("Hauptstrasse", "1", "", "12345", "Berlin"),
                new Email("sandra@example.com"),
                LocalDate.of(2000, 1, 1),
                "Berlin"
        );
        voter.verify();
        voterRepository.save(voter);

        Poll closesSoon = createPoll("poll-soon", now.minusHours(1), now.plusHours(2));
        Poll closesLater = createPoll("poll-later", now.minusDays(1), now.plusDays(2));
        Poll alreadyVoted = createPoll("poll-voted", now.minusDays(1), now.plusHours(5));
        Poll notStarted = createPoll("poll-future", now.plusDays(1), now.plusDays(2));
        Poll alreadyClosed = createPoll("poll-closed", now.minusDays(3), now.minusDays(1));

        voter.markVoted(alreadyVoted.getPollId());
        voterRepository.save(voter);

        pollRepository.save(closesSoon);
        pollRepository.save(closesLater);
        pollRepository.save(alreadyVoted);
        pollRepository.save(notStarted);
        pollRepository.save(alreadyClosed);

        List<PollDTO> result = pollService.findActivePollsForVoter(voter.getVoterId());

        assertEquals(List.of("poll-soon", "poll-later"), result.stream().map(PollDTO::pollId).toList());
        assertTrue(result.get(0).endDate().isBefore(result.get(1).endDate()));
    }

    @Test
    void findActivePollsForVoter_throwsForUnknownVoter() {
        assertThrows(IllegalArgumentException.class, () -> pollService.findActivePollsForVoter("missing"));
    }

    private Poll createPoll(String pollId, LocalDateTime start, LocalDateTime end) {
        return new Poll(
                pollId,
                "Abstimmung " + pollId,
                List.of("Ja", "Nein"),
                start,
                end,
                100,
                fixedClock
        );
    }
}
