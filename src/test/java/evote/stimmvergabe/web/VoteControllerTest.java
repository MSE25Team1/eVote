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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VoteController.class)

class VoteControllerTest {

    //@Autowired
    MockMvc mockMvc;

    //@Autowired
    ObjectMapper objectMapper;

    @MockBean
    VoteService voteService;  //voteService wird durch den Mock ersetzt. Wir testen den Controller isoliert und kontrollieren die Ausgabe des Services.

    @Test
    @DisplayName("POST /api/vote – gültiger Request → 200 OK + Response-Body")
    void create_validRequest_returnsVoteCreateResponse() throws Exception {
        // ---------- Arrange ----------
        VoteCreateRequest requestDto = new VoteCreateRequest(
                "POLL-CK-2026",
                "OPTION-MIX",
                "voter-123"
        );

        doNothing().when(voteService).create(any(VoteCreateRequest.class));   // Wenn der Controller den Service aufruft, geben wir das fertige Response-Objekt zurück.

        String jsonBody = objectMapper.writeValueAsString(requestDto);  // Umwandlung in json für den Post Handler (wie es das Frontend tun würde)

        // ---------- Act & Assert ----------
        mockMvc.perform(post("/api/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated());  // status.isCreated()?

        // prüfen, dass der Service wirklich aufgerufen wurde
        verify(voteService).create(any(VoteCreateRequest.class));
    }
}
