package com.mse.eVote.buergerVerwaltung.domain.repository;

import com.mse.eVote.buergerVerwaltung.domain.model.Voter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

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
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String voterId) {
        store.remove(voterId);
    }
}
