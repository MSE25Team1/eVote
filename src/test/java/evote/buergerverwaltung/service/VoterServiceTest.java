package evote.buergerverwaltung.service;

import evote.buergerverwaltung.application.VoterAssembler;
import evote.buergerverwaltung.application.VoterService;
import evote.buergerverwaltung.application.dto.VoterCreateRequest;
import evote.buergerverwaltung.infrastructure.persistence.InMemoryVoterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class VoterServiceTest {

    private VoterService service;
    private InMemoryVoterRepository repo;

    @BeforeEach
    void setup() {
        repo = new InMemoryVoterRepository();
        VoterAssembler assembler = new VoterAssembler();
        service = new VoterService(repo, assembler);
    }

    private VoterCreateRequest sampleRequest() {
        var name = new VoterCreateRequest.NameRequest("Max", "Mustermann");
        var adresse = new VoterCreateRequest.AdresseRequest("Musterstraße 12", "12345", "Berlin");
        return new VoterCreateRequest(name, "max@test.de", adresse, "WK1");
    }

    @Test
    @DisplayName("create() should map DTO to domain and return a valid response")
    void create_shouldReturnValidResponse() {
        var req = sampleRequest();
        var res = service.create(req);

        assertNotNull(res.id());
        assertEquals("Max", res.name().firstName());
        assertEquals("Mustermann", res.name().lastName());
        assertEquals("Max Mustermann", res.name().fullName());
        assertEquals("max@test.de", res.email());

        assertEquals("Musterstraße", res.address().street());
        assertEquals("12", res.address().houseNumber());
        assertEquals("12345", res.address().postalCode());
        assertEquals("Berlin", res.address().city());
        assertEquals("Musterstraße 12, 12345 Berlin", res.address().formatted());

        assertEquals("WK1", res.district());
        assertTrue(res.verified());

        assertTrue(repo.findById(res.id()).isPresent());
    }

    @Test
    @DisplayName("getById() should throw 404 when voter does not exist")
    void getById_shouldThrowException() {
        var ex = assertThrows(ResponseStatusException.class,
                () -> service.getById("unknown-id"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
