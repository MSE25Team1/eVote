package evote.abstimmungsverwaltung.application;

import evote.abstimmungsverwaltung.application.dto.PollDTO;
import evote.abstimmungsverwaltung.domain.repository.PollRepository;
import evote.buergerverwaltung.domain.model.Voter;
import evote.buergerverwaltung.domain.repository.VoterRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
public class PollService {

    private final PollRepository pollRepository;
    private final VoterRepository voterRepository;
    private final Clock clock;
    private final PollAssembler pollAssembler;

    public PollService(PollRepository pollRepository,
                       VoterRepository voterRepository,
                       Clock clock,
                       PollAssembler pollAssembler) {
        this.pollRepository = pollRepository;
        this.voterRepository = voterRepository;
        this.clock = clock;
        this.pollAssembler = pollAssembler;
    }

    /**
     * Liefert alle aktuell offenen Abstimmungen, an denen der Voter noch teilnehmen kann.
     */
    public List<PollDTO> findActivePollsForVoter(String voterId) {
        Voter voter = voterRepository.findById(voterId)
                .orElseThrow(() -> new IllegalArgumentException("Voter not found: " + voterId));

        Instant now = Instant.now(clock);

        return pollRepository.findAll().stream()
                .filter(poll -> !voter.hasVoted(poll.getPollId()))
                .filter(poll -> poll.isOpenAt(now))
                .map(pollAssembler::toDTO)
                .sorted(Comparator.comparing(PollDTO::endDate))
                .toList();
    }
}
