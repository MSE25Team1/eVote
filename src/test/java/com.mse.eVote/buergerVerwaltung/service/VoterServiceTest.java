package com.mse.eVote.buergerVerwaltung.service;

import com.mse.eVote.buergerVerwaltung.api.VoterCreateRequest;
import com.mse.eVote.buergerVerwaltung.domain.repository.InMemoryVoterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VoterServiceTest {

    private VoterService service;
    private InMemoryVoterRepository repo;

    @BeforeEach
    void setup() {
        repo = new InMemoryVoterRepository();
        service = new VoterService(repo);
    }

    private VoterCreateRequest sampleRequest() {
        VoterCreateRequest r = new VoterCreateRequest();
        r.vorname = "Max";
        r.nachname = "Mustermann";
        r.email = "max@test.de";
        r.strasse = "Musterstraße 12";
        r.plz = "12345";
        r.ort = "Berlin";
        r.wahlkreis = "WK1";
        return r;
    }

    @Test
    @DisplayName("create() should map DTO to domain and return a valid response")
    void create_shouldReturnValidResponse() {
        var req = sampleRequest();
        var res = service.create(req);

        assertNotNull(res.id);
        assertEquals("Max", res.vorname);
        assertEquals("Mustermann", res.nachname);
        assertEquals("max@test.de", res.email);
        assertEquals("Musterstraße 12", res.strasse);
        assertEquals("12345", res.plz);
        assertEquals("Berlin", res.ort);
        assertEquals("WK1", res.wahlkreis);

        assertTrue(repo.findById(res.id).isPresent());
    }

    @Test
    @DisplayName("getById() should throw IllegalArgumentException when voter does not exist")
    void getById_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.getById("unknown-id"));
    }
}
