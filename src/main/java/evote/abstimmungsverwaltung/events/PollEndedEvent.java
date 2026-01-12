package evote.abstimmungsverwaltung.events;

import java.time.Instant;

/**
 * Domänenereignis, das das Ende einer Abstimmung signalisiert.
 * Der Record übernimmt zugleich die Validierung der Eingaben.
 */
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
