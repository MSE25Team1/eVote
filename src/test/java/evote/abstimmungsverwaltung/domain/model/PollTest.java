package evote.abstimmungsverwaltung.domain.model;

import evote.abstimmungsverwaltung.events.PollEndedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PollTest {

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

    private Poll createDefaultPoll() {
        return new Poll(
                "poll-1",
                "Bürgerentscheid Innenstadt",
                List.of("Option-A", "Option-B"),
                now.minusDays(1),
                now.plusDays(1),
                1000,
                fixedClock
        );
    }

    private Poll createPollWithTitle(String title) {
        return new Poll("poll-1", title, List.of("Option-A"), now.minusDays(1), now.plusDays(1), 10, fixedClock);
    }

    private Poll createPollWithOptions(List<String> options) {
        return new Poll("poll-1", "Test", options, now.minusDays(1), now.plusDays(1), 10, fixedClock);
    }

    private Poll createPollWithDates(LocalDateTime start, LocalDateTime end) {
        return new Poll("poll-1", "Test", List.of("Ja", "Nein"), start, end, 10, fixedClock);
    }

    private Poll createPollWithEligibleVoterCount(int count) {
        return new Poll("poll-1", "Test", List.of("Ja", "Nein"), now.minusDays(1), now.plusDays(1), count, fixedClock);
    }

    private Poll createPollWithId(String pollId) {
        return new Poll(pollId, "Test", List.of("Ja", "Nein"), now.minusDays(1), now.plusDays(1), 100, fixedClock);
    }

    private Poll createPollWithClock(Clock clock) {
        return new Poll("poll-1", "Test", List.of("Ja", "Nein"), now.minusDays(1), now.plusDays(1), 100, clock);
    }

    private Poll createPollWithStartDate(LocalDateTime startDate) {
        return new Poll("poll-1", "Test", List.of("Ja", "Nein"), startDate, now.plusDays(1), 100, fixedClock);
    }

    private Poll createPollWithEndDate(LocalDateTime endDate) {
        return new Poll("poll-1", "Test", List.of("Ja", "Nein"), now.minusDays(1), endDate, 100, fixedClock);
    }

    // ----------------------------------------------------------------------
    // Konstruktion & Invarianten
    // ----------------------------------------------------------------------

    @Test
    void validPoll_shouldInitializeCorrectly() {
        // Arrange & Act
        Poll poll = createDefaultPoll();

        // Assert
        assertEquals("poll-1", poll.getPollId());
        assertEquals("Bürgerentscheid Innenstadt", poll.getTitle());
        assertEquals(2, poll.getTotalOptions());
        assertEquals(1000, poll.getEligibleVoterCount());
        assertTrue(poll.isOpen());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void invalidTitle_shouldThrowException(String invalidTitle) {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> createPollWithTitle(invalidTitle));
    }

    @Test
    void emptyOptions_shouldThrowException() {
        // Arrange
        List<String> emptyList = List.of();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> createPollWithOptions(emptyList));
    }

    @Test
    void duplicateOptions_shouldThrowException() {
        // Arrange
        List<String> duplicateList = List.of("Ja", "Ja");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> createPollWithOptions(duplicateList));
    }

    @Test
    void startDate_after_endDate_shouldThrowException() {
        // Arrange
        LocalDateTime start = now.plusDays(2);
        LocalDateTime end = now.plusDays(1);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> createPollWithDates(start, end));
    }

    @Test
    void negativeEligibleVoterCount_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> createPollWithEligibleVoterCount(-1));
    }

    // ----------------------------------------------------------------------
    // isOpen() – Zeitfenster + manuelles close()
    // ----------------------------------------------------------------------

    @Test
    void isOpen_shouldBeTrue_whenNowBetweenStartAndEnd_andNotManuallyClosed() {
        // Arrange & Act
        Poll poll = new Poll(
                "poll-1",
                "Bürgerentscheid",
                List.of("Ja", "Nein"),
                now.minusHours(1),
                now.plusHours(1),
                100,
                fixedClock
        );

        // Assert
        assertTrue(poll.isOpen());
    }

    @Test
    void isOpen_shouldBeFalse_beforeStartDate() {
        // Arrange & Act
        Poll poll = new Poll(
                "poll-1",
                "Bürgerentscheid",
                List.of("Ja", "Nein"),
                now.plusHours(1),   // Start in der Zukunft
                now.plusDays(1),
                100,
                fixedClock
        );

        // Assert
        assertFalse(poll.isOpen());
    }

    @Test
    void isOpen_shouldBeFalse_afterEndDate() {
        // Arrange & Act
        Poll poll = new Poll(
                "poll-1",
                "Bürgerentscheid",
                List.of("Ja", "Nein"),
                now.minusDays(2),
                now.minusDays(1),
                100,
                fixedClock
        );

        // Assert
        assertFalse(poll.isOpen());
    }

    @Test
    void isOpen_shouldBeFalse_whenPollHasBeenClosedManually() {
        // Arrange
        Poll poll = createDefaultPoll();
        assertTrue(poll.isOpen());

        // Act
        poll.close();

        // Assert
        assertFalse(poll.isOpen());
    }

    @Test
    void close_shouldBeIdempotent() {
        // Arrange
        Poll poll = createDefaultPoll();

        // Act
        poll.close();
        poll.close(); // darf keinen Fehler werfen

        // Assert
        assertFalse(poll.isOpen());
    }

    // ----------------------------------------------------------------------
    // recordVote(optionId)
    // ----------------------------------------------------------------------

    @Test
    void recordVote_forValidOption_whenOpen_shouldSucceed() {
        // Arrange
        Poll poll = createDefaultPoll();

        // Act
        assertDoesNotThrow(() -> poll.recordVote("Option-A"));

        // Assert
        assertEquals(1, poll.getVoteCountFor("Option-A"));
    }

    //

    @ParameterizedTest
    @ValueSource(strings = {"UNBEKANNTE_OPTION", "   "})
    @NullSource
    void recordVote_withInvalidOption_shouldThrowException(String invalidOption) {
        // Arrange
        Poll poll = createDefaultPoll();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> poll.recordVote(invalidOption));
    }

    @Test
    void getVoteCountFor_unknownOption_shouldThrowException() {
        // Arrange
        Poll poll = createDefaultPoll();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> poll.getVoteCountFor("UNBEKANNTE_OPTION"));
    }

    @Test
    void nullPollId_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> createPollWithId(null));
    }

    @Test
    void nullClock_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> createPollWithClock(null));
    }

    @Test
    void nullStartDate_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> createPollWithStartDate(null));
    }

    @Test
    void nullEndDate_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> createPollWithEndDate(null));
    }

    @Test
    void nullOptions_shouldThrowException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> createPollWithOptions(null));
    }

    @Test
    void optionsContainingNull_shouldThrowException() {
        // Arrange: Liste, die null *erlaubt*
        List<String> optionsWithNull = new java.util.ArrayList<>();
        optionsWithNull.add("Ja");
        optionsWithNull.add(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> createPollWithOptions(optionsWithNull));
    }


    @Test
    void optionsContainingBlank_shouldThrowException() {
        // Arrange
        List<String> optionsWithBlank = List.of("Ja", "   ");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> createPollWithOptions(optionsWithBlank));
    }

    @Test
    void startDate_equal_endDate_shouldThrowException() {
        // Arrange
        LocalDateTime startEnd = now;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> createPollWithDates(startEnd, startEnd));
    }

    @Test
    void recordVote_whenPollClosedByDate_shouldThrowException() {
        // Arrange
        Poll closedByDate = new Poll(
                "poll-1",
                "Bürgerentscheid",
                List.of("Ja", "Nein"),
                now.minusDays(2),
                now.minusDays(1),  // bereits vorbei
                100,
                fixedClock
        );

        // Assert (verify precondition)
        assertFalse(closedByDate.isOpen());

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> closedByDate.recordVote("Ja"));
    }

    @Test
    void recordVote_whenPollClosedManually_shouldThrowException() {
        // Arrange
        Poll poll = createDefaultPoll();
        poll.close();

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> poll.recordVote("Option-A"));
    }


    @Test
    void recordVote_shouldAllowUnlimitedVotes_whenEligibleVoterCountIsZero() {
        // Arrange: Poll ohne Limit (eligibleVoterCount = 0)
        Poll poll = new Poll(
                "poll-unlimited",
                "Unlimited Test",
                List.of("Ja", "Nein"),
                now.minusDays(1),
                now.plusDays(1),
                0,              // 0 = kein Limit
                fixedClock
        );

        // Act & Assert: mehrere Votes, sollten NICHT knallen
        assertDoesNotThrow(() -> poll.recordVote("Ja"));
        assertDoesNotThrow(() -> poll.recordVote("Ja"));
        assertDoesNotThrow(() -> poll.recordVote("Nein"));
    }

    @Test
    void votesShouldNotExceedEligibleVoterCount_whenEnforced() {
        // Arrange
        Poll poll = new Poll(
                "poll-1",
                "Mini-Wahl",
                List.of("Ja", "Nein"),
                now.minusDays(1),
                now.plusDays(1),
                1,              // nur 1 wahlberechtigter
                fixedClock
        );
        poll.recordVote("Ja"); // erster Vote ok

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> poll.recordVote("Ja"));
    }

    // ----------------------------------------------------------------------
    // getEligibleVoterCount() / getTotalOptions()
    // ----------------------------------------------------------------------

    @Test
    void getTotalOptions_shouldReturnNumberOfConfiguredOptions() {
        // Arrange & Act
        Poll poll = new Poll(
                "poll-1",
                "Bürgerentscheid",
                List.of("Option-1", "Option-2", "Option-3"),
                now.minusDays(1),
                now.plusDays(1),
                42,
                fixedClock
        );

        // Assert
        assertEquals(3, poll.getTotalOptions());
    }

    @Test
    void getEligibleVoterCount_shouldReturnConfiguredValue() {
        // Arrange & Act
        Poll poll = new Poll(
                "poll-1",
                "Bürgerentscheid",
                List.of("Ja", "Nein"),
                now.minusDays(1),
                now.plusDays(1),
                1234,
                fixedClock
        );

        // Assert
        assertEquals(1234, poll.getEligibleVoterCount());
    }

    // ----------------------------------------------------------------------
    // close() & PollEndedEvent
    // ----------------------------------------------------------------------

    @Test
    void close_shouldCreatePollEndedEvent() {
        // Arrange
        Poll poll = createDefaultPoll();
        List<Object> eventsBeforeClose = poll.getDomainEvents();

        // Act
        poll.close();

        // Assert
        List<Object> eventsAfterClose = poll.getDomainEvents();
        assertEquals(1, eventsAfterClose.size());
        assertTrue(eventsAfterClose.get(0) instanceof PollEndedEvent);
    }

    @Test
    void close_shouldCreateEventWithCorrectPollId() {
        // Arrange
        Poll poll = createPollWithId("poll-abc-123");

        // Act
        poll.close();

        // Assert
        List<Object> events = poll.getDomainEvents();
        PollEndedEvent event = (PollEndedEvent) events.get(0);
        assertEquals("poll-abc-123", event.pollId());
    }

    @Test
    void close_shouldCreateEventWithCurrentTimestamp() {
        // Arrange
        Poll poll = createDefaultPoll();
        var closedAtBeforeClose = java.time.Instant.now(fixedClock);

        // Act
        poll.close();

        // Assert
        List<Object> events = poll.getDomainEvents();
        PollEndedEvent event = (PollEndedEvent) events.get(0);
        assertEquals(closedAtBeforeClose, event.endedAt());
    }

    @Test
    void close_shouldNotDuplicateEvent_whenCalledMultipleTimes() {
        // Arrange
        Poll poll = createDefaultPoll();

        // Act
        poll.close();
        poll.close();
        poll.close();

        // Assert
        List<Object> events = poll.getDomainEvents();
        assertEquals(3, events.size());
        assertTrue(events.stream().allMatch(e -> e instanceof PollEndedEvent));
    }

    @Test
    void getDomainEvents_shouldReturnImmutableList() {
        // Arrange
        Poll poll = createDefaultPoll();
        poll.close();
        List<Object> events = poll.getDomainEvents();

        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> events.add(new Object()));
    }

    // ----------------------------------------------------------------------
    // Optional: gleichzeitige / konsistente Nutzung von recordVote & isOpen
    // ----------------------------------------------------------------------

    @Nested
    class VotingLifecycle {

        @Test
        void openPoll_allowsVotes_untilClosed() {
            // Arrange
            Poll poll = createDefaultPoll();

            // Act & Assert (precondition)
            assertTrue(poll.isOpen());
            poll.recordVote("Option-A");

            // Act
            poll.close();

            // Assert
            assertFalse(poll.isOpen());
            assertThrows(IllegalStateException.class, () -> poll.recordVote("Option-B"));
        }
    }
}
