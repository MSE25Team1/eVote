package evote.buergerverwaltung.domain.repository;

import evote.buergerverwaltung.domain.model.Voter;
import evote.buergerverwaltung.domain.valueobjects.Adresse;
import evote.buergerverwaltung.domain.valueobjects.Email;
import evote.buergerverwaltung.domain.valueobjects.Name;
import evote.buergerverwaltung.infrastructure.persistence.InMemoryVoterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryVoterRepositoryTest {

    private InMemoryVoterRepository repo;
    private Voter voter;

    @BeforeEach
    void setup() {
        repo = new InMemoryVoterRepository();

        voter = Voter.register(
                new Name("Max", "Mustermann"),
                new Adresse("Straße", "1", "", "12345", "Stadt"),
                new Email("max@test.de"),
                LocalDate.of(1990, 1, 1),
                "WK1"
        );
        voter.verify();
    }

    @Test
    @DisplayName("save() should persist voter")
    void save_shouldStoreVoter() {
        repo.save(voter);
        assertTrue(repo.findById(voter.getVoterId()).isPresent());
    }

    @Test
    @DisplayName("findById() should return empty when not found")
    void findById_shouldReturnEmpty() {
        assertTrue(repo.findById("unknown").isEmpty());
    }

    @Test
    @DisplayName("findByEmail() should find voter by case-insensitive search")
    void findByEmail_shouldFindVoter() {
        repo.save(voter);
        assertTrue(repo.findByEmail("MAX@TEST.DE").isPresent());
    }

    @Test
    @DisplayName("findByWahlkreis() should return all voters in district")
    void findByWahlkreis_shouldReturnVoters() {
        repo.save(voter);
        var found = repo.findByWahlkreis("wk1");
        assertTrue(found.iterator().hasNext());
    }

    @Test
    @DisplayName("delete() should remove voter")
    void delete_shouldRemoveVoter() {
        repo.save(voter);
        repo.delete(voter.getVoterId());
        assertTrue(repo.findById(voter.getVoterId()).isEmpty());
    }
}
