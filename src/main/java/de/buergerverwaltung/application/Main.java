package de.buergerverwaltung.application;

import de.buergerverwaltung.domain.model.Buerger;
import de.buergerverwaltung.domain.repository.BuergerRepository;
import de.buergerverwaltung.domain.service.BuergerService;
import de.buergerverwaltung.validation.BuergerValidator;

public class Main {
    public static void main(String[] args) {
        BuergerRepository repo = new BuergerRepository();
        BuergerValidator validator = new BuergerValidator();
        BuergerService service = new BuergerService(repo, validator);

        Buerger anna = new Buerger("Anna", "Meier", "anna.meier@example.com", "10115");
        service.registrieren(anna);

        System.out.println("Registrierte Bürger:");
        repo.findAll().forEach(b -> System.out.println("- " + b.getVorname() + " " + b.getNachname()));
    }
}
