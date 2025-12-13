package evote.config.seeder;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import evote.buergerverwaltung.domain.repository.VoterRepository;
import evote.abstimmungsverwaltung.domain.repository.PollRepository;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test to verify that seeders populate the repositories correctly
 */
@SpringBootTest
@ActiveProfiles("test")
public class SeederIntegrationTest {

    @Autowired
    private VoterRepository voterRepository;

    @Autowired
    private PollRepository pollRepository;

    @Test
    public void testVotersAreSeeded() {
        // After application startup, seeders should have created voters
        // We can verify by checking that at least one voter exists
        assertTrue(voterRepository.findByEmail("max.mustermann@example.org").isPresent(),
                "Max Mustermann voter should be seeded");

        assertTrue(voterRepository.findByEmail("anna.schmidt@example.de").isPresent(),
                "Anna Schmidt voter should be seeded");
    }

    @Test
    public void testPollsAreSeeded() {
        // After application startup, seeders should have created polls
        assertTrue(pollRepository.findById("POLL-CK-2026").isPresent(),
                "Campus-Kantinenkonzept poll should be seeded");

        assertTrue(pollRepository.findById("POLL-PK-2026").isPresent(),
                "Parkplatzkonzept poll should be seeded");
    }

    @Test
    public void testVotersAreVerified() {
        // Seeded voters should be verified and ready to vote
        var voter = voterRepository.findByEmail("max.mustermann@example.org");
        assertTrue(voter.isPresent());
        assertTrue(voter.get().isVerified(),
                "Seeded voters should be verified");
    }

    @Test
    public void testPollIsActive() {
        // Main test poll (POLL-CK-2026) should be active
        var poll = pollRepository.findById("POLL-CK-2026");
        assertTrue(poll.isPresent());
        assertTrue(poll.get().isOpen(),
                "POLL-CK-2026 should be open for voting");
    }
}

