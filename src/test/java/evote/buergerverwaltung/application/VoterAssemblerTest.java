package evote.buergerverwaltung.application;

import evote.buergerverwaltung.application.dto.VoterCreateRequest;
import evote.buergerverwaltung.application.dto.VoterResponse;
import evote.buergerverwaltung.domain.model.Voter;
import evote.buergerverwaltung.domain.valueobjects.Adresse;
import evote.buergerverwaltung.domain.valueobjects.Email;
import evote.buergerverwaltung.domain.valueobjects.Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VoterAssembler - DTO Mapping Tests")
class VoterAssemblerTest {

    private VoterAssembler assembler;

    @BeforeEach
    void setUp() {
        assembler = new VoterAssembler();
    }

    // ============ toDomain() Tests ============

    @Test
    @DisplayName("toDomain should map VoterCreateRequest to Voter domain model")
    void toDomain_shouldMapRequestToVoter() {
        // Arrange
        VoterCreateRequest.NameRequest nameRequest = new VoterCreateRequest.NameRequest("Max", "Mustermann");
        VoterCreateRequest.AdresseRequest adresseRequest = new VoterCreateRequest.AdresseRequest(
            "Hauptstraße 42",
            "12345",
            "Berlin"
        );
        VoterCreateRequest request = new VoterCreateRequest(
            nameRequest,
            "max@example.com",
            adresseRequest,
            "Mitte"
        );

        // Act
        Voter voter = assembler.toDomain(request);

        // Assert
        assertNotNull(voter);
        assertEquals("Max", voter.getName().firstName());
        assertEquals("Mustermann", voter.getName().lastName());
        assertEquals("max@example.com", voter.getEmail().toString());
        assertEquals("Mitte", voter.getWahlkreis());
        assertNotNull(voter.getVoterId());
        assertFalse(voter.isVerified());
    }

    @ParameterizedTest
    @CsvSource({
        "'Hauptstraße 42', Hauptstraße, 42",
        "'An der Alten Brücke 15', 'An der Alten Brücke', 15",
        "'Hauptstraße 42a', Hauptstraße, 42a",
        "'  Hauptstraße   42  ', Hauptstraße, 42"
    })
    @DisplayName("toDomain should correctly parse various street and house number formats")
    void toDomain_shouldParseStreetAndHouseNumberVariations(String combinedAddress, String expectedStreet, String expectedHouseNumber) {
        // Arrange
        VoterCreateRequest.NameRequest nameRequest = new VoterCreateRequest.NameRequest("Max", "Mustermann");
        VoterCreateRequest.AdresseRequest adresseRequest = new VoterCreateRequest.AdresseRequest(
            combinedAddress,
            "12345",
            "Berlin"
        );
        VoterCreateRequest request = new VoterCreateRequest(
            nameRequest,
            "max@example.com",
            adresseRequest,
            "Mitte"
        );

        // Act
        Voter voter = assembler.toDomain(request);

        // Assert
        assertEquals(expectedStreet, voter.getAdresse().getStreet());
        assertEquals(expectedHouseNumber, voter.getAdresse().getHouseNumber());
        assertEquals("12345", voter.getAdresse().getPostalCode());
        assertEquals("Berlin", voter.getAdresse().getCity());
    }

    @Test
    @DisplayName("toDomain should use fallback when no house number can be extracted")
    void toDomain_shouldUseFallbackForMissingHouseNumber() {
        // Arrange
        VoterCreateRequest.NameRequest nameRequest = new VoterCreateRequest.NameRequest("Max", "Mustermann");
        VoterCreateRequest.AdresseRequest adresseRequest = new VoterCreateRequest.AdresseRequest(
            "Hauptstraße",
            "12345",
            "Berlin"
        );
        VoterCreateRequest request = new VoterCreateRequest(
            nameRequest,
            "max@example.com",
            adresseRequest,
            "Mitte"
        );

        // Act
        Voter voter = assembler.toDomain(request);

        // Assert
        assertEquals("Hauptstraße", voter.getAdresse().getStreet());
        assertEquals("1", voter.getAdresse().getHouseNumber()); // Fallback
    }

    @Test
    @DisplayName("toDomain should set birthdate to 18 years ago")
    void toDomain_shouldSetBirthdateTo18YearsAgo() {
        // Arrange
        VoterCreateRequest.NameRequest nameRequest = new VoterCreateRequest.NameRequest("Max", "Mustermann");
        VoterCreateRequest.AdresseRequest adresseRequest = new VoterCreateRequest.AdresseRequest(
            "Hauptstraße 42",
            "12345",
            "Berlin"
        );
        VoterCreateRequest request = new VoterCreateRequest(
            nameRequest,
            "max@example.com",
            adresseRequest,
            "Mitte"
        );

        // Act
        Voter voter = assembler.toDomain(request);

        // Assert
        LocalDate expectedDate = LocalDate.now().minusYears(18);
        assertEquals(expectedDate, voter.getGeburtsdatum());
    }

    @Test
    @DisplayName("toDomain should throw exception for blank street")
    void toDomain_shouldThrowExceptionForBlankStreet() {
        // Arrange
        VoterCreateRequest.NameRequest nameRequest = new VoterCreateRequest.NameRequest("Max", "Mustermann");
        VoterCreateRequest.AdresseRequest adresseRequest = new VoterCreateRequest.AdresseRequest(
            "   ",
            "12345",
            "Berlin"
        );
        VoterCreateRequest request = new VoterCreateRequest(
            nameRequest,
            "max@example.com",
            adresseRequest,
            "Mitte"
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> assembler.toDomain(request));
    }

