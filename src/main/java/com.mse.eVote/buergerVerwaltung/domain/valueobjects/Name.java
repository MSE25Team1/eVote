package com.mse.eVote.buergerVerwaltung.domain.valueobjects;

import java.util.Objects;

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
 * Als Value Object implementiert dieses Objekt Wert-Semantik:
 * - Zwei Namen mit gleichen Werten sind äquivalent
 * - Unveränderbar nach Erstellung
 * - Keine Geschäftslogik, nur Validierung
 */
public class Name {
    private final String firstName;  // Vorname (English: firstName, German: Vorname)
    private final String lastName;   // Nachname (English: lastName, German: Nachname)

    /**
     * Creates a Name value object with strict validation.
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
     * @param firstName first/given name(s) - e.g., "Max", "Jean-Pierre", "Marie-Louise"
     * @param lastName last/family name(s) - e.g., "Mustermann", "van der Berg", "De La Cruz"
     * @throws IllegalArgumentException if firstName or lastName is null, empty, or invalid format
     */
    public Name(String firstName, String lastName) {
        if (firstName == null || firstName.trim().isEmpty())
            throw new IllegalArgumentException("First name must not be null or empty");
        if (lastName == null || lastName.trim().isEmpty())
            throw new IllegalArgumentException("Last name must not be null or empty");

        // Trim first to normalize input
        String firstNameClean = firstName.trim();
        String lastNameClean = lastName.trim();

        // Regex allows:
        // - Uppercase start: [A-ZÄÖÜ]
        // - Followed by 0+ of: lowercase letters, umlauts, hyphens, spaces
        // - Pattern: ^[A-ZÄÖÜ][a-zA-ZäöüßÄÖÜ\- ]*$
        String nameRegex = "^[A-ZÄÖÜ][a-zA-ZäöüßÄÖÜ\\- ]*$";

        if (!firstNameClean.matches(nameRegex))
            throw new IllegalArgumentException("Invalid first name format: '" + firstName +
                    "'. Must start with uppercase letter and contain only letters, hyphens, or spaces.");
        if (!lastNameClean.matches(nameRegex))
            throw new IllegalArgumentException("Invalid last name format: '" + lastName +
                    "'. Must start with uppercase letter and contain only letters, hyphens, or spaces.");

        this.firstName = firstNameClean;
        this.lastName = lastNameClean;
    }

    // ============ Getters with English names ============
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }

    // ============ Legacy German aliases (for backward compatibility) ============
    public String getVorname() { return firstName; }
    public String getNachname() { return lastName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Name)) return false;
        Name name = (Name) o;
        return firstName.equals(name.firstName) && lastName.equals(name.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

}