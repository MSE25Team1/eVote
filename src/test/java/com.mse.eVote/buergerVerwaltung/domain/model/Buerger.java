package com.mse.eVote.buergerVerwaltung.domain.model;

import com.mse.eVote.buergerVerwaltung.domain.valueobjects.Adresse;
import com.mse.eVote.buergerVerwaltung.domain.valueobjects.Name;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class BuergerTest {

    // Happy-Path
    @Test
    void validBuerger_shouldCreateBuergerObject() {
        Name name = new Name("Max", "Mustermann");
        Adresse adresse = new Adresse("Musterstraße", "12a", "", "12345", "Musterstadt");
        assertDoesNotThrow(() -> new Buerger(name, adresse, "max@mustermann.de", LocalDate.of(1990, 1, 1), true, true));
    }

    // Edge-Cases
    @Test
    void minimalValidBuerger_shouldCreateBuergerObject() {
        Name name = new Name("A", "B");
        Adresse adresse = new Adresse("A", "1", "", "00001", "B");
        assertDoesNotThrow(() -> new Buerger(name, adresse, "a@b.de", LocalDate.of(1900, 1, 1), false, false));
    }

    // Negative Tests
    @Test
    void nullName_shouldThrowException() {
        Adresse adresse = new Adresse("Straße", "1", "", "12345", "Stadt");
        assertThrows(IllegalArgumentException.class, () -> new Buerger(null, adresse, "test@test.de", LocalDate.now(), false, false));
    }

    @Test
    void nullAdresse_shouldThrowException() {
        Name name = new Name("Max", "Mustermann");
        assertThrows(IllegalArgumentException.class, () -> new Buerger(name, null, "test@test.de", LocalDate.now(), false, false));
    }

    @Test
    void invalidEmail_shouldThrowException() {
        Name name = new Name("Max", "Mustermann");
        Adresse adresse = new Adresse("Straße", "1", "", "12345", "Stadt");
        assertThrows(IllegalArgumentException.class, () -> new Buerger(name, adresse, "invalid-email", LocalDate.now(), false, false));
    }

    @Test
    void nullGeburtsdatum_shouldThrowException() {
        Name name = new Name("Max", "Mustermann");
        Adresse adresse = new Adresse("Straße", "1", "", "12345", "Stadt");
        assertThrows(IllegalArgumentException.class, () -> new Buerger(name, adresse, "test@test.de", null, false, false));
    }

}