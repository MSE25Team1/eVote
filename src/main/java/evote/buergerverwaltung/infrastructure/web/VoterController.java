package evote.buergerverwaltung.infrastructure.web;

import evote.buergerverwaltung.application.VoterService;
import evote.buergerverwaltung.application.dto.VoterCreateRequest;
import evote.buergerverwaltung.application.dto.VoterResponse;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * VoterController - REST Adapter (Infrastructure Layer)
 * 
 * Responsibility: Exposes HTTP endpoints for voter management.
 * This is an adapter that translates HTTP requests into application service calls.
 * Thin layer - all business logic is in the application/domain layers.
 */
@RestController
@RequestMapping("/api/voter")
@CrossOrigin(origins = "http://localhost:3000")
public class VoterController {

    private final VoterService service;

    public VoterController(VoterService service) {
        this.service = service;
    }

    @PostMapping
    public VoterResponse create(@RequestBody @Valid VoterCreateRequest req) {
        return service.create(req);
    }

    @GetMapping("/{id}")
    public VoterResponse getById(@PathVariable("id") String id) {
        return service.getById(id);
    }
}

