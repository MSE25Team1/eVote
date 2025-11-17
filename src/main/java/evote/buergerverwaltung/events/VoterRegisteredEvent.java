package evote.buergerverwaltung.events;

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
public record VoterRegisteredEvent(
        String voterId,
        String firstName,
        String lastName,
        String district,
        LocalDateTime registeredAt,
        String correlationId
) {
}
