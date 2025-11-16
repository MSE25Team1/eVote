package evote.buergerverwaltung.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

@DisplayName("VoterRegisteredEvent - Domain Event Tests")
class VoterRegisteredEventTest {

    @Test
    @DisplayName("VoterRegisteredEvent should create with all fields")
    void voterRegisteredEvent_shouldCreateSuccessfully() {
        String voterId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        String correlationId = UUID.randomUUID().toString();
        
        VoterRegisteredEvent event = new VoterRegisteredEvent(
            voterId, "Max", "Mustermann", "Kreis-12345", now, correlationId
        );
        
        assertEquals(voterId, event.voterId());
        assertEquals("Max", event.firstName());
        assertEquals("Mustermann", event.lastName());
        assertEquals("Kreis-12345", event.district());
        assertEquals(now, event.registeredAt());
        assertEquals(correlationId, event.correlationId());
    }

    @Test
    @DisplayName("VoterRegisteredEvent should have meaningful toString")
    void voterRegisteredEvent_shouldHaveMeaningfulToString() {
        String voterId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        String correlationId = UUID.randomUUID().toString();
        
        VoterRegisteredEvent event = new VoterRegisteredEvent(
            voterId, "Max", "Mustermann", "Kreis-12345", now, correlationId
        );
        
        String toString = event.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("VoterRegisteredEvent"));
        assertTrue(toString.contains(voterId));
        assertTrue(toString.contains("Max"));
        assertTrue(toString.contains("Mustermann"));
    }
}

