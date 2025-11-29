package evote.buergerverwaltung.domain.model;

import evote.buergerverwaltung.domain.valueobjects.Adresse;
import evote.buergerverwaltung.domain.valueobjects.Name;
import evote.buergerverwaltung.domain.valueobjects.Email;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
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
        // Arrange & Act
        Voter voter = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);

        // Assert
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
        // Arrange & Act
        Voter voter = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);

        // Assert
        assertTrue(voter.getVotedPollIds().isEmpty());
        assertFalse(voter.hasVoted("poll-1"));
    }

    // ============ Verification Tests ============

    @Test
    @DisplayName("Voter verification should set isVerified to true")
    void voterVerification_shouldSetVerifiedFlag() {
        // Arrange
        Voter voter = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);

        // Act
        voter.verify();

        // Assert
        assertTrue(voter.isVerified());
        assertNotNull(voter.getRegisteredAt());
    }

    @Test
    @DisplayName("Voter verification should produce VoterRegisteredEvent")
    void voterVerification_shouldProduceEvent() {
        // Arrange
        Voter voter = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);

        // Act
        voter.verify();

        // Assert
        assertNotNull(voter.getPendingEvent());
        assertEquals(voter.getVoterId(), voter.getPendingEvent().voterId());
        assertEquals("Max", voter.getPendingEvent().firstName());
        assertEquals("Mustermann", voter.getPendingEvent().lastName());
    }

    @Test
    @DisplayName("Double verification should throw exception")
    void doubleVerification_shouldThrowException() {
        // Arrange
        Voter voter = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);
        voter.verify();

        // Act & Assert
        assertThrows(IllegalStateException.class, voter::verify);
    }

    // ============ Vote Marking Tests (Double-Vote Prevention) ============

    @Test
    @DisplayName("Unverified voter cannot vote")
    void unverifiedVoter_cannotVote() {
        // Arrange
        Voter voter = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> voter.markVoted("poll-1"));
    }

    @Test
    @DisplayName("Verified voter can mark vote on poll")
    void verifiedVoter_canMarkVote() {
        // Arrange
        Voter voter = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);
        voter.verify();

        // Act
        assertDoesNotThrow(() -> voter.markVoted("poll-1"));

        // Assert
        assertTrue(voter.hasVoted("poll-1"));
    }

    @Test
    @DisplayName("Voter cannot vote twice on same poll (double-voting prevention)")
    void voter_cannotVoteTwiceOnSamePoll() {
        // Arrange
        Voter voter = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);
        voter.verify();
        voter.markVoted("poll-1");

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> voter.markVoted("poll-1"));
    }

    @Test
    @DisplayName("Voter can vote on different polls")
    void voter_canVoteOnMultiplePolls() {
        // Arrange
        Voter voter = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);
        voter.verify();

        // Act
        assertDoesNotThrow(() -> voter.markVoted("poll-1"));
        assertDoesNotThrow(() -> voter.markVoted("poll-2"));
        assertDoesNotThrow(() -> voter.markVoted("poll-3"));

        // Assert
        assertTrue(voter.hasVoted("poll-1"));
        assertTrue(voter.hasVoted("poll-2"));
        assertTrue(voter.hasVoted("poll-3"));
        assertEquals(3, voter.getVotedPollIds().size());
    }

    @Test
    @DisplayName("Voter can check poll participation independently")
    void voter_canCheckPollParticipation() {
        // Arrange
        Voter voter = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);
        voter.verify();
        voter.markVoted("poll-1");

        // Act & Assert
        assertTrue(voter.hasVoted("poll-1"));
        assertFalse(voter.hasVoted("poll-2"));
        assertFalse(voter.hasVoted("non-existent-poll"));
    }

    // ============ Input Validation Tests ============

    @Test
    @DisplayName("Null name should throw exception")
    void nullName_shouldThrowException() {
        // Arrange
        java.time.LocalDate birthdate = java.time.LocalDate.of(1990, 1, 1);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> Voter.register(null, VALID_ADRESSE, VALID_EMAIL, birthdate, VALID_WAHLKREIS));
    }

    @Test
    @DisplayName("Null adresse should throw exception")
    void nullAdresse_shouldThrowException() {
        // Arrange
        java.time.LocalDate birthdate = java.time.LocalDate.of(1990, 1, 1);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> Voter.register(VALID_NAME, null, VALID_EMAIL, birthdate, VALID_WAHLKREIS));
    }

    // ============ Aggregate Identity Tests ============

    @Test
    @DisplayName("Voter equals itself should return this == o) ")
    void voter_equalsItself_shouldReturnTrue() {
        // Arrange & Act
        Voter voter = Voter.register(
                VALID_NAME,
                VALID_ADRESSE,
                VALID_EMAIL,
                java.time.LocalDate.of(1990, 1, 1),
                VALID_WAHLKREIS
        );

        // Assert
        assertEquals(voter, voter);
    }

    @Test
    @DisplayName("Voter should not equal null")
    void voter_equalsNULL_shouldBeFalse() {
        // Arrange & Act
        Voter voter = Voter.register(
                VALID_NAME,
                VALID_ADRESSE,
                VALID_EMAIL,
                java.time.LocalDate.of(1990, 1, 1),
                VALID_WAHLKREIS
        );

        // Assert
        assertNotEquals(null, voter);
    }

    @Test
    @DisplayName("Voter should not equal non voter")
    void voter_shouldNotEqualString() {
        // Arrange & Act
        Voter voter1 = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);

        // Assert
        assertNotEquals( "not a voter", voter1);
    }

    @Test
    @DisplayName("Two voters with same voterId should be equal")
    void identicalVoterIds_shouldBeEqual() {
        // Arrange
        Voter voter1 = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);
        String voterId = voter1.getVoterId();

        // Act: Simulate reconstruction from database
        Voter voter2 = Voter.reconstruct(
            voterId, VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1),
            VALID_WAHLKREIS, false, null, java.util.Set.of()
        );

        // Assert
        assertEquals(voter1, voter2);
        assertEquals(voter1.hashCode(), voter2.hashCode());
    }

    @Test
    @DisplayName("Two voters with different voteIds should not be equal")
    void differentVoterIds_shouldNotBeEqual() {
        // Arrange & Act
        Voter voter1 = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);
        Voter voter2 = Voter.register(VALID_NAME, VALID_ADRESSE, new Email("other@mail.de"), java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);

        // Assert
        assertNotEquals(voter1, voter2);
    }

    @Test
    @DisplayName("Event should be clearable after publishing")
    void pendingEvent_shouldBeClearable() {
        // Arrange
        Voter voter = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);
        voter.verify();
        assertNotNull(voter.getPendingEvent());

        // Act
        voter.clearPendingEvent();

        // Assert
        assertNull(voter.getPendingEvent());
    }

    // ============ Reconstruction from Persistence Tests ============

    @Test
    @DisplayName("Reconstruct verified voter with voted polls")
    void reconstructVerifiedVoter_shouldPreserveState() {
        // Arrange
        Voter original = Voter.register(VALID_NAME, VALID_ADRESSE, VALID_EMAIL, java.time.LocalDate.of(1990, 1, 1), VALID_WAHLKREIS);
        original.verify();
        original.markVoted("poll-1");
        original.markVoted("poll-2");

        // Act: Simulate reconstruction from persistence
        Voter reconstructed = Voter.reconstruct(
            original.getVoterId(),
            original.getName(),
            original.getAdresse(),
            original.getEmail(),
            original.getGeburtsdatum(),
            original.getWahlkreis(),
            original.isVerified(),
            original.getRegisteredAt(),
            original.getVotedPollIds()
        );

        // Assert
        assertTrue(reconstructed.isVerified());
        assertEquals(2, reconstructed.getVotedPollIds().size());
        assertTrue(reconstructed.hasVoted("poll-1"));
        assertTrue(reconstructed.hasVoted("poll-2"));
    }
}

