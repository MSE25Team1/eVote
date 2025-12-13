package evote.abstimmungsverwaltung.domain.validator;

import evote.abstimmungsverwaltung.domain.model.Poll;

import java.time.Clock;

/**
 * PollValidator - Validiert Poll-spezifische Regeln
 *
 * Bounded Context: Abstimmungsverwaltung
 * Diese Validator lebt im Poll-Kontext und enforced Invarianten des Poll-Aggregates
 *
 * Prüfungen:
 * - Poll muss zeitlich offen sein (startDate <= now < endDate)
 * - Poll darf nicht manuell geschlossen sein
 */
public class PollValidator {
    
    /**
     * Validiert, dass eine Poll für die Abstimmung geöffnet ist
     *
     * @param poll Die zu validierende Poll
     * @param clock Die Uhr zur Zeitprüfung
     * @throws IllegalStateException wenn Poll nicht offen ist
     */
    public void validateForVoting(Poll poll, Clock clock) {
        if (poll == null) {
            throw new IllegalArgumentException("Poll must not be null");
        }
        
        if (!poll.isOpen()) {
            throw new IllegalStateException(
                    "Poll is not open for voting. Poll ID: " + poll.getPollId()
            );
        }
    }
}

