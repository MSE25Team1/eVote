package evote.stimmvergabe.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request-DTO für die Stimmabgabe.
 * Entspricht dem Stil und der Struktur von VoterCreateRequest.
 *
 * {
 *   "vote": {
 *      "pollId": "...",
 *      "optionId": "...",
 *      "voterId": "..."
 *   }
 * }
 */
public record VoteCreateRequest(

        @NotBlank
        @JsonProperty("pollId") String pollId,

        @NotBlank
        @JsonProperty("optionId") String optionId,

        @NotBlank
        @JsonProperty("voterId") String voterId

) {}
