package evote.abstimmungsverwaltung.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PollEndedEvent - Event Record Tests")
class PollEndedEventTest {

    private static final Instant FIXED_INSTANT = Instant.parse("2030-01-01T12:00:00Z");

    // ============ Constructor Tests - Happy Path ============

    @Test
    @DisplayName("Valid parameters should create event successfully")
    void validParameters_shouldCreateEvent() {
        // Act & Assert
        assertDoesNotThrow(() -> new PollEndedEvent("poll-1", FIXED_INSTANT));
    }

    @Test
    @DisplayName("Event should store pollId correctly")
    void event_shouldStorePollId() {
        // Arrange & Act
        PollEndedEvent event = new PollEndedEvent("poll-1", FIXED_INSTANT);

        // Assert
        assertEquals("poll-1", event.pollId());
    }

    @Test
    @DisplayName("Event should store endedAt timestamp correctly")
    void event_shouldStoreEndedAt() {
        // Arrange & Act
        PollEndedEvent event = new PollEndedEvent("poll-1", FIXED_INSTANT);

        // Assert
        assertEquals(FIXED_INSTANT, event.endedAt());
    }

    // ============ Validation Tests - Null Parameters ============

    @Test
    @DisplayName("Null pollId should throw exception")
    void nullPollId_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> new PollEndedEvent(null, FIXED_INSTANT));
    }

    @Test
    @DisplayName("Null endedAt should throw exception")
    void nullEndedAt_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> new PollEndedEvent("poll-1", null));
    }

    @Test
    @DisplayName("Both parameters null should throw exception")
    void bothParametersNull_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> new PollEndedEvent(null, null));
    }

    // ============ Validation Tests - Invalid pollId ============

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    @DisplayName("Empty or whitespace-only pollId should throw exception")
    void emptyOrWhitespacePollId_shouldThrowException(String invalidPollId) {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> new PollEndedEvent(invalidPollId, FIXED_INSTANT));
    }

    // ============ Value Object Semantics - Equality ============

    @Test
    @DisplayName("Same instance should equal itself")
    void sameInstance_shouldEqualItself() {
        // Arrange & Act
        PollEndedEvent event = new PollEndedEvent("poll-1", FIXED_INSTANT);

        // Assert
        assertEquals(event, event);
    }

    @Test
    @DisplayName("Two events with same values should be equal")
    void sameValues_shouldBeEqual() {
        // Arrange & Act
        PollEndedEvent event1 = new PollEndedEvent("poll-1", FIXED_INSTANT);
        PollEndedEvent event2 = new PollEndedEvent("poll-1", FIXED_INSTANT);

        // Assert
        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    @DisplayName("Event should not equal null")
    void event_shouldNotEqualNull() {
        // Arrange & Act
        PollEndedEvent event = new PollEndedEvent("poll-1", FIXED_INSTANT);

        // Assert
        assertNotEquals(null, event);
    }

    @Test
    @DisplayName("Event should not equal different type")
    void event_shouldNotEqualDifferentType() {
        // Arrange & Act
        PollEndedEvent event = new PollEndedEvent("poll-1", FIXED_INSTANT);

        // Assert
        assertNotEquals("not-an-event", event);
    }

    @Test
    @DisplayName("Events with different pollId should not be equal")
    void differentPollId_shouldNotBeEqual() {
        // Arrange & Act
        PollEndedEvent event1 = new PollEndedEvent("poll-1", FIXED_INSTANT);
        PollEndedEvent event2 = new PollEndedEvent("poll-2", FIXED_INSTANT);

        // Assert
        assertNotEquals(event1, event2);
    }

    @Test
    @DisplayName("Events with different endedAt should not be equal")
    void differentEndedAt_shouldNotBeEqual() {
        // Arrange & Act
        Instant instant1 = FIXED_INSTANT;
        Instant instant2 = FIXED_INSTANT.plusSeconds(60);
        PollEndedEvent event1 = new PollEndedEvent("poll-1", instant1);
        PollEndedEvent event2 = new PollEndedEvent("poll-1", instant2);

        // Assert
        assertNotEquals(event1, event2);
    }

    // ============ toString Tests ============

    @Test
    @DisplayName("toString should include pollId and endedAt")
    void toString_shouldIncludeAllFields() {
        // Arrange & Act
        PollEndedEvent event = new PollEndedEvent("poll-1", FIXED_INSTANT);
        String result = event.toString();

        // Assert
        assertTrue(result.contains("poll-1"));
        assertTrue(result.contains(FIXED_INSTANT.toString()));
    }

    // ============ Edge Cases ============

    @Test
    @DisplayName("Very long pollId should be accepted")
    void veryLongPollId_shouldBeAccepted() {
        // Arrange
        String longPollId = "poll-" + "x".repeat(1000);

        // Act & Assert
        assertDoesNotThrow(() -> new PollEndedEvent(longPollId, FIXED_INSTANT));
    }

    @Test
    @DisplayName("PollId with special characters should be accepted")
    void pollIdWithSpecialCharacters_shouldBeAccepted() {
        // Act & Assert
        assertDoesNotThrow(() -> new PollEndedEvent("poll-123_test-α", FIXED_INSTANT));
    }

    @Test
    @DisplayName("Past timestamp should be accepted")
    void pastTimestamp_shouldBeAccepted() {
        // Arrange
        Instant pastInstant = Instant.parse("2000-01-01T00:00:00Z");

        // Act & Assert
        assertDoesNotThrow(() -> new PollEndedEvent("poll-1", pastInstant));
    }

    @Test
    @DisplayName("Future timestamp should be accepted")
    void futureTimestamp_shouldBeAccepted() {
        // Arrange
        Instant futureInstant = Instant.parse("2099-12-31T23:59:59Z");

        // Act & Assert
        assertDoesNotThrow(() -> new PollEndedEvent("poll-1", futureInstant));
    }

    @Test
    @DisplayName("Epoch instant should be accepted")
    void epochInstant_shouldBeAccepted() {
        // Arrange
        Instant epochInstant = Instant.EPOCH;

        // Act & Assert
        assertDoesNotThrow(() -> new PollEndedEvent("poll-1", epochInstant));
    }
}

