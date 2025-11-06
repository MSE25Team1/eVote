package de.buergerverwaltung.validation;

import de.buergerverwaltung.domain.model.Buerger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BuergerValidatorTest {

    private final BuergerValidator validator = new BuergerValidator();

    @Test
    void validBuerger_shouldPass() {
        Buerger b = new Buerger("Max", "Muster", "max@muster.de", "12345");
        assertTrue(validator.isValid(b));
    }

    @Test
    void invalidEmail_shouldFail() {
        Buerger b = new Buerger("Max", "Muster", "max.muster.de", "12345");
        assertFalse(validator.isValid(b));
    }

    @Test
    void invalidPLZ_shouldFail() {
        Buerger b = new Buerger("Max", "Muster", "max@muster.de", "ABCDE");
        assertFalse(validator.isValid(b));
    }

    @Test
    void emptyVorname_shouldFail() {
        Buerger b = new Buerger("", "Muster", "max@muster.de", "12345");
        assertFalse(validator.isValid(b));
    }

    @Test
    void nullNachname_shouldFail() {
        Buerger b = new Buerger("Max", null, "max@muster.de", "12345");
        assertFalse(validator.isValid(b));
    }
}
