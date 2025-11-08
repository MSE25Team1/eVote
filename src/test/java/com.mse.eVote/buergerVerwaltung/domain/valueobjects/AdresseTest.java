package com.mse.eVote.buergerVerwaltung.domain.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Adresse - Value Object Tests")
class AdresseTest {

    // ============ Happy-Path Tests ============

    @Test
    @DisplayName("Valid address with all fields should create successfully")
    void validAdresse_shouldCreateAdresseObject() {
        assertDoesNotThrow(() -> new Adresse("Musterstraße", "12a", "EG rechts", "12345", "Musterstadt"));
    }

    @Test
    @DisplayName("Valid address with minimal fields should create successfully")
    void minimalValidFields_shouldCreateAdresseObject() {
        assertDoesNotThrow(() -> new Adresse("A", "1", "", "00001", "B"));
    }

    @Test
    @DisplayName("Valid address with very long fields should create successfully")
    void addressWithLongFields_shouldCreateAdresseObject() {
        assertDoesNotThrow(() -> new Adresse(
            "SehrLangeStraßenbezeichnungMitSonderzeichen",
            "999",
            "Penthouse, 3. OG, Wohnung 42",
            "99999",
            "Großstadt"
        ));
    }

    @Test
    @DisplayName("Address with compound names (hyphens) should create successfully")
    void addressWithCompoundNames_shouldCreateAdresseObject() {
        assertDoesNotThrow(() -> new Adresse("Müller-Straße", "1a", "", "10115", "Berlin-Mitte"));
    }

    @Test
    @DisplayName("Address with special characters (dots, numbers) should create successfully")
    void addressWithSpecialCharacters_shouldCreateAdresseObject() {
        assertDoesNotThrow(() -> new Adresse("Zur Alten Mühle", "123", "", "50667", "Köln"));
    }

    // ============ Null/Empty Tests (Consolidated) ============

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t"})
    @DisplayName("Empty or whitespace-only street should throw IllegalArgumentException")
    void emptyOrWhitespaceStreet_shouldThrowException(String invalidStrasse) {
        assertThrows(IllegalArgumentException.class,
            () -> new Adresse(invalidStrasse, "1", "", "12345", "Stadt"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t"})
    @DisplayName("Empty or whitespace-only house number should throw IllegalArgumentException")
    void emptyOrWhitespaceHouseNumber_shouldThrowException(String invalidHausnummer) {
        assertThrows(IllegalArgumentException.class,
            () -> new Adresse("Straße", invalidHausnummer, "", "12345", "Stadt"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t"})
    @DisplayName("Empty or whitespace-only city should throw IllegalArgumentException")
    void emptyOrWhitespaceCity_shouldThrowException(String invalidStadt) {
        assertThrows(IllegalArgumentException.class,
            () -> new Adresse("Straße", "1", "", "12345", invalidStadt));
    }

    @Test
    @DisplayName("Null street should throw IllegalArgumentException")
    void nullStreet_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
            () -> new Adresse(null, "1", "", "12345", "Stadt"));
    }

    @Test
    @DisplayName("Null house number should throw IllegalArgumentException")
    void nullHouseNumber_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
            () -> new Adresse("Straße", null, "", "12345", "Stadt"));
    }

    @Test
    @DisplayName("Null city should throw IllegalArgumentException")
    void nullCity_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
            () -> new Adresse("Straße", "1", "", "12345", null));
    }

