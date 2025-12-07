package evote.stimmvergabe.domain.repository;

import evote.stimmvergabe.domain.model.Vote;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface VoteRepository {

    /**
     * Persistiert oder aktualisiert eine Stimme.
     */
    Vote save(Vote vote);

    /**
     * Findet eine Stimme anhand ihrer technischen ID.
     */
    Optional<Vote> findById(String voteId);

    /**
     * Alle Stimmen zu einer bestimmten Abstimmung (Poll).
     */
    List<Vote> findAllByPollId(String pollId);

    /**
     * Für Idempotenz: gleiche correlationId → gleiche Vote.
     */
    Optional<Vote> findByCorrelationId(String correlationId);

    /** Anzahl der aktuell gespeicherten Votes – nur für Tests verwendet. */
    int count();

    List<Object> findAll();
}
