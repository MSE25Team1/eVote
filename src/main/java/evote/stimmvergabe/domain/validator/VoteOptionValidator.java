package evote.stimmvergabe.domain.validator;

import evote.stimmvergabe.domain.model.Vote;
import evote.abstimmungsverwaltung.domain.model.Poll;
import evote.buergerverwaltung.domain.model.Voter;

/**
 * VoteOptionValidator - Validiert Vote-spezifische Regeln
 *
 * Bounded Context: Stimmvergabe
 * Diese Validator lebt im Vote-Kontext und enforced Invarianten des Vote-Aggregates
 *
 * Prüfungen:
 * - Die optionId darf nicht leer sein (bereits durch Vote kompakt Konstruktor validiert)
 * - Die optionId muss eine gültige Option in der Poll sein
 *
 * Designprinzip: Validiert Vote Domain Model statt DTO
 * Dies ermöglicht Wiederverwendung für Create, Update und andere Operationen
 */
public class VoteOptionValidator implements CompositeVoteValidator {

    @Override
    public void validate(Vote vote, Poll poll, Voter voter) {
        if (vote == null) {
            throw new IllegalArgumentException("Vote must not be null");
        }

        if (poll == null) {
            throw new IllegalArgumentException("Poll must not be null");
        }

        // Prüfe, ob die Option in der Poll existiert
        try {
            poll.getVoteCountFor(vote.getOptionId());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid option for poll. Option ID: " + vote.getOptionId() +
                    ", Poll ID: " + vote.getPollId()
            );
        }
    }
}

