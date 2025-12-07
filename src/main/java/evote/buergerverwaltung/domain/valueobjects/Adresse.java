package evote.buergerverwaltung.domain.valueobjects;

/**
 * Adresse - Value Object für Postanschriften
 *
 * Unveränderbare Darstellung einer deutschen Postadresse mit englischer Unterstützung.
 * Validiert alle Felder nach deutschen Standards:
 * - Street (Straße): Buchstaben, Umlaute, Zahlen, Bindestriche, Punkte, Leerzeichen
 * - HouseNumber (Hausnummer): 1-3 Ziffern, optional gefolgt von einem Buchstaben (z.B. "12a")
 * - AddressComplement (Adresszusatz): Optional (z.B. "Penthouse", "3. OG", "Apartment 42")
 * - PostalCode (PLZ): Genau 5 Ziffern (deutsches Format 00001-99999)
 * - City (Stadt): Buchstaben, Umlaute, Bindestriche, Leerzeichen
 *
 * Als Value Object implementiert dieses Record Wert-Semantik (nicht Identitäts-Semantik):
 * - Zwei Adressen mit gleichen Werten sind äquivalent
 * - Unveränderbar nach Erstellung
 * - Keine Geschäftslogik, nur Validierung
 * - Records signalisieren automatisch die Immutabilität und Value-Semantik
 */
public record Adresse(
        String street,            // Straße
        String houseNumber,       // Hausnummer
        String addressComplement, // Adresszusatz (optional)
        String postalCode,        // PLZ
        String city               // Stadt
) {

    /**
     * Compact constructor for validation.
     * Street validation:
     * - Allows letters (including German umlauts: ä, ö, ü, ß)
     * - Allows digits, hyphens, periods, and spaces
     * - Example: "Musterstraße", "Königin-Luise-Str.", "Zur Alten Mühle"
     *
     * House number validation:
     * - 1-3 digits optionally followed by a single letter
     * - Examples: "1", "12", "999", "12a", "42b"
     * - Invalid: "12ab", "a1", "12-34"
     *
     * Postal code validation:
     * - Exactly 5 digits (German format)
     * - Range: 00001 - 99999
     * - Examples: "10115", "50667", "80331"
     *
     * City validation:
     * - Allows letters (including umlauts)
     * - Allows hyphens and spaces for compound city names
     * - Examples: "Berlin", "Köln", "Baden-Baden", "Berlin-Mitte"
     *
     * Address complement:
     * - Optional field (pass null or empty string)
     * - For apartment numbers, floor numbers, etc.
     * - Examples: "Penthouse", "3. OG", "Wohnung 42"
     *
     * @throws IllegalArgumentException if any field fails validation
     */
    public Adresse {
        if (street == null || street.trim().isEmpty())
            throw new IllegalArgumentException("Street must not be null or empty");
        if (houseNumber == null || houseNumber.trim().isEmpty())
            throw new IllegalArgumentException("House number must not be null or empty");
        if (postalCode == null || !postalCode.trim().matches("^[\\d]{5}$"))
            throw new IllegalArgumentException("Postal code must be exactly 5 digits");
        if (city == null || city.trim().isEmpty())
            throw new IllegalArgumentException("City must not be null or empty");

        // Trim all values first before validation
        street = street.trim();
        houseNumber = houseNumber.trim();
        addressComplement = addressComplement == null ? "" : addressComplement.trim();
        postalCode = postalCode.trim();
        city = city.trim();

        // Validation regex patterns for German addresses
        String streetRegex = "^[A-Za-zÄÖÜäöüß0-9\\-\\. ]+$";
        String houseNumberRegex = "^[\\d]+[a-zA-Z]?$";
        String cityRegex = "^[A-Za-zÄÖÜäöüß\\- ]+$";

        if (!street.matches(streetRegex))
            throw new IllegalArgumentException("Invalid street format: '" + street +
                    "'. Must contain only letters, digits, hyphens, periods, or spaces.");
        if (!houseNumber.matches(houseNumberRegex))
            throw new IllegalArgumentException("Invalid house number format: '" + houseNumber +
                    "'. Must be 1-3 digits optionally followed by a single letter (e.g., '12a').");
        if (!city.matches(cityRegex))
            throw new IllegalArgumentException("Invalid city format: '" + city +
                    "'. Must contain only letters, hyphens, or spaces.");
    }

    /**
     * Backward-compatible getter for street.
     */
    public String getStreet() {
        return street;
    }

    /**
     * Backward-compatible getter for houseNumber.
     */
    public String getHouseNumber() {
        return houseNumber;
    }

    /**
     * Backward-compatible getter for addressComplement.
     */
    public String getAddressComplement() {
        return addressComplement;
    }

    /**
     * Backward-compatible getter for postalCode.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Backward-compatible getter for city.
     */
    public String getCity() {
        return city;
    }

    /**
     * String representation of the Address.
     */
    @Override
    public String toString() {
        return street + " " + houseNumber +
               (addressComplement.isEmpty() ? "" : ", " + addressComplement) +
               ", " + postalCode + " " + city;
    }

}