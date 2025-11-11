package com.mse.eVote.buergerVerwaltung.domain.valueobjects;

/**
 * Name - Value Object für Namen
 *
 * Unveränderbare Darstellung eines Namens mit internationaler Unterstützung.
 * Validiert Namensteile nach europäischen Standards:
 * - Vorname/Nachname muss mit Großbuchstabe beginnen
 * - Unterstützt Buchstaben (inkl. Umlaute), Leerzeichen und Bindestriche
 * - Ermöglicht Zusammensetzungen: "Jean-Pierre", "van der Berg", "Maria-Luise"
 * - Unterstützt internationale Namen: Deutsch, Englisch, Französisch, etc.
 *
 * Als Value Object implementiert dieses Record Wert-Semantik:
 * - Zwei Namen mit gleichen Werten sind äquivalent
 * - Unveränderbar nach Erstellung
 * - Keine Geschäftslogik, nur Validierung
 * - Records signalisieren automatisch die Immutabilität und Value-Semantik
 */
public record Name(
        String firstName,  // Vorname (English: firstName, German: Vorname)
        String lastName    // Nachname (English: lastName, German: Nachname)
) {
    /**
     * Compact constructor for validation.
     * Supports multiple name formats:
     * - Single character: "A", "B"
     * - Compound names: "Jean-Pierre", "Maria-Luise"
     * - Multi-part names: "van der Berg", "De La Cruz"
     * - International names with diacritics: "José", "François", "Müller", "Björn"
     *
     * Validation rules:
     * - Must start with uppercase letter (A-Z, Ä, Ö, Ü)
     * - Can contain lowercase letters, digits are NOT allowed
     * - Supports hyphens and spaces for compound names
     * - Supports German umlauts (ä, ö, ü, ß) and accented characters
     *
     * @throws IllegalArgumentException if firstName or lastName is null, empty, or invalid format
     */
    public Name {
        if (firstName == null || firstName.trim().isEmpty())
            throw new IllegalArgumentException("First name must not be null or empty");
        if (lastName == null || lastName.trim().isEmpty())
            throw new IllegalArgumentException("Last name must not be null or empty");

        // Trim first to normalize input
        firstName = firstName.trim();
        lastName = lastName.trim();

        // Regex allows:
        // - Uppercase start: [A-ZÄÖÜ]
        // - Followed by 0+ of: lowercase letters, umlauts, hyphens, spaces
        // - Pattern: ^[A-ZÄÖÜ][a-zA-ZäöüßÄÖÜ\- ]*$
        String nameRegex = "^[A-ZÄÖÜ][a-zA-ZäöüßÄÖÜ\\- ]*$";

        if (!firstName.matches(nameRegex))
            throw new IllegalArgumentException("Invalid first name format: '" + firstName +
                    "'. Must start with uppercase letter and contain only letters, hyphens, or spaces.");
        if (!lastName.matches(nameRegex))
            throw new IllegalArgumentException("Invalid last name format: '" + lastName +
                    "'. Must start with uppercase letter and contain only letters, hyphens, or spaces.");
    }

    /**
     * Backward-compatible getter for firstName.
     */
    @Override
    public String firstName() {
        return firstName;
    }

    /**
     * Backward-compatible getter for lastName.
     */
    @Override
    public String lastName() {
        return lastName;
    }

    /**
     * Explicit getFirstName() for backward compatibility with code using getXxx() naming.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Explicit getLastName() for backward compatibility with code using getXxx() naming.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * String representation of the Name.
     */
    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

}