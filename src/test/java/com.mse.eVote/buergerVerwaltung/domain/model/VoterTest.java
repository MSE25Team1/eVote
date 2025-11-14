package com.mse.eVote.buergerVerwaltung.domain.model;

import com.mse.eVote.buergerVerwaltung.domain.valueobjects.Adresse;
import com.mse.eVote.buergerVerwaltung.domain.valueobjects.Name;
import com.mse.eVote.buergerVerwaltung.domain.valueobjects.Email;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Voter - Aggregate Root Tests")
class VoterTest {

    private static final Name VALID_NAME = new Name("Max", "Mustermann");
    private static final Adresse VALID_ADRESSE = new Adresse("Musterstraße", "12a", "", "12345", "Musterstadt");
    private static final Email VALID_EMAIL = new Email("max@mustermann.de");
    private static final String VALID_WAHLKREIS = "Kreis-12345";

    // ============ Factory Method & Creation Tests ============

    @Test
    @DisplayName("Voter registration should create unverified voter")
    void voterRegistration_shouldCreateUnverifiedVoter() {
        Voter voter = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);

        assertNotNull(voter.getVoterId());
        assertEquals(VALID_NAME, voter.getName());
        assertEquals(VALID_ADRESSE, voter.getAdresse());
        assertEquals(VALID_EMAIL, voter.getEmail());
        assertFalse(voter.isVerified());
        assertNull(voter.getRegisteredAt());
    }

    @Test
    @DisplayName("New voter should have empty voted polls set")
    void newVoter_shouldHaveNoVotedPolls() {
        Voter voter = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);

        assertTrue(voter.getVotedPollIds().isEmpty());
        assertFalse(voter.hasVoted("poll-1"));
    }

    // ============ Verification Tests ============

    @Test
    @DisplayName("Voter verification should set isVerified to true")
    void voterVerification_shouldSetVerifiedFlag() {
        Voter voter = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);
        voter.verify();

        assertTrue(voter.isVerified());
        assertNotNull(voter.getRegisteredAt());
    }

    @Test
    @DisplayName("Voter verification should produce VoterRegisteredEvent")
    void voterVerification_shouldProduceEvent() {
        Voter voter = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);
        voter.verify();

        assertNotNull(voter.getPendingEvent());
        assertEquals(voter.getVoterId(), voter.getPendingEvent().getVoterId());
        assertEquals("Max", voter.getPendingEvent().getFirstName());
        assertEquals("Mustermann", voter.getPendingEvent().getLastName());
    }

    @Test
    @DisplayName("Double verification should throw exception")
    void doubleVerification_shouldThrowException() {
        Voter voter = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);
        voter.verify();

        assertThrows(IllegalStateException.class, voter::verify);
    }

    // ============ Vote Marking Tests (Double-Vote Prevention) ============

    @Test
    @DisplayName("Unverified voter cannot vote")
    void unverifiedVoter_cannotVote() {
        Voter voter = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);

        assertThrows(IllegalStateException.class, () -> voter.markVoted("poll-1"));
    }

    @Test
    @DisplayName("Verified voter can mark vote on poll")
    void verifiedVoter_canMarkVote() {
        Voter voter = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);
        voter.verify();

        assertDoesNotThrow(() -> voter.markVoted("poll-1"));
        assertTrue(voter.hasVoted("poll-1"));
    }

    @Test
    @DisplayName("Voter cannot vote twice on same poll (double-voting prevention)")
    void voter_cannotVoteTwiceOnSamePoll() {
        Voter voter = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);
        voter.verify();
        voter.markVoted("poll-1");

        assertThrows(IllegalStateException.class, () -> voter.markVoted("poll-1"));
    }

    @Test
    @DisplayName("Voter can vote on different polls")
    void voter_canVoteOnMultiplePolls() {
        Voter voter = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);
        voter.verify();

        assertDoesNotThrow(() -> {
            voter.markVoted("poll-1");
            voter.markVoted("poll-2");
            voter.markVoted("poll-3");
        });

        assertTrue(voter.hasVoted("poll-1"));
        assertTrue(voter.hasVoted("poll-2"));
        assertTrue(voter.hasVoted("poll-3"));
        assertEquals(3, voter.getVotedPollIds().size());
    }

    @Test
    @DisplayName("Voter can check poll participation independently")
    void voter_canCheckPollParticipation() {
        Voter voter = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);
        voter.verify();
        voter.markVoted("poll-1");

        assertTrue(voter.hasVoted("poll-1"));
        assertFalse(voter.hasVoted("poll-2"));
        assertFalse(voter.hasVoted("non-existent-poll"));
    }

    // ============ Input Validation Tests ============

    @Test
    @DisplayName("Null name should throw exception")
    void nullName_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
            () -> Voter.register(null, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS));
    }

    @Test
    @DisplayName("Null adresse should throw exception")
    void nullAdresse_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
            () -> Voter.register(VALID_NAME, null, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "test@", "@domain.de", "", " "})
    @DisplayName("Invalid email formats should throw exception")
    void invalidEmail_shouldThrowException(String invalidEmail) {
        assertThrows(IllegalArgumentException.class,
            () -> Voter.register(VALID_NAME, VALID_ADRESSE, new Email(invalidEmail), java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS));
    }

    @Test
    @DisplayName("Null email should throw exception")
    void nullEmail_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
            () -> Voter.register(VALID_NAME, VALID_ADRESSE, new Email(null), java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS));
    }

    // ============ Aggregate Identity Tests ============

    @Test
    // if (this == o) return true;
    @DisplayName("Voter equals itself should return this == o) ")
    void voter_equalsItself_shouldReturnTrue() {
        Voter voter = Voter.register(
                VALID_NAME,
                VALID_ADRESSE,
                VALID_EMAIL,
                java.time.LocalDate.of(1990, 1, 1),
                VALID_WAHLKREIS
        );
        assertTrue(voter.equals(voter));
    }

    @Test
    @DisplayName("Voter should not equal null")
    void voter_equalsNULL_shouldBeFalse() {
        Voter voter = Voter.register(
                VALID_NAME,
                VALID_ADRESSE,
                VALID_EMAIL,
                java.time.LocalDate.of(1990, 1, 1),
                VALID_WAHLKREIS
        );
        assertNotEquals(voter, null);
        assertFalse(voter.equals(null));
    }

    @Test
    @DisplayName("Voter should not equal non voter")
    void voter_shouldNotEqualString() {
        Voter voter1 = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);
        String voterId = voter1.getVoterId();

        assertNotEquals(voter1, "not a voter");
        assertFalse(voter1.equals("not a voter"));
    }

    @Test
    @DisplayName("Two voters with same voterId should be equal")
    void identicalVoterIds_shouldBeEqual() {
        Voter voter1 = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);
        String voterId = voter1.getVoterId();

        // Simulate reconstruction from database
        Voter voter2 = Voter.reconstruct(
            voterId, VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1),
            VALID_WAHLKREIS, false, null, java.util.Set.of()
        );

        assertEquals(voter1, voter2);
        assertEquals(voter1.hashCode(), voter2.hashCode());
    }

    @Test
    @DisplayName("Two voters with different voteIds should not be equal")
    void differentVoterIds_shouldNotBeEqual() {
        Voter voter1 = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);
        Voter voter2 = Voter.register(VALID_NAME, VALID_ADRESSE, new Email("other@mail.de"), java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);

        assertNotEquals(voter1, voter2);
    }

    @Test
    @DisplayName("Event should be clearable after publishing")
    void pendingEvent_shouldBeClearable() {
        Voter voter = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);
        voter.verify();
        assertNotNull(voter.getPendingEvent());

        voter.clearPendingEvent();
        assertNull(voter.getPendingEvent());
    }

    // ============ Reconstruction from Persistence Tests ============

    @Test
    @DisplayName("Reconstruct verified voter with voted polls")
    void reconstructVerifiedVoter_shouldPreserveState() {
        Voter original = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);
        original.verify();
        original.markVoted("poll-1");
        original.markVoted("poll-2");

        // Simulate reconstruction from persistence
        java.util.Set<String> votedPolls = original.getVotedPollIds();
        Voter reconstructed = Voter.reconstruct(
            original.getVoterId(),
            original.getName(),
            original.getAdresse(),
            original.getEmail(),
            original.getGeburtsdatum(),
            original.getWahlkreis(),
            original.isVerified(),
            original.getRegisteredAt(),
            votedPolls
        );

        assertTrue(reconstructed.isVerified());
        assertEquals(2, reconstructed.getVotedPollIds().size());
        assertTrue(reconstructed.hasVoted("poll-1"));
        assertTrue(reconstructed.hasVoted("poll-2"));
    }
}

