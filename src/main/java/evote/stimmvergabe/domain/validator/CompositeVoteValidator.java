package evote.stimmvergabe.domain.validator;

import evote.stimmvergabe.domain.model.Vote;
import evote.abstimmungsverwaltung.domain.model.Poll;
import evote.buergerverwaltung.domain.model.Voter;

/**
 * CompositeVoteValidator - FunctionalInterface für Cross-Context Validierung
 *
 * Validiert Vote Domain Model mit Poll und Voter aus verschiedenen Bounded Contexts:
 * - Abstimmungsverwaltung (Poll validation)
 * - Bürgerverwaltung (Voter validation)
 * - Stimmvergabe (Vote validation)
 *
 * Dies ist eine "Anti-Corruption Layer" die Validatoren aus verschiedenen
 * Kontexten zusammenfasst ohne enge Kopplung zu erzeugen.
 *
 * Designprinzip: Validiere Domain Models, nicht DTOs
 * Dies ermöglicht die Wiederverwendung von Validatoren für verschiedene Operationen:
 * - Create
 * - Update
 * - Other future operations
 *
 * Beispiel:
 * CompositeVoteValidator chain = new PollValidatorAdapter(pollValidator, clock)
 *     .and(new VoterValidatorAdapter(voterValidator))
 *     .and(new VoteOptionValidator());
 * chain.validate(vote, poll, voter);
 */
@FunctionalInterface
public interface CompositeVoteValidator {

    /**
     * Validiert ein Vote Aggregate mit Poll und Voter aus anderen Kontexten
     *
     * @param vote Das zu validierende Vote Domain Model
     * @param poll Die Poll aus dem Abstimmungsverwaltung-Kontext
     * @param voter Der Voter aus dem Bürgerverwaltung-Kontext
     * @throws IllegalStateException wenn eine Zustandsbedingung verletzt wird
     * @throws IllegalArgumentException wenn ein Argument ungültig ist
     */
    void validate(Vote vote, Poll poll, Voter voter);

    /**
     * Kombiniert diesen Validator mit einem anderen Validator
     * Beide Validatoren werden sequenziell ausgeführt
     *
     * @param other Ein weiterer CompositeVoteValidator
     * @return Ein neuer Validator, der beide Validatoren kombiniert
     */
    default CompositeVoteValidator and(CompositeVoteValidator other) {
        return (vote, poll, voter) -> {
            this.validate(vote, poll, voter);
            other.validate(vote, poll, voter);
        };
    }
}

