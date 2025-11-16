package evote.stimmvergabe.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class VoteCastEventTest {

    private static final Instant FIXED_INSTANT = Instant.parse("2030-01-01T12:00:00Z");

    // ---------- Konstruktor-Tests ----------

    @Test
    @DisplayName("Konstruktor: alle Parameter != null → Event wird erzeugt")
    void constructor_allNonNull_shouldCreateInstance() {
        VoteCastEvent event = new VoteCastEvent(
                "vote-1",
                "poll-1",
                "option-A",
                FIXED_INSTANT
        );

        assertNotNull(event);
        assertEquals("vote-1", event.getVoteId());
        assertEquals("poll-1", event.getPollId());
        assertEquals("option-A", event.getOptionId());
        assertEquals(FIXED_INSTANT, event.getCastAt());
    }

    @Test
    @DisplayName("Konstruktor: voteId == null → IllegalArgumentException")
    void constructor_nullVoteId_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () ->
                new VoteCastEvent(
                        null,
                        "poll-1",
                        "option-A",
                        FIXED_INSTANT
                )
        );
    }

    @Test
    @DisplayName("Konstruktor: pollId == null → IllegalArgumentException")
    void constructor_nullPollId_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () ->
                new VoteCastEvent(
                        "vote-1",
                        null,
                        "option-A",
                        FIXED_INSTANT
                )
        );
    }

    @Test
    @DisplayName("Konstruktor: optionId == null → IllegalArgumentException")
    void constructor_nullOptionId_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () ->
                new VoteCastEvent(
                        "vote-1",
                        "poll-1",
                        null,
                        FIXED_INSTANT
                )
        );
    }

    @Test
    @DisplayName("Konstruktor: castAt == null → IllegalArgumentException")
    void constructor_nullCastAt_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () ->
                new VoteCastEvent(
                        "vote-1",
                        "poll-1",
                        "option-A",
                        null
                )
        );
    }

    // ---------- equals-Tests ----------

    @Test
    @DisplayName("equals: gleiche Referenz → true (this == o)")
    void equals_sameInstance_shouldReturnTrue() {
        VoteCastEvent event = new VoteCastEvent("vote-1", "poll-1", "option-A", FIXED_INSTANT);

        assertTrue(event.equals(event));
    }

    @Test
    @DisplayName("equals: gleicher Inhalt → true (symmetrisch)")
    void equals_sameValues_shouldReturnTrue() {
        VoteCastEvent e1 = new VoteCastEvent("vote-1", "poll-1", "option-A", FIXED_INSTANT);
        VoteCastEvent e2 = new VoteCastEvent("vote-1", "poll-1", "option-A", FIXED_INSTANT);

        assertTrue(e1.equals(e2));
        assertTrue(e2.equals(e1));
        assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    @DisplayName("equals: null → false")
    void equals_null_shouldReturnFalse() {
        VoteCastEvent e1 = new VoteCastEvent("vote-1", "poll-1", "option-A", FIXED_INSTANT);

        assertFalse(e1.equals(null));
    }

    @Test
    @DisplayName("equals: anderer Typ → false")
    void equals_differentType_shouldReturnFalse() {
        VoteCastEvent e1 = new VoteCastEvent("vote-1", "poll-1", "option-A", FIXED_INSTANT);

        assertFalse(e1.equals("not-an-event"));
    }

    @Test
    @DisplayName("equals: unterschiedliche voteId → false")
    void equals_differentVoteId_shouldReturnFalse() {
        VoteCastEvent e1 = new VoteCastEvent("vote-1", "poll-1", "option-A", FIXED_INSTANT);
        VoteCastEvent e2 = new VoteCastEvent("vote-2", "poll-1", "option-A", FIXED_INSTANT);

        assertFalse(e1.equals(e2));
    }

    @Test
    @DisplayName("equals: unterschiedliche pollId → false")
    void equals_differentPollId_shouldReturnFalse() {
        VoteCastEvent e1 = new VoteCastEvent("vote-1", "poll-1", "option-A", FIXED_INSTANT);
        VoteCastEvent e2 = new VoteCastEvent("vote-1", "poll-2", "option-A", FIXED_INSTANT);

        assertFalse(e1.equals(e2));
    }

    @Test
    @DisplayName("equals: unterschiedliche optionId → false")
    void equals_differentOptionId_shouldReturnFalse() {
        VoteCastEvent e1 = new VoteCastEvent("vote-1", "poll-1", "option-A", FIXED_INSTANT);
        VoteCastEvent e2 = new VoteCastEvent("vote-1", "poll-1", "option-B", FIXED_INSTANT);

        assertFalse(e1.equals(e2));
    }

    @Test
    @DisplayName("equals: unterschiedliche castAt → false")
    void equals_differentCastAt_shouldReturnFalse() {
        VoteCastEvent e1 = new VoteCastEvent("vote-1", "poll-1", "option-A", FIXED_INSTANT);
        VoteCastEvent e2 = new VoteCastEvent("vote-1", "poll-1", "option-A", FIXED_INSTANT.plusSeconds(60));

        assertFalse(e1.equals(e2));
    }
}
