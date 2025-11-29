package evote.buergerverwaltung.infrastructure.persistence;

import evote.buergerverwaltung.domain.model.Voter;
import evote.buergerverwaltung.domain.repository.VoterRepository;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * InMemoryVoterRepository - Persistence Adapter (Infrastructure Layer)
 * 
 * Responsibility: Implements VoterRepository interface using in-memory storage.
 * This is an infrastructure concern - the domain only knows about the interface.
 */
@Component
public class InMemoryVoterRepository implements VoterRepository {

    private final Map<String, Voter> store = new HashMap<>();

    @Override
    public void save(Voter voter) {
        store.put(voter.getVoterId(), voter);
    }

    @Override
    public Optional<Voter> findById(String voterId) {
        return Optional.ofNullable(store.get(voterId));
    }

    @Override
    public Optional<Voter> findByEmail(String email) {
        return store.values().stream()
                .filter(v -> v.getEmail().toString().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public Iterable<Voter> findByWahlkreis(String wahlkreis) {
        return store.values().stream()
                .filter(v -> v.getWahlkreis().equalsIgnoreCase(wahlkreis))
                .toList();
    }

    @Override
    public void delete(String voterId) {
        store.remove(voterId);
    }
}

