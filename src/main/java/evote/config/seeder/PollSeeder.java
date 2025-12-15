package evote.config.seeder;

import evote.abstimmungsverwaltung.domain.model.Poll;
import evote.abstimmungsverwaltung.domain.repository.PollRepository;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * PollSeeder - Erstellt Sample-Abstimmungen beim Start der Anwendung
 * 
 * Responsibility:
 * - Erstellt Demo-Abstimmungen mit realistischen Daten
 * - Speichert sie im PollRepository
 * - Erstellt Abstimmungen mit verschiedenen Status (aktiv, beendet, etc.)
 * 
 * Diese Klasse wird vom DatabaseInitializer aufgerufen und sollte nur
 * während der Entwicklung/Demo-Phase verwendet werden.
 */
@Component
public class PollSeeder {

    private static final Logger logger = Logger.getLogger(PollSeeder.class.getName());
    private final PollRepository pollRepository;
    private final Clock clock;

    public PollSeeder(PollRepository pollRepository, Clock clock) {
        this.pollRepository = pollRepository;
        this.clock = clock;
    }

    /**
     * Erstellt und speichert Sample-Abstimmungen
     * 
     * @return Liste der erstellten Abstimmungen
     */
    public List<Poll> seedPolls() {
        List<Poll> polls = new ArrayList<>();

        // Abstimmung 1: Campus-Kantinenkonzept 2026 (AKTIV)
        LocalDateTime startTime1 = LocalDateTime.now(clock).minusDays(5);
        LocalDateTime endTime1 = LocalDateTime.now(clock).plusDays(10);
        
        Poll poll1 = new Poll(
                "POLL-CK-2026",
                "Campus-Kantinenkonzept 2026",
                List.of(
                        "OPTION-REGIONAL",
                        "OPTION-MIX",
                        "OPTION-FOODTRUCK"
                ),
                startTime1,
                endTime1,
                1000,
                clock
        );
        pollRepository.save(poll1);
        polls.add(poll1);
        logger.info("✓ Abstimmung erstellt: Campus-Kantinenkonzept 2026 (POLL-CK-2026) [AKTIV]");

        // Abstimmung 2: Parkplatzkonzept (AKTIV)
        LocalDateTime startTime2 = LocalDateTime.now(clock).minusDays(2);
        LocalDateTime endTime2 = LocalDateTime.now(clock).plusDays(14);
        
        Poll poll2 = new Poll(
                "POLL-PK-2026",
                "Neues Parkplatzkonzept auf dem Campus",
                List.of(
                        "OPTION-EXPANSION",
                        "OPTION-CARSHARING",
                        "OPTION-NAHVERKEHR"
                ),
                startTime2,
                endTime2,
                1000,
                clock
        );
        pollRepository.save(poll2);
        polls.add(poll2);
        logger.info("✓ Abstimmung erstellt: Parkplatzkonzept (POLL-PK-2026) [AKTIV]");

        // Abstimmung 3: Bibliotheksöffnungszeiten (BEENDET - für Test-Zwecke)
        LocalDateTime startTime3 = LocalDateTime.now(clock).minusDays(30);
        LocalDateTime endTime3 = LocalDateTime.now(clock).minusDays(5);
        
        Poll poll3 = new Poll(
                "POLL-BZ-2025",
                "Bibliotheksöffnungszeiten im WS 2025/26",
                List.of(
                        "OPTION-EXTENDED",
                        "OPTION-CURRENT",
                        "OPTION-REDUCED"
                ),
                startTime3,
                endTime3,
                1000,
                clock
        );
        poll3.close(); // Manuell geschlossen
        pollRepository.save(poll3);
        polls.add(poll3);
        logger.info("✓ Abstimmung erstellt: Bibliotheksöffnungszeiten (POLL-BZ-2025) [BEENDET]");

        // Abstimmung 4: Semesterticket (AKTIV - kommende Abstimmung)
        LocalDateTime startTime4 = LocalDateTime.now(clock).plusDays(7);
        LocalDateTime endTime4 = LocalDateTime.now(clock).plusDays(21);
        
        Poll poll4 = new Poll(
                "POLL-ST-2026",
                "Höhe des Semesterticketpreises 2026",
                List.of(
                        "OPTION-CURRENT",
                        "OPTION-PLUS10",
                        "OPTION-PLUS20"
                ),
                startTime4,
                endTime4,
                1000,
                clock
        );
        pollRepository.save(poll4);
        polls.add(poll4);
        logger.info("✓ Abstimmung erstellt: Semesterticket (POLL-ST-2026) [KOMMEND]");

        logger.info("✓ PollSeeder abgeschlossen: " + polls.size() + " Abstimmungen erstellt");
        return polls;
    }
}

