package com.mse.eVote.buergerVerwaltung.service;

import com.mse.eVote.buergerVerwaltung.api.VoterCreateRequest;
import com.mse.eVote.buergerVerwaltung.api.VoterResponse;
import com.mse.eVote.buergerVerwaltung.domain.model.Voter;
import com.mse.eVote.buergerVerwaltung.domain.repository.VoterRepository;
import com.mse.eVote.buergerVerwaltung.domain.valueobjects.Adresse;
import com.mse.eVote.buergerVerwaltung.domain.valueobjects.Email;
import com.mse.eVote.buergerVerwaltung.domain.valueobjects.Name;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class VoterService {

    private final VoterRepository repo;

    public VoterService(VoterRepository repo) {
        this.repo = repo;
    }

    public VoterResponse create(VoterCreateRequest req) {
        // API -> Domain
        Name name = new Name(req.vorname, req.nachname);
        Adresse adresse = mapAdresse(req);
        Email email = new Email(req.email);

        // TODO: Geburtsdatum kommt aktuell nicht vom Frontend.
        // Für die Demo setzen wir "mindestens 18 Jahre".
        LocalDate geburtsdatum = LocalDate.now().minusYears(18);

        Voter voter = Voter.register(name, adresse, email, geburtsdatum, req.wahlkreis);

        // Für das Bürgerverwaltungs-Frontend direkt verifizieren,
        // damit isVerified / registeredAt gesetzt sind.
        voter.verify();

        repo.save(voter);

        return mapResponse(voter);
    }

    public VoterResponse getById(String id) {
        Voter voter = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Voter mit ID " + id + " nicht gefunden"));
        return mapResponse(voter);
    }

    // ----------------- Mapping-Helfer -----------------

    private Adresse mapAdresse(VoterCreateRequest req) {
        if (req.strasse == null || req.strasse.isBlank()) {
            throw new IllegalArgumentException("Straße darf nicht leer sein");
        }

        String trimmed = req.strasse.trim();
        String[] parts = trimmed.split("\\s+");

        if (parts.length < 2) {
            // Fallback: alles als Straße, Hausnummer "1"
            return new Adresse(trimmed, "1", "", req.plz, req.ort);
        }

        String houseNumber = parts[parts.length - 1];
        String street = String.join(" ", java.util.Arrays.copyOf(parts, parts.length - 1));

        return new Adresse(street, houseNumber, "", req.plz, req.ort);
    }

    private VoterResponse mapResponse(Voter voter) {
        VoterResponse res = new VoterResponse();
        res.id = voter.getVoterId();
        res.vorname = voter.getName().firstName();
        res.nachname = voter.getName().lastName();
        res.email = voter.getEmail().toString();

        res.strasse = voter.getAdresse().getStreet() + " " + voter.getAdresse().getHouseNumber();
        res.plz = voter.getAdresse().getPostalCode();
        res.ort = voter.getAdresse().getCity();

        res.wahlkreis = voter.getWahlkreis();
        res.registeredAt = voter.getRegisteredAt() != null
                ? voter.getRegisteredAt().toString()
                : null;
        res.isVerified = voter.isVerified();
        return res;
    }
}
