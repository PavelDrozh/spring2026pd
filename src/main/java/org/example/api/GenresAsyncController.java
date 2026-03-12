package org.example.api;

import jakarta.validation.Valid;
import org.example.dto.GenreCreateRequest;
import org.example.kafka.GenreCreateCommand;
import org.example.kafka.GenreCreateProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/genres")
public class GenresAsyncController {

    private final GenreCreateProducer genreCreateProducer;

    public GenresAsyncController(GenreCreateProducer genreCreateProducer) {
        this.genreCreateProducer = genreCreateProducer;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> create(@Valid @RequestBody GenreCreateRequest request) {
        genreCreateProducer.send(new GenreCreateCommand(request.getName()));
        return ResponseEntity.accepted().build();
    }
}
