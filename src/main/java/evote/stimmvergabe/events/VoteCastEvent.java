package evote.stimmvergabe.events;

import java.time.Instant;
import java.util.Objects;

public class VoteCastEvent {

    private final String voteId;
    private final String pollId;
    private final String optionId;
    private final Instant castAt;

    public VoteCastEvent(String voteId, String pollId, String optionId, Instant castAt) {
        if (voteId == null || pollId == null || optionId == null || castAt == null) {
            throw new IllegalArgumentException("All event fields must be non-null");
        }
        this.voteId = voteId;
        this.pollId = pollId;
        this.optionId = optionId;
        this.castAt = castAt;
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

    public Instant getCastAt() {
        return castAt;
    }

    @Override
    public String toString() {
        return "VoteCastEvent{" +
                "voteId='" + voteId + '\'' +
                ", pollId='" + pollId + '\'' +
                ", optionId='" + optionId + '\'' +
                ", castAt=" + castAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VoteCastEvent)) return false;
        VoteCastEvent that = (VoteCastEvent) o;
        return Objects.equals(voteId, that.voteId) &&
                Objects.equals(pollId, that.pollId) &&
                Objects.equals(optionId, that.optionId) &&
                Objects.equals(castAt, that.castAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(voteId, pollId, optionId, castAt);
    }
}
