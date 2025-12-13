package evote.stimmvergabe.config;

import evote.stimmvergabe.domain.validator.CompositeVoteValidator;
import evote.stimmvergabe.domain.validator.VoteOptionValidator;
import evote.stimmvergabe.domain.validator.adapter.PollValidatorAdapter;
import evote.stimmvergabe.domain.validator.adapter.VoterValidatorAdapter;
import evote.abstimmungsverwaltung.domain.validator.PollValidator;
import evote.buergerverwaltung.domain.validator.VoterValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * VoteValidationConfiguration - Cross-Context Validierungs-Orchestrierung
 *
 * Diese Konfiguration demonstriert das Anti-Corruption Layer Muster in DDD.
 * Sie kombiniert Validatoren aus verschiedenen Bounded Contexts:
 *
 * 1. Abstimmungsverwaltung-Kontext: PollValidator
 *    - Validiert Poll-spezifische Invarianten
 *    - Prüft ob Poll zeitlich offen ist
 *
 * 2. Bürgerverwaltung-Kontext: VoterValidator
 *    - Validiert Voter-spezifische Invarianten
 *    - Prüft Verifikation und Double-Voting-Prevention
 *
 * 3. Stimmvergabe-Kontext: VoteOptionValidator
 *    - Validiert Vote-spezifische Invarianten
 *    - Prüft ob Option in Poll existiert
 *
 * Die Komposition erfolgt durch Adapter, die die Grenze zwischen Kontexten respektieren.
 * Dies verhindert enge Kopplung und ermöglicht Independent Deployment der Kontexte.
 */
@Configuration
public class VoteValidationConfiguration {

    /**
     * Spring Bean: Composite Validator für die Vote-Erstellung
     *
     * Die Validator-Chain wird in dieser Reihenfolge ausgeführt:
     * 1. PollValidator - Prüft Poll-Status
     * 2. VoterValidator - Prüft Voter-Berechtigung
     * 3. VoteOptionValidator - Prüft Option-Gültigkeit
     *
     * @param pollValidator Validator aus Abstimmungsverwaltung-Kontext
     * @param voterValidator Validator aus Bürgerverwaltung-Kontext
     * @param clock Clock für Zeitprüfungen
     * @return CompositeVoteValidator mit allen Validierungen
     */
    @Bean
    public CompositeVoteValidator compositeVoteValidator(
            PollValidator pollValidator,
            VoterValidator voterValidator,
            Clock clock) {

        return new PollValidatorAdapter(pollValidator, clock)
                .and(new VoterValidatorAdapter(voterValidator))
                .and(new VoteOptionValidator());
    }
}




