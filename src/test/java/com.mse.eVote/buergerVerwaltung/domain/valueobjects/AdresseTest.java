package com.mse.eVote.buergerVerwaltung.domain.valueobjects;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AdresseTest {

    // Happy-Path
    @Test
    void validAdresse_shouldCreateAdresseObject() {
        assertDoesNotThrow(() -> new Adresse("Musterstraße", "12a", "EG rechts", "12345", "Musterstadt"));
    }

    // Edge-Cases
    @Test
    void minimalValidFields_shouldCreateAdresseObject() {
        assertDoesNotThrow(() -> new Adresse("A", "1", "", "00001", "B"));
    }

    @Test
    void addressWithLongFields_shouldCreateAdresseObject() {
        assertDoesNotThrow(() -> new Adresse("SehrLangeStraßenbezeichnungMitSonderzeichen", "999", "Penthouse, 3. OG", "99999", "Großstadt"));
    }

    // Negative Tests
    @Test
    void nullStreet_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Adresse(null, "1", "", "12345", "Stadt"));
    }

    @Test
    void emptyHouseNumber_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Adresse("Straße", "", "", "12345", "Stadt"));
    }

    @Test
    void invalidPLZ_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Adresse("Straße", "1", "", "12", "Stadt"));
    }

    @Test
    void nullCity_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Adresse("Straße", "1", "", "12345", null));
    }

    @Test
    void emptyFields_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Adresse("", "", "", "", ""));
    }
}