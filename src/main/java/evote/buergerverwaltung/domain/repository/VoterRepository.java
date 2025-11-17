package evote.buergerverwaltung.domain.repository;

import evote.buergerverwaltung.domain.model.Voter;

import java.util.Optional;

/**
 * VoterRepository - Repository Interface
 * 
 * Definiert den Persistenz-Zugriff auf Voter-Aggregate.
 * Implementierungen können verschiedene Backends verwenden (JPA, Mongo, etc.).
 * 
 * Anti-Corruption Layer Prinzip:
 * - Domain-Code hängt von diesem Interface ab (nicht von konkreten Implementierungen)
 * - Ermöglicht einfache Migration zwischen Persistence-Technologien
 */
public interface VoterRepository {
    
    /**
     * Speichert einen Voter (neu oder update)
     */
    void save(Voter voter);
    
    /**
     * Findet einen Voter nach seiner voterId
     */
    Optional<Voter> findById(String voterId);
    
    /**
     * Findet einen Voter nach Email (für Login/Lookup)
     */
    Optional<Voter> findByEmail(String email);
    
    /**
     * Findet alle Voter in einem bestimmten Wahlkreis
     */
    Iterable<Voter> findByWahlkreis(String wahlkreis);
    
    /**
     * Löscht einen Voter (für Tests/Admin-Operationen)
     */
    void delete(String voterId);
}

