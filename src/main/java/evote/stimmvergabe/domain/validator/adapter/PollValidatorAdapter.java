package evote.stimmvergabe.domain.validator.adapter;

import evote.stimmvergabe.domain.validator.CompositeVoteValidator;
import evote.stimmvergabe.domain.model.Vote;
import evote.abstimmungsverwaltung.domain.model.Poll;
import evote.abstimmungsverwaltung.domain.validator.PollValidator;
import evote.buergerverwaltung.domain.model.Voter;

import java.time.Clock;

/**
 * PollValidatorAdapter - Anti-Corruption Layer für Poll-Validierung
 *
 * Bounded Context: Stimmvergabe (Vote Context)
 *
 * Adapter zwischen Stimmvergabe und Abstimmungsverwaltung Kontexten.
 * Ermöglicht die Verwendung von Poll-Validierung aus dem Abstimmungsverwaltung-Kontext
 * innerhalb der CompositeVoteValidator Chain.
 *
 * Arbeitet mit Vote Domain Model statt DTO für Wiederverwendbarkeit
 */
public class PollValidatorAdapter implements CompositeVoteValidator {

    private final PollValidator pollValidator;
    private final Clock clock;

    public PollValidatorAdapter(PollValidator pollValidator, Clock clock) {
        this.pollValidator = pollValidator;
        this.clock = clock;
    }

    @Override
    public void validate(Vote vote, Poll poll, Voter voter) {
        // Delegiere Poll-Validierung an den Poll-Kontext
        pollValidator.validateForVoting(poll, clock);
    }
}

