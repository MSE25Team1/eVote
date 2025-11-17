package evote.buergerverwaltung.application.dto;

public record VoterResponse(
        String id,
        NameDTO name,
        String email,
        AddressDTO address,
        String district,
        String registeredAt,
        boolean verified
) {
    public static record NameDTO(
            String firstName,
            String lastName,
            String fullName
    ) {}

    public static record AddressDTO(
            String street,
            String houseNumber,
            String postalCode,
            String city,
            String formatted
    ) {}
}

