package evote.stimmvergabe.domain.model;

import evote.stimmvergabe.events.VoteCastEvent;

import java.time.Clock;
import java.time.Instant;

/**
 * Vote - A value object representing a cast vote in Domain-Driven Design.
 * Records signal immutability and provide automatic equals(), hashCode(), and toString() implementations.
 */
public record Vote(
        String voteId,
        String pollId,
        String optionId,
        String correlationId,
        Instant castAt
) {

    /**
     * Compact constructor for validation.
     * Ensures all fields are valid before creating a Vote instance.
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
     * Static factory method to create a Vote with a Clock.
     * This method accepts a Clock and captures the current instant for the vote.
     */
    public static Vote of(String voteId, String pollId, String optionId, String correlationId, Clock clock) {
        if (clock == null) {
            throw new IllegalArgumentException("clock must not be null");
        }
        return new Vote(voteId, pollId, optionId, correlationId, clock.instant());
    }

    /**
     * Backward-compatible getter for voteId.
     */
    public String getVoteId() {
        return voteId;
    }

    /**
     * Backward-compatible getter for pollId.
     */
    public String getPollId() {
        return pollId;
    }

    /**
     * Backward-compatible getter for optionId.
     */
    public String getOptionId() {
        return optionId;
    }

    /**
     * Backward-compatible getter for correlationId.
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * Backward-compatible getter for castAt.
     */
    public Instant getCastAt() {
        return castAt;
    }

    /**
     * Domain Event, created when the vote was cast.
     */
    public VoteCastEvent getDomainEvent() {
        return new VoteCastEvent(voteId, pollId, optionId, castAt);
    }
}