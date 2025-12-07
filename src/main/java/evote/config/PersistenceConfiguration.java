package evote.config;

import evote.stimmvergabe.domain.repository.VoteRepository;
import evote.stimmvergabe.infrastructure.persistence.InMemoryVoteRepository;
import evote.stimmvergabe.application.DomainEventPublisher;
import evote.buergerverwaltung.domain.repository.VoterRepository;
import evote.buergerverwaltung.infrastructure.persistence.InMemoryVoterRepository;
import evote.abstimmungsverwaltung.domain.repository.PollRepository;
import evote.abstimmungsverwaltung.infrastructure.persistence.InMemoryPollRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Configuration für die Persistenz-Layer.
 * Registriert die In-Memory Repository-Implementierungen als Spring Beans.
 */
@Configuration
public class PersistenceConfiguration {

    @Bean
    public VoteRepository voteRepository() {
        return new InMemoryVoteRepository();
    }

    @Bean
    public VoterRepository voterRepository() {
        return new InMemoryVoterRepository();
    }

    @Bean
    public PollRepository pollRepository() {
        return new InMemoryPollRepository();
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public DomainEventPublisher domainEventPublisher() {
        return new DomainEventPublisher();
    }
}

