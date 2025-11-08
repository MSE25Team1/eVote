package com.mse.eVote.stimmvergabe.domain.model;

import com.mse.eVote.stimmvergabe.domain.event.VoteCastEvent;

import java.time.Clock;
import java.time.Instant;

public class Vote {

    private final String voteId;
    private final String pollId;
    private final String optionId;
    private final String correlationId;
    private final Instant castAt;

    private final VoteCastEvent domainEvent;

    public Vote(String voteId, String pollId, String optionId, String correlationId, Clock clock) {
        if (voteId == null || voteId.trim().isEmpty()) throw new IllegalArgumentException("voteId must not be null or blank");
        if (pollId == null || pollId.trim().isEmpty()) throw new IllegalArgumentException("pollId must not be null or blank");
        if (optionId == null || optionId.trim().isEmpty()) throw new IllegalArgumentException("optionId must not be null or blank");
        if (correlationId == null || correlationId.trim().isEmpty()) throw new IllegalArgumentException("correlationId must not be null or blank");
        if (clock == null) throw new IllegalArgumentException("clock must not be null");

        this.voteId = voteId;
        this.pollId = pollId;
        this.optionId = optionId;
        this.correlationId = correlationId;
        this.castAt = clock.instant();

        //  Domain Event erstellen
        this.domainEvent = new VoteCastEvent(voteId, pollId, optionId, castAt);
    }

    public String getVoteId() { return voteId; }
    public String getPollId() { return pollId; }
    public String getOptionId() { return optionId; }
    public String getCorrelationId() { return correlationId; }
    public Instant getCastAt() { return castAt; }

    /** Domain Event, das beim Abgeben der Stimme erzeugt wurde */
    public VoteCastEvent getDomainEvent() {
        return domainEvent;
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