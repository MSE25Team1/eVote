package evote.stimmvergabe.events;

import java.time.Instant;

/**
 * Domänenereignis, das die erfolgreiche Stimmabgabe beschreibt.
 * Der Record stellt eine schlanke, validierende Darstellung bereit.
 */
public record VoteCastEvent(String voteId, String pollId, String optionId, Instant castAt) {

    public VoteCastEvent {
        if (voteId == null || pollId == null || optionId == null || castAt == null) {
            throw new IllegalArgumentException("All event fields must be non-null");
        }
    }
}
