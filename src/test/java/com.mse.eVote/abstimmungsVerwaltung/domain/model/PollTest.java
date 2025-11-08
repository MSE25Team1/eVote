import com.mse.eVote.abstimmungsVerwaltung.domain.model.Poll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Clock;
import java.time.Instant;
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

    // ----------------------------------------------------------------------
    // Konstruktion & Invarianten
    // ----------------------------------------------------------------------

    @Test
    void validPoll_shouldInitializeCorrectly() {
        Poll poll = createDefaultPoll();

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
        assertThrows(IllegalArgumentException.class, () ->
                new Poll(
                        "poll-1",
                        invalidTitle,
                        List.of("Option-A"),
                        now.minusDays(1),
                        now.plusDays(1),
                        10,
                        fixedClock
                )
        );
    }

    @Test
    void emptyOptions_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Poll(
                        "poll-1",
                        "Irgendein Titel",
                        List.of(),
                        now.minusDays(1),
                        now.plusDays(1),
                        10,
                        fixedClock
                )
        );
    }

    @Test
    void duplicateOptions_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Poll(
                        "poll-1",
                        "Irgendein Titel",
                        List.of("Ja", "Ja"), // doppelte Option
                        now.minusDays(1),
                        now.plusDays(1),
                        10,
                        fixedClock
                )
        );
    }

    @Test
    void startDate_after_endDate_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Poll(
                        "poll-1",
                        "Irgendein Titel",
                        List.of("Ja", "Nein"),
                        now.plusDays(1),      // Start in der Zukunft
                        now.minusDays(1),     // Ende in der Vergangenheit
                        10,
                        fixedClock
                )
        );
    }

    @Test
    void negativeEligibleVoterCount_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Poll(
                        "poll-1",
                        "Irgendein Titel",
                        List.of("Ja", "Nein"),
                        now.minusDays(1),
                        now.plusDays(1),
                        -1,
                        fixedClock
                )
        );
    }

    // ----------------------------------------------------------------------
    // isOpen() – Zeitfenster + manuelles close()
    // ----------------------------------------------------------------------

    @Test
    void isOpen_shouldBeTrue_whenNowBetweenStartAndEnd_andNotManuallyClosed() {
        Poll poll = new Poll(
                "poll-1",
                "Bürgerentscheid",
                List.of("Ja", "Nein"),
                now.minusHours(1),
                now.plusHours(1),
                100,
                fixedClock
        );

        assertTrue(poll.isOpen());
    }

    @Test
    void isOpen_shouldBeFalse_beforeStartDate() {
        Poll poll = new Poll(
                "poll-1",
                "Bürgerentscheid",
                List.of("Ja", "Nein"),
                now.plusHours(1),   // Start in der Zukunft
                now.plusDays(1),
                100,
                fixedClock
        );

        assertFalse(poll.isOpen());
    }

    @Test
    void isOpen_shouldBeFalse_afterEndDate() {
        Poll poll = new Poll(
                "poll-1",
                "Bürgerentscheid",
                List.of("Ja", "Nein"),
                now.minusDays(2),
                now.minusDays(1),
                100,
                fixedClock
        );

        assertFalse(poll.isOpen());
    }

    @Test
    void isOpen_shouldBeFalse_whenPollHasBeenClosedManually() {
        Poll poll = createDefaultPoll();
        assertTrue(poll.isOpen());

        poll.close();

        assertFalse(poll.isOpen());
    }

    @Test
    void close_shouldBeIdempotent() {
        Poll poll = createDefaultPoll();
        poll.close();
        poll.close(); // darf keinen Fehler werfen

        assertFalse(poll.isOpen());
    }

    // ----------------------------------------------------------------------
    // recordVote(optionId)
    // ----------------------------------------------------------------------

    @Test
    void recordVote_forValidOption_whenOpen_shouldSucceed() {
        Poll poll = createDefaultPoll();

        assertDoesNotThrow(() -> poll.recordVote("Option-A"));
        assertEquals(1, poll.getVoteCountFor("Option-A"));
    }

    //

    @Test
    void recordVote_forUnknownOption_shouldThrowException() {
        Poll poll = createDefaultPoll();

        assertThrows(IllegalArgumentException.class, () ->
                poll.recordVote("UNBEKANNTE_OPTION")
        );
    }

    @Test
    void getVoteCountFor_unknownOption_shouldThrowException() {
        Poll poll = createDefaultPoll();

        assertThrows(IllegalArgumentException.class, () ->
                poll.getVoteCountFor("UNBEKANNTE_OPTION")
        );
    }

    @Test
    void nullPollId_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Poll(
                        null,
                        "Testwahl",
                        List.of("Ja", "Nein"),
                        now.minusDays(1),
                        now.plusDays(1),
                        100,
                        fixedClock
                )
        );
    }

    @Test
    void nullClock_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Poll(
                        "poll-1",
                        "Testwahl",
                        List.of("Ja", "Nein"),
                        now.minusDays(1),
                        now.plusDays(1),
                        100,
                        null
                )
        );
    }

    @Test
    void nullStartDateOrEndDate_shouldThrowException() {
        // startDate null
        assertThrows(IllegalArgumentException.class, () ->
                new Poll(
                        "poll-1",
                        "Testwahl",
                        List.of("Ja", "Nein"),
                        null,
                        now.plusDays(1),
                        100,
                        fixedClock
                )
        );

        // endDate null
        assertThrows(IllegalArgumentException.class, () ->
                new Poll(
                        "poll-1",
                        "Testwahl",
                        List.of("Ja", "Nein"),
                        now.minusDays(1),
                        null,
                        100,
                        fixedClock
                )
        );
    }

    @Test
    void nullOptions_shouldThrowException() {
        assertThrows(NullPointerException.class, () ->
                new Poll(
                        "poll-1",
                        "Testwahl",
                        null,                    // <--- WICHTIG
                        now.minusDays(1),
                        now.plusDays(1),
                        100,
                        fixedClock
                )
        );
    }

  /*  @Test
    void optionsContainingNull_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->  // <--- vorher NPE
                new Poll(
                        "poll-1",
                        "Testwahl",
                        List.of("Ja", null),
                        now.minusDays(1),
                        now.plusDays(1),
                        100,
                        fixedClock
                )
        );
    } List.of darf kein null enthalten - daher sinnloser Test, weil Nullpointer geworfen wird bevor der Pollkonstruktor ausgeführt wird */

    @Test
    void optionsContainingNull_shouldThrowException() {
        // Liste, die null *erlaubt*:
        List<String> optionsWithNull = new java.util.ArrayList<>();
        optionsWithNull.add("Ja");
        optionsWithNull.add(null);  // hier ist null gültig im Sinne der Liste

        assertThrows(IllegalArgumentException.class, () ->
                new Poll(
                        "poll-1",
                        "Testwahl",
                        optionsWithNull,
                        now.minusDays(1),
                        now.plusDays(1),
                        100,
                        fixedClock
                )
        );
    }


    @Test
    void optionsContainingBlank_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Poll(
                        "poll-1",
                        "Testwahl",
                        List.of("Ja", "   "),
                        now.minusDays(1),
                        now.plusDays(1),
                        100,
                        fixedClock
                )
        );
    }

    @Test
    void startDate_equal_endDate_shouldThrowException() {
        LocalDateTime startEnd = now;

        assertThrows(IllegalArgumentException.class, () ->
                new Poll(
                        "poll-1",
                        "Testwahl",
                        List.of("Ja", "Nein"),
                        startEnd,
                        startEnd,     // <--- gleich!
                        100,
                        fixedClock
                )
        );
    }

    @Test
    void recordVote_whenPollClosedByDate_shouldThrowException() {
        Poll closedByDate = new Poll(
                "poll-1",
                "Bürgerentscheid",
                List.of("Ja", "Nein"),
                now.minusDays(2),
                now.minusDays(1),  // bereits vorbei
                100,
                fixedClock
        );

        assertFalse(closedByDate.isOpen());
        assertThrows(IllegalStateException.class, () ->
                closedByDate.recordVote("Ja")
        );
    }

    @Test
    void recordVote_whenPollClosedManually_shouldThrowException() {
        Poll poll = createDefaultPoll();
        poll.close();

        assertThrows(IllegalStateException.class, () ->
                poll.recordVote("Option-A")
        );
    }

    @Test
    void recordVote_withNullOption_shouldThrowException() {
        Poll poll = createDefaultPoll(); // benutzt eligibleVoterCount > 0 und offenen Zeitraum

        assertThrows(IllegalArgumentException.class, () ->
                poll.recordVote(null)
        );
    }

    @Test
    void recordVote_withBlankOption_shouldThrowException() {
        Poll poll = createDefaultPoll();

        assertThrows(IllegalArgumentException.class, () ->
                poll.recordVote("   ")
        );
    }

    @Test
    void recordVote_shouldAllowUnlimitedVotes_whenEligibleVoterCountIsZero() {
        // Poll ohne Limit (eligibleVoterCount = 0)
        Poll poll = new Poll(
                "poll-unlimited",
                "Unlimited Test",
                List.of("Ja", "Nein"),
                now.minusDays(1),
                now.plusDays(1),
                0,              // 0 = kein Limit
                fixedClock
        );

        // mehrere Votes, sollten NICHT knallen
        assertDoesNotThrow(() -> {
            poll.recordVote("Ja");
            poll.recordVote("Ja");
            poll.recordVote("Nein");
        });
    }


    // Optionale Variante: wenn du ein Limit oder Plausibilitätscheck brauchst
    @Test
    void votesShouldNotExceedEligibleVoterCount_whenEnforced() {
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

        assertThrows(IllegalStateException.class, () ->
                poll.recordVote("Ja")
        );
    }

    // ----------------------------------------------------------------------
    // getEligibleVoterCount() / getTotalOptions()
    // ----------------------------------------------------------------------

    @Test
    void getTotalOptions_shouldReturnNumberOfConfiguredOptions() {
        Poll poll = new Poll(
                "poll-1",
                "Bürgerentscheid",
                List.of("Option-1", "Option-2", "Option-3"),
                now.minusDays(1),
                now.plusDays(1),
                42,
                fixedClock
        );

        assertEquals(3, poll.getTotalOptions());
    }

    @Test
    void getEligibleVoterCount_shouldReturnConfiguredValue() {
        Poll poll = new Poll(
                "poll-1",
                "Bürgerentscheid",
                List.of("Ja", "Nein"),
                now.minusDays(1),
                now.plusDays(1),
                1234,
                fixedClock
        );

        assertEquals(1234, poll.getEligibleVoterCount());
    }

    // ----------------------------------------------------------------------
    // Optional: gleichzeitige / konsistente Nutzung von recordVote & isOpen
    // ----------------------------------------------------------------------

    @Nested
    class VotingLifecycle {

        @Test
        void openPoll_allowsVotes_untilClosed() {
            Poll poll = createDefaultPoll();

            assertTrue(poll.isOpen());
            poll.recordVote("Option-A");

            poll.close();

            assertFalse(poll.isOpen());
            assertThrows(IllegalStateException.class, () -> poll.recordVote("Option-B"));
        }
    }
}
