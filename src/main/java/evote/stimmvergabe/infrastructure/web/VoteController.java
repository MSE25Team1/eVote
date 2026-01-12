package evote.stimmvergabe.infrastructure.web;

import evote.stimmvergabe.application.VoteService;
import evote.stimmvergabe.application.dto.VoteCreateRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST-Adapter der Stimmvergabe, der HTTP-Requests an den VoteService weiterleitet.
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
        service.create(req);
        return ResponseEntity.created(null).build();
    }
}
