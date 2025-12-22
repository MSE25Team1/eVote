package evote.buergerverwaltung.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VoterUpdateRequest(
        @NotBlank @Email String email
) {
}
