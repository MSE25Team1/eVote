package evote.buergerverwaltung.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request-DTO zum Aktualisieren der E-Mail einer wahlberechtigten Person.
 */
public record VoterUpdateRequest(
        @NotBlank @Email String email
) {
}
