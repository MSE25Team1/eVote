package com.mse.eVote.buergerVerwaltung.events;

import java.time.LocalDateTime;

/**
 * VoterRegisteredEvent - Domain Event
 *
 * Published when a new Voter successfully completes registration and verification.
 * This is a factual record of what happened in the domain, used for:
 * - Triggering side effects (e.g., sending confirmation email)
 * - Synchronizing other Bounded Contexts (e.g., poll eligibility)
 * - Audit logging
 */
public class VoterRegisteredEvent {
    private final String voterId;
    private final String firstName;
    private final String lastName;
    private final String district;
    private final LocalDateTime registeredAt;
    private final String correlationId;

    public VoterRegisteredEvent(
            String voterId,
            String firstName,
            String lastName,
            String district,
            LocalDateTime registeredAt,
            String correlationId) {
        this.voterId = voterId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.district = district;
        this.registeredAt = registeredAt;
        this.correlationId = correlationId;
    }

    // ============ English getters ============
    public String getVoterId() { return voterId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDistrict() { return district; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public String getCorrelationId() { return correlationId; }

    // ============ German aliases (for backward compatibility) ============
    public String getVorname() { return firstName; }
    public String getNachname() { return lastName; }
    public String getWahlkreis() { return district; }

    @Override
    public String toString() {
        return "VoterRegisteredEvent{" +
                "voterId='" + voterId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", district='" + district + '\'' +
                ", registeredAt=" + registeredAt +
                ", correlationId='" + correlationId + '\'' +
                '}';
    }
}

