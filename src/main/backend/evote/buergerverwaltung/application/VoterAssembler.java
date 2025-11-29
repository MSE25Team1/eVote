package evote.buergerverwaltung.application;

import evote.buergerverwaltung.application.dto.VoterCreateRequest;
import evote.buergerverwaltung.application.dto.VoterResponse;
import evote.buergerverwaltung.domain.model.Voter;
import evote.buergerverwaltung.domain.valueobjects.Adresse;
import evote.buergerverwaltung.domain.valueobjects.Email;
import evote.buergerverwaltung.domain.valueobjects.Name;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * VoterAssembler - Mapper between Domain and Application DTOs
 * 
 * Responsibility: Translate between the domain model (Voter) and DTOs used by the API.
 * This keeps the domain model pure and free from presentation concerns.
 */
@Component
public class VoterAssembler {

    /**
     * Maps a VoterCreateRequest DTO to a domain Voter entity.
     */
    public Voter toDomain(VoterCreateRequest request) {
        Name name = new Name(request.name().vorname(), request.name().nachname());
        Adresse adresse = parseAdresse(request.adresse());
        Email email = new Email(request.email());
        
        // Hinweis: Geburtsdatum wird derzeit nicht vom Frontend geliefert.
        // Für die Demo setzen wir "mindestens 18 Jahre".
        LocalDate geburtsdatum = LocalDate.now().minusYears(18);
        
        return Voter.register(name, adresse, email, geburtsdatum, request.wahlkreis());
    }

    /**
     * Maps a domain Voter entity to a VoterResponse DTO.
     */
    public VoterResponse toResponse(Voter voter) {
        var name = new VoterResponse.NameDTO(
                voter.getName().firstName(),
                voter.getName().lastName(),
                voter.getName().firstName() + " " + voter.getName().lastName()
        );

        var address = new VoterResponse.AddressDTO(
                voter.getAdresse().getStreet(),
                voter.getAdresse().getHouseNumber(),
                voter.getAdresse().getPostalCode(),
                voter.getAdresse().getCity(),
                formatAddress(voter.getAdresse())
        );

        return new VoterResponse(
                voter.getVoterId(),
                name,
                voter.getEmail().toString(),
                address,
                voter.getWahlkreis(),
                voter.getRegisteredAt() != null ? voter.getRegisteredAt().toString() : null,
                voter.isVerified()
        );
    }

    /**
     * Parses address from DTO and handles street/house number extraction.
     * This is presentation-layer logic, not domain logic.
     */
    private Adresse parseAdresse(VoterCreateRequest.AdresseRequest req) {
        if (req.strasse() == null || req.strasse().isBlank()) {
            throw new IllegalArgumentException("Straße darf nicht leer sein");
        }

        String trimmed = req.strasse().trim();
        String[] parts = trimmed.split("\\s+");

        if (parts.length < 2) {
            // Fallback: alles als Straße, Hausnummer "1"
            return new Adresse(trimmed, "1", "", req.plz(), req.ort());
        }

        String houseNumber = parts[parts.length - 1];
        String street = String.join(" ", java.util.Arrays.copyOf(parts, parts.length - 1));

        return new Adresse(street, houseNumber, "", req.plz(), req.ort());
    }

    /**
     * Formats address for display purposes.
     */
    private String formatAddress(Adresse adresse) {
        return String.format("%s %s, %s %s",
                adresse.getStreet(),
                adresse.getHouseNumber(),
                adresse.getPostalCode(),
                adresse.getCity()
        );
    }
}

