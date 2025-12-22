package evote.abstimmungsverwaltung.application.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Einfaches DTO zur Anzeige von Abstimmungen im Frontend.
 */
public record PollDTO(
        String pollId,
        String title,
        LocalDateTime startDate,
        LocalDateTime endDate,
        List<String> options
) {
}
