package evote.buergerverwaltung.infrastructure.web;

import evote.buergerverwaltung.application.VoterService;
import evote.buergerverwaltung.application.dto.VoterCreateRequest;
import evote.buergerverwaltung.application.dto.VoterResponse;
import evote.buergerverwaltung.application.dto.VoterUpdateRequest;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * REST-Adapter der Infrastruktur, der HTTP-Aufrufe in Use-Cases der Anwendungsschicht übersetzt.
 * Die Fachlogik bleibt in Domäne und Anwendungsschicht.
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

    @PutMapping("/{id}")
    public VoterResponse updateProfile(@PathVariable("id") String id,
                                       @RequestBody @Valid VoterUpdateRequest request) {
        return service.updateEmail(id, request.email());
    }
}
