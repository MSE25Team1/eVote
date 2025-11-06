package de.buergerverwaltung.domain.service;

import de.buergerverwaltung.domain.model.Buerger;
import de.buergerverwaltung.domain.repository.BuergerRepository;
import de.buergerverwaltung.validation.BuergerValidator;

public class BuergerService {

    private final BuergerRepository repository;
    private final BuergerValidator validator;

    public BuergerService(BuergerRepository repository, BuergerValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public void registrieren(Buerger buerger) {
        if (!validator.isValid(buerger)) {
            throw new IllegalArgumentException("Ungültige Bürgerdaten!");
        }
        repository.speichern(buerger);
    }

    public int anzahlRegistrierteBuerger() {
        return repository.findAll().size();
    }
}
