package evote.buergerverwaltung.domain.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Name - Value Object Tests")
class NameTest {

    // ============ Happy-Path Tests ============

    @Test
    @DisplayName("Valid name should create successfully")
    void validName_shouldCreateNameObject() {
        assertDoesNotThrow(() -> new Name("Max", "Mustermann"));
    }

    @Test
    @DisplayName("Minimal length names (single character) should create successfully")
    void minimalLengthNames_shouldCreateNameObject() {
        assertDoesNotThrow(() -> new Name("A", "B"));
    }

    @Test
    @DisplayName("Very long names should create successfully")
    void veryLongNames_shouldCreateNameObject() {
        assertDoesNotThrow(() -> new Name("Maximilian", "Mustermann-Müller-Schmidt"));
    }

    @Test
    @DisplayName("Names with umlauts should create successfully")
    void namesWithUmlauts_shouldCreateNameObject() {
        assertDoesNotThrow(() -> new Name("Jürgen", "Müller"));
    }

    @Test
    @DisplayName("Names with compound parts (hyphens) should create successfully")
    void compoundNames_shouldCreateNameObject() {
        assertDoesNotThrow(() -> new Name("Jean-Pierre", "De-La-Cruz"));
    }

    @Test
    @DisplayName("Names with multiple compound parts should create successfully")
    void multipleCompoundNames_shouldCreateNameObject() {
        assertDoesNotThrow(() -> new Name("Maria-Luise", "Müller-Schmidt"));
    }

    @Test
    @DisplayName("Names with spaces should create successfully")
    void namesWithSpaces_shouldCreateNameObject() {
        assertDoesNotThrow(() -> new Name("Marie Louise", "De Berg"));
    }

    // ============ Null/Empty Tests (Consolidated) ============

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t"})
    @DisplayName("Empty or whitespace-only first name should throw IllegalArgumentException")
    void emptyOrWhitespaceFirstName_shouldThrowException(String invalidVorname) {
        assertThrows(IllegalArgumentException.class,
            () -> new Name(invalidVorname, "Mustermann"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t"})
    @DisplayName("Empty or whitespace-only last name should throw IllegalArgumentException")
    void emptyOrWhitespaceLastName_shouldThrowException(String invalidNachname) {
        assertThrows(IllegalArgumentException.class,
            () -> new Name("Max", invalidNachname));
    }

    @Test
    @DisplayName("Null first name should throw IllegalArgumentException")
    void nullFirstName_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
            () -> new Name(null, "Mustermann"));
    }

    @Test
    @DisplayName("Null last name should throw IllegalArgumentException")
    void nullLastName_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
            () -> new Name("Max", null));
    }

    // ============ Format Validation Tests ============

    @ParameterizedTest
    @CsvSource({
        "max,Mustermann",         // Lowercase start
        "Max,mustermann",         // Lowercase last name
        "123,Mustermann",         // Numbers in first name
        "Max1,Mustermann",        // Number in first name
        "Max,Mustermann2",        // Number in last name
        "Max,Muster@mann",        // Special character @ in last name
        "Max#,Mustermann",        // Special character # in first name
        "Max,Muster$mann",        // Special character $ in last name
        "Max,Muster%mann"         // Special character % in last name
    })
    @DisplayName("Invalid name formats should throw IllegalArgumentException")
    void invalidNameFormats_shouldThrowException(String invalidVorname, String invalidNachname) {
        assertThrows(IllegalArgumentException.class,
            () -> new Name(invalidVorname, invalidNachname));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Max_Mustermann",     // Underscore
        "Max/Mustermann",     // Slash
        "Max\\Mustermann",    // Backslash
        "Max.Mustermann",     // Dot in middle (only allowed at end per spec)
    })
    @DisplayName("Names with invalid special characters should throw IllegalArgumentException")
    void namesWithInvalidSpecialCharacters_shouldThrowException(String invalidName) {
        assertThrows(IllegalArgumentException.class,
            () -> new Name(invalidName, "Mustermann"));
    }

    // ============ German Umlauts Tests ============

    @ParameterizedTest
    @ValueSource(strings = {"Äpfel", "Ölbaum", "Über", "Ängstlich"})
    @DisplayName("German uppercase umlauts should create successfully")
    void germanUppercaseUmlauts_shouldCreateNameObject(String firstName) {
        assertDoesNotThrow(() -> new Name(firstName, "Schmidt"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Müller", "Köhler", "Äsch", "Özdemir"})
    @DisplayName("German lowercase umlauts should create successfully")
    void germanLowercaseUmlauts_shouldCreateNameObject(String lastName) {
        assertDoesNotThrow(() -> new Name("Max", lastName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Straße", "Schöß", "Überraschung"})
    @DisplayName("German eszett (ß) should create successfully")
    void germanEszett_shouldCreateNameObject(String name) {
        assertDoesNotThrow(() -> new Name(name, "Schmidt"));
    }

    // ============ Value Object Semantics Tests ============

    @Test
    @DisplayName("Two names with same values should be equal")
    void identicalNames_shouldBeEqual() {
        Name name1 = new Name("Max", "Mustermann");
        Name name2 = new Name("Max", "Mustermann");

        assertEquals(name1, name2);
        assertEquals(name1.hashCode(), name2.hashCode());
    }

    @Test
    @DisplayName("Two names with different first names should not be equal")
    void differentFirstNames_shouldNotBeEqual() {
        Name name1 = new Name("Max", "Mustermann");
        Name name2 = new Name("Anna", "Mustermann");

        assertNotEquals(name1, name2);
    }

    @Test
    @DisplayName("Two names with different last names should not be equal")
    void differentLastNames_shouldNotBeEqual() {
        Name name1 = new Name("Max", "Mustermann");
        Name name2 = new Name("Max", "Schmidt");

        assertNotEquals(name1, name2);
    }

    @Test
    @DisplayName("Whitespace should be trimmed")
    void whitespaceShouldbeTrimmed() {
        Name name = new Name("Max", "Mustermann");

        assertEquals("Max", name.getFirstName());
        assertEquals("Mustermann", name.getLastName());
    }

    @Test
    @DisplayName("toString should return concatenated name")
    void toStringFormat_shouldReturnConcatenated() {
        Name name = new Name("Max", "Mustermann");
        assertEquals("Max Mustermann", name.toString());
    }

    // ============ Real-World Edge Cases ============

    @Test
    @DisplayName("Common German names with hyphens and umlauts should work")
    void realWorldNames_shouldCreateSuccessfully() {
        assertDoesNotThrow(() -> new Name("Jose-Maria", "Garcia-Lopez"));
        assertDoesNotThrow(() -> new Name("Marie-Therese", "Beaumont"));
        assertDoesNotThrow(() -> new Name("Jean-Paul", "Schmidt"));
        assertDoesNotThrow(() -> new Name("Ulrich", "Müller-Schmid"));
    }
}