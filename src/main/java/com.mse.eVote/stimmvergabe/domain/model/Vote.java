package com.mse.eVote.stimmvergabe.domain.model;

import com.mse.eVote.stimmvergabe.events.VoteCastEvent;

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



/*import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public class Vote {

    private final String voteId;
    private final String pollId;
    private final String optionId;
    private final String correlationId;
    private final Instant castAt;

    public Vote(
            String voteId,
            String pollId,
            String optionId,
            String correlationId,
            Clock clock
    ) {
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
        if (clock == null) {
            throw new IllegalArgumentException("clock must not be null");
        }

        this.voteId = voteId;
        this.pollId = pollId;
        this.optionId = optionId;
        this.correlationId = correlationId;
        this.castAt = clock.instant();
    }

    public String getVoteId() {
        return voteId;
    }

    public String getPollId() {
        return pollId;
    }

    public String getOptionId() {
        return optionId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public Instant getCastAt() {
        return castAt;
    }

    // Optional – kann helfen bei Tests oder Collections
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vote)) return false;
        Vote vote = (Vote) o;
        return Objects.equals(voteId, vote.voteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(voteId);
    }

    @Override
    public String toString() {
        return "Vote{" +
                "voteId='" + voteId + '\'' +
                ", pollId='" + pollId + '\'' +
                ", optionId='" + optionId + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", castAt=" + castAt +
                '}';
    }
}
*/