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

        VoterCreateRequest.NameRequest name = new VoterCreateRequest.NameRequest();
        name.vorname = "Max";
        name.nachname = "Mustermann";
        r.name = name;

        r.email = "max@test.de";

        VoterCreateRequest.AdresseRequest adresse = new VoterCreateRequest.AdresseRequest();
        adresse.strasse = "Musterstraße 12";
        adresse.plz = "12345";
        adresse.ort = "Berlin";
        r.adresse = adresse;

        r.wahlkreis = "WK1";
        return r;
    }

    @Test
    @DisplayName("create() should map DTO to domain and return a valid response")
    void create_shouldReturnValidResponse() {
        var req = sampleRequest();
        var res = service.create(req);

        assertNotNull(res.id);
        assertEquals("Max", res.name.firstName);
        assertEquals("Mustermann", res.name.lastName);
        assertEquals("Max Mustermann", res.name.fullName);
        assertEquals("max@test.de", res.email);

        assertEquals("Musterstraße", res.address.street);
        assertEquals("12", res.address.houseNumber);
        assertEquals("12345", res.address.postalCode);
        assertEquals("Berlin", res.address.city);
        assertEquals("Musterstraße 12, 12345 Berlin", res.address.formatted);

        assertEquals("WK1", res.district);
        assertTrue(res.verified);

        assertTrue(repo.findById(res.id).isPresent());
    }

    @Test
    @DisplayName("getById() should throw IllegalArgumentException when voter does not exist")
    void getById_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.getById("unknown-id"));
    }
}
