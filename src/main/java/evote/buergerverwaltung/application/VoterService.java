package evote.buergerverwaltung.application;

import evote.buergerverwaltung.application.dto.VoterCreateRequest;
import evote.buergerverwaltung.application.dto.VoterResponse;
import evote.buergerverwaltung.domain.model.Voter;
import evote.buergerverwaltung.domain.repository.VoterRepository;
import evote.buergerverwaltung.domain.valueobjects.Email;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Anwendungsschnittstelle der Bürgerverwaltung, die die Use-Cases orchestriert
 * und das Mapping an den {@link VoterAssembler} delegiert.
 */
@Service
public class VoterService {

    private final VoterRepository repo;
    private final VoterAssembler assembler;

    public VoterService(VoterRepository repo, VoterAssembler assembler) {
        this.repo = repo;
        this.assembler = assembler;
    }

    /**
     * Use Case: Registriert eine neue wahlberechtigte Person.
     */
    public VoterResponse create(VoterCreateRequest request) {
        Voter voter = assembler.toDomain(request);
        
        // Für das Bürgerverwaltungs-Frontend direkt verifizieren,
        // damit isVerified / registeredAt gesetzt sind.
        voter.verify();
        
        repo.save(voter);
        
        return assembler.toResponse(voter);
    }

    /**
     * Use Case: Lädt eine wahlberechtigte Person per ID.
     */
    public VoterResponse getById(String id) {
        Voter voter = findVoterOrThrow(id);
        return assembler.toResponse(voter);
    }

    public VoterResponse updateEmail(String voterId, String email) {
        Voter voter = findVoterOrThrow(voterId);

        try {
            voter.setEmail(new Email(email));
            repo.save(voter);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }

        return assembler.toResponse(voter);
    }

    private Voter findVoterOrThrow(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Bürger mit ID " + id + " nicht gefunden"));
    }
}
