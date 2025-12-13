package evote.buergerverwaltung.config;

import evote.buergerverwaltung.domain.validator.VoterValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * VoterValidationConfiguration - Voter-Kontext Validierungs-Beans
 *
 * Bounded Context: Bürgerverwaltung
 *
 * Stellt die Voter-Validator als Spring Bean bereit, die vom Vote-Kontext
 * über Adapter verwendet wird.
 */
@Configuration
public class VoterValidationConfiguration {

    @Bean
    public VoterValidator voterValidator() {
        return new VoterValidator();
    }
}

