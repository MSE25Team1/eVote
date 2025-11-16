package com.mse.eVote.buergerVerwaltung.api;

import com.mse.eVote.buergerVerwaltung.service.VoterService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/voter")
@CrossOrigin(origins = "http://localhost:3000")
public class VoterController {

    private final VoterService service;

    public VoterController(VoterService service) {
        this.service = service;
    }

    @PostMapping
    public VoterResponse create(@RequestBody VoterCreateRequest req) {
        return service.create(req);
    }

    @GetMapping("/{id}")
    public VoterResponse getById(@PathVariable String id) {
        return service.getById(id);
    }
}
