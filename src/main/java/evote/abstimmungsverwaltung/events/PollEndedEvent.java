package evote.abstimmungsverwaltung.events;

import java.time.Instant;
import java.util.Objects;

public final class PollEndedEvent {
    private final String pollId;
    private final Instant endedAt;

    public PollEndedEvent(String pollId, Instant endedAt) {
        if (pollId == null || pollId.trim().isEmpty()) {
            throw new IllegalArgumentException("pollId must not be null or blank");
        }
        if (endedAt == null) {
            throw new IllegalArgumentException("endedAt must not be null");
        }
        this.pollId = pollId;
        this.endedAt = endedAt;
    }

    public String getPollId() {
        return pollId;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PollEndedEvent)) return false;
        PollEndedEvent that = (PollEndedEvent) o;
        return Objects.equals(pollId, that.pollId) && Objects.equals(endedAt, that.endedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pollId, endedAt);
    }

    @Override
    public String toString() {
        return "PollEndedEvent{" +
                "pollId='" + pollId + '\'' +
                ", endedAt=" + endedAt +
                '}';
    }
}

