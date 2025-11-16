package evote.abstimmungsverwaltung.domain.model;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class Poll {

    private final String pollId;
    private final String title;
    private final List<String> options;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final int eligibleVoterCount;
    private final Clock clock;

    private boolean manuallyClosed = false;
    private int totalVotes = 0;
    private final Map<String, Integer> voteCounts = new HashMap<>();

    public Poll(
            String pollId,
            String title,
            List<String> options,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int eligibleVoterCount,
            Clock clock
    ) {
        validateConstructorArguments(pollId, title, options, startDate, endDate, eligibleVoterCount, clock);

        this.pollId = pollId;
        this.title = title;
        this.options = List.copyOf(options); // unveränderliche Kopie
        this.startDate = startDate;
        this.endDate = endDate;
        this.eligibleVoterCount = eligibleVoterCount;
        this.clock = clock;

        initializeVoteCounts();
    }

    private void initializeVoteCounts() {
        for (String opt : this.options) {
            voteCounts.put(opt, 0);
        }
    }

    private void validateConstructorArguments(
            String pollId,
            String title,
            List<String> options,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int eligibleVoterCount,
            Clock clock
    ) {
        // --- Null-Checks ---
        if (pollId == null) {
            throw new IllegalArgumentException("pollId must not be null");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("startDate and endDate must not be null");
        }
        if (clock == null) {
            throw new IllegalArgumentException("clock must not be null");
        }

        // --- Titel prüfen ---
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("title must not be null or blank");
        }

        // --- Options prüfen ---
        validateOptions(options);

        // --- Zeit-Invariante: startDate < endDate ---
        if (!startDate.isBefore(endDate)) {
            throw new IllegalArgumentException("startDate must be before endDate");
        }

        // --- Wahlberechtigte ---
        if (eligibleVoterCount < 0) {
            throw new IllegalArgumentException("eligibleVoterCount must not be negative");
        }
    }

    private void validateOptions(List<String> options) {
        if (options == null) {
            throw new NullPointerException("options must not be null");
        }
        if (options.isEmpty()) {
            throw new IllegalArgumentException("options must not be empty");
        }

        for (String opt : options) {
            if (opt == null || opt.trim().isEmpty()) {
                throw new IllegalArgumentException("options must not contain null or blank values");
            }
        }

        Set<String> unique = new HashSet<>(options);
        if (unique.size() != options.size()) {
            throw new IllegalArgumentException("options must not contain duplicates");
        }
    }

    // ----------------------------------------------------------------------
    // Getter, die in den Tests verwendet werden
    // ----------------------------------------------------------------------

    public String getPollId() {
        return pollId;
    }

    public String getTitle() {
        return title;
    }

    public int getTotalOptions() {
        return options.size();
    }

    public int getEligibleVoterCount() {
        return eligibleVoterCount;
    }

    // Optional, falls du intern Counts prüfen willst
    public int getVoteCountFor(String optionId) {
        Integer count = voteCounts.get(optionId);
        if (count == null) {
            throw new IllegalArgumentException("Unknown option: " + optionId);
        }
        return count;
    }

    // ----------------------------------------------------------------------
    // Öffnungszustand
    // ----------------------------------------------------------------------

    public boolean isOpen() {
        Instant instantNow = Instant.now(clock);
        return isOpenAt(instantNow);
    }

    public boolean isOpenAt(Instant instant) {
        if (manuallyClosed) {
            return false;
        }
        ZoneId zone = clock.getZone();
        LocalDateTime now = LocalDateTime.ofInstant(instant,zone);
        // [startDate, endDate) – an Endzeitpunkt ist die Abstimmung geschlossen
        return !now.isBefore(startDate) && now.isBefore(endDate);
    }

    public void close() {
        // idempotent
        this.manuallyClosed = true;
    }





    // ----------------------------------------------------------------------
    // Stimmen abgeben
    // ----------------------------------------------------------------------

    public void recordVote(String optionId) {
        // Poll muss geöffnet sein
        if (!isOpen()) {
            throw new IllegalStateException("Poll is not open for voting");
        }

        // Option muss gültig sein
        if (optionId == null || optionId.trim().isEmpty() || !voteCounts.containsKey(optionId)) {
            throw new IllegalArgumentException("Unknown or invalid option: " + optionId);
        }

        // Anzahl wahlberechtigter Personen nicht überschreiten (falls gesetzt)
        if (eligibleVoterCount > 0 && totalVotes >= eligibleVoterCount) {
            throw new IllegalStateException("Maximum number of eligible votes reached");
        }

        // Vote registrieren
        voteCounts.computeIfPresent(optionId, (opt, count) -> count + 1);
        totalVotes++;
    }
}
