package evote.config.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.logging.Logger;

/**
 * SeederConfiguration - Orchestriert die Ausführung der Seeder beim Start
 * 
 * Responsibility:
 * - Definiert einen CommandLineRunner Bean, der nach dem Start aufgerufen wird
 * - Orchestriert die Ausführung von VoterSeeder und PollSeeder
 * - Logged die Seed-Aktivitäten
 * 
 * Diese Configuration wird automatisch von Spring erkannt und ausgeführt,
 * wenn die Anwendung startet. Die Seeder füllen die In-Memory-Repositories
 * mit Demo-Daten, damit die Frontend-Anwendung gegen realistische Daten arbeiten kann.
 */
@Configuration
public class SeederConfiguration {

    private static final Logger logger = Logger.getLogger(SeederConfiguration.class.getName());

    @Bean
    public CommandLineRunner seedDatabase(VoterSeeder voterSeeder, PollSeeder pollSeeder) {
        return args -> {
            logger.info("================================================");
            logger.info("🌱 Starte Database Seeding...");
            logger.info("================================================");
            
            // Wähler erstellen
            voterSeeder.seedVoters();
            
            // Abstimmungen erstellen
            pollSeeder.seedPolls();
            
            logger.info("================================================");
            logger.info("✅ Database Seeding abgeschlossen!");
            logger.info("================================================");
            logger.info("");
            logger.info("Demo-Daten sind nun verfügbar:");
            logger.info("  - Wähler: VOTER-001 (Max Mustermann)");
            logger.info("  - Abstimmung: POLL-CK-2026 (Campus-Kantinenkonzept)");
            logger.info("");
        };
    }
}

