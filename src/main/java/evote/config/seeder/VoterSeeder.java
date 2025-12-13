package evote.config.seeder;

import evote.buergerverwaltung.domain.model.Voter;
import evote.buergerverwaltung.domain.repository.VoterRepository;
import evote.buergerverwaltung.domain.valueobjects.Adresse;
import evote.buergerverwaltung.domain.valueobjects.Email;
import evote.buergerverwaltung.domain.valueobjects.Name;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * VoterSeeder - Erstellt Sample-Wähler beim Start der Anwendung
 * 
 * Responsibility:
 * - Erstellt Demo-Wähler mit validen Daten
 * - Speichert sie im VoterRepository
 * - Verifiziert die Wähler, um sie abstimmungsberechtigt zu machen
 * 
 * Diese Klasse wird vom DatabaseInitializer aufgerufen und sollte nur
 * während der Entwicklung/Demo-Phase verwendet werden.
 */
@Component
public class VoterSeeder {

    private static final Logger logger = Logger.getLogger(VoterSeeder.class.getName());
    private final VoterRepository voterRepository;

    public VoterSeeder(VoterRepository voterRepository) {
        this.voterRepository = voterRepository;
    }

    /**
     * Erstellt und speichert Sample-Wähler
     * 
     * @return Liste der erstellten Wähler
     */
    public List<Voter> seedVoters() {
        List<Voter> voters = new ArrayList<>();

        // Wähler 1: Max Mustermann
        Voter voter1 = Voter.register(
                new Name("Max", "Mustermann"),
                new Adresse("Musterstraße", "12", null, "12345", "Musterstadt"),
                new Email("max.mustermann@example.org"),
                LocalDate.of(1990, 5, 15),
                "101"
        );
        voter1.verify();
        voterRepository.save(voter1);
        voters.add(voter1);
        logger.info("✓ Wähler erstellt: Max Mustermann (ID: " + voter1.getVoterId() + ")");

        // Wähler 2: Anna Schmidt
        Voter voter2 = Voter.register(
                new Name("Anna", "Schmidt"),
                new Adresse("Hauptstraße", "45", "Wohnung 3", "10115", "Berlin"),
                new Email("anna.schmidt@example.de"),
                LocalDate.of(1985, 3, 22),
                "101"
        );
        voter2.verify();
        voterRepository.save(voter2);
        voters.add(voter2);
        logger.info("✓ Wähler erstellt: Anna Schmidt (ID: " + voter2.getVoterId() + ")");

        // Wähler 3: Peter Müller
        Voter voter3 = Voter.register(
                new Name("Peter", "Müller"),
                new Adresse("Königstraße", "78", null, "50667", "Köln"),
                new Email("peter.mueller@example.com"),
                LocalDate.of(1992, 7, 8),
                "102"
        );
        voter3.verify();
        voterRepository.save(voter3);
        voters.add(voter3);
        logger.info("✓ Wähler erstellt: Peter Müller (ID: " + voter3.getVoterId() + ")");

        // Wähler 4: Lisa Wagner
        Voter voter4 = Voter.register(
                new Name("Lisa", "Wagner"),
                new Adresse("Parkstraße", "23", "Penthouse", "80331", "München"),
                new Email("lisa.wagner@example.org"),
                LocalDate.of(1988, 11, 30),
                "102"
        );
        voter4.verify();
        voterRepository.save(voter4);
        voters.add(voter4);
        logger.info("✓ Wähler erstellt: Lisa Wagner (ID: " + voter4.getVoterId() + ")");

        // Wähler 5: Thomas Becker
        Voter voter5 = Voter.register(
                new Name("Thomas", "Becker"),
                new Adresse("Waldweg", "5", null, "69115", "Heidelberg"),
                new Email("thomas.becker@example.de"),
                LocalDate.of(1980, 2, 14),
                "103"
        );
        voter5.verify();
        voterRepository.save(voter5);
        voters.add(voter5);
        logger.info("✓ Wähler erstellt: Thomas Becker (ID: " + voter5.getVoterId() + ")");

        logger.info("✓ VoterSeeder abgeschlossen: " + voters.size() + " Wähler erstellt");
        return voters;
    }
}

