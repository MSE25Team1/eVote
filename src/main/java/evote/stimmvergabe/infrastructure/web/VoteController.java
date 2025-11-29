package evote.stimmvergabe.infrastructure.web;

//import evote.buergerverwaltung.application.VoterService;
//import evote.buergerverwaltung.application.dto.VoterCreateRequest;
//import evote.buergerverwaltung.application.dto.VoterResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * VoteController - REST Adapter (Infrastructure Layer)
 *
 * Responsibility: Exposes HTTP endpoints for vote casting.
 * This is an adapter that translates HTTP requests into application service calls.
 * Thin layer - all business logic is in the application/domain layers.
 */
@RestController
@RequestMapping("/api/vote")
@CrossOrigin(origins = "http://localhost:3000")
public class VoteController {

    private final VoteService service;

    public VoteController(VoteService service) {
        this.service = service;
    }

    /**
     * POST /api/vote
     *
     * @param req
     * @return Frontend sendet pollId, ...
     * Geschäftsprozess: Stimme abgeben
     */

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid VoteCreateRequest req) {
        return service.create(req);
    }
}
