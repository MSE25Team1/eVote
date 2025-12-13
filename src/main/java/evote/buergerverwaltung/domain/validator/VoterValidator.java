package evote.buergerverwaltung.domain.validator;

import evote.buergerverwaltung.domain.model.Voter;
import evote.abstimmungsverwaltung.domain.model.Poll;

/**
 * VoterValidator - Validiert Voter-spezifische Regeln
 *
 * Bounded Context: Bürgerverwaltung
 * Diese Validator lebt im Voter-Kontext und enforced Invarianten des Voter-Aggregates
 *
 * Prüfungen:
 * - Voter muss existieren (nicht null)
 * - Voter muss verifiziert sein
 * - Voter darf nicht bereits für diese Poll abgestimmt haben (Double-Voting-Prevention)
 */
public class VoterValidator {
    
    /**
     * Validiert, dass ein Voter berechtigt ist zu wählen
     *
     * @param voter Der zu validierende Voter
     * @param poll Die Poll auf die der Voter abstimmen möchte
     * @throws IllegalArgumentException wenn Voter nicht existiert
     * @throws IllegalStateException wenn Voter nicht verifiziert oder bereits abgestimmt hat
     */
    public void validateForVoting(Voter voter, Poll poll) {
        if (voter == null) {
            throw new IllegalArgumentException("Voter must not be null");
        }
        
        if (!voter.isVerified()) {
            throw new IllegalStateException(
                    "Voter must be verified in order to vote. Voter ID: " + voter.getVoterId()
            );
        }
        
        if (voter.hasVoted(poll.getPollId())) {
            throw new IllegalStateException(
                    "Voter has already voted for this poll. Voter ID: " + voter.getVoterId() +
                    ", Poll ID: " + poll.getPollId()
            );
        }
    }
}