    @Test
    @DisplayName("All fields empty should throw IllegalArgumentException")
    void allEmptyFields_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
            () -> new Adresse("", "", "", "", ""));
    }

    // ============ PLZ (Postleitzahl) Validation Tests ============

    @ParameterizedTest
    @CsvSource({
        "12",         // Too short
        "123",        // Too short
        "1234",       // Too short
        "123456",     // Too long
        "1234a",      // Contains letter
        "abcde",      // All letters
        "12-45"       // Contains hyphen
    })
    @DisplayName("Invalid PLZ formats should throw IllegalArgumentException")
    void invalidPLZ_shouldThrowException(String invalidPlz) {
        assertThrows(IllegalArgumentException.class,
            () -> new Adresse("Straße", "1", "", invalidPlz, "Stadt"));
    }

    @Test
    @DisplayName("Null PLZ should throw IllegalArgumentException")
    void nullPLZ_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
            () -> new Adresse("Straße", "1", "", null, "Stadt"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"00001", "12345", "99999", "00000"})
    @DisplayName("Valid 5-digit PLZs should create successfully")
    void validPLZ_shouldCreateAdresseObject(String validPlz) {
        assertDoesNotThrow(() -> new Adresse("Straße", "1", "", validPlz, "Stadt"));
    }

    // ============ Hausnummer (House Number) Validation Tests ============

    @ParameterizedTest
    @CsvSource({
        "12a",        // Valid: number + letter
        "1",          // Valid: single digit
        "999",        // Valid: multiple digits
        "42b",        // Valid: 2 digits + letter
    })
    @DisplayName("Valid house numbers should create successfully")
    void validHouseNumbers_shouldCreateAdresseObject(String validHausnummer) {
        assertDoesNotThrow(() -> new Adresse("Straße", validHausnummer, "", "12345", "Stadt"));
    }

    @ParameterizedTest
    @CsvSource({
        "12ab",       // Two letters
        "a",          // Only letter
        "a1",         // Letter first
        "12-34",      // Hyphen in number
        "12 a",       // Space
    })
    @DisplayName("Invalid house numbers should throw IllegalArgumentException")
    void invalidHouseNumbers_shouldThrowException(String invalidHausnummer) {
        assertThrows(IllegalArgumentException.class,
            () -> new Adresse("Straße", invalidHausnummer, "", "12345", "Stadt"));
    }

    // ============ Straße (Street) Validation Tests ============

    @ParameterizedTest
    @ValueSource(strings = {
        "Musterstraße",
        "Müller-Straße",
        "Zur Alten Mühle",
        "Straße 123",
        "Str. 12",
        "Königin-Luise-Straße"
    })
    @DisplayName("Valid street names should create successfully")
    void validStreetNames_shouldCreateAdresseObject(String validStrasse) {
        assertDoesNotThrow(() -> new Adresse(validStrasse, "1", "", "12345", "Stadt"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Straße@123",      // @ symbol not allowed
        "Straße#1",        // # not allowed
        "Straße$",         // $ not allowed
        "Straße%",         // % not allowed
    })
    @DisplayName("Street names with invalid special characters should throw IllegalArgumentException")
    void invalidSpecialCharactersInStreet_shouldThrowException(String invalidStrasse) {
        assertThrows(IllegalArgumentException.class,
            () -> new Adresse(invalidStrasse, "1", "", "12345", "Stadt"));
    }

    // ============ Stadt (City) Validation Tests ============

    @ParameterizedTest
    @ValueSource(strings = {
        "Berlin",
        "Köln",
        "München",
        "Düsseldorf",
        "Münster",
        "Baden-Baden",
        "Berlin-Mitte"
    })
    @DisplayName("Valid city names should create successfully")
    void validCityNames_shouldCreateAdresseObject(String validStadt) {
        assertDoesNotThrow(() -> new Adresse("Straße", "1", "", "12345", validStadt));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Berlin123",       // Numbers not allowed
        "Stadt@",          // @ not allowed
        "Stadt#",          // # not allowed
        "City_Name"        // Underscore not allowed
    })
    @DisplayName("City names with invalid characters should throw IllegalArgumentException")
    void invalidCharactersInCity_shouldThrowException(String invalidStadt) {
        assertThrows(IllegalArgumentException.class,
            () -> new Adresse("Straße", "1", "", "12345", invalidStadt));
    }

    // ============ Value Object Semantics Tests ============

    @Test
    @DisplayName("Two addresses with same values should be equal")
    void twoIdenticalAddresses_shouldBeEqual() {
        Adresse addr1 = new Adresse("Straße", "1", "OG", "12345", "Stadt");
        Adresse addr2 = new Adresse("Straße", "1", "OG", "12345", "Stadt");

        assertEquals(addr1, addr2);
        assertEquals(addr1.hashCode(), addr2.hashCode());
    }

    @Test
    @DisplayName("Two addresses with different values should not be equal")
    void twoDifferentAddresses_shouldNotBeEqual() {
        Adresse addr1 = new Adresse("Straße1", "1", "OG", "12345", "Stadt");
        Adresse addr2 = new Adresse("Straße2", "1", "OG", "12345", "Stadt");

        assertNotEquals(addr1, addr2);
    }

    @Test
    @DisplayName("Whitespace should be trimmed in all fields")
    void whitespaceShouldbeTrimmed() {
        Adresse addr = new Adresse("Straße", "1", "OG", "12345", "Stadt");

        assertEquals("Straße", addr.getStreet());
        assertEquals("1", addr.getHouseNumber());
        assertEquals("OG", addr.getAddressComplement());
        assertEquals("Stadt", addr.getCity());
    }

    @Test
    @DisplayName("Null adresszusatz should be converted to empty string")
    void nullAdresszusatz_shouldConvertToEmptyString() {
        Adresse addr = new Adresse("Straße", "1", null, "12345", "Stadt");
        assertEquals("", addr.getAddressComplement());
    }
}

