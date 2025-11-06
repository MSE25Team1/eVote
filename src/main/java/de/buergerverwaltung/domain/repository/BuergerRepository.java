package de.buergerverwaltung.domain.repository;

import de.buergerverwaltung.domain.model.Buerger;
import java.util.ArrayList;
import java.util.List;

public class BuergerRepository {

    private final List<Buerger> datenbank = new ArrayList<>();

    public void speichern(Buerger b) {
        datenbank.add(b);
    }

    public List<Buerger> findAll() {
        return new ArrayList<>(datenbank);
    }

    public void clear() {
        datenbank.clear();
    }
}
