package evote.stimmvergabe.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request-DTO für die Stimmabgabe.
 * Entspricht dem Stil und der Struktur von VoterCreateRequest.
 *
 * Die correlationId wird vom Server vorgegeben und dient der Idempotenz:
 * - Server generiert bei Abstimmungsstart eine eindeutige correlationId
 * - Frontend sendet diese mit dem Vote Request zurück
 * - Mehrfache Requests mit gleicher correlationId werden erkannt (Double-Voting-Prevention)
 *
 * {
 *   "vote": {
 *      "pollId": "...",
 *      "optionId": "...",
 *      "voterId": "...",
 *      "correlationId": "..."
 *   }
 * }
 */
public record VoteCreateRequest(

        @NotBlank
        @JsonProperty("pollId") String pollId,

        @NotBlank
        @JsonProperty("optionId") String optionId,

        @NotBlank
        @JsonProperty("voterId") String voterId,

        @NotBlank
        @JsonProperty("correlationId") String correlationId

) {}
