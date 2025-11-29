package evote.buergerverwaltung.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VoterCreateRequest(
        @NotNull @Valid @JsonProperty("name") NameRequest name,
        @NotBlank @Email String email,
        @NotNull @Valid @JsonProperty("adresse") AdresseRequest adresse,
        @NotBlank String wahlkreis
) {
    public static record NameRequest(
            @NotBlank String vorname,
            @NotBlank String nachname
    ) {}

    public static record AdresseRequest(
            @NotBlank String strasse,
            @NotBlank String plz,
            @NotBlank String ort
    ) {}
}

