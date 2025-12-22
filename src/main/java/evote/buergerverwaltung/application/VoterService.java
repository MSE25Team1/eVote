package evote.buergerverwaltung.application;

import evote.buergerverwaltung.application.dto.VoterCreateRequest;
import evote.buergerverwaltung.application.dto.VoterResponse;
import evote.buergerverwaltung.domain.model.Voter;
import evote.buergerverwaltung.domain.repository.VoterRepository;
import evote.buergerverwaltung.domain.valueobjects.Email;
import org.springframework.stereotype.Service;

/**
 * VoterService - Application Service
 * 
 * Responsibility: Orchestrates use cases for voter management.
 * Delegates mapping to VoterAssembler, keeping this layer thin.
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
     * Use Case: Register a new voter
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
     * Use Case: Retrieve voter by ID
     */
    public VoterResponse getById(String id) {
        Voter voter = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Voter mit ID " + id + " nicht gefunden"));
        return assembler.toResponse(voter);
    }

    public VoterResponse updateEmail(String voterId, String email) {
        Voter voter = repo.findById(voterId)
                .orElseThrow(() -> new IllegalArgumentException("Bürger nicht gefunden!"));

        voter.setEmail(new Email(email));
        repo.save(voter);
        return assembler.toResponse(voter);
    }
}