    @Test
    @DisplayName("toDomain should throw exception for null street")
    void toDomain_shouldThrowExceptionForNullStreet() {
        // Arrange
        VoterCreateRequest.NameRequest nameRequest = new VoterCreateRequest.NameRequest("Max", "Mustermann");
        VoterCreateRequest.AdresseRequest adresseRequest = new VoterCreateRequest.AdresseRequest(
            null,
            "12345",
            "Berlin"
        );
        VoterCreateRequest request = new VoterCreateRequest(
            nameRequest,
            "max@example.com",
            adresseRequest,
            "Mitte"
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> assembler.toDomain(request));
    }

    // ============ toResponse() Tests ============

    @Test
    @DisplayName("toResponse should map Voter to VoterResponse DTO")
    void toResponse_shouldMapVoterToResponse() {
        // Arrange
        Name name = new Name("Max", "Mustermann");
        Adresse adresse = new Adresse("Hauptstraße", "42", "", "12345", "Berlin");
        Email email = new Email("max@example.com");
        Voter voter = Voter.register(name, adresse, email, LocalDate.of(1990, 1, 1), "Mitte");

        // Act
        VoterResponse response = assembler.toResponse(voter);

        // Assert
        assertNotNull(response);
        assertEquals(voter.getVoterId(), response.id());
        assertEquals("Max", response.name().firstName());
        assertEquals("Mustermann", response.name().lastName());
        assertEquals("Max Mustermann", response.name().fullName());
        assertEquals("max@example.com", response.email());
        assertEquals("Mitte", response.district());
        assertFalse(response.verified());
    }

    @Test
    @DisplayName("toResponse should map address fields correctly")
    void toResponse_shouldMapAddressFieldsCorrectly() {
        // Arrange
        Name name = new Name("Max", "Mustermann");
        Adresse adresse = new Adresse("Hauptstraße", "42", "", "12345", "Berlin");
        Email email = new Email("max@example.com");
        Voter voter = Voter.register(name, adresse, email, LocalDate.of(1990, 1, 1), "Mitte");

        // Act
        VoterResponse response = assembler.toResponse(voter);

        // Assert
        assertNotNull(response.address());
        assertEquals("Hauptstraße", response.address().street());
        assertEquals("42", response.address().houseNumber());
        assertEquals("12345", response.address().postalCode());
        assertEquals("Berlin", response.address().city());
        assertEquals("Hauptstraße 42, 12345 Berlin", response.address().formatted());
    }

    @Test
    @DisplayName("toResponse should return null registeredAt for unverified voter")
    void toResponse_shouldReturnNullRegisteredAtForUnverifiedVoter() {
        // Arrange
        Name name = new Name("Max", "Mustermann");
        Adresse adresse = new Adresse("Hauptstraße", "42", "", "12345", "Berlin");
        Email email = new Email("max@example.com");
        Voter voter = Voter.register(name, adresse, email, LocalDate.of(1990, 1, 1), "Mitte");

        // Act
        VoterResponse response = assembler.toResponse(voter);

        // Assert
        assertNull(response.registeredAt());
        assertFalse(response.verified());
    }

    @Test
    @DisplayName("toResponse should include registeredAt for verified voter")
    void toResponse_shouldIncludeRegisteredAtForVerifiedVoter() {
        // Arrange
        Name name = new Name("Max", "Mustermann");
        Adresse adresse = new Adresse("Hauptstraße", "42", "", "12345", "Berlin");
        Email email = new Email("max@example.com");
        Voter voter = Voter.register(name, adresse, email, LocalDate.of(1990, 1, 1), "Mitte");
        voter.verify();

        // Act
        VoterResponse response = assembler.toResponse(voter);

        // Assert
        assertNotNull(response.registeredAt());
        assertTrue(response.verified());
    }

    // ============ Round-trip Tests ============

    @Test
    @DisplayName("Round-trip mapping should preserve essential data")
    void roundTrip_shouldPreserveEssentialData() {
        // Arrange
        VoterCreateRequest.NameRequest nameRequest = new VoterCreateRequest.NameRequest("Max", "Mustermann");
        VoterCreateRequest.AdresseRequest adresseRequest = new VoterCreateRequest.AdresseRequest(
            "Hauptstraße 42",
            "12345",
            "Berlin"
        );
        VoterCreateRequest request = new VoterCreateRequest(
            nameRequest,
            "max@example.com",
            adresseRequest,
            "Mitte"
        );

        // Act
        Voter voter = assembler.toDomain(request);
        VoterResponse response = assembler.toResponse(voter);

        // Assert
        assertEquals("Max", response.name().firstName());
        assertEquals("Mustermann", response.name().lastName());
        assertEquals("max@example.com", response.email());
        assertEquals("Hauptstraße", response.address().street());
        assertEquals("42", response.address().houseNumber());
        assertEquals("12345", response.address().postalCode());
        assertEquals("Berlin", response.address().city());
        assertEquals("Mitte", response.district());
    }

    // ============ Edge Cases ============

    @Test
    @DisplayName("toResponse should handle address with house number containing letters")
    void toResponse_shouldHandleHouseNumberWithLetters() {
        // Arrange
        Name name = new Name("Max", "Mustermann");
        Adresse adresse = new Adresse("Hauptstraße", "42a", "", "12345", "Berlin");
        Email email = new Email("max@example.com");
        Voter voter = Voter.register(name, adresse, email, LocalDate.of(1990, 1, 1), "Mitte");

        // Act
        VoterResponse response = assembler.toResponse(voter);

        // Assert
        assertEquals("42a", response.address().houseNumber());
        assertEquals("Hauptstraße 42a, 12345 Berlin", response.address().formatted());
    }
}

