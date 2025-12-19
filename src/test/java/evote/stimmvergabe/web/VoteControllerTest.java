package evote.stimmvergabe.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import evote.stimmvergabe.application.VoteService;
import evote.stimmvergabe.application.dto.VoteCreateRequest;
import evote.stimmvergabe.infrastructure.web.VoteController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VoteController.class)

class VoteControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    VoteService voteService;  //voteService wird durch den Mock ersetzt. Wir testen den Controller isoliert und kontrollieren die Ausgabe des Services.

    @Test
    @DisplayName("POST /api/vote – gültiger Request → 201 Created")
    void create_validRequest_returnsVoteCreateResponse() throws Exception {
        // ---------- Arrange ----------
        VoteCreateRequest requestDto = new VoteCreateRequest(
                "POLL-CK-2026",
                "OPTION-MIX",
                "voter-123",
                "correlation-uuid-001"
        );

        doNothing().when(voteService).create(any(VoteCreateRequest.class));

        String jsonBody = objectMapper.writeValueAsString(requestDto);

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated());

        verify(voteService).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – leere pollId → 400 Bad Request")
    void create_emptyPollId_returnsBadRequest() throws Exception {
        // ---------- Arrange ----------
        VoteCreateRequest requestDto = new VoteCreateRequest(
                "",  // leere pollId
                "OPTION-MIX",
                "voter-123",
                "correlation-uuid-002"
        );

        String jsonBody = objectMapper.writeValueAsString(requestDto);

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());

        verify(voteService, never()).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – null pollId → 400 Bad Request")
    void create_nullPollId_returnsBadRequest() throws Exception {
        // ---------- Arrange ----------
        String jsonBody = "{\"optionId\": \"OPTION-MIX\", \"voterId\": \"voter-123\", \"correlationId\": \"correlation-uuid-003\"}";

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());

        verify(voteService, never()).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – leere optionId → 400 Bad Request")
    void create_emptyOptionId_returnsBadRequest() throws Exception {
        // ---------- Arrange ----------
        VoteCreateRequest requestDto = new VoteCreateRequest(
                "POLL-CK-2026",
                "",  // leere optionId
                "voter-123",
                "correlation-uuid-004"
        );

        String jsonBody = objectMapper.writeValueAsString(requestDto);

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());

        verify(voteService, never()).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – leere voterId → 400 Bad Request")
    void create_emptyVoterId_returnsBadRequest() throws Exception {
        // ---------- Arrange ----------
        VoteCreateRequest requestDto = new VoteCreateRequest(
                "POLL-CK-2026",
                "OPTION-MIX",
                "",  // leere voterId
                "correlation-uuid-005"
        );

        String jsonBody = objectMapper.writeValueAsString(requestDto);

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());

        verify(voteService, never()).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – fehlender Content-Type → 415 Unsupported Media Type")
    void create_noContentType_returnsUnsupportedMediaType() throws Exception {
        // ---------- Arrange ----------
        VoteCreateRequest requestDto = new VoteCreateRequest(
                "POLL-CK-2026",
                "OPTION-MIX",
                "voter-123",
                "correlation-uuid-006"
        );

        String jsonBody = objectMapper.writeValueAsString(requestDto);

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .content(jsonBody))
                .andExpect(status().isUnsupportedMediaType());

        verify(voteService, never()).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – ungültiges JSON → 400 Bad Request (Spring MVC)")
    void create_invalidJson_returnsBadRequest() throws Exception {
        // ---------- Arrange ----------
        // Ungültiges JSON-Format wird von Spring MVC verarbeitet und führt zu 400
        String invalidJsonBody = "{\"pollId\": \"POLL-CK-2026\", invalid json}";

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJsonBody))
                .andExpect(status().isBadRequest());

        verify(voteService, never()).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – Felder mit nur Whitespace → 400 Bad Request")
    void create_whitespaceOnlyFields_returnsBadRequest() throws Exception {
        // ---------- Arrange ----------
        VoteCreateRequest requestDto = new VoteCreateRequest(
                "   ",  // nur Whitespace
                "OPTION-MIX",
                "voter-123",
                "correlation-uuid-007"
        );

        String jsonBody = objectMapper.writeValueAsString(requestDto);

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());

        verify(voteService, never()).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – mehrere ungültige Felder → 400 Bad Request")
    void create_multipleInvalidFields_returnsBadRequest() throws Exception {
        // ---------- Arrange ----------
        VoteCreateRequest requestDto = new VoteCreateRequest(
                "",  // leere pollId
                "",  // leere optionId
                "voter-123",
                "correlation-uuid-008"
        );

        String jsonBody = objectMapper.writeValueAsString(requestDto);

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());

        verify(voteService, never()).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – sehr lange Felder → 201 Created (Validierung akzeptiert lange Werte)")
    void create_veryLongFields_returnsCreated() throws Exception {
        // ---------- Arrange ----------
        String longValue = "A".repeat(1000);
        VoteCreateRequest requestDto = new VoteCreateRequest(
                longValue,
                longValue,
                longValue,
                "correlation-uuid-009"
        );

        doNothing().when(voteService).create(any(VoteCreateRequest.class));

        String jsonBody = objectMapper.writeValueAsString(requestDto);

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated());

        verify(voteService).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – Felder mit Sonderzeichen → 201 Created")
    void create_fieldsWithSpecialCharacters_returnsCreated() throws Exception {
        // ---------- Arrange ----------
        VoteCreateRequest requestDto = new VoteCreateRequest(
                "POLL-CK-2026@#$%",
                "OPTION-MIX!&*",
                "voter-123_äöü",
                "correlation-uuid-010"
        );

        doNothing().when(voteService).create(any(VoteCreateRequest.class));

        String jsonBody = objectMapper.writeValueAsString(requestDto);

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated());

        verify(voteService).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – fehlender Request Body → 400 Bad Request (Spring MVC)")
    void create_missingRequestBody_returnsBadRequest() throws Exception {
        // ---------- Arrange ----------
        // Spring MVC verarbeitet fehlenden Body und gibt 400 zurück

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON))
                // kein .content() gesetzt
                .andExpect(status().isBadRequest());

        verify(voteService, never()).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – nur optionId und voterId → 400 Bad Request (pollId fehlt)")
    void create_missingPollId_returnsBadRequest() throws Exception {
        // ---------- Arrange ----------
        String jsonBody = "{\"optionId\": \"OPTION-MIX\", \"voterId\": \"voter-123\", \"correlationId\": \"correlation-uuid-011\"}";

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());

        verify(voteService, never()).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – nur pollId und voterId → 400 Bad Request (optionId fehlt)")
    void create_missingOptionId_returnsBadRequest() throws Exception {
        // ---------- Arrange ----------
        String jsonBody = "{\"pollId\": \"POLL-CK-2026\", \"voterId\": \"voter-123\", \"correlationId\": \"correlation-uuid-012\"}";

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());

        verify(voteService, never()).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – nur pollId und optionId → 400 Bad Request (voterId fehlt)")
    void create_missingVoterId_returnsBadRequest() throws Exception {
        // ---------- Arrange ----------
        String jsonBody = "{\"pollId\": \"POLL-CK-2026\", \"optionId\": \"OPTION-MIX\", \"correlationId\": \"correlation-uuid-013\"}";

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());

        verify(voteService, never()).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – leeres JSON Object {} → 400 Bad Request")
    void create_emptyJsonObject_returnsBadRequest() throws Exception {
        // ---------- Arrange ----------
        String jsonBody = "{}";

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());

        verify(voteService, never()).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – ungültiges Content-Type (text/plain) → 415 Unsupported Media Type")
    void create_invalidContentType_returnsUnsupportedMediaType() throws Exception {
        // ---------- Arrange ----------
        VoteCreateRequest requestDto = new VoteCreateRequest(
                "POLL-CK-2026",
                "OPTION-MIX",
                "voter-123",
                "correlation-uuid-021"
        );

        String jsonBody = objectMapper.writeValueAsString(requestDto);

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(jsonBody))
                .andExpect(status().isUnsupportedMediaType());

        verify(voteService, never()).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – alle Felder null in JSON → 400 Bad Request")
    void create_allFieldsNull_returnsBadRequest() throws Exception {
        // ---------- Arrange ----------
        String jsonBody = "{\"pollId\": null, \"optionId\": null, \"voterId\": null, \"correlationId\": null}";

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());

        verify(voteService, never()).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – mit zusätzlichen unbekannten Feldern → 201 Created (werden ignoriert)")
    void create_withExtraUnknownFields_returnsCreated() throws Exception {
        // ---------- Arrange ----------
        String jsonBody = "{\"pollId\": \"POLL-CK-2026\", \"optionId\": \"OPTION-MIX\", \"voterId\": \"voter-123\", \"correlationId\": \"correlation-uuid-014\", \"unknownField\": \"should-be-ignored\"}";

        doNothing().when(voteService).create(any(VoteCreateRequest.class));

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated());

        verify(voteService).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – nur Whitespace in Body → 400 Bad Request (Spring MVC)")
    void create_onlyWhitespaceBody_returnsBadRequest() throws Exception {
        // ---------- Arrange ----------
        // Spring MVC verarbeitet ungültiges/leeres JSON und gibt 400 zurück

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("   \n\t  "))
                .andExpect(status().isBadRequest());

        verify(voteService, never()).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – Request mit UTF-8 Umlauten → 201 Created")
    void create_requestWithUtf8Characters_returnsCreated() throws Exception {
        // ---------- Arrange ----------
        VoteCreateRequest requestDto = new VoteCreateRequest(
                "POLL-2026-Überblick",
                "OPTION-Häufig",
                "voter-Müller",
                "correlation-uuid-018"
        );

        doNothing().when(voteService).create(any(VoteCreateRequest.class));

        String jsonBody = objectMapper.writeValueAsString(requestDto);

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isCreated());

        verify(voteService).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – alle Felder mit Minimaltext → 201 Created")
    void create_minimalValidFields_returnsCreated() throws Exception {
        // ---------- Arrange ----------
        VoteCreateRequest requestDto = new VoteCreateRequest(
                "1",  // minimal gültiger String
                "2",
                "3",
                "4"
        );

        doNothing().when(voteService).create(any(VoteCreateRequest.class));

        String jsonBody = objectMapper.writeValueAsString(requestDto);

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated());

        verify(voteService).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – pollId mit führenden/nachfolgenden Spaces → 400 Bad Request")
    void create_pollIdWithLeadingTrailingSpaces_returnsBadRequest() throws Exception {
        // ---------- Arrange ----------
        String jsonBody = "{\"pollId\": \"  POLL-CK-2026  \", \"optionId\": \"OPTION-MIX\", \"voterId\": \"voter-123\", \"correlationId\": \"correlation-uuid-019\"}";

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated());

        verify(voteService).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – Service wirft IllegalArgumentException → 400 Bad Request (bei ungültigen Daten)")
    void create_serviceThrowsIllegalArgumentException_returnsBadRequest() throws Exception {
        // ---------- Arrange ----------
        VoteCreateRequest requestDto = new VoteCreateRequest(
                "INVALID-POLL",
                "INVALID-OPTION",
                "INVALID-VOTER",
                "correlation-uuid-015"
        );

        doThrow(new IllegalArgumentException("Invalid poll ID"))
                .when(voteService).create(any(VoteCreateRequest.class));

        String jsonBody = objectMapper.writeValueAsString(requestDto);

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());

        verify(voteService).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – Service wirft IllegalStateException → 409 Conflict (Voter hat bereits abgestimmt)")
    void create_serviceThrowsIllegalStateException_returnsConflict() throws Exception {
        // ---------- Arrange ----------
        VoteCreateRequest requestDto = new VoteCreateRequest(
                "POLL-CK-2026",
                "OPTION-MIX",
                "VOTER-001",
                "correlation-uuid-double-voting"
        );

        doThrow(new IllegalStateException("Voter has already voted for this poll. Voter ID: VOTER-001, Poll ID: POLL-CK-2026"))
                .when(voteService).create(any(VoteCreateRequest.class));

        String jsonBody = objectMapper.writeValueAsString(requestDto);

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isConflict())  // 409 statt 500
                .andExpect(status().is(409));

        verify(voteService).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – mehrere Requests hintereinander → beide erfolgreich")
    void create_multipleRequestsSequentially_bothSucceed() throws Exception {
        // ---------- Arrange ----------
        VoteCreateRequest request1 = new VoteCreateRequest(
                "POLL-1",
                "OPTION-1",
                "voter-1",
                "correlation-uuid-016"
        );

        VoteCreateRequest request2 = new VoteCreateRequest(
                "POLL-2",
                "OPTION-2",
                "voter-2",
                "correlation-uuid-017"
        );

        doNothing().when(voteService).create(any(VoteCreateRequest.class));

        String jsonBody1 = objectMapper.writeValueAsString(request1);
        String jsonBody2 = objectMapper.writeValueAsString(request2);

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody1))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody2))
                .andExpect(status().isCreated());

        // Service sollte zweimal aufgerufen worden sein
        verify(voteService, times(2)).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – Request mit führenden/nachfolgenden Zeilenumbrüchen → 201 Created (werden ignoriert)")
    void create_bodyWithLeadingTrailingNewlines_returnsCreated() throws Exception {
        // ---------- Arrange ----------
        // Jackson ignoriert führende/nachfolgende Zeilenumbrüche beim Parsen
        String jsonBody = "\n\n{\"pollId\": \"POLL-CK-2026\", \"optionId\": \"OPTION-MIX\", \"voterId\": \"voter-123\", \"correlationId\": \"correlation-uuid-023\"}\n\n";

        doNothing().when(voteService).create(any(VoteCreateRequest.class));

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated());

        verify(voteService).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – Falsche HTTP-Methode (GET statt POST) → 405 Method Not Allowed")
    void create_wrongHttpMethod_returnsMethodNotAllowed() throws Exception {
        // ---------- Act & Assert ----------
        mockMvc.perform(get("/api/vote"))
                .andExpect(status().isMethodNotAllowed());

        verify(voteService, never()).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – Response-Status ist 201 Created (nicht 200 OK)")
    void create_validRequest_returnsCorrectStatusCode() throws Exception {
        // ---------- Arrange ----------
        VoteCreateRequest requestDto = new VoteCreateRequest(
                "POLL-CK-2026",
                "OPTION-MIX",
                "voter-123",
                "correlation-uuid-022"
        );

        doNothing().when(voteService).create(any(VoteCreateRequest.class));

        String jsonBody = objectMapper.writeValueAsString(requestDto);

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())  // 201 statt 200
                .andExpect(status().is(201));      // explizite Assertion

        verify(voteService).create(any(VoteCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/vote – Request mit application/json;charset=UTF-8 → 201 Created")
    void create_requestWithCharsetInContentType_returnsCreated() throws Exception {
        // ---------- Arrange ----------
        VoteCreateRequest requestDto = new VoteCreateRequest(
                "POLL-CK-2026",
                "OPTION-MIX",
                "voter-123",
                "correlation-uuid-020"
        );

        doNothing().when(voteService).create(any(VoteCreateRequest.class));

        String jsonBody = objectMapper.writeValueAsString(requestDto);

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType("application/json;charset=UTF-8")
                        .content(jsonBody))
                .andExpect(status().isCreated());

        verify(voteService).create(any(VoteCreateRequest.class));
    }
}
