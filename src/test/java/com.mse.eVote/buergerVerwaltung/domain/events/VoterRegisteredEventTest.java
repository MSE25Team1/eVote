package com.mse.eVote.buergerVerwaltung.domain.events;

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
        
        assertEquals(voterId, event.getVoterId());
        assertEquals("Max", event.getVorname());
        assertEquals("Mustermann", event.getNachname());
        assertEquals("Kreis-12345", event.getWahlkreis());
        assertEquals(now, event.getRegisteredAt());
        assertEquals(correlationId, event.getCorrelationId());
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

