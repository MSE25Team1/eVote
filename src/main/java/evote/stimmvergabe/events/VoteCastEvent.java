package evote.stimmvergabe.events;

import java.time.Instant;

/**
 * Immutable event representing that a vote has been cast.
 * Implemented as a Java record to remove boilerplate while keeping
 * input validation and backward-compatible accessor names.
 */
public record VoteCastEvent(String voteId, String pollId, String optionId, Instant castAt) {

    public VoteCastEvent {
        if (voteId == null || pollId == null || optionId == null || castAt == null) {
            throw new IllegalArgumentException("All event fields must be non-null");
        }
    }
}
