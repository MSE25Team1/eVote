package evote.stimmvergabe.domain.validator.adapter;

import evote.stimmvergabe.domain.validator.CompositeVoteValidator;
import evote.stimmvergabe.domain.model.Vote;
import evote.abstimmungsverwaltung.domain.model.Poll;
import evote.buergerverwaltung.domain.model.Voter;
import evote.buergerverwaltung.domain.validator.VoterValidator;

/**
 * VoterValidatorAdapter - Anti-Corruption Layer für Voter-Validierung
 *
 * Bounded Context: Stimmvergabe (Vote Context)
 *
 * Adapter zwischen Stimmvergabe und Bürgerverwaltung Kontexten.
 * Ermöglicht die Verwendung von Voter-Validierung aus dem Bürgerverwaltung-Kontext
 * innerhalb der CompositeVoteValidator Chain.
 *
 * Arbeitet mit Vote Domain Model statt DTO für Wiederverwendbarkeit
 */
public class VoterValidatorAdapter implements CompositeVoteValidator {

    private final VoterValidator voterValidator;

    public VoterValidatorAdapter(VoterValidator voterValidator) {
        this.voterValidator = voterValidator;
    }

    @Override
    public void validate(Vote vote, Poll poll, Voter voter) {
        // Delegiere Voter-Validierung an den Voter-Kontext
        voterValidator.validateForVoting(voter, poll);
    }
}

