package evote.abstimmungsverwaltung.events;

import java.time.Instant;

/**
 * Immutable event representing that a poll has ended.
 *
 * This is implemented as a Java record to reduce boilerplate. A compact canonical
 * constructor performs input validation equivalent to the previous class.
 */
// TODO: wird von der close Methode der Poll ausgelöst.
public record PollEndedEvent(String pollId, Instant endedAt) {

    public PollEndedEvent {
        if (pollId == null || pollId.trim().isEmpty()) {
            throw new IllegalArgumentException("pollId must not be null or blank");
        }
        if (endedAt == null) {
            throw new IllegalArgumentException("endedAt must not be null");
        }
    }
}
