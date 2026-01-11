package evote.stimmvergabe.domain.model;

import evote.stimmvergabe.events.VoteCastEvent;

import java.time.Clock;
import java.time.Instant;

/**
 * Value Object der Stimmvergabe, das eine abgegebene Stimme samt Zeitstempel abbildet.
 */
public record Vote(
        String voteId,
        String pollId,
        String optionId,
        String correlationId,
        Instant castAt
) {

    /**
     * Kompakter Konstruktor zur Validierung aller Pflichtfelder.
     */
    public Vote {
        if (voteId == null || voteId.trim().isEmpty()) {
            throw new IllegalArgumentException("voteId must not be null or blank");
        }
        if (pollId == null || pollId.trim().isEmpty()) {
            throw new IllegalArgumentException("pollId must not be null or blank");
        }
        if (optionId == null || optionId.trim().isEmpty()) {
            throw new IllegalArgumentException("optionId must not be null or blank");
        }
        if (correlationId == null || correlationId.trim().isEmpty()) {
            throw new IllegalArgumentException("correlationId must not be null or blank");
        }
        if (castAt == null) {
            throw new IllegalArgumentException("castAt must not be null");
        }
    }

    /**
     * Factory-Methode zum Erzeugen einer Stimme mit Zeitstempel aus einer Clock.
     */
    public static Vote of(String voteId, String pollId, String optionId, String correlationId, Clock clock) {
        if (clock == null) {
            throw new IllegalArgumentException("clock must not be null");
        }
        return new Vote(voteId, pollId, optionId, correlationId, clock.instant());
    }

    /**
     * Getter für die Vote-ID (kompatibel mit bestehendem Code).
     */
    public String getVoteId() {
        return voteId;
    }

    /**
     * Getter für die Poll-ID (kompatibel mit bestehendem Code).
     */
    public String getPollId() {
        return pollId;
    }

    /**
     * Getter für die Options-ID (kompatibel mit bestehendem Code).
     */
    public String getOptionId() {
        return optionId;
    }

    /**
     * Getter für die Correlation-ID (kompatibel mit bestehendem Code).
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * Getter für den Zeitpunkt der Stimmabgabe (kompatibel mit bestehendem Code).
     */
    public Instant getCastAt() {
        return castAt;
    }

    /**
     * Erzeugt das Domänenereignis für die abgegebene Stimme.
     */
    public VoteCastEvent getDomainEvent() {
        return new VoteCastEvent(voteId, pollId, optionId, castAt);
    }
}
