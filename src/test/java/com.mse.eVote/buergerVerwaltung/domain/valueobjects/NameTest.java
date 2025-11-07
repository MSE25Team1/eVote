package com.mse.eVote.buergerVerwaltung.domain.valueobjects;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NameTest {

    // Happy-Path
    @Test
    void validName_shouldCreateNameObject() {
        assertDoesNotThrow(() -> new Name("Max", "Mustermann"));
    }

    // Edge-Cases
    @Test
    void minimalLengthNames_shouldCreateNameObject() {
        assertDoesNotThrow(() -> new Name("A", "B"));
    }

    // Negative Tests
    @Test
    void nullFirstName_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Name(null, "Mustermann"));
    }

    @Test
    void emptyLastName_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Name("Max", ""));
    }

    @Test
    void nameWithDigits_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Name("Max1", "Mustermann"));
    }

    @Test
    void nameWithSpecialCharacters_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Name("Max", "Muster@mann"));
    }

    @Test
    void nameWithUmlauts_shouldCreateNameObject() {
        assertDoesNotThrow(() -> new Name("Jürgen", "Müller"));
    }
}