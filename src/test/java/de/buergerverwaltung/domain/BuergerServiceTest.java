package de.buergerverwaltung.domain;

import de.buergerverwaltung.domain.model.Buerger;
import de.buergerverwaltung.domain.repository.BuergerRepository;
import de.buergerverwaltung.domain.service.BuergerService;
import de.buergerverwaltung.validation.BuergerValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuergerServiceTest {

    private BuergerRepository repo;
    private BuergerService service;

    @BeforeEach
    void setup() {
        repo = new BuergerRepository();
        service = new BuergerService(repo, new BuergerValidator());
    }

    @Test
    void registerValidBuerger_shouldIncreaseCount() {
        service.registrieren(new Buerger("Lisa", "Schmidt", "lisa@web.de", "40210"));
        assertEquals(1, service.anzahlRegistrierteBuerger());
    }

    @Test
    void registerInvalidBuerger_shouldThrowException() {
        Buerger invalid = new Buerger("Lisa", "Schmidt", "lisaweb.de", "40210");
        assertThrows(IllegalArgumentException.class, () -> service.registrieren(invalid));
    }
}
