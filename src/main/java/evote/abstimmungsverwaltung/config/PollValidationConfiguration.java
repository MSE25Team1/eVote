package evote.abstimmungsverwaltung.config;

import evote.abstimmungsverwaltung.domain.validator.PollValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * PollValidationConfiguration - Poll-Kontext Validierungs-Beans
 *
 * Bounded Context: Abstimmungsverwaltung
 *
 * Stellt die Poll-Validator als Spring Bean bereit, die vom Vote-Kontext
 * über Adapter verwendet wird.
 */
@Configuration
public class PollValidationConfiguration {
    
    @Bean
    public PollValidator pollValidator() {
        return new PollValidator();
    }
}

