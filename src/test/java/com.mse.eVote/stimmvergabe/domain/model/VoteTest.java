import com.mse.eVote.stimmvergabe.domain.model.Vote;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class VoteTest {

    private Clock fixedClock;
    private Instant nowInstant;

    @BeforeEach
    void setUp() {
        // deterministische Zeitbasis für castAt
        LocalDateTime now = LocalDateTime.of(2030, 1, 1, 12, 0);
        fixedClock = Clock.fixed(
                now.atZone(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault()
        );
        nowInstant = fixedClock.instant();
    }

    @Test
    void validVote_shouldInitializeCorrectly() {
        Vote vote = new Vote(
                "vote-1",
                "poll-1",
                "Option-A",
                "corr-123",
                fixedClock
        );

        assertEquals("vote-1", vote.getVoteId());
        assertEquals("poll-1", vote.getPollId());
        assertEquals("Option-A", vote.getOptionId());
        assertEquals("corr-123", vote.getCorrelationId());
        assertEquals(nowInstant, vote.getCastAt());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void invalidVoteId_shouldThrowException(String invalidVoteId) {
        assertThrows(IllegalArgumentException.class, () ->
                new Vote(
                        invalidVoteId,
                        "poll-1",
                        "Option-A",
                        "corr-123",
                        fixedClock
                )
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void invalidPollId_shouldThrowException(String invalidPollId) {
        assertThrows(IllegalArgumentException.class, () ->
                new Vote(
                        "vote-1",
                        invalidPollId,
                        "Option-A",
                        "corr-123",
                        fixedClock
                )
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void invalidOptionId_shouldThrowException(String invalidOptionId) {
        assertThrows(IllegalArgumentException.class, () ->
                new Vote(
                        "vote-1",
                        "poll-1",
                        invalidOptionId,
                        "corr-123",
                        fixedClock
                )
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void invalidCorrelationId_shouldThrowException(String invalidCorrelationId) {
        assertThrows(IllegalArgumentException.class, () ->
                new Vote(
                        "vote-1",
                        "poll-1",
                        "Option-A",
                        invalidCorrelationId,
                        fixedClock
                )
        );
    }

    @Test
    void nullClock_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Vote(
                        "vote-1",
                        "poll-1",
                        "Option-A",
                        "corr-123",
                        null
                )
        );
    }
}
