package org.example.genres.kafka;

import org.example.genres.model.Genre;
import org.example.genres.repository.GenreRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class GenreCreateListener {

    private final GenreRepository genreRepository;

    public GenreCreateListener(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @KafkaListener(topics = "${app.kafka.genre-create-topic}")
    public void handle(GenreCreateCommand command) {
        if (command == null || command.getName() == null || command.getName().isBlank()) {
            return;
        }
        genreRepository.save(new Genre(null, command.getName()));
    }
}
