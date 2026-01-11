package evote.buergerverwaltung.events;

import java.time.LocalDateTime;

/**
 * Domänenereignis, das nach erfolgreicher Registrierung und Verifikation ausgelöst wird.
 * Dient der Synchronisation anderer Bounded Contexts und dem Nachvollziehen von Abläufen.
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
