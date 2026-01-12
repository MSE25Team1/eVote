package evote.buergerverwaltung.application.dto;

/**
 * Antwort-DTO der Bürgerverwaltung für das Frontend.
 */
public record VoterResponse(
        String id,
        NameDTO name,
        String email,
        AddressDTO address,
        String district,
        String registeredAt,
        boolean verified
) {
    /**
     * Kompakter Name für die API-Ausgabe.
     */
    public static record NameDTO(
            String firstName,
            String lastName,
            String fullName
    ) {}

    /**
     * Adressdarstellung für die API-Ausgabe.
     */
    public static record AddressDTO(
            String street,
            String houseNumber,
            String postalCode,
            String city,
            String formatted
    ) {}
}
